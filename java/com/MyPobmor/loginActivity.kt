package com.MyPobmor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class loginActivity : AppCompatActivity() {

    private lateinit var edtfullmail: EditText
    private lateinit var edtpassword: TextInputEditText
    private lateinit var btnlogin: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        edtfullmail = findViewById<EditText>(R.id.etFullmail)
        edtpassword = findViewById(R.id.etPassword)
        btnlogin = findViewById<Button>(R.id.btnSignUp)

        //ลิ้งค์ไปยังหน้า signup
        val txSignup = findViewById<TextView>(R.id.tvsignup)
        txSignup.setOnClickListener {
            val intent = Intent(this, docorpatientActivity::class.java)
            startActivity(intent)
        }

        //ลิ้งค์ไปหน้าลืมรหัสผ่าน
        val forGotPass = findViewById<TextView>(R.id.tvforgetpassword)
        forGotPass.setOnClickListener {
            val intent = Intent(this, forgot_passwordActivity::class.java)
            startActivity(intent)
        }

        btnlogin.setOnClickListener {
            val email = edtfullmail.text.toString()
            val password = edtpassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@loginActivity, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT)
                    .show()
            } else {
                login(email, password)
            }
        }
    }

    //function login
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // บันทึกสถานะการเข้าสู่ระบบ
                    val sharedPref = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("isLoggedIn", true).apply()
                    // open home activity
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                    Toast.makeText(this@loginActivity, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@loginActivity, "เข้าสู่ระบบไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                }
            }
    }
}