package com.example.batani.ui.chatbot

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batani.databinding.ActivityChatBotBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatBotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBotBinding
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatAdapter = ChatAdapter(messages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = chatAdapter

        binding.sendButton.setOnClickListener {
            val userMessage = binding.inputMessage.text.toString()
            if (userMessage.isNotBlank()) {
                addMessage(userMessage, true)
                modelCall(userMessage)
                binding.inputMessage.text.clear()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMessage(message: String, isUser: Boolean) {
        messages.add(ChatMessage(message, isUser))
        chatAdapter.notifyDataSetChanged()
        binding.chatRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun modelCall(prompt: String) {

        binding.loadingProgressBar.visibility = View.VISIBLE

        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
//            apiKey = "yout_key"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = generativeModel.generateContent(prompt)

                withContext(Dispatchers.Main) {

                    binding.loadingProgressBar.visibility = View.GONE
                    addMessage(response.text.toString(), false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {

                    binding.loadingProgressBar.visibility = View.GONE
                    addMessage("Error: ${e.message}", false)
                }
            }
        }
    }
}
