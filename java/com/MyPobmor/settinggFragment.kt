package com.MyPobmor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class settinggFragment : Fragment() {

    private lateinit var lnNoti: LinearLayout
    private lateinit var lnAbout: LinearLayout
    private lateinit var lnHelp: LinearLayout
    private lateinit var lnLogout: LinearLayout
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settingg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        // กำหนดค่า LinearLayouts
        lnNoti = view.findViewById(R.id.lnnoti)
        lnAbout = view.findViewById(R.id.lnge)
        lnHelp = view.findViewById(R.id.lnhelp)
        lnLogout = view.findViewById(R.id.lnlogout)
        val backButton = view.findViewById<ImageView>(R.id.backbutton)

        // ฟังก์ชันปุ่มย้อนกลับ
        backButton.setOnClickListener {
            // สามารถใช้ FragmentManager เพื่อย้อนกลับไปยัง Fragment ก่อนหน้า
            requireActivity().onBackPressed()
        }

        // ตั้งค่าการคลิกไปยัง fragment ต่างๆ
        lnNoti.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, setnotiFragment())
                .addToBackStack(null)
                .commit()
        }

        lnAbout.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, aboutFragment())
                .addToBackStack(null)
                .commit()
        }

        lnHelp.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, helpsupportFragment())
                .addToBackStack(null)
                .commit()
        }

        lnLogout.setOnClickListener {
            showLogoutDialog()
        }

        // ค้นหาด้วย EditText
        val searchSettings = view.findViewById<EditText>(R.id.search_settings)
        searchSettings.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase()
                filterSettings(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // ฟังก์ชันสำหรับกรองการตั้งค่าตามคำค้นหา
    private fun filterSettings(query: String) {
        lnNoti.isVisible = "แจ้งเตือน".contains(query) || "notification".contains(query)
        lnAbout.isVisible = "เกี่ยวกับแอพ".contains(query) || "about".contains(query)
        lnHelp.isVisible = "ช่วยเหลือ".contains(query) || "support".contains(query)
        lnLogout.isVisible = "ออกจากระบบ".contains(query) || "logout".contains(query)
    }

    // ฟังก์ชันแสดง AlertDialog สำหรับยืนยันการออกจากระบบ
    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("ออกจากระบบ")
            .setMessage("ยืนยันการออกจากระบบหรือไม่?")
            .setPositiveButton("ยืนยัน") { dialog, _ ->
                dialog.dismiss()

                logout()

            }
            .setNegativeButton("ยกเลิก") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        // ออกจากระบบ Firebase
        mAuth.signOut()
        // ลบสถานะการเข้าสู่ระบบใน SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("isLoggedIn", false).apply()
        // กลับไปหน้า LoginActivity
        val intent = Intent(requireActivity(), loginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
    }
}