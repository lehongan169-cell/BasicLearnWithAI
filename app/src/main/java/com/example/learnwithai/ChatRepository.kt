package com.example.learnwithai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository(private val chatDao: ChatDao) {
    // Khởi tạo Gemini Model
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = "Hãy điền API key của bạn, hãy tạo dự án mới trên Google AI Studio",
        systemInstruction = Content(
            parts = listOf(
                TextPart("Bạn là một giáo viên dạy tiếng Anh cho học sinh trung học phổ thông và sinh viên đại học ở Việt Nam."),
                TextPart("Hãy giao tiếp và giảng dạy bằng tiếng Việt.")
            )
        ),
        generationConfig = generationConfig {
            temperature = 0.8f
            maxOutputTokens = 1024
        }
    )
    private var chatSession = generativeModel.startChat()

    // Lấy dữ liệu từ DB (Observable)
    val allMessages = chatDao.getAllMessages()

    suspend fun sendMessage(userText: String) {
        // 1. Lưu tin nhắn User vào DB
        val userMessage = ChatMessage(text = userText, isUser = true)
        chatDao.insertMessage(userMessage)

        try {
            // 2. Gửi đến Gemini
            val response = withContext(Dispatchers.IO) {
                chatSession.sendMessage(userText)
            }

            // 3. Lấy phản hồi và lưu vào DB
            val aiText = response.text ?: "Xin lỗi! Tôi không hiểu câu hỏi của bạn."
            val aiMessage = ChatMessage(text = aiText, isUser = false)
            chatDao.insertMessage(aiMessage)

        } catch (e: Exception) {
            // Xử lý lỗi (ví dụ: mất mạng)
            chatDao.insertMessage(ChatMessage(text = "Lỗi: ${e.message}", isUser = false))
        }
    }

    suspend fun clearHistory() {
        chatDao.clearHistory()
        chatSession = generativeModel.startChat()
    }
}