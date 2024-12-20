package com.MyPobmor

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.NOTIFICATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.Locale

class MedicDialogFragment: DialogFragment() {

    private lateinit var mDbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.dialog_set_reminder, container, false)

        // ใช้ findViewById เพื่อค้นหาปุ่ม cancelButton
        val cancelButton = rootView.findViewById<Button>(R.id.cancelButton)
        val submitButton = rootView.findViewById<Button>(R.id.submitButton)
        val timePicker = rootView.findViewById<TextView>(R.id.timePicker)
        val spinnerMedicineType = rootView.findViewById<Spinner>(R.id.spinnerMedicineType)
        val datePicker = rootView.findViewById<TextView>(R.id.datePicker)
        val etQuantity = rootView.findViewById<EditText>(R.id.editTextQuantity)
        val etNotes = rootView.findViewById<EditText>(R.id.edNotes)
        val etMedicName = rootView.findViewById<EditText>(R.id.etMedicName)

        val medicineTypes = resources.getStringArray(R.array.medicine_types)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, medicineTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMedicineType.adapter = adapter

        timePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                timePicker.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        datePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                datePicker.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        cancelButton.setOnClickListener { dismiss() }

        submitButton.setOnClickListener {
            val medicineName = etMedicName.text.toString()
            val selectedTime = timePicker.text.toString()
            val selectedDate = datePicker.text.toString()
            val medicineType = spinnerMedicineType.selectedItem.toString()
            val quantity = etQuantity.text.toString()
            val notes = etNotes.text.toString()

            if (medicineName.isEmpty() || selectedTime.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            scheduleLocalNotification(medicineName, selectedTime, selectedDate)

            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val ref = FirebaseDatabase.getInstance()
                .reference.child("Users").child(userId).child("medicRemin").push()

            val medicationData = mapOf(
                "medicineName" to medicineName,
                "selectedTime" to selectedTime,
                "selectedDate" to selectedDate,
                "medicineType" to medicineType,
                "quantity" to quantity,
                "notes" to notes
            )

            ref.setValue(medicationData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val resultBundle = Bundle().apply {
                        putString("medicineName", medicineName)
                        putString("selectedTime", selectedTime)
                        putString("selectedDate", selectedDate)
                        putString("medicineType", medicineType)
                        putString("quantity", quantity)
                        putString("notes", notes)
                    }
                    parentFragmentManager.setFragmentResult("medicationAdded", resultBundle)
                    Toast.makeText(requireContext(), "บันทึกการแจ้งเตือนสำเร็จ", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "เกิดข้อผิดพลาดในการบันทึกข้อมูล", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }

    private fun scheduleLocalNotification(
        medicineName: String,
        selectedTime: String,
        selectedDate: String
    ) {
        val intent = Intent(requireContext(), Notification::class.java).apply {
            putExtra(titleExtra, "Medication Reminder")
            putExtra(messageExtra, "It's time to take your $medicineName")
        }

        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val alarmTimeInMillis = dateTimeFormat.parse("$selectedDate $selectedTime")?.time ?: return

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTimeInMillis,
                    pendingIntent
                )
            } else {
                Toast.makeText(requireContext(), "Cannot schedule exact alarms on this device.", Toast.LENGTH_SHORT).show()
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTimeInMillis,
                pendingIntent
            )
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Set dialog width to match parent
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Set dialog to appear from the bottom
            setGravity(Gravity.BOTTOM)
            // Add animations if desired
            setWindowAnimations(R.style.DialogAnimation)
        }
    }
}