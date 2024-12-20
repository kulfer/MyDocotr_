package com.MyPobmor

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

const val medicName = "medicName"
const val medicTime = "medicTime"
const val medicDate = "medicDate"
const val medicQuantity = "medicQuantity"
const val medicType = "medicType"
const val medicNote = "medicNote"

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val notification = NotificationCompat.Builder(context!!, channelID)
            .setSmallIcon(R.drawable.logo2)
            .setContentTitle(intent!!.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setPriority(NotificationCompat.PRIORITY_HIGH) // ตั้งความสำคัญ
            .setAutoCancel(true) // ปิด notification เมื่อผู้ใช้กด
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)

    }

}