package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.content
import com.harshdeep.jasnify.domain.repository.*
import com.harshdeep.jasnify.domain.model.*
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.presentation.screens.others.AiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GenerativeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val budgetRepository: BudgetRepository,
    private val guestRepository: GuestRepository,
    private val venueRepository: VenueRepository,
    private val vendorRepository: VendorRepository,
    private val checklistRepository: ChecklistRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AiMessage>>(emptyList())
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _eventId = MutableStateFlow<String?>(null)
    
    private val _globalContext = MutableStateFlow("")

    // Initialize Firebase AI with the requested model
    private val generativeModel = FirebaseAI.getInstance(FirebaseApp.getInstance()).generativeModel(
        modelName = "gemini-3.1-flash-lite",
        generationConfig = GenerationConfig.builder()
            .setTemperature(0.7f)
            .setTopK(40)
            .setTopP(0.95f)
            .build(),
        systemInstruction = content {
            text("""
                You are Jasnify AI, a professional wedding and event planning assistant. 
                Your goal is to help users manage their event using ONLY the provided CURRENT EVENT CONTEXT.
                
                CRITICAL RULES:
                1. NEVER hallucinate details (like event names or guest counts) that are not in the context.
                2. If the context is empty, politely ask the user to select or create an event first.
                3. NEVER mention IDs (like venueId, guestId, etc.) in your conversational text.
                4. Use the IDs ONLY within the JSON_DATA block for UI card rendering.
                5. Always provide helpful, concise, and friendly advice.
                
                STRUCTURED OUTPUT FOR UI CARDS:
                If you mention or suggest specific venues, vendors, guests, expenses, or checklists, you MUST append a JSON block at the end of your response starting with 'JSON_DATA:' on a new line.
                Format: JSON_DATA: {"venueIds": [], "vendorIds": [], "guestIds": [], "expenseIds": [], "checklistIds": []}
                
                - Use 'venueIds' for suggested venues.
                - Use 'vendorIds' for suggested vendors.
                - Use 'guestIds' for specific guests mentioned.
                - Use 'expenseIds' for specific expenses/transactions mentioned.
                - Use 'checklistIds' for specific checklist items mentioned.
                
                Example: "I found a great venue for you: The Grand Ballroom. JSON_DATA: {"venueIds": ["v123"]}"
            """.trimIndent())
        }
    )

    private var chat = generativeModel.startChat()

    init {
        observeContext()
    }

    fun setEventId(id: String) {
        if (_eventId.value != id) {
            _eventId.value = id
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeContext() {
        _eventId.flatMapLatest { id ->
            if (id == null) flowOf("")
            else {
                combine(
                    getEventFlow(id),
                    budgetRepository.getBudgetSettings(id),
                    budgetRepository.getAllExpenses(id),
                    guestRepository.getGuests(id),
                    checklistRepository.getAllChecklists(id),
                    venueRepository.getAllVenues(),
                    vendorRepository.getAllVendors()
                ) { flows ->
                    val event = flows[0] as? Event
                    val budget = flows[1] as? com.harshdeep.jasnify.data.local.BudgetEntity
                    val expenses = flows[2] as? List<ExpenseEntity> ?: emptyList()
                    val guests = flows[3] as? List<Guest> ?: emptyList()
                    val checklists = flows[4] as? List<Checklist> ?: emptyList()
                    val allVenues = flows[5] as? List<Venue> ?: emptyList()
                    val allVendors = flows[6] as? List<Vendor> ?: emptyList()

                    buildContextString(event, budget, expenses, guests, checklists, allVenues, allVendors)
                }
            }
        }.onEach { context ->
            _globalContext.value = context
            android.util.Log.d("GenerativeViewModel", "Context Updated: ${context.take(100)}...")
        }.launchIn(viewModelScope)
    }

    private fun getEventFlow(eventId: String): Flow<Event?> = callbackFlow {
        val registration = firestore.collection("events").document(eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(Event::class.java))
            }
        awaitClose { registration.remove() }
    }

    private fun buildContextString(
        event: Event?,
        budget: com.harshdeep.jasnify.data.local.BudgetEntity?,
        expenses: List<ExpenseEntity>,
        guests: List<Guest>,
        checklists: List<Checklist>,
        allVenues: List<Venue>,
        allVendors: List<Vendor>
    ): String {
        if (event == null) return "No active event found."

        val sb = StringBuilder()
        sb.append("### CURRENT EVENT CONTEXT\n")
        sb.append("EVENT NAME: ${event.name}\n")
        sb.append("OWNER: ${event.ownerName}\n")
        sb.append("DATE: ${event.date?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) } ?: "TBD"}\n")
        
        val totalBudget = budget?.totalBudget ?: event.budget ?: 0.0
        val totalSpent = expenses.sumOf { it.amount }
        sb.append("TOTAL BUDGET: ₹$totalBudget\n")
        sb.append("TOTAL SPENT: ₹$totalSpent\n")
        sb.append("REMAINING BUDGET: ₹${totalBudget - totalSpent}\n")
        
        sb.append("\n### GUEST STATISTICS\n")
        sb.append("TOTAL GUESTS: ${guests.size}\n")
        sb.append("INVITED: ${guests.count { it.invited }}\n")
        
        sb.append("\n### AVAILABLE RESOURCES (USE THESE IDs IN JSON_DATA ONLY)\n")
        sb.append("VENUES:\n")
        allVenues.forEach { sb.append("- ID: ${it.id} | NAME: ${it.name}\n") }
        sb.append("VENDORS:\n")
        allVendors.forEach { sb.append("- ID: ${it.id} | NAME: ${it.name} | CAT: ${it.category}\n") }

        sb.append("\n### GUEST LIST (IDs for JSON_DATA):\n")
        guests.forEach { sb.append("- ID: ${it.id} | NAME: ${it.name} | TYPE: ${it.type}\n") }

        sb.append("\n### EXPENSES (IDs for JSON_DATA):\n")
        expenses.forEach { sb.append("- ID: ${it.id} | TITLE: ${it.title} | AMT: ${it.amount}\n") }

        sb.append("\n### CHECKLIST PROGRESS (IDs for JSON_DATA):\n")
        checklists.forEach { cl ->
            val done = cl.items.count { it.checked }
            sb.append("- ID: ${cl.id} | TITLE: ${cl.title}: $done/${cl.items.size} done\n")
        }

        return sb.toString()
    }

    fun sendMessage(userText: String) {
        val userMessage = AiMessage(
            id = UUID.randomUUID().toString(),
            text = userText,
            isUser = true
        )
        _messages.value += userMessage

        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val fullPrompt = "CONTEXT:\n${_globalContext.value}\n\nUSER: $userText"
                val response = chat.sendMessage(fullPrompt)
                val responseText = response.text ?: ""

                val (cleanText, aiMsg) = parseAiResponse(responseText)
                _messages.value += aiMsg.copy(text = cleanText)
            } catch (e: Exception) {
                _messages.value += AiMessage(
                    id = UUID.randomUUID().toString(),
                    text = "Sorry, I encountered an error: ${e.localizedMessage}",
                    isUser = false
                )
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private fun parseAiResponse(rawText: String): Pair<String, AiMessage> {
        val jsonMarker = "JSON_DATA:"
        val parts = rawText.split(jsonMarker)
        val cleanText = parts[0].trim()
        
        var aiMsg = AiMessage(id = UUID.randomUUID().toString(), text = cleanText, isUser = false)
        
        if (parts.size > 1) {
            try {
                val jsonStr = parts[1].trim()
                val json = JSONObject(jsonStr)
                
                aiMsg = aiMsg.copy(
                    venueIds = json.optJSONArray("venueIds")?.let { arr -> List(arr.length()) { arr.getString(it) } } ?: emptyList(),
                    vendorIds = json.optJSONArray("vendorIds")?.let { arr -> List(arr.length()) { arr.getString(it) } } ?: emptyList(),
                    guestIds = json.optJSONArray("guestIds")?.let { arr -> List(arr.length()) { arr.getString(it) } } ?: emptyList(),
                    expenseIds = json.optJSONArray("expenseIds")?.let { arr -> List(arr.length()) { arr.getString(it) } } ?: emptyList(),
                    checklistIds = json.optJSONArray("checklistIds")?.let { arr -> List(arr.length()) { arr.getString(it) } } ?: emptyList(),
                    showBudgetSummary = false // Explicitly disabled as per user request
                )
            } catch (e: Exception) {
                android.util.Log.e("GenerativeViewModel", "JSON Parsing failed", e)
            }
        }
        
        return cleanText to aiMsg
    }
}
