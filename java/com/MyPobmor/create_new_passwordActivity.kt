package com.MyPobmor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class create_new_passwordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_password)

        val backImg = findViewById<ImageView>(R.id.backimg)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // คลิกปุ่มย้อนกลับ
        backImg.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }

        // คลิกปุ่ม Save เพื่อบันทึกรหัสผ่านใหม่
        btnSave.setOnClickListener {
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            // ตรวจสอบว่า รหัสผ่านกับการยืนยันตรงกัน
            if (newPassword == confirmPassword) {
                if (newPassword.length >= 6) {
                    // คุณสามารถเพิ่มโค้ดสำหรับการบันทึกรหัสผ่านใหม่ในฐานข้อมูลที่นี่
                    Toast.makeText(this, "เปลี่ยนรหัสผ่านสำเร็จ!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, loginActivity::class.java)
                    startActivity(intent)

                    // เมื่อบันทึกเรียบร้อยแล้ว คุณสามารถกลับไปหน้าหลัก หรือหน้าล็อกอิน
                    finish() // หรือคุณอาจเปลี่ยนไปหน้าอื่นโดยใช้ Intent
                } else {
                    Toast.makeText(this, "รหัสผ่านควรยาวกว่า 6 ตัวอักษร", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show()
            }
        }
    }
}