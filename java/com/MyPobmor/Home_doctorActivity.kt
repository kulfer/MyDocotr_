package com.MyPobmor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView

class Home_doctorActivity : AppCompatActivity() {

    private lateinit var proimg: CircleImageView
    private val PROFILE_UPDATE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_doctor)

        val home_doctorFragment = Home_doctorFragment()
        val userName = intent.getStringExtra("USER_NAME")
        val bundle = Bundle()
        bundle.putString("USER_NAME", userName)
        home_doctorFragment.arguments = bundle


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
