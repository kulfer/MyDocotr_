package com.MyPobmor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class verify_emailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        val backImg = findViewById<ImageView>(R.id.backimg)
        val otpInput1 = findViewById<EditText>(R.id.otpinput1)
        val otpInput2 = findViewById<EditText>(R.id.otpinput2)
        val otpInput3 = findViewById<EditText>(R.id.otpinput3)
        val otpInput4 = findViewById<EditText>(R.id.otpinput4)
        val btnVerify = findViewById<Button>(R.id.btnVerify)
        val txMailUser = findViewById<TextView>(R.id.txmailuser)

        // ดึงอีเมลที่ส่งมาจากหน้า Forgot Password
        val email = intent.getStringExtra("email")
        txMailUser.text = email

        // คลิกปุ่มย้อนกลับ
        backImg.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }

        // คลิกปุ่มยืนยัน OTP
        btnVerify.setOnClickListener {
            val otp = otpInput1.text.toString() + otpInput2.text.toString() + otpInput3.text.toString() + otpInput4.text.toString()

            if (otp.length == 4) {
                // ตรวจสอบรหัส OTP ที่ผู้ใช้กรอก (ในที่นี้ คุณสามารถเพิ่มการตรวจสอบ OTP ได้เอง)
                if (otp == "1234") {  // สมมติว่า OTP ถูกต้องคือ 1234
                    Toast.makeText(this, "ยืนยัน OTP สำเร็จ", Toast.LENGTH_SHORT).show()

                    // ส่งไปที่หน้า Reset Password หรือหน้าอื่น ๆ
                    val intent = Intent(this, create_new_passwordActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "OTP ไม่ถูกต้อง!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "กรอก OTP ให้ครบทั้ง 4 ช่อง", Toast.LENGTH_SHORT).show()
            }
        }
        // ตั้งค่า TextWatcher สำหรับแต่ละ EditText
        setupOtpEditTexts(otpInput1, otpInput2, otpInput3, otpInput4)
    }

    private fun setupOtpEditTexts(vararg editTexts: EditText) {
        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        val nextIndex = index + 1
                        if (nextIndex < editTexts.size) {
                            editTexts[nextIndex].requestFocus()
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}
