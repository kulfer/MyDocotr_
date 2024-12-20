package com.MyPobmor

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve the medication name from the intent
        val medicationName = intent.getStringExtra("medicationName")

        // Check permission before creating the notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            createNotification(context, medicationName)
        }
    }

    private fun createNotification(context: Context, medicationName: String?) {
        // Ensure that the notification channel exists
        val notificationManager = NotificationManagerCompat.from(context)

        // Create the notification
        val notificationId = 1 // Unique ID for the notification
        val builder = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(R.drawable.baseline_notifications_24) // Use your own icon here
            .setContentTitle("Medication Reminder")
            .setContentText("It's time to take your medication: $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, builder.build())
    }
}