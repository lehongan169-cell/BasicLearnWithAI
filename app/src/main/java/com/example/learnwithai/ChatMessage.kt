package com.example.learnwithai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_history")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val isUser: Boolean, // true: Người dùng, false: AI
    val timestamp: Long = System.currentTimeMillis()
)

