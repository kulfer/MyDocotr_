package com.MyPobmor

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class AlldrugActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alldrug)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.showAllDrug)
        medicationList = mutableListOf()
        medicationAdapter = MedicationAdapter(medicationList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = medicationAdapter

        // Back button click
        val backImg = findViewById<ImageView>(R.id.backimg)
        backImg.setOnClickListener {
            finish() // Go back to previous activity
        }

        val database = FirebaseDatabase.getInstance()
        val ref = database.reference.child("medicationReminders")
        // ดึงข้อมูลจาก Firebase
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the existing medication list before adding new data
                medicationList.clear()
                // วนลูปดึงข้อมูลยาแต่ละตัวจาก Firebase
                for (medicationSnapshot in snapshot.children) {
                    val medication = medicationSnapshot.getValue(MedicationData::class.java)
                    if (medication != null) {
                        // เพิ่มข้อมูลยาเข้าไปในรายการ
                        medicationList.add(medication)
                    }
                }
                // เรียงลำดับรายการยาตามวันที่และเวลา
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // รูปแบบวันที่และเวลาที่เก็บไว้
                medicationList.sortWith { med1, med2 ->
                    val dateTime1 = dateFormat.parse("${med1.selectedDate} ${med1.selectedTime}")
                    val dateTime2 = dateFormat.parse("${med2.selectedDate} ${med2.selectedTime}")

                    when {
                        dateTime1 == null -> 1 // หาก dateTime1 เป็น null ให้เรียงไว้ด้านล่าง
                        dateTime2 == null -> -1 // หาก dateTime2 เป็น null ให้เรียงไว้ด้านบน
                        else -> dateTime1.compareTo(dateTime2) // เปรียบเทียบวันที่และเวลา
                    }
                }
                // Notify adapter ว่ามีการอัปเดตข้อมูล
                medicationAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read value.", error.toException())
            }
        })

    }
}