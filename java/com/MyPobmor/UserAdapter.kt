package com.MyPobmor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context,
                  val userList: ArrayList<User>,
                  private val fragmentActivity: FragmentActivity
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val mDbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("user")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.doctor_layout, parent, false)
        return UserViewHolder(view)

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        // ตั้งค่าข้อมูลลงใน TextView
        holder.textname.text = currentUser.name
        holder.specialtext.text = currentUser.specialty

        // ดึงข้อมูลรูปโปรไฟล์จาก Firebase
        mDbRef.child(currentUser.uid.toString()).child("profileImage")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageBase64 = snapshot.getValue(String::class.java)
                    if (!profileImageBase64.isNullOrEmpty()) {
                        try {
                            val decodedBytes = android.util.Base64.decode(profileImageBase64, android.util.Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            holder.profileImageView1.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            holder.profileImageView1.setImageResource(R.drawable.profile2) // รูปดีฟอลต์
                        }
                    } else {
                        holder.profileImageView1.setImageResource(R.drawable.profile2) // รูปดีฟอลต์
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // จัดการกับข้อผิดพลาด
                }
            })

        // เมื่อกดที่ item จะเปิด ChatFragment พร้อมส่งข้อมูล
        holder.itemView.setOnClickListener {
            // สร้าง instance ของ ChatFragment และส่งข้อมูลผ่าน Bundle
            val chatFragment = ChatFragment()
            val args = Bundle().apply {
                putString("name", currentUser.name)
                putString("uid", currentUser.uid)
                putString("profileImage", currentUser.profileImage)  // เพิ่มข้อมูลรูปโปรไฟล์
            }
            chatFragment.arguments = args

            // ใช้ FragmentManager จาก fragmentActivity เพื่อเปิด Fragment
            fragmentActivity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container, chatFragment
                )  // ตรวจสอบ ID ของ fragment_container ใน layout ของคุณ
                .addToBackStack(null)
                .commit()
        }
        }
        class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // เชื่อมต่อ view ต่าง ๆ กับ ViewHolder
            val textname = itemView.findViewById<TextView>(R.id.nameTextView4)
            val specialtext = itemView.findViewById<TextView>(R.id.specialtyTextView4)
            val profileImageView1: CircleImageView = itemView.findViewById(R.id.profile_image4) // ImageView สำหรับรูปโปรไฟล์

        }
}