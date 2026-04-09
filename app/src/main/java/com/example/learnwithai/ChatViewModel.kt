package com.example.learnwithai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChatRepository
    val allMessages: LiveData<List<ChatMessage>>

    init {
        val chatDao = AppDatabase.getDatabase(application).chatDao()
        repository = ChatRepository(chatDao)
        allMessages = repository.allMessages.asLiveData()
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(text)
        }
    }

    fun requestVocabularyTest(topic: String) {
        viewModelScope.launch {
            // Tạo prompt dựa trên input
            val prompt = "Tạo bài kiểm tra từ vựng tiếng Anh với mức độ khó, gồm 5 câu với chủ đề '$topic', sau đó hãy cho biết đáp án và giải thích."
            repository.sendMessage(prompt)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
