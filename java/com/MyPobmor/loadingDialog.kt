package com.MyPobmor

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater

class loadingDialog (private val activity: Activity) {

    private lateinit var alertDialog: AlertDialog

    fun startLoadingDialog() {
        // สร้าง AlertDialog Builder
        val builder = AlertDialog.Builder(activity)

        // ตั้งค่า View จาก layout custom_dialog
        val inflater: LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))

        // ตั้งค่าให้สามารถยกเลิกการแสดง Dialog ได้
        builder.setCancelable(true)

        // สร้าง AlertDialog และแสดง
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismissDialog() {
        // ปิด Dialog หากสร้างแล้ว
        if (::alertDialog.isInitialized) {
            alertDialog.dismiss()
        }
    }
}