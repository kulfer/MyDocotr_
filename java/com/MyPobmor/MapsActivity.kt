package com.MyPobmor

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import android.Manifest
import android.app.VoiceInteractor
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.MyPobmor.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var client = OkHttpClient()
    private lateinit var searchView: SearchView
    private val apiKey = "AIzaSyBY6xg0Rlyx6isWaq4GDEbu-jX5_qHUKCM"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchView = findViewById(R.id.search_bar)
        setupSearchView()


        //สร้าง instance ของ fusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //กลับหน้าที่แล้ว
        val backImg = findViewById<ImageView>(R.id.backimg).setOnClickListener {
            finish()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val lnMapMe = findViewById<LinearLayout>(R.id.lnMapme).setOnClickListener {
            moveToCurrentLocation()
        }

    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchPlaces(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) ==  PackageManager.PERMISSION_GRANTED
            ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
                    findNearbyHospital(it) // ค้นหาโรงพยาบาลใกล้เคียง
                }
            }
        } else {
            requestLocationPermission()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //ครวจสอบและขออนุญาติการเข้าถึงตำแหน่ง
        if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        // ตรวจสอบสิทธิ์การเข้าถึงตำแหน่งอีกครั้ง
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let{
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                findNearbyHospital(location)
            }
        }
    }

    //ฟังค์ชั่นการค้นหา
    private fun searchPlaces(query: String) {
        // ตรวจสอบสิทธิ์การเข้าถึงตำแหน่งอีกครั้ง
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // รับตำแหน่งปัจจุบัน
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val locationString = "${it.latitude},${it.longitude}"
                val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$query+โรงพยาบาล|คลินิก&key=$apiKey"
                val request = Request.Builder().url(url).build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: okhttp3.Call, response: Response) {
                        if (response.isSuccessful) {
                            val json = JSONObject(response.body?.string() ?: "")
                            val results = json.getJSONArray("results")
                            runOnUiThread {
                                mMap.clear() // ลบ Marker ที่มีอยู่ก่อนหน้านี้
                                for (i in 0 until results.length()) {
                                    val place = results.getJSONObject(i)
                                    val name = place.getString("name")
                                    val lat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                                    val lng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                                    val placeLatLng = LatLng(lat, lng)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(placeLatLng)
                                            .title(name)

                                    )
                                }
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MapsActivity,
                                    "เกิดข้อผิดพลาดในการค้นหา",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
        }
    }

    private fun findNearbyHospital(location: Location) {
        val locationString = "${location.latitude},${location.longitude}"
        val radius = 20000 // 20 กม.
        val type = "hospital"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$radius&type=$type&key=$apiKey"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    val results = json.getJSONArray("results")
                    runOnUiThread {
                        mMap.clear()

                        if (results.length() == 0) {
                            Toast.makeText(this@MapsActivity, "ไม่พบโรงพยาบาลใกล้เคียง", Toast.LENGTH_SHORT).show()
                        }
                    }
                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val name = place.getString("name")
                        val lat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                        val lng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                        val placeLatLng = LatLng(lat, lng)
                        runOnUiThread {
                            mMap.addMarker(MarkerOptions()
                                .position(placeLatLng)
                                .title(name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                        }
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }
    //เมื่อผู้ใช้ไม่ไ้ให้สิทธิ์ในการเข้าถึงแผนที่
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}