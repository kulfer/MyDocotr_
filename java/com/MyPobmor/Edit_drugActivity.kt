package com.MyPobmor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Edit_drugActivity : AppCompatActivity() {

    private lateinit var etMedicName: EditText
    private lateinit var timePicker: TextView
    private lateinit var datePicker: TextView
    private lateinit var spinnerMedicineType: Spinner
    private lateinit var editTextQuantity: EditText
    private lateinit var edNotes: EditText
    private lateinit var medication: MedicationData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_drug)

        val backimg = findViewById<ImageView>(R.id.backimg)
        backimg.setOnClickListener {
            finish()
        }

        // เชื่อมโยง View กับตัวแปร
        etMedicName = findViewById(R.id.etMedicName)
        timePicker = findViewById(R.id.timePicker)
        datePicker = findViewById(R.id.datePicker)
        spinnerMedicineType = findViewById(R.id.spinnerMedicineType)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        edNotes = findViewById(R.id.edNotes)

        // รับข้อมูลที่ส่งมาจาก Intent โดยใช้ getParcelableExtra
        medication = intent.getParcelableExtra("medicationData") ?: return // กรณีข้อมูลเป็น null จะไม่ทำงานต่อ

        // แสดงข้อมูลใน View
        etMedicName.setText(medication.medicineName)
        timePicker.text = medication.selectedTime
        datePicker.text = medication.selectedDate
        editTextQuantity.setText(medication.quantity)
        edNotes.setText(medication.notes)

        // ตั้งค่า Spinner (เพิ่ม Adapter ตามประเภทของยา)
        val medicineTypes = resources.getStringArray(R.array.medicine_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicineTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMedicineType.adapter = adapter

        val selectedTypeIndex = medicineTypes.indexOf(medication.medicineType)
        spinnerMedicineType.setSelection(if (selectedTypeIndex >= 0) selectedTypeIndex else 0)

        // ตั้งค่าปุ่มยืนยัน
        findViewById<Button>(R.id.submitButton).setOnClickListener {
            saveEditedMedication()
        }

        // ตั้งค่าปุ่มย้อนกลับ
        findViewById<ImageView>(R.id.backimg).setOnClickListener {
            finish()
        }
    }

    private fun saveEditedMedication() {
        // ตรวจสอบค่าว่างก่อนบันทึก
        if (etMedicName.text.isNullOrEmpty()) {
            Log.e("Edit_drugActivity", "Medicine name is required")
            return
        }
        if (timePicker.text.isNullOrEmpty() || datePicker.text.isNullOrEmpty()) {
            Log.e("Edit_drugActivity", "Time and Date are required")
            return
        }

        val quantityText = editTextQuantity.text.toString()
        val quantity = if (quantityText.isNotEmpty()) quantityText.toInt() else 0

        // สร้างออบเจ็กต์ที่แก้ไข
        val updatedMedication = MedicationData(
            medicineName = etMedicName.text.toString(),
            selectedTime = timePicker.text.toString(),
            selectedDate = datePicker.text.toString(),
            medicineType = spinnerMedicineType.selectedItem.toString(),
            quantity = quantity.toString(), // แปลง quantity เป็น String
            notes = edNotes.text.toString()
        )

        // ส่งข้อมูลกลับ
        val resultIntent = Intent().apply {
            putExtra("updatedMedication", updatedMedication)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish() // ปิด Activity
    }
}