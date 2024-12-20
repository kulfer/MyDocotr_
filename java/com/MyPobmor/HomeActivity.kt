package com.MyPobmor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivity : AppCompatActivity() {

    private lateinit var proimg: CircleImageView
    private val PROFILE_UPDATE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val homeFragment = homeFragment()
        val userName = intent.getStringExtra("USER_NAME")
        val bundle = Bundle()
        bundle.putString("USER_NAME", userName)
        homeFragment.arguments = bundle


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(homeFragment())
                    true
                }
                R.id.nav_appointments -> {
                    loadFragment(doctorFragment())
                    true
                }
                R.id.nav_diseases -> {
                    loadFragment(emergencyFragment())
                    true
                }
                R.id.nav_medical_history -> {
                    loadFragment(notificatiosFragment())
                    true
                }
                R.id.nav_checkup_history -> {
                    loadFragment(settinggFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(homeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}