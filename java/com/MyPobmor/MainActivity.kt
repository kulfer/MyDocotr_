package com.MyPobmor

import android.content.Context
import android.content.Intent
import android.media.session.MediaSession.Token
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val sharePref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharePref.getBoolean("isLoggedIn",false)


        // ตรวจสอบสถานะการเข้าสู่ระบบ
        if (mAuth.currentUser != null) {
            // ถ้ามีผู้ใช้เข้าสู่ระบบแล้ว, ไปที่ HomeActivity โดยตรง
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else if (isLoggedIn) {
            // ถ้าไม่มีผู้ใช้เข้าสู่ระบบ, ไปที่หน้า LoginActivity
            startActivity(Intent(this, loginActivity::class.java))
            finish()
        } else {
            // สำหรับผู้ใช้ใหม่ที่ยังไม่ได้ลงทะเบียนหรือเข้าสู่ระบบ
            // ไปที่ MainActivity แสดงให้ผู้ใช้เลือกลงทะเบียนหรือเข้าสู่ระบบ
        }

        // เชื่อมโยงปุ่มและ TextView
        val button = findViewById<Button>(R.id.btn_sign_up)
        val textLog = findViewById<TextView>(R.id.login_link)

        button.setOnClickListener {
            val intent = Intent(this,docorpatientActivity::class.java)
            startActivity(intent)
        }

        textLog.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }
    }
}