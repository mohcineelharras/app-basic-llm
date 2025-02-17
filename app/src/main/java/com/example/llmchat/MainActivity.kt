package com.example.llmchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(val prompt: String)
data class ChatResponse(val response: String)

interface LLMApi {
    @POST("chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}

class MainActivity : AppCompatActivity() {
    private lateinit var api: LLMApi
    private lateinit var inputText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputText = findViewById(R.id.inputText)
        sendButton = findViewById(R.id.sendButton)
        chatDisplay = findViewById(R.id.chatDisplay)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-llm-api-endpoint.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(LLMApi::class.java)

        sendButton.setOnClickListener {
            val prompt = inputText.text.toString()
            if (prompt.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = api.chat(ChatRequest(prompt))
                        chatDisplay.append("\nYou: $prompt")
                        chatDisplay.append("\nAI: ${response.response}\n")
                        inputText.text.clear()
                    } catch (e: Exception) {
                        chatDisplay.append("\nError: ${e.message}\n")
                    }
                }
            }
        }
    }
}
