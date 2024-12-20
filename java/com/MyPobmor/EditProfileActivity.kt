package com.MyPobmor

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var edTxName: EditText
    private lateinit var edTxLastname: EditText
    private lateinit var edTxGender: EditText
    private lateinit var edTxAge: EditText
    private lateinit var edTxAddress: EditText
    private lateinit var edTxTell: EditText
    private lateinit var edTxEmail: EditText
    private lateinit var edTxDiseat: EditText
    private lateinit var edTxDrug: EditText
    private lateinit var updateProfileButton: Button
    private lateinit var mStorageRef: StorageReference
    private lateinit var profileImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // ใน onCreate() เพิ่ม
        mStorageRef = FirebaseStorage.getInstance().reference

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users") // Users node

        // Bind UI elements
        edTxName = findViewById(R.id.edTxName)
        edTxLastname = findViewById(R.id.edTxlastname)
        edTxGender = findViewById(R.id.edTxGender)
        edTxAge = findViewById(R.id.edTxAge)
        edTxAddress = findViewById(R.id.edTxAddress)
        edTxTell = findViewById(R.id.edTxTell)
        edTxEmail = findViewById(R.id.edTxEmail)
        edTxDiseat = findViewById(R.id.edTxDiseat)
        edTxDrug = findViewById(R.id.edTxDrug)
        updateProfileButton = findViewById(R.id.update_profile_button)
        val backImg = findViewById<ImageView>(R.id.backimg)

        // Set back button action
        backImg.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Profile image click action
        profileImage = findViewById(R.id.profile_image)
        profileImage.setOnClickListener {
            openGallery()
        }

        // Fetch existing data from Firebase and populate the fields
        val currentUserUid = mAuth.currentUser?.uid
        if (currentUserUid != null) {
            mDbRef.child(currentUserUid).child("personal_data")
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        edTxName.setText(
                            snapshot.child("first_name").getValue(String::class.java) ?: ""
                        )
                        edTxLastname.setText(
                            snapshot.child("last_name").getValue(String::class.java) ?: ""
                        )
                        edTxGender.setText(
                            snapshot.child("gender").getValue(String::class.java) ?: ""
                        )
                        edTxAge.setText(
                            snapshot.child("birth_date").getValue(String::class.java) ?: ""
                        )
                        edTxAddress.setText(
                            snapshot.child("address_user").getValue(String::class.java) ?: ""
                        )
                        edTxTell.setText(snapshot.child("tell").getValue(String::class.java) ?: "")
                        edTxEmail.setText(
                            snapshot.child("email").getValue(String::class.java) ?: ""
                        )
                        edTxDiseat.setText(
                            snapshot.child("diseat").getValue(String::class.java) ?: ""
                        )
                        edTxDrug.setText(snapshot.child("drug").getValue(String::class.java) ?: "")
                        val profileImageBase64 =
                            snapshot.child("profileImage").getValue(String::class.java)

                        // แปลง base64 string เป็น Bitmap และแสดงใน CircleImageView
                        if (!profileImageBase64.isNullOrEmpty()) {
                            try {
                                val decodedBytes = android.util.Base64.decode(
                                    profileImageBase64,
                                    android.util.Base64.DEFAULT
                                )
                                val bitmap =
                                    BitmapFactory.decodeByteArray(
                                        decodedBytes,
                                        0,
                                        decodedBytes.size
                                    )
                                profileImage.setImageBitmap(bitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                profileImage.setImageResource(R.drawable.profile2) // กำหนดค่าดีฟอลต์
                            }
                        } else {
                            profileImage.setImageResource(R.drawable.profile2) // กำหนดค่าดีฟอลต์ถ้าไม่มีภาพ
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Failed to fetch data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        // Update button click listener
        updateProfileButton.setOnClickListener {
            val updatedName = edTxName.text.toString()
            val updatedLastname = edTxLastname.text.toString()
            val updatedGender = edTxGender.text.toString()
            val updatedAge = edTxAge.text.toString()
            val updatedAddress = edTxAddress.text.toString()
            val updatedTell = edTxTell.text.toString()
            val updatedEmail = edTxEmail.text.toString()
            val updatedDiseat = edTxDiseat.text.toString()
            val updatedDrug = edTxDrug.text.toString()

            // Handle profile image change
            if (::profileImageUri.isInitialized) {
                val imageByteArray = getImageByteArray(profileImageUri)
                val base64Image = Base64.encodeToString(imageByteArray, Base64.DEFAULT)

                // Prepare data to update in Firebase
                val updatedUserData = mutableMapOf<String, Any?>(
                    "first_name" to updatedName,
                    "last_name" to updatedLastname,
                    "gender" to updatedGender,
                    "birth_date" to updatedAge,
                    "address_user" to updatedAddress,
                    "tell" to updatedTell,
                    "email" to updatedEmail,
                    "diseat" to updatedDiseat,
                    "drug" to updatedDrug,
                    "profileImage" to base64Image
                )

                val currentUserUid = mAuth.currentUser?.uid
                if (currentUserUid != null) {
                    mDbRef.child(currentUserUid).child("personal_data")
                        .updateChildren(updatedUserData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "อัพเดพข้อมูลโปรไฟล์สำเร็จ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish() // Close the activity after update
                            } else {
                                Toast.makeText(
                                    this,
                                    "เกิดข้อผิดพลาดในการอัพเดพข้อมูล",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            } else {
                // If no profile image selected, just update the text fields
                val updatedUserData = mutableMapOf<String, Any?>(
                    "first_name" to updatedName,
                    "last_name" to updatedLastname,
                    "gender" to updatedGender,
                    "birth_date" to updatedAge,
                    "address_user" to updatedAddress,
                    "tell" to updatedTell,
                    "email" to updatedEmail,
                    "diseat" to updatedDiseat,
                    "drug" to updatedDrug
                )

                val currentUserUid = mAuth.currentUser?.uid
                if (currentUserUid != null) {
                    mDbRef.child(currentUserUid).child("personal_data")
                        .updateChildren(updatedUserData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "อัพเดพข้อมูลโปรไฟล์สำเร็จ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish() // Close the activity after update
                            } else {
                                Toast.makeText(
                                    this,
                                    "เกิดข้อผิดพลาดในการอัพเดพข้อมูล",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun getImageByteArray(imageUri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(imageUri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
        return byteArrayOutputStream.toByteArray()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val PICK_IMAGE_REQUEST = 1
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.data!!
            profileImage.setImageURI(profileImageUri)
        }

        edTxTell.addTextChangedListener(object : TextWatcher {
            var isUpdating = false // ตัวแปรควบคุมการแก้ไขซ้ำ

            override fun afterTextChanged(s: Editable?) {
                // ไม่ต้องทำอะไรที่นี่
            }

            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // ไม่ต้องทำอะไรที่นี่
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!isUpdating && charSequence != null) {
                    isUpdating = true // ป้องกันการเกิด loop

                    // ลบอักขระที่ไม่ใช่ตัวเลขออกก่อน
                    var cleanString = charSequence.replace(Regex("[^0-9]"), "")

                    // ใช้รูปแบบหมายเลขโทรศัพท์ (099-999-9999)
                    if (cleanString.length > 3 && cleanString.length <= 6) {
                        cleanString = cleanString.substring(0, 3) + "-" + cleanString.substring(3)
                    } else if (cleanString.length > 6) {
                        cleanString = cleanString.substring(0, 3) + "-" +
                                cleanString.substring(3, 6) + "-" +
                                cleanString.substring(6, Math.min(cleanString.length, 10))
                    }

                    // ตั้งค่าหมายเลขโทรศัพท์ที่มีขีดกลับไปที่ EditText
                    edTxTell.setText(cleanString)
                    edTxTell.setSelection(cleanString.length) // เคอร์เซอร์ไปตำแหน่งสุดท้าย
                    isUpdating = false
                }
            }
        })
    }
}