package com.MyPobmor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class allroctActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_allroct)

        //open general diseat
        val genenalroct = findViewById<CardView>(R.id.generalItem)
        genenalroct.setOnClickListener {
            val intent = Intent(this, diseaseActivity::class.java)
            startActivity(intent)
        }

        //open general diseat
        val hertroct = findViewById<CardView>(R.id.heartItem)
        hertroct.setOnClickListener {
            val intent = Intent(this, heartroctActivity::class.java)
            startActivity(intent)
        }

        //open general diseat
        val gramroct = findViewById<CardView>(R.id.gramItem)
        gramroct.setOnClickListener {
            val intent = Intent(this, GramActivity::class.java)
            startActivity(intent)
        }

        //open general diseat
        val brainroct = findViewById<CardView>(R.id.brainItem)
        brainroct.setOnClickListener {
            val intent = Intent(this, BrainroctActivity::class.java)
            startActivity(intent)
        }

        val backImg = findViewById<ImageView>(R.id.backimg)
        // คลิกปุ่มย้อนกลับ
        backImg.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }


    }
}