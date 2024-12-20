package com.MyPobmor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView


class doctorFragment : Fragment() {

    private lateinit var userrecyclerview: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var filteredList: ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_doctor, container, false)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        // กำหนดค่า RecyclerView
        userrecyclerview = view.findViewById(R.id.userRecyclerView)
        userrecyclerview.layoutManager = LinearLayoutManager(context)  // ตั้งค่า LayoutManager


        //กรอกรายการชื่อแพทย์
        userList = ArrayList()
        filteredList = ArrayList()

        // สร้างรายการผู้ใช้ (หรือแพทย์)
        userList = ArrayList()

        // ตั้งค่า adapter
        adapter = UserAdapter(requireContext(), filteredList, requireActivity())
        userrecyclerview.adapter = adapter

        mDbRef.child("user").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                filteredList.clear()
                filteredList.addAll(userList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val searchView = view.findViewById<SearchView>(R.id.search_Doctor)
        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText) // เรียกฟังก์ชันกรอง
                return true
            }
        })

        return view
    }
    private fun filter(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(userList) // ถ้าไม่มีคำค้นหา เพิ่มข้อมูลทั้งหมด
        } else {
            for (user in userList) {
                if (user.name?.contains(query, ignoreCase = true) == true) { // ตรวจสอบชื่อ
                    filteredList.add(user)
                }
            }
        }
        adapter.notifyDataSetChanged() // อัปเดตรายการใน adapter
    }
}