package com.MyPobmor

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class FirebaseNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return START_NOT_STICKY

        val database = FirebaseDatabase.getInstance().reference
            .child("Users").child(userId).child("medicRemin")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val medicineName = child.child("medicineName").value.toString()
                    val selectedTime = child.child("selectedTime").value.toString()
                    val selectedDate = child.child("selectedDate").value.toString()

                    // ตั้งค่าการแจ้งเตือน
                    scheduleLocalNotification(medicineName, selectedTime, selectedDate)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleLocalNotification(
        medicineName: String,
        selectedTime: String,
        selectedDate: String
    ) {
        val intent = Intent(this, Notification::class.java).apply {
            putExtra(titleExtra, "Medication Reminder")
            putExtra(messageExtra, "It's time to take your $medicineName")
        }

        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateTimeString = "$selectedDate $selectedTime"
        val alarmTimeInMillis = dateTimeFormat.parse(dateTimeString)?.time ?: return

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTimeInMillis,
            pendingIntent
        )
    }

}