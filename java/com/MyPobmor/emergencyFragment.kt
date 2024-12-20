package com.MyPobmor

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class emergencyFragment : Fragment() {

    private lateinit var callAmbulanceButton: CardView
    private lateinit var mapInfo: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_emergency, container, false)

        // Initialize views
        callAmbulanceButton = view.findViewById(R.id.callam) // Replace with your actual CardView ID
        mapInfo = view.findViewById(R.id.mapinto) // Replace with your actual Button ID

        // Set up click listeners
        callAmbulanceButton.setOnClickListener {
            makePhoneCall()
        }

        mapInfo.setOnClickListener {
            // Create an Intent to start MapsActivity
            val intent = Intent(activity, MapsActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun makePhoneCall() {
        val phoneNumber = "1669" // เบอร์โทรศัพท์ที่ต้องการโทร
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
        } else {
            // Permission has already been granted
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with phone call
                makePhoneCall() // Re-attempt the phone call
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Permission denied to make phone calls", Toast.LENGTH_SHORT).show()
                // Optionally, direct the user to app settings
                // openAppSettings()
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireActivity().packageName, null)
        }
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_PHONE_CALL = 1
    }
}