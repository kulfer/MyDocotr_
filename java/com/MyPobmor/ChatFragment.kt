package com.MyPobmor

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MyPobmor.R
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView


class ChatFragment : Fragment() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageinput: EditText
    private lateinit var sentButton: ImageButton
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var profileImageView: CircleImageView

    var receiverRoom: String? = null
    var senderRooom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val name = arguments?.getString("name")
        val receiverUid = arguments?.getString("uid")
        val titleNameTextView = view.findViewById<TextView>(R.id.title_name)
        titleNameTextView.text = name // ตั้งชื่อผู้ใช้ใน title_name
        profileImageView = view.findViewById(R.id.profile_image2)


        val backImg = view.findViewById<ImageView>(R.id.backimg)
        backImg.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRooom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        // Find views
        messageinput = view.findViewById(R.id.message_input)
        messageRecyclerView = view.findViewById(R.id.chatRe)
        sentButton = view.findViewById(R.id.send_button)
        messageList = ArrayList()


        // ตั้งค่า adapter และ LayoutManager สำหรับ RecyclerView
        messageAdapter = MessageAdapter(requireContext(), messageList)
        messageRecyclerView.layoutManager = LinearLayoutManager(context)
        messageRecyclerView.adapter = messageAdapter

        // แปลง base64 string เป็น Bitmap และแสดงใน CircleImageView
        val profileImage = arguments?.getString("profileImage") // รับค่าจาก arguments
        if (!profileImage.isNullOrEmpty()) {
            try {
                // แปลง Base64 string เป็น Bitmap
                val decodedBytes = android.util.Base64.decode(profileImage, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                profileImageView.setImageBitmap(bitmap) // แสดงผลรูปโปรไฟล์
            } catch (e: Exception) {
                e.printStackTrace()
                profileImageView.setImageResource(R.drawable.profile2) // ใช้รูปดีฟอลต์ในกรณีที่เกิดข้อผิดพลาด
            }
        } else {
            profileImageView.setImageResource(R.drawable.profile2) // ใช้รูปดีฟอลต์ในกรณีที่ไม่มีรูปโปรไฟล์
        }

        // ดึงข้อความจาก Firebase และแสดงใน RecyclerView
        mDbRef.child("chats").child(senderRooom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    // แจ้งให้ adapter ทราบว่าข้อมูลเปลี่ยนแปลงแล้ว
                    messageAdapter.notifyDataSetChanged()
                    messageRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    // จัดการกับข้อผิดพลาด
                }
            })

        //add message to database
        sentButton.setOnClickListener {
            val message = messageinput.text.toString()
            if (message.isNotEmpty()) {
                val messageObject = Message(message, senderUid)

                // เพิ่มข้อความไปยัง Firebase
                mDbRef.child("chats").child(senderRooom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                // ล้างข้อความในช่องกรอกหลังจากส่งแล้ว
                messageinput.text.clear()
            }
        }
        return view
    }
    override fun onResume() {
        super.onResume()
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.visibility = View.VISIBLE
    }
}

