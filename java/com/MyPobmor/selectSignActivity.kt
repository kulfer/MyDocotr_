package com.MyPobmor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class selectSignActivity : AppCompatActivity() {

    private var speciality: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var birthDate: String? = null
    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_sign)

        // รับค่าที่ส่งมาจาก datauseActivity
        speciality = intent.getStringExtra("SPECIALITY")
        firstName = intent.getStringExtra("FIRST_NAME")
        lastName = intent.getStringExtra("LAST_NAME")
        birthDate = intent.getStringExtra("BIRTH_DATE")
        gender = intent.getStringExtra("GENDER")

        // กลับหน้าที่แล้ว
        val backImg = findViewById<ImageView>(R.id.backimg)
        backImg.setOnClickListener {
            finish()
        }

        // เรียกใช้งานฟังก์ชันเปิดหน้าถัดไป
        openSign()
    }

    // ฟังก์ชันเปิดหน้าถัดไป
    private fun openSign() {
        // ตัวแปรสำหรับ LinearLayout
        val selectEmail = findViewById<LinearLayout>(R.id.lnSignEmail)
        val selectTell = findViewById<LinearLayout>(R.id.lnSignTell)

        selectEmail.setOnClickListener {
            val intent = Intent(this, signupActivity::class.java)
            // ส่งข้อมูลที่ได้รับไปยัง signupActivity
            intent.putExtra("SPECIALITY", speciality)
            intent.putExtra("FIRST_NAME", firstName)
            intent.putExtra("LAST_NAME", lastName)
            intent.putExtra("BIRTH_DATE", birthDate)
            intent.putExtra("GENDER", gender)
            startActivity(intent)
        }

        selectTell.setOnClickListener {
            val intent = Intent(this, TellsignupActivity::class.java)
            // ส่งข้อมูลที่ได้รับไปยัง TellsignupActivity
            intent.putExtra("SPECIALITY", speciality)
            intent.putExtra("FIRST_NAME", firstName)
            intent.putExtra("LAST_NAME", lastName)
            intent.putExtra("BIRTH_DATE", birthDate)
            intent.putExtra("GENDER", gender)
            startActivity(intent)
        }
    }
}