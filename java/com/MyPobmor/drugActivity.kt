package com.MyPobmor

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.*

class drugActivity : AppCompatActivity() {

    private lateinit var llMedicationHistory: LinearLayout
    private lateinit var llScheduledMedications: LinearLayout
    private lateinit var etMedicationName: EditText
    private lateinit var etMedicationTime: EditText
    private lateinit var etMedicationNotes: EditText
    private lateinit var setDateTitle: TextView
    private lateinit var lnDate: LinearLayout
    private lateinit var lnSetTime: LinearLayout
    private lateinit var medicationList: MutableList<MedicationData>
    private lateinit var medicationAdapter: MedicationAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var mdbRef: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_drug)

        // Initialize Firebase and UI
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        mdbRef = FirebaseDatabase.getInstance().getReference("Users")

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.showAllDrug)
        medicationList = mutableListOf()
        medicationAdapter = MedicationAdapter(medicationList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = medicationAdapter



        // open AlldrugActivity
        val showAllDrug = findViewById<TextView>(R.id.showalldrug)
        showAllDrug.setOnClickListener {
            val intent = Intent(this, AlldrugActivity::class.java)
            startActivity(intent)
        }

        // ตรวจสอบว่า user ได้ล็อกอินหรือยัง
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "กรุณาเข้าสู่ระบบก่อนใช้งาน", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = currentUser.uid

        // ดึงข้อมูลจาก Firebase
        fetchMedicationData(userId)

        // Listen for the fragment result
        supportFragmentManager.setFragmentResultListener(
            "medicationAdded",
            this
        ) { requestKey, result ->
            val medicineName = result.getString("medicineName", "")
            val selectedTime = result.getString("selectedTime", "")
            val selectedDate = result.getString("selectedDate", "")
            val medicineType = result.getString("medicineType", "")
            val quantity = result.getString("quantity", "")
            val notes = result.getString("notes", "")

        }

        // เปิด DialogFragment เมื่อกดปุ่ม "Add Medication"
        val addButton = findViewById<LinearLayout>(R.id.lnSetTime)
        addButton.setOnClickListener {
            val dialog = MedicDialogFragment()
            dialog.show(supportFragmentManager, "MedicDialog")
        }

        //open sheet dialog
        // ตัวอย่างการเปิด BottomSheetDialog เมื่อกดที่ item ของยา
        medicationAdapter.setOnItemClickListener { medication ->
            showMedicationDetails(medication)
        }

        // ตั้งค่า Adapter ให้ RecyclerView
        recyclerView.adapter = medicationAdapter

        fun getFCMToken() {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.i("My token is", token ?: "No token received")
                }
            }
        }
        // เพิ่ม Firebase token
        getFCMToken()

        // Initialize UI elements
        llMedicationHistory = findViewById(R.id.MedicationHistory)
        llScheduledMedications = findViewById(R.id.llSedMedications)
        setDateTitle = findViewById(R.id.setDateTitle)
        lnDate = findViewById(R.id.lnDate)
        lnSetTime = findViewById(R.id.lnSetTime)

        val database = FirebaseDatabase.getInstance()
        val ref = database.reference.child("Users").child(userId).child("medicRemin")

        //set current date
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(
            "EEEE ที่ d MMM yyyy",
            Locale("th", "th")
        ) // Format: วันอาทิตย์ ที่ 22 กันยายน 2024
        val currentDate = dateFormat.format(calendar.time)
        setDateTitle.text = currentDate

        addDateTextView()

        //open alert dialog set time medic
        lnSetTime.setOnClickListener {
            showSetReminderDialog()
        }

        // Back button click
        val backImg = findViewById<ImageView>(R.id.backimg)
        backImg.setOnClickListener {
            finish() // Go back to previous activity
        }
    }

    // ดึงข้อมูลจาก Firebase
    private fun fetchMedicationData(userId: String) {
        val ref = database.reference.child("Users").child(userId).child("medicRemin")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // ดึงข้อมูลที่เพิ่มเข้ามา
                val medication = snapshot.getValue(MedicationData::class.java)
                if (medication != null) {
                    medicationList.add(medication)
                    // เรียงลำดับตามวันที่และเวลา

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    medicationList.sortWith { med1, med2 ->
                        val dateTime1: Date? = if (!med1.selectedDate.isNullOrEmpty() && !med1.selectedTime.isNullOrEmpty()) {
                            dateFormat.parse("${med1.selectedDate} ${med1.selectedTime}")
                        } else {
                            null
                        }

                        val dateTime2: Date? = if (!med2.selectedDate.isNullOrEmpty() && !med2.selectedTime.isNullOrEmpty()) {
                            dateFormat.parse("${med2.selectedDate} ${med2.selectedTime}")
                        } else {
                            null
                        }

                        when {
                            dateTime1 == null && dateTime2 == null -> 0
                            dateTime1 == null -> 1
                            dateTime2 == null -> -1
                            else -> dateTime1.compareTo(dateTime2)
                        }
                    }
                    // อัปเดต RecyclerView
                    medicationAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // ถ้ามีการแก้ไขข้อมูลให้จัดการที่นี่
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // ถ้ามีการลบข้อมูลให้จัดการที่นี่
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // ถ้ามีการย้ายข้อมูลให้จัดการที่นี่
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })
    }

    private fun showSetReminderDialog() {
        val dialog = MedicDialogFragment()
        dialog.show(supportFragmentManager, "MedicDialog")
    }

    private fun fetchSavedDays(userId: String, dateTextViews: Map<Int, TextView>) {
        val ref = database.reference.child("Users").child(userId).child("savemedic_day")

        ref.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // ดึงข้อมูลวันที่ที่มีค่าเป็น 1
                for (daySnapshot in snapshot.children) {
                    val day = daySnapshot.key?.toIntOrNull() // แปลง key เป็นตัวเลข
                    val value = daySnapshot.getValue(Int::class.java)

                    if (day != null && value == 1) {
                        // ถ้าวันที่มีค่าเป็น 1 ให้ปรับ UI
                        val textView = dateTextViews[day]
                        textView?.apply {
                            setBackgroundResource(R.drawable.background_button)
                            setTextColor(Color.WHITE)
                        }
                    }
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "เกิดข้อผิดพลาดในการโหลดข้อมูล: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    // ฟังก์ชันเพื่อเก็บข้อมูลวันที่บันทึก
     private fun saveMedicationDay(userId: String, day: Int, dateTextView: TextView) {
        val ref = database.reference.child("Users").child(userId).child("savemedic_day").child(day.toString())

        // อ่านค่าปัจจุบันจาก Firebase
        ref.get().addOnSuccessListener { snapshot ->
            val currentValue = snapshot.getValue(Int::class.java) ?: 0 // ถ้าไม่มีค่า ให้ตั้งเป็น 0

            // ถ้าค่าปัจจุบันยังเป็น 0 ให้บันทึกเป็น 1
            if (currentValue == 0) {
                ref.setValue(1)
                    .addOnSuccessListener {
                        // อัปเดต UI เมื่อบันทึกสำเร็จ
                        dateTextView.setBackgroundResource(R.drawable.background_button)
                        dateTextView.setTextColor(Color.WHITE)
                        Toast.makeText(this, "บันทึกการทานยาสำเร็จ", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "เกิดข้อผิดพลาดในการบันทึก: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "ข้อมูลนี้ถูกบันทึกไปแล้ว", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "เกิดข้อผิดพลาด: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMedicationDetails(medication: MedicationData) {
        // สร้าง BottomSheetDialog
        val bottomSheetView = layoutInflater.inflate(R.layout.sheet_dialog_mgdrug, null)

        // การเชื่อมต่อข้อมูลกับ views
        val medicNameTextView = bottomSheetView.findViewById<TextView>(R.id.medicName)
        val medicQuantityTextView = bottomSheetView.findViewById<TextView>(R.id.medicQuantity)
        val medicTypeTextView = bottomSheetView.findViewById<TextView>(R.id.medicType)
        val medicTimeTextView = bottomSheetView.findViewById<TextView>(R.id.medicTime)
        val medicDateTextView = bottomSheetView.findViewById<TextView>(R.id.medicDate)
        val deleteButton = bottomSheetView.findViewById<Button>(R.id.delete_medic)
        val editButton = bottomSheetView.findViewById<Button>(R.id.edit_medic)

        // แสดงข้อมูล
        medicNameTextView.text = medication.medicineName
        medicQuantityTextView.text = medication.quantity.toString()
        medicTypeTextView.text = medication.medicineType
        medicTimeTextView.text = medication.selectedTime
        medicDateTextView.text = medication.selectedDate

        // กดลบรายการ
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(medication)
            deleteMedication(medication.id)
        }

        // กดแก้ไขรายการ
        editButton.setOnClickListener {
            editMedication(medication)
        }

        // เปิด BottomSheet
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun showDeleteConfirmationDialog(medication: MedicationData) {
        AlertDialog.Builder(this)
            .setTitle("ยืนยันการลบ")
            .setMessage("คุณต้องการลบรายการยา ${medication.medicineName} หรือไม่?")
            .setPositiveButton("ลบ") { dialog, _ ->
                // เมื่อกดปุ่ม "ลบ"
                deleteMedication(medication.toString())
                dialog.dismiss()
            }
            .setNegativeButton("ยกเลิก") { dialog, _ ->
                // เมื่อกดปุ่ม "ยกเลิก"
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteMedication(medicationId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val ref = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(userId)
                .child("medicRemin")

            ref.child(medicationId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "ลบรายการสำเร็จ", Toast.LENGTH_SHORT).show()
                    fetchMedicationData(userId) // Refresh data
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "เกิดข้อผิดพลาด: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Edit medication
    private fun editMedication(medication: MedicationData) {
        val intent = Intent(this, Edit_drugActivity::class.java)
        intent.putExtra("medicationData", medication)
        startActivity(intent)
    }


    private fun addDateTextView() {
        val calendar = Calendar.getInstance()
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // จำนวนวันสูงสุดของเดือนนี้
        val dayAbbreviation =
            arrayOf("อา", "จ", "อ", "พ", "พฤ", "ศ", "ส") // อักษรย่อของแต่ละวันในสัปดาห์

        // เก็บค่าปัจจุบันของวันและเดือน
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        // ตั้งค่าวันแรกของเดือน
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // สร้างการอ้างอิงไปที่ HorizontalScrollView
        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.horizontalScrollView)
        val lnDate = findViewById<LinearLayout>(R.id.lnDate)

        // ตัวแปรเพื่อเก็บตำแหน่งของวันที่ปัจจุบัน
        var currentDayPosition = -1

        // วนลูปตามจำนวนวันในเดือน
        for (day in 1..maxDays) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // ตรวจสอบว่าวันนั้นเป็นวันอะไร
            val dayText =
                "${dayAbbreviation[dayOfWeek - 1]} $day" // สร้างข้อความที่ต้องการ เช่น "อา 1"
            // สร้าง TextView ใหม่
            val dateTextView = TextView(this).apply {
                text = dayText
                textSize = 16f
                setBackgroundResource(R.drawable.circle_background)
                setTypeface(null, Typeface.NORMAL)
                setPadding(8, 8, 8, 8)
                // กำหนดขนาดความกว้างและความสูง
                layoutParams = LinearLayout.LayoutParams(80, 80)

                // กำหนด Margin
                val layoutParams = LinearLayout.LayoutParams(120, 100)
                layoutParams.setMargins(16, 1, 16, 1)
                this.layoutParams = layoutParams

                // จัดตำแหน่งข้อความให้อยู่ตรงกลาง
                gravity = Gravity.CENTER // ทำให้ข้อความอยู่ตรงกลาง

                if (day == currentDay && calendar.get(Calendar.MONTH) == currentMonth && calendar.get(
                        Calendar.YEAR
                    ) == currentYear
                ) {
                    // เพิ่มขนาดของวันที่ปัจจุบันหรือเปลี่ยนสไตล์ให้สังเกตได้ง่าย
                    textSize = 18f
                    setTextColor(Color.BLACK)
                    setBackgroundResource(R.drawable.circle_background)
                    setTypeface(null, Typeface.BOLD) // เปลี่ยนรูปแบบเป็นตัวหนาและตัวเอียง
                    // กำหนดขนาดความกว้างและความสูง
                    val layoutParams = LinearLayout.LayoutParams(150, 120)
                    // กำหนด Margin
                    layoutParams.setMargins(16, 16, 16, 16)
                    this.layoutParams = layoutParams
                    // จัดตำแหน่งข้อความให้อยู่ตรงกลาง
                    gravity = Gravity.CENTER // ทำให้ข้อความอยู่ตรงกลาง

                    // เก็บตำแหน่งของวันที่ปัจจุบัน
                    currentDayPosition = lnDate.childCount
                }
                // เมื่อคลิกที่วันที่ให้เปิด DialogFragment
                setOnClickListener {
                    // เมื่อคลิกที่วันที่ให้เปิด DialogFragment
                    setOnClickListener {
                        // สร้าง AlertDialog เมื่อคลิกที่วันที่
                        showSavedDrugUser(day, this)
                    }
                }
            }
            //
            lnDate.addView(dateTextView)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        // หลังจากเพิ่มวันที่ทั้งหมดแล้ว เลื่อนเพื่อให้วันที่ปัจจุบันอยู่กลาง
        lnDate.post {
            if (currentDayPosition != -1) {
                // คำนวณตำแหน่งที่จะเลื่อนไปและให้วันที่ปัจจุบันอยู่กลาง
                val offset = (currentDayPosition * (120 + 16)) - (horizontalScrollView.width / 2)
                horizontalScrollView.smoothScrollTo(offset, 0)
            }
        }
    }

    private fun showSavedDrugUser(day: Int, dateTextView: TextView) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        // หากไม่มีผู้ใช้ล็อกอิน
        if (userId == null) {
            Toast.makeText(this, "กรุณาล็อกอินก่อน", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("บันทึกการทานยา")
            .setMessage("คุณต้องการบันทึกการทานยาของวันที่ : $day หรือไม่?")
            .setPositiveButton("ยืนยัน") { dialog, _ ->
                dialog.dismiss()

                // บันทึกค่าใน Firebase และปรับ UI
                saveMedicationDay(userId, day, dateTextView)
            }
            .setNegativeButton("ยกเลิก") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}