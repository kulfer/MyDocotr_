package com.MyPobmor

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.lifecycle.ViewModel


class datauseActivity : AppCompatActivity() {

    private lateinit var dateEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var fullLastNameEditText: EditText
    private var speciality: String? = null // รับค่าประเภทจาก Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datause)

        // รับค่าประเภทที่ส่งมาจาก docorpatientActivity
        speciality = intent.getStringExtra("SPECIALITY")

        // Initialize views
        dateEditText = findViewById(R.id.datetext)
        genderEditText = findViewById(R.id.genderText)
        fullNameEditText = findViewById(R.id.fullname)
        fullLastNameEditText = findViewById(R.id.fullLastname)
        val btnSave = findViewById<Button>(R.id.buttonnext)

        // Setup DatePicker for date of birth
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Setup gender selection
        genderEditText.setOnClickListener {
            val genderOptions = arrayOf("ชาย", "หญิง")
            AlertDialog.Builder(this)
                .setTitle("กรุณาเลือกเพศ")
                .setItems(genderOptions) { _, which ->
                    genderEditText.setText(genderOptions[which])
                }
                .show()
        }

        // เมื่อกดปุ่ม Save ให้ตรวจสอบข้อมูลและส่งไปหน้าถัดไป
        btnSave.setOnClickListener {
            val firstName = fullNameEditText.text.toString().trim()
            val lastName = fullLastNameEditText.text.toString().trim()
            val birthDate = dateEditText.text.toString().trim()
            val gender = genderEditText.text.toString().trim()

            // ตรวจสอบว่าข้อมูลครบถ้วนหรือไม่
            if (firstName.isEmpty() || lastName.isEmpty() || gender.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ส่งข้อมูลไปยังหน้าถัดไป (selectSignActivity)
            val intent = Intent(this, selectSignActivity::class.java)
            intent.putExtra("SPECIALITY", speciality) // ส่งประเภท
            intent.putExtra("FIRST_NAME", firstName) // ส่งชื่อ
            intent.putExtra("LAST_NAME", lastName) // ส่งนามสกุล
            intent.putExtra("BIRTH_DATE", birthDate) // ส่งวันเกิด
            intent.putExtra("GENDER", gender) // ส่งเพศ
            startActivity(intent)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // กำหนดวันที่ที่เลือกให้กับ EditText
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateEditText.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}