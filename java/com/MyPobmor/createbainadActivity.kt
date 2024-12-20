package com.MyPobmor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class createbainadActivity : AppCompatActivity() {

    private lateinit var dateTextView: TextView
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createbainad)

        // general var
        val sumButton = findViewById<Button>(R.id.submitButton)
        val backImg = findViewById<ImageView>(R.id.backimg)

        //ตัวแปร date time dialog
        val lNDate = findViewById<LinearLayout>(R.id.LNdate)
        dateTextView = findViewById(R.id.dateTextView)

        // กลับหน้าที่แล้ว
        backImg.setOnClickListener {
            finish()
        }

        sumButton.setOnClickListener {
            val intent = Intent(this, allbainadActivity::class.java)
            startActivity(intent)
        }

        //ส่วนการเรียกใช้ function
        lNDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    // function create date dialog
    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
                showTimePickerDialog()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // function create time dialog
    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeInView()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd-MMMM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        dateTextView.text = sdf.format(calendar.time)
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        dateTextView.append(" ${sdf.format(calendar.time)}")
    }
}