package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.model.*
import com.harshdeep.jasnify.domain.repository.*
import com.harshdeep.jasnify.presentation.screens.others.AiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@IgnoreExtraProperties
data class ChatSession(
    var id: String = "",
    var title: String = "New Chat",
    var timestamp: Long = System.currentTimeMillis()
)

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

    private val _chatSessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val chatSessions: StateFlow<List<ChatSession>> = _chatSessions.asStateFlow()

    private val _currentChatId = MutableStateFlow<String?>(null)
    val currentChatId: StateFlow<String?> = _currentChatId.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _eventId = MutableStateFlow<String?>(null)
    private val _globalContext = MutableStateFlow("")

    private var sessionsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

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
            """.trimIndent())
        }
    )

    private var chat = generativeModel.startChat()

    init {
        observeContext()
    }

    fun setEventId(id: String) {
        if (id.isBlank()) return
        if (_eventId.value != id) {
            _eventId.value = id
            loadChatSessions(id)
        }
    }

    private fun loadChatSessions(eventId: String) {
        sessionsListener?.remove()
        sessionsListener = firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    android.util.Log.e("GenerativeViewModel", "Error fetching sessions: ${e.message}", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val sessions = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatSession::class.java)?.copy(id = doc.id)
                    }
                    _chatSessions.value = sessions
                } else {
                    _chatSessions.value = emptyList()
                }
            }
    }

    fun selectChatSession(chatId: String) {
        _currentChatId.value = chatId
        val eventId = _eventId.value ?: return

        messagesListener?.remove()
        messagesListener = firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    android.util.Log.e("GenerativeViewModel", "Error fetching messages: ${e.message}", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val msgs = snapshot.toObjects(AiMessage::class.java)
                    _messages.value = msgs

                    val history = msgs.filter { it.text.isNotBlank() }.map { msg ->
                        content(role = if (msg.isUser) "user" else "model") { text(msg.text) }
                    }
                    chat = generativeModel.startChat(history)
                }
            }
    }

    fun createNewChatSession(initialTitle: String = "New Chat") {
        val eventId = _eventId.value ?: return
        val newChatId = UUID.randomUUID().toString()
        val session = ChatSession(
            id = newChatId,
            title = initialTitle,
            timestamp = System.currentTimeMillis()
        )

        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(newChatId)
            .set(session)
            .addOnSuccessListener {
                selectChatSession(newChatId)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("GenerativeViewModel", "Failed to create chat session", e)
            }
    }

    fun deleteChatSession(chatId: String) {
        val eventId = _eventId.value ?: return
        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(chatId)
            .delete()
            .addOnSuccessListener {
                if (_currentChatId.value == chatId) {
                    _messages.value = emptyList()
                    val remaining = _chatSessions.value.filter { it.id != chatId }
                    if (remaining.isNotEmpty()) {
                        selectChatSession(remaining.first().id)
                    } else {
                        createNewChatSession("New Chat")
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        sessionsListener?.remove()
        messagesListener?.remove()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeContext() {
        _eventId.flatMapLatest { id ->
            if (id.isNullOrBlank()) flowOf("")
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
        val eventId = _eventId.value
        var chatId = _currentChatId.value

        if (eventId.isNullOrBlank()) {
            val systemWarning = AiMessage(
                id = UUID.randomUUID().toString(),
                text = "Please select an active event first so I can assist you with your planning details.",
                isUser = false,
                timestamp = System.currentTimeMillis()
            )
            _messages.update { current -> current + systemWarning }
            return
        }

        if (chatId == null) {
            chatId = UUID.randomUUID().toString()
            _currentChatId.value = chatId
            val autoTitle = if (userText.length > 25) userText.take(25) + "..." else userText
            createNewChatSession(autoTitle)
        } else {
            val currentSession = _chatSessions.value.find { it.id == chatId }
            if (currentSession != null && (currentSession.title == "New Chat" || currentSession.title == "Welcome Chat")) {
                val newTitle = if (userText.length > 25) userText.take(25) + "..." else userText
                firestore.collection("events")
                    .document(eventId)
                    .collection("aiChatHistory")
                    .document(chatId)
                    .update("title", newTitle)
            }
        }

        val userMessage = AiMessage(
            id = UUID.randomUUID().toString(),
            text = userText,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )

        _messages.update { current -> current + userMessage }
        saveMessageToFirestore(chatId, userMessage)

        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val fullPrompt = "CONTEXT:\n${_globalContext.value}\n\nUSER: $userText"
                val response = chat.sendMessage(fullPrompt)
                val responseText = response.text

                if (responseText.isNullOrBlank()) {
                    throw Exception("Empty response from AI engine")
                }

                val (cleanText, aiMsg) = parseAiResponse(responseText)
                val finalAiMsg = aiMsg.copy(
                    id = UUID.randomUUID().toString(),
                    text = cleanText,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )

                _messages.update { current -> current + finalAiMsg }
                saveMessageToFirestore(chatId, finalAiMsg)
            } catch (e: Exception) {
                android.util.Log.e("GenerativeViewModel", "Error sending message", e)
                val errorMessage = AiMessage(
                    id = UUID.randomUUID().toString(),
                    text = "Sorry, I ran into an error: ${e.localizedMessage ?: "Please try again."}",
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                _messages.update { current -> current + errorMessage }
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun updateMessageText(messageId: String, newText: String) {
        val eventId = _eventId.value ?: return
        val chatId = _currentChatId.value ?: return

        _messages.update { currentList ->
            currentList.map { msg ->
                if (msg.id == messageId) msg.copy(text = newText) else msg
            }
        }

        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .update("text", newText)
            .addOnFailureListener { e ->
                android.util.Log.e("GenerativeViewModel", "Failed to update interrupted message text", e)
            }
    }

    private fun saveMessageToFirestore(chatId: String, message: AiMessage) {
        val eventId = _eventId.value ?: return

        val messageMap = hashMapOf(
            "id" to message.id,
            "text" to message.text,
            "isUser" to message.isUser,
            "timestamp" to message.timestamp,
            "venueIds" to message.venueIds,
            "vendorIds" to message.vendorIds,
            "guestIds" to message.guestIds,
            "expenseIds" to message.expenseIds,
            "checklistIds" to message.checklistIds,
            "showBudgetSummary" to message.showBudgetSummary
        )

        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(messageMap)
    }

    private fun parseAiResponse(rawText: String): Pair<String, AiMessage> {
        val jsonMarker = "JSON_DATA:"
        val parts = rawText.split(jsonMarker)
        var cleanText = parts[0].trim()

        if (cleanText.isEmpty() && parts.size > 1) {
            cleanText = "Here is what I found for you:"
        }

        var aiMsg = AiMessage(
            id = UUID.randomUUID().toString(),
            text = cleanText,
            isUser = false,
            timestamp = System.currentTimeMillis()
        )

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
                    showBudgetSummary = false
                )
            } catch (e: Exception) {
                android.util.Log.e("GenerativeViewModel", "JSON Parsing failed", e)
            }
        }

        return cleanText to aiMsg
    }
}