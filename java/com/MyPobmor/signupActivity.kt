package com.MyPobmor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class signupActivity : AppCompatActivity() {

    private lateinit var edtFullName: EditText
    private lateinit var edtFullMail: EditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private var speciality: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var birthDate: String? = null
    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        // เชื่อมต่อกับ FirebaseAuth และ FirebaseDatabase
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users")

        edtFullName = findViewById(R.id.etFullName)
        edtFullMail = findViewById(R.id.etEmailAddress)
        edtPassword = findViewById(R.id.etPassword)
        btnSignUp = findViewById(R.id.btnSignUp)

        // รับค่าจาก Intent ที่ส่งมาจาก selectSignActivity
        speciality = intent.getStringExtra("SPECIALITY")
        firstName = intent.getStringExtra("FIRST_NAME")
        lastName = intent.getStringExtra("LAST_NAME")
        birthDate = intent.getStringExtra("BIRTH_DATE")
        gender = intent.getStringExtra("GENDER")

        // ตั้งค่า edtFullName ให้แสดง firstName
        edtFullName.setText(firstName)

        // ตั้งค่า OnClickListener สำหรับปุ่ม SignUp
        btnSignUp.setOnClickListener {
            val email = edtFullMail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            } else {
                signUpAndSaveData(email, password)
            }
        }
    }

    private fun signUpAndSaveData(email: String, password: String) {
        // สร้างบัญชีใน Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    if (userId != null) {
                        // บันทึกข้อมูลลงใน Realtime Database
                        saveUserDataToRealtimeDatabase(userId)

                        // เปิดหน้า HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "สร้างบัญชีไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserDataToRealtimeDatabase(userId: String) {
        // ข้อมูล status
        val userStatus = mapOf(
            "speciality" to speciality
        )

        // ข้อมูล personal_data
        val personalData = mapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "birth_date" to birthDate,
            "gender" to gender,
            "email" to edtFullMail.text.toString().trim()
        )

        // บันทึกข้อมูล status
        mDbRef.child(userId)
            .child("status")
            .setValue(userStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "บันทึกข้อมูล สถานะสำเร็จ", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "เกิดข้อผิดพลาดในการบันทึก สถานะ", Toast.LENGTH_SHORT).show()
            }

        // บันทึกข้อมูล personal_data
        mDbRef.child(userId)
            .child("personal_data")
            .setValue(personalData)
            .addOnSuccessListener {
                Toast.makeText(this, "บันทึกข้อมูล ส่วนบุคคล สำเร็จ", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "เกิดข้อผิดพลาดในการบันทึก ข้อมูลส่วนบุคคล", Toast.LENGTH_SHORT).show()
            }
    }
}