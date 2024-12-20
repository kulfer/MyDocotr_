package com.MyPobmor

// HistoryActivity.kt
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ListView
import com.MyPobmor.HistoryAdapter.HistoryItem


class tuatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tuat)

        val listView: ListView = findViewById(R.id.historyListView)
        val backImg = findViewById<ImageView>(R.id.backimg)

        // คลิกปุ่มย้อนกลับ
        backImg.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }

        // Create dummy data
        val historyItems = listOf(
            HistoryItem("2024-08-01", "ตรวจเลือด", "ผ่าน"),
            HistoryItem("2024-08-15", "ตรวจร่างกาย", "รอผล")
        )

        // Set up the adapter
        val adapter = HistoryAdapter(this, historyItems)
        listView.adapter = adapter
    }
}