package com.example.learnwithai

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Views
        recyclerView = findViewById(R.id.recyclerView)
        etMessage = findViewById(R.id.etMessage)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        val btnCreateTest = findViewById<ImageButton>(R.id.btnCreateTest)

        //RecyclerView
        adapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter

        // Quan sát dữ liệu từ ViewModel (LiveData)
        viewModel.allMessages.observe(this) { messages ->
            if (messages.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            adapter.submitList(messages) {
                // Tự động cuộn xuống cuối khi có tin nhắn mới
                if (messages.isNotEmpty()) {
                    recyclerView.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        btnCreateTest.setOnClickListener {
            val topic = etMessage.text.toString().trim()
            if (topic.isNotEmpty()) {
                viewModel.requestVocabularyTest(topic)
                etMessage.text.clear()
                // Thông báo
                val message = "Đang tạo bài kiểm tra với chủ đề '$topic'..."
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        // Xử lý sự kiện gửi tin
        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.sendMessage(text)
                etMessage.text.clear()
            }
        }
    }

    // Xử lý Menu (Nút Clear)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                showClearHistoryConfirm()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showClearHistoryConfirm() {
        AlertDialog.Builder(this)
            .setTitle("Xoá lịch sử trò chuyện")
            .setMessage("Bạn có chắc chắn muốn xoá cuộc trò chuyện hiện tại và bắt đầu cuộc hội thoại mới?")
            .setPositiveButton("Có") { dialog, _ ->
                viewModel.clearHistory()
                dialog.dismiss()
                Toast.makeText(this, "Đã xoá!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Không") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}