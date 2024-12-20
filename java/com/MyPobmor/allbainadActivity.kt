package com.MyPobmor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge

class allbainadActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_allbainad)

        // กลับหน้าที่แล้ว
        val backImg = findViewById<ImageView>(R.id.backimg)
        backImg.setOnClickListener {
            finish()
        }

    }
}