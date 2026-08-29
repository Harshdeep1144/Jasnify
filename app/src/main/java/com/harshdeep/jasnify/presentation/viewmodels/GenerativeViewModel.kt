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
import com.google.firebase.firestore.SetOptions
import com.harshdeep.jasnify.data.local.ExpenseEntity
import com.harshdeep.jasnify.domain.model.Checklist
import com.harshdeep.jasnify.domain.model.Event
import com.harshdeep.jasnify.domain.model.Guest
import com.harshdeep.jasnify.domain.model.Vendor
import com.harshdeep.jasnify.domain.model.Venue
import com.harshdeep.jasnify.domain.repository.BudgetRepository
import com.harshdeep.jasnify.domain.repository.ChecklistRepository
import com.harshdeep.jasnify.domain.repository.GuestRepository
import com.harshdeep.jasnify.domain.repository.VendorRepository
import com.harshdeep.jasnify.domain.repository.VenueRepository
import com.harshdeep.jasnify.presentation.screens.chats.AiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
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

    private var activeStreamingMessageId: String? = null
    private var activeStreamingMessage: AiMessage? = null

    private var sessionsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

    private val systemInstructionsText = """
        You are Jasnify AI, a friendly, smart wedding & event planning assistant.
        
        CRITICAL BEHAVIOR RULES:
        1. CONCISE & USER FRIENDLY: Keep spoken and text responses short, friendly, and natural (1 to 3 sentences max). Never write long paragraphs or unsolicited essays.
        2. DYNAMIC LANGUAGE MATCHING: Strictly match the user's input language and tone:
           - If Hinglish (e.g. "Kitna budget bacha hai?"), reply in natural Hinglish.
           - If Hindi (Devanagari), reply in clear Hindi.
           - If English, reply in crisp English.
        3. STRICT CONTEXT ACCURACY: Rely only on the CURRENT EVENT CONTEXT provided. Do not hallucinate numbers, names, or items.
        4. SELECTIVE COMPONENT USAGE:
           - DO NOT attach cards for general conversation, chit-chat, greetings, or advice.
           - Attach UI cards ONLY when the user explicitly asks to view, compare, or modify venues, vendors, guests, expenses, or checklists.
           - NEVER mention raw database IDs in your readable text.
        5. GUEST CARD LIMIT:
           - In 'guestIds', attach a MAXIMUM of 5 guest IDs.
           - Mention remaining pending/invited count in text if more exist.
        
        STRUCTURED UI ATTACHMENT FORMAT:
        Only when relevant cards need to be displayed, add this exact JSON block at the very end of your response:
        JSON_DATA: {"venueIds": [], "vendorIds": [], "guestIds": [], "expenseIds": [], "checklistIds": []}
    """.trimIndent()

    private val candidateModels = listOf(
        "gemini-3.1-flash-lite",
        "gemini-2.0-flash",    // Modern, ultra-fast
        "gemini-1.5-flash",    // Reliable standard
        "gemini-1.5-flash-002",
        "gemini-1.5-pro",      // Powerful backup
        "gemini-1.5-pro-002"
    )

    private fun getModel(name: String) = FirebaseAI.getInstance(FirebaseApp.getInstance()).generativeModel(
        modelName = name,
        generationConfig = GenerationConfig.builder()
            .setTemperature(0.6f)
            .setTopK(40)
            .setTopP(0.95f)
            .build(),
        systemInstruction = content { text(systemInstructionsText) }
    )

    private var chat = getModel(candidateModels.first()).startChat()

    init {
        observeContext()
    }

    fun setEventId(id: String) {
        if (id.isBlank() || _eventId.value == id) return
        _eventId.value = id
        loadChatSessions(id)
    }

    private fun loadChatSessions(eventId: String) {
        sessionsListener?.remove()
        sessionsListener = firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val sessions = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ChatSession::class.java)?.copy(id = doc.id)
                }
                _chatSessions.value = sessions
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
                if (e != null || snapshot == null) return@addSnapshotListener

                val remoteMsgs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AiMessage::class.java)?.apply {
                        if (id.isBlank()) id = doc.id
                    }
                }

                val currentStreaming = activeStreamingMessage
                val currentStreamingId = activeStreamingMessageId
                val combinedMsgs = if (currentStreamingId != null && currentStreaming != null) {
                    val listWithoutStream = remoteMsgs.filter { it.id != currentStreamingId }
                    listWithoutStream + currentStreaming
                } else {
                    remoteMsgs
                }

                _messages.value = combinedMsgs

                val history = remoteMsgs.filter { it.text.isNotBlank() }.map { msg ->
                    content(role = if (msg.isUser) "user" else "model") { text(msg.text) }
                }
                chat = getModel(candidateModels.first()).startChat(history)
            }
    }

    fun createNewChatSession(initialTitle: String = "New Chat", customChatId: String? = null) {
        val eventId = _eventId.value ?: return
        val newChatId = customChatId ?: UUID.randomUUID().toString()
        val sessionMap = hashMapOf(
            "title" to initialTitle,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(newChatId)
            .set(sessionMap)
            .addOnSuccessListener {
                selectChatSession(newChatId)
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

    fun restoreChatSession(session: ChatSession) {
        val eventId = _eventId.value ?: return
        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(session.id)
            .set(session)
            .addOnSuccessListener {
                selectChatSession(session.id)
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
        sb.append("CURRENT EVENT CONTEXT:\n")
        sb.append("NAME: ${event.name} | OWNER: ${event.ownerName}\n")

        val totalBudget = budget?.totalBudget ?: event.budget ?: 0.0
        val totalSpent = expenses.sumOf { it.amount }
        sb.append("BUDGET: Total ₹$totalBudget, Spent ₹$totalSpent, Remaining ₹${totalBudget - totalSpent}\n")

        val invited = guests.count { it.invited }
        sb.append("GUESTS: Total ${guests.size}, Invited $invited, Pending ${guests.size - invited}\n")

        sb.append("GUEST LIST (Max 5 for JSON_DATA):\n")
        guests.forEach { sb.append("- ID: ${it.id} | NAME: ${it.name} | STATUS: ${if (it.invited) "INVITED" else "PENDING"}\n") }

        sb.append("VENUES:\n")
        allVenues.forEach { sb.append("- ID: ${it.id} | NAME: ${it.name}\n") }

        sb.append("VENDORS:\n")
        allVendors.forEach { sb.append("- ID: ${it.id} | NAME: ${it.name} | CAT: ${it.category}\n") }

        sb.append("EXPENSES:\n")
        expenses.forEach { sb.append("- ID: ${it.id} | TITLE: ${it.title} | AMT: ₹${it.amount}\n") }

        sb.append("CHECKLIST:\n")
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
                text = "Please select an active event first so I can assist you.",
                isUser = false,
                timestamp = System.currentTimeMillis()
            )
            _messages.update { current -> current + systemWarning }
            return
        }

        if (chatId == null) {
            val newChatId = UUID.randomUUID().toString()
            _currentChatId.value = newChatId
            chatId = newChatId
            val autoTitle = if (userText.length > 25) userText.take(25) + "..." else userText
            createNewChatSession(autoTitle, customChatId = newChatId)
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

        val aiMessageId = UUID.randomUUID().toString()
        val streamingAiMsg = AiMessage(
            id = aiMessageId,
            text = "",
            isUser = false,
            timestamp = System.currentTimeMillis()
        )

        activeStreamingMessageId = aiMessageId
        activeStreamingMessage = streamingAiMsg

        _messages.update { current -> current + streamingAiMsg }

        viewModelScope.launch {
            _isGenerating.value = true
            val fullRawAccumulator = StringBuilder()
            var success = false

            val fullPrompt = "CONTEXT:\n${_globalContext.value}\n\nUSER QUERY: $userText"
            val history = _messages.value.filter { it.text.isNotBlank() && it.id != aiMessageId }.map { msg ->
                content(role = if (msg.isUser) "user" else "model") { text(msg.text) }
            }

            for (modelName in candidateModels) {
                if (success) break
                try {
                    android.util.Log.d("GenerativeViewModel", "Trying model: $modelName")
                    val currentModel = getModel(modelName)
                    val currentChat = currentModel.startChat(history)

                    currentChat.sendMessageStream(fullPrompt).collect { chunk ->
                        processStreamChunk(chunk.text ?: "", fullRawAccumulator, streamingAiMsg, aiMessageId)
                    }

                    val (cleanText, finalParsedMsg) = parseAiResponse(fullRawAccumulator.toString())
                    val completedMsg = finalParsedMsg.copy(
                        id = aiMessageId,
                        text = cleanText,
                        isUser = false,
                        timestamp = System.currentTimeMillis()
                    )

                    activeStreamingMessageId = null
                    activeStreamingMessage = null

                    _messages.update { list ->
                        list.map { msg -> if (msg.id == aiMessageId) completedMsg else msg }
                    }

                    saveMessageToFirestore(chatId, completedMsg)
                    success = true

                } catch (e: Exception) {
                    android.util.Log.e("GenerativeViewModel", "Model $modelName failed: ${e.message}", e)
                    fullRawAccumulator.clear()
                    
                    if (e.message?.contains("App Check", ignoreCase = true) == true) {
                        android.util.Log.e("GenerativeViewModel", "CRITICAL: App Check verification failed. This usually means the device/token is not registered in Firebase Console.")
                    }
                    continue
                }
            }

            if (!success) {
                activeStreamingMessageId = null
                activeStreamingMessage = null
                
                val errorMsg = "Sorry, I'm having trouble connecting to my brain right now. Please try again in a moment."
                _messages.update { list ->
                    list.map { msg -> if (msg.id == aiMessageId) msg.copy(text = errorMsg) else msg }
                }
            }
            
            _isGenerating.value = false
        }
    }

    private fun processStreamChunk(
        chunkText: String,
        accumulator: StringBuilder,
        streamingMsg: AiMessage,
        messageId: String
    ) {
        accumulator.append(chunkText)
        val visibleText = accumulator.split("JSON_DATA:")[0].trimStart()
        val updated = streamingMsg.copy(text = visibleText)
        activeStreamingMessage = updated

        _messages.update { list ->
            list.map { msg -> if (msg.id == messageId) updated else msg }
        }
    }

    fun toggleMessageFeedback(messageId: String, newFeedback: Int) {
        if (messageId.isBlank()) return
        val eventId = _eventId.value ?: return
        val chatId = _currentChatId.value ?: return

        val currentMsg = _messages.value.find { it.id == messageId }
        val targetFeedback = if (currentMsg?.feedback == newFeedback) 0 else newFeedback

        _messages.update { list ->
            list.map { msg -> if (msg.id == messageId) msg.copy(feedback = targetFeedback) else msg }
        }

        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .set(mapOf("feedback" to targetFeedback), SetOptions.merge())
    }

    fun updateMessageText(messageId: String, newText: String) {
        val eventId = _eventId.value ?: return
        val chatId = _currentChatId.value ?: return

        _messages.update { currentList ->
            currentList.map { msg -> if (msg.id == messageId) msg.copy(text = newText) else msg }
        }

        firestore.collection("events")
            .document(eventId)
            .collection("aiChatHistory")
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .update("text", newText)
    }

    private fun saveMessageToFirestore(chatId: String, message: AiMessage) {
        val eventId = _eventId.value ?: return

        val messageMap = hashMapOf(
            "text" to message.text,
            "isUser" to message.isUser,
            "timestamp" to message.timestamp,
            "venueIds" to message.venueIds,
            "vendorIds" to message.vendorIds,
            "guestIds" to message.guestIds,
            "expenseIds" to message.expenseIds,
            "checklistIds" to message.checklistIds,
            "showBudgetSummary" to message.showBudgetSummary,
            "feedback" to message.feedback
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
            cleanText = "Here are the details:"
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

                val parsedGuestIds = json.optJSONArray("guestIds")?.let { arr ->
                    List(arr.length()) { arr.getString(it) }.take(5)
                } ?: emptyList()

                aiMsg = aiMsg.copy(
                    venueIds = json.optJSONArray("venueIds")?.let { arr -> List(arr.length()) { arr.getString(it) } } ?: emptyList(),
                    vendorIds = json.optJSONArray("vendorIds")?.let { arr -> List(arr.length()) { arr.getString(it) } } ?: emptyList(),
                    guestIds = parsedGuestIds,
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