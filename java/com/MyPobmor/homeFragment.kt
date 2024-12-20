package com.MyPobmor

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class homeFragment : Fragment() {

    private lateinit var proimg: CircleImageView
    private lateinit var shname: TextView
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var shdateTextView: TextView
    private lateinit var shtimeTextView: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ประกาศ Firebase
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference("Users")

        // Initialize views
        proimg = view.findViewById(R.id.profile_image)
        shname = view.findViewById(R.id.showname)
        // Initialize TextViews
        shdateTextView = view.findViewById(R.id.shdate)
        shtimeTextView = view.findViewById(R.id.shtime)

        // Update date and time
        updateDateTime()

        setOnClickListeners(view)

        // ดึงข้อมูลจาก Firebase
        val currentUserUid = mAuth.currentUser?.uid
        if (currentUserUid != null) {
            // ดึงข้อมูลจาก Users/{userId}/personal_data
            mDbRef.child(currentUserUid).child("personal_data").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // ดึงข้อมูลจาก personaldata
                    val shName = snapshot.child("first_name").getValue(String::class.java) ?: "ชื่อไม่ระบุ"
                    val profileImageBase64 = snapshot.child("profileImage").getValue(String::class.java)

                        // Update UI
                        shname.text = shName ?: "Users"

                    if (!profileImageBase64.isNullOrEmpty()) {
                        try {
                            val decodedBytes = android.util.Base64.decode(profileImageBase64, android.util.Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            proimg.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            proimg.setImageResource(R.drawable.profile2) // กำหนดค่าดีฟอลต์
                        }
                    } else {
                        proimg.setImageResource(R.drawable.profile2) // กำหนดค่าดีฟอลต์ถ้าไม่มีภาพ
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "ไม่สามารถดึงข้อมูลได้", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Set click listeners
        shname.setOnClickListener {
            callActivity()
        }
        proimg.setOnClickListener {
            callActivity()
        }

        // Initialize ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                val updatedName = data?.getStringExtra("EXTRA_MESSAGE")
                val profileImageUri = data?.getStringExtra("PROFILE_IMAGE_URI")

                if (updatedName != null) {
                    shname.text = updatedName
                }

                if (profileImageUri != null) {
                    proimg.setImageURI(Uri.parse(profileImageUri))
                }
            }
        }
    }

    private fun setOnClickListeners(view: View) {
        // Find the LinearLayout with id 'bainaditem'
        val bainadItem = view.findViewById<CardView>(R.id.bainaditem)
        val disitem = view.findViewById<CardView>(R.id.diseaseitem)
        val drugitem = view.findViewById<CardView>(R.id.yaitem)
        val lineitem = view.findViewById<CardView>(R.id.lineitem)
       // val tuatitem = view.findViewById<LinearLayout>(R.id.tuatitem)

        // Set OnClickListener for LinearLayouts
        bainadItem.setOnClickListener {
            startActivity(Intent(activity, mgbainadActivity::class.java))
        }
        disitem.setOnClickListener {
            startActivity(Intent(activity, allroctActivity::class.java))
        }
        drugitem.setOnClickListener {
            startActivity(Intent(activity, drugActivity::class.java))
        }

        // Set OnClickListener for news items
        val news1 = view.findViewById<View>(R.id.news1)
        val news2 = view.findViewById<View>(R.id.news2)
        val news3 = view.findViewById<View>(R.id.news3)

        news1.setOnClickListener { openNews("https://www.sosthailand.org/blog/dengue-fever") }
        news2.setOnClickListener { openNews("https://th.yanhee.net/ศูนย์ย่อยการรักษา/ศูนย์หัวใจ") }
        news3.setOnClickListener { openNews("https://www.wattanosothcancerhospital.com/all-about-cancer/general-information-about-cancer") }

        // Handle clicks for lineitem (Contact Hospital)
        lineitem.setOnClickListener {
            // เปิด AlertDialog เพื่อยืนยันการติดต่อ
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("ต้องการติดต่อที่ รพ.สตบึงคอไห2 หรือไม่?")
                .setPositiveButton("ยืนยัน") { dialog, id ->
                    // เมื่อกดยืนยันให้โทรไปยังเบอร์ 021909796
                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:021909796"))
                    startActivity(dialIntent)
                }
                .setNegativeButton("ยกเลิก") { dialog, id ->
                    // เมื่อกดยกเลิก ให้ปิด Dialog
                    dialog.dismiss()
                }
            // สร้างและแสดง AlertDialog
            builder.create().show()
        }

        shname.setOnClickListener { callActivity() }
        proimg.setOnClickListener { callActivity() }

    }

    private fun openNews(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun callActivity() {
        val fullname = shname.text.toString()
        val intent = Intent(activity, ProActivity::class.java).apply {
            putExtra("EXTRA_MESSAGE", fullname)
        }
            activityResultLauncher.launch(intent)

    }


    private fun updateDateTime() {
        // Get current date and time
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) // Format for date
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // Format for time

        val currentDate = dateFormat.format(calendar.time)
        val currentTime = timeFormat.format(calendar.time)

        // Set date and time to TextViews
        shdateTextView.text = currentDate
        shtimeTextView.text = "$currentTime - ${getEndTime(currentTime)}"
    }

    private fun getEndTime(startTime: String): String {
        // Function to calculate end time (1 hour after start time)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = timeFormat.parse(startTime) ?: calendar.time
        calendar.add(Calendar.HOUR, 1) // Add 1 hour

        return timeFormat.format(calendar.time)
    }
    override fun onResume() {
        super.onResume()
        loadProfileData()  // ฟังก์ชันนี้จะใช้ดึงข้อมูลจาก Firebase และอัพเดต UI
    }

    private fun loadProfileData() {
        val currentUserUid = mAuth.currentUser?.uid
        if (currentUserUid != null) {
            // เปลี่ยนเส้นทางในการดึงข้อมูลเป็น User/{userId}/personal_data
            mDbRef.child(currentUserUid).child("personal_data")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // ดึงข้อมูล firstname
                        val name = snapshot.child("first_name").getValue(String::class.java) ?: "ไม่ได้ระบุชื่อผู้ใช้"
                        // ดึงข้อมูลรูปโปรไฟล์
                        val profileImageBase64 = snapshot.child("profileImage").getValue(String::class.java)

                        // อัพเดต UI
                        shname.text = name
                        if (!profileImageBase64.isNullOrEmpty()) {
                            try {
                                val decodedBytes = android.util.Base64.decode(profileImageBase64, android.util.Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                proimg.setImageBitmap(bitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                proimg.setImageResource(R.drawable.profile2) // รูปโปรไฟล์ดีฟอลต์
                            }
                        } else {
                            proimg.setImageResource(R.drawable.profile2) // รูปโปรไฟล์ดีฟอลต์
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "ไม่สามารถโหลดข้อมูลผู้ใช้ได้", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}