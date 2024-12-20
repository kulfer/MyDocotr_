package com.MyPobmor

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.FileNotFoundException
import java.io.InputStream

class ProActivity : AppCompatActivity() {

    private lateinit var profileImageView: CircleImageView
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var loadingDialog: loadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pro)

        // สร้างอินสแตนซ์ของ LoadingDialog
        loadingDialog = loadingDialog(this)

        profileImageView = findViewById(R.id.profile_image)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users") // ใช้ "Users" เป็น root node

        val backImg = findViewById<ImageView>(R.id.backimg)

        val nameIn = findViewById<TextView>(R.id.textFirstName)
        val textgender = findViewById<TextView>(R.id.textGender)
        val textname = findViewById<TextView>(R.id.textfirstName)
        val textsakun = findViewById<TextView>(R.id.textlastname)
        val textbirthdate = findViewById<TextView>(R.id.textBirthdate)
        val textaddress = findViewById<TextView>(R.id.textAddress)
        val textemail = findViewById<TextView>(R.id.textEmail)
        val textroct = findViewById<TextView>(R.id.textRoct)
        val textdrug = findViewById<TextView>(R.id.textDrug)
        val texttell = findViewById<TextView>(R.id.textTell)

        // ดึงข้อมูลจาก Firebase
        val currentUserUid = mAuth.currentUser?.uid
        if (currentUserUid != null) {
            // ดึงข้อมูลจาก Users/{userId}/personal_data
            mDbRef.child(currentUserUid).child("personal_data").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // ดึงข้อมูลจาก personaldata
                    val firstName = snapshot.child("first_name").getValue(String::class.java) ?: "ชื่อไม่ระบุ"
                    val lastName = snapshot.child("last_name").getValue(String::class.java) ?: "นามสกุลไม่ระบุ"
                    val gender = snapshot.child("gender").getValue(String::class.java) ?: "ไม่ระบุเพศ"
                    val birthdate = snapshot.child("birth_date").getValue(String::class.java) ?: "วันเกิดไม่ระบุ"
                    val address = snapshot.child("address").getValue(String::class.java) ?: "ที่อยู่ไม่ระบุ"
                    val email = snapshot.child("email").getValue(String::class.java) ?: "อีเมลไม่ระบุ"
                    val diseat = snapshot.child("diseat").getValue(String::class.java) ?: "โรคประจำตัวไม่ระบุ"
                    val drugInfo = snapshot.child("drug").getValue(String::class.java) ?: "ข้อมูลยาไม่ระบุ"
                    val tell = snapshot.child("tell").getValue(String::class.java) ?: "หมายเลขโทรศัพท์ไม่ระบุ"
                    // เรียกใช้ฟังก์ชัน formatPhoneNumber
                    texttell.text = formatPhoneNumber(tell)

                    val profileImageBase64 = snapshot.child("profileImage").getValue(String::class.java)

                    // ตั้งค่าข้อมูลใน TextView
                    nameIn.text = firstName ?: "Name not available"
                    textsakun.text = lastName ?: "Sakun not available"
                    textgender.text = gender ?: "Gender not available"
                    textname.text = firstName ?: "Name not available"
                    textbirthdate.text = birthdate ?: "Birthdate not available"
                    textaddress.text = address ?: "Address not available"
                    textemail.text = email ?: "Email not available"
                    textroct.text = diseat ?: "Diseat not available"
                    textdrug.text = drugInfo ?: "Drug information not available"
                    texttell.text = tell ?: "Telephone not available"

                    // แปลง base64 string เป็น Bitmap และแสดงใน CircleImageView
                    if (!profileImageBase64.isNullOrEmpty()) {
                        try {
                            val decodedBytes = android.util.Base64.decode(profileImageBase64, android.util.Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            profileImageView.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            profileImageView.setImageResource(R.drawable.profile2) // กำหนดค่าดีฟอลต์
                        }
                    } else {
                        profileImageView.setImageResource(R.drawable.profile2) // กำหนดค่าดีฟอลต์ถ้าไม่มีภาพ
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProActivity, "ไม่สามารถดึงข้อมูลได้", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // คลิกปุ่มย้อนกลับ
        backImg.setOnClickListener {
            finish() // กลับไปหน้าก่อนหน้า
        }

        // ตัวแปรการเปลี่ยนรูปโปรไฟล์
        profileImageView = findViewById(R.id.profile_image)

        // ประกาศตัวแปรการรับข้อมูล
        val fullname = intent.getStringExtra("EXTRA_MESSAGE")

        // ปุ่มที่ให้ผู้ใช้ไปหน้า EditProfileActivity
        val btnChangData = findViewById<Button>(R.id.chang_profile_button)
        btnChangData.setOnClickListener {
            // แสดง Loading Dialog
            loadingDialog.startLoadingDialog()

            // ใช้ Handler เพื่อหน่วงเวลา 3 วินาที
            android.os.Handler().postDelayed({
                // ปิด Loading Dialog
                loadingDialog.dismissDialog()

                val intent = Intent(this, EditProfileActivity::class.java)
                startActivityForResult(intent, 100) // ส่ง request code
                // ปิด Loading Dialog หลังจากเปิดหน้าใหม่
            }, 1500) // 3000 มิลลิวินาที = 3 วินาที
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // รับผลจากการเลือกภาพใน Gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            try {
                val imageStream: InputStream? = contentResolver.openInputStream(selectedImageUri!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                profileImageView.setImageBitmap(selectedImage)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        }

        // รับข้อมูลจาก EditProfileActivity หลังจากบันทึกข้อมูล
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // รีเฟรชข้อมูลจาก Firebase อีกครั้งหลังการแก้ไข
            onCreate(null)
        }

    }
    private fun formatPhoneNumber(phone: String): String {
        val cleanString = phone.replace(Regex("[^0-9]"), "") // ลบตัวอักษรที่ไม่ใช่ตัวเลข
        return when {
            cleanString.length > 6 -> {
                "${cleanString.substring(0, 3)}-${cleanString.substring(3, 6)}-${cleanString.substring(6, Math.min(cleanString.length, 10))}"
            }
            cleanString.length > 3 -> {
                "${cleanString.substring(0, 3)}-${cleanString.substring(3)}"
            }
            else -> cleanString
        }
    }
}
