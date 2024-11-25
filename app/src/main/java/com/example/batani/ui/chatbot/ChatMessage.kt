package com.example.batani.ui.chatbot

data class ChatMessage(
    val message: String,
    val isUser: Boolean // True jika user, False jika AI
)
