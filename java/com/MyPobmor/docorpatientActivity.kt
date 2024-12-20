package com.MyPobmor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import androidx.lifecycle.ViewModel


class docorpatientActivity : AppCompatActivity() {

    private lateinit var SelectDoc: EditText
    private var selectedSpeciality: String? = null // เก็บค่าประเภทที่เลือก

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_docorpatient)

        // กลับหน้าที่แล้ว
        val backImg = findViewById<ImageView>(R.id.backimg)
        backImg.setOnClickListener {
            finish()
        }

        // เชื่อมต่อ EditText กับ View จาก layout
        SelectDoc = findViewById(R.id.selectdoc)
        val btNext = findViewById<Button>(R.id.btnext)

        // เลือกประเภทผู้ใช้งาน
        SelectDoc.setOnClickListener {
            val selectOption = arrayOf("ผู้ใช้ทั่วไป", "แพทย์")
            AlertDialog.Builder(this)
                .setTitle("กรุณาเลือกประเภท")
                .setItems(selectOption) { _, which ->
                    // เก็บค่าประเภทที่เลือก
                    selectedSpeciality = selectOption[which]
                    SelectDoc.setText(selectedSpeciality)

                    Toast.makeText(
                        this,
                        "ประเภทที่เลือก: $selectedSpeciality",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .show()
        }

        // ตั้งค่า OnClickListener สำหรับปุ่ม btNext
        btNext.setOnClickListener {
            if (selectedSpeciality.isNullOrEmpty()) {
                // หากยังไม่ได้เลือกประเภท
                Toast.makeText(this, "กรุณาเลือกประเภทก่อนดำเนินการต่อ", Toast.LENGTH_SHORT).show()
            } else {
                // ตรวจสอบว่าเลือก "แพทย์" หรือไม่
                if (selectedSpeciality == "แพทย์") {
                    // ไปยังหน้า Doctor_signActivity
                    val intent = Intent(this, Doctor_useActivity::class.java)
                    intent.putExtra("SPECIALITY", selectedSpeciality) // ส่งข้อมูลประเภทที่เลือก
                    startActivity(intent)
                } else {
                    // ไปยังหน้า datauseActivity
                    val intent = Intent(this, datauseActivity::class.java)
                    intent.putExtra("SPECIALITY", selectedSpeciality) // ส่งข้อมูลประเภทที่เลือก
                    startActivity(intent)
                }
            }
        }
    }
}
