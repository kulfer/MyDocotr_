package com.MyPobmor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class forgot_passwordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mAuth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etemail)
        val btnSend = findViewById<Button>(R.id.btnsend)
        val imgBack = findViewById<ImageView>(R.id.backimg)

        // คลิกปุ่มย้อนกลับ
        imgBack.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }

        //checkว่ากรอกอีเมลรึยัง
        btnSend.setOnClickListener {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            val EtEmail = etEmail.text.toString()
            val emailText = etEmail.text.toString()

            if (EtEmail.isEmpty()){
                Toast.makeText(this,"กรุณากรอกอีเมล",Toast.LENGTH_SHORT).show()
            }else if (!emailText.matches(emailPattern.toRegex())) {
                Toast.makeText(this, "รูปแบบอีเมลไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordResetEmail(emailText)
                etEmail.text.clear()
            }
        }

    }

    private fun sendPasswordResetEmail(email: String) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this,"ส่งลิงก์รีเซ็ตรหัสผ่านไปยังอีเมลของคุณแล้ว",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"เกิดข้อผิดพลาด: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}