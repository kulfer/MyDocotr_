package com.MyPobmor

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var databas: DatabaseReference
    // ประกาศ channelId และ channelName
    private val channelId = "medication_reminder_channel"
    private val channelName = "Medication Reminders"

    // ฟังก์ชันรับข้อความจาก Firebase
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val medicationId = remoteMessage.data["medicationId"] ?: "Default Title"
            val medicationName = remoteMessage.data["medicationName"] ?: "No message"
            val reminderTime = remoteMessage.data["reminderTime"] ?: "No time"

            // ตรวจสอบว่าเวลาปัจจุบันตรงกับเวลาที่ตั้งไว้
            val currentTime = Calendar.getInstance().timeInMillis
            val reminderTimeMillis = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .parse(reminderTime)?.time ?: 0

            if (reminderTimeMillis <= currentTime) {
                // ถ้าเวลาการทานยาใกล้เคียงหรือเท่ากับเวลาปัจจุบัน แสดงการแจ้งเตือน
                generateNotification(medicationName, reminderTime)
            }
        }
    }

    // ฟังก์ชันสำหรับการสร้าง Notification
    private fun generateNotification(title: String, message: String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this,drugActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // ใช้ FLAG_IMMUTABLE สำหรับ PendingIntent ในเวอร์ชันใหม่ๆ
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)

        // ตรวจสอบ Android version เพื่อสร้าง Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        //channel id , channel name
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo2)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0,builder.build())
    }
    // สร้าง Notification Channel สำหรับ Android 8.0 ขึ้นไป
    @RequiresApi(VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {

    }
}