package com.MyPobmor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class helpsupportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_helpsupport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etProblem = view.findViewById<EditText>(R.id.etProblem)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val backButton = view.findViewById<ImageView>(R.id.backbutton)

        // ฟังก์ชันปุ่มย้อนกลับ
        backButton.setOnClickListener {
            // สามารถใช้ FragmentManager เพื่อย้อนกลับไปยัง Fragment ก่อนหน้า
            requireActivity().onBackPressed()
        }

        // ฟังก์ชันปุ่มส่ง
        btnSubmit.setOnClickListener {
            Toast.makeText(requireContext(), "ทางเราได้ทราบถึงปัญหาของคุณแล้ว", Toast.LENGTH_SHORT).show()
        }
    }
}