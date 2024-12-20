package com.MyPobmor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge

class mgbainadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mgbainad)

        var mgnad = findViewById<LinearLayout>(R.id.newbaimad)
        var allnad = findViewById<LinearLayout>(R.id.Lnallbainad)
        val backImg = findViewById<ImageView>(R.id.backimg)

        // คลิกปุ่มย้อนกลับ
        backImg.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }

        mgnad.setOnClickListener {
            val intent = Intent(this, createbainadActivity::class.java)
            startActivity(intent)
        }

        allnad.setOnClickListener {
            val intent = Intent(this,allbainadActivity::class.java)
            startActivity(intent)
        }
    }
}