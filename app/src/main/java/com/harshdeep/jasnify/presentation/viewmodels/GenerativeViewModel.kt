package com.harshdeep.jasnify.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.harshdeep.jasnify.presentation.screens.others.AiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerativeViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableStateFlow<List<AiMessage>>(emptyList())
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private var globalContext: String? = null

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-3.1-flash-lite",
            systemInstruction = content { 
                text("""
                    You are an expert wedding and event planner assistant for the app Jasnify.
                    When providing information about venues or vendors, you MUST use the following JSON format at the end of your response if you want to display cards:
                    JSON_DATA: {"venues": ["id1", "id2"], "vendors": ["id3", "id4"]}
                    Keep your text responses concise and helpful. Use images and cards instead of long text explanations where possible.
                """.trimIndent()) 
            }
        )

    private var chat = model.startChat()

    fun sendMessage(text: String) {
        val userMessage = AiMessage(
            id = System.currentTimeMillis().toString(),
            text = text,
            isUser = true
        )
        _messages.value = _messages.value + userMessage
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                val prompt = if (globalContext != null && _messages.value.size <= 2) {
                    "Current Event Context: $globalContext\n\nUser Question: $text"
                } else {
                    text
                }

                val response = chat.sendMessage(prompt)
                val fullText = response.text ?: "Sorry, I couldn't generate a response."
                
                // Parse potential JSON data for cards (case-insensitive and multiline support)
                val jsonMatch = Regex("JSON_DATA: (\\{.*\\})", RegexOption.DOT_MATCHES_ALL).find(fullText)
                val cleanText = fullText.replace(Regex("JSON_DATA: \\{.*\\}", RegexOption.DOT_MATCHES_ALL), "").trim()
                
                var venueIds = emptyList<String>()
                var vendorIds = emptyList<String>()
                
                jsonMatch?.let {
                    try {
                        val jsonStr = it.groupValues[1]
                        val jsonObject = com.google.gson.JsonParser.parseString(jsonStr).asJsonObject
                        venueIds = jsonObject.getAsJsonArray("venues")?.map { el -> el.asString } ?: emptyList()
                        vendorIds = jsonObject.getAsJsonArray("vendors")?.map { el -> el.asString } ?: emptyList()
                    } catch (e: Exception) {
                        // Ignore parsing errors
                    }
                }

                val aiResponse = AiMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    text = cleanText,
                    isUser = false,
                    venueIds = venueIds,
                    vendorIds = vendorIds
                )
                _messages.value = _messages.value + aiResponse
            } catch (e: Exception) {
                val errorMessage = AiMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    text = "Error: ${e.message}",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun setGlobalContext(context: String) {
        this.globalContext = context
        if (_messages.value.isEmpty()) {
            _messages.value = listOf(
                AiMessage(
                    id = System.currentTimeMillis().toString(),
                    text = "How can I help you today?",
                    isUser = false
                )
            )
        }
    }
}
