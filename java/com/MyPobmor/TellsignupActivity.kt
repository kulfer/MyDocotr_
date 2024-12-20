package com.MyPobmor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TellsignupActivity : AppCompatActivity() {

    //var general text input
    private lateinit var fullName: EditText
    private lateinit var etTell2: EditText
    private lateinit var etPassword2: EditText
    private lateinit var btnTell: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tellsignup)

        //var general text input
        fullName = findViewById(R.id.etFullName)
        etTell2 = findViewById(R.id.etTell)
        etPassword2 = findViewById(R.id.etPassword)
        btnTell = findViewById(R.id.btnTellSignUp)

        //ตัวแปรกลับหน้า ล็อกอิน ย้อนหน้าที่แล้ว
        val backlpg = findViewById<TextView>(R.id.tvLogin)
        val backImg = findViewById<ImageView>(R.id.backimg)

        // คลิกปุ่มย้อนกลับ
        backImg.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }
        // กลับัไปหน้า login
        backlpg.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }

    }
}