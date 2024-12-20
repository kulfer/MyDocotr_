package com.MyPobmor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicationAdapter(
    private var medicationList: List<MedicationData>,
    private var onMedicationClick: ((MedicationData) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_ITEM = 1
    }

    // ViewHolder สำหรับรายการยา
    class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicationName: TextView = itemView.findViewById(R.id.medicationName)
        val medicationQuantity: TextView = itemView.findViewById(R.id.medicationQuantity)
        val medicationType: TextView = itemView.findViewById(R.id.medicationType)
        val medicationTime: TextView = itemView.findViewById(R.id.medicationTime)
        val medicationDate: TextView = itemView.findViewById(R.id.medicationDate)
        val medicationNotes: TextView = itemView.findViewById(R.id.medicationNotes)
    }

    // ViewHolder สำหรับกรณีที่ไม่มีข้อมูล
    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Handle different types of views (Empty or Item)
    override fun getItemViewType(position: Int): Int {
        return if (medicationList.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    // Create appropriate ViewHolder based on the type (Empty or Item)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_EMPTY) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.empty_layout, parent, false)
            EmptyViewHolder(view)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.drug_layout, parent, false)
            MedicationViewHolder(itemView)
        }
    }

    // Bind data to the appropriate view holder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MedicationViewHolder) {
            val medication = medicationList[position]
            holder.medicationName.text = medication.medicineName
            holder.medicationQuantity.text = medication.quantity
            holder.medicationType.text = medication.medicineType
            holder.medicationTime.text = "เวลา: ${medication.selectedTime}"
            holder.medicationDate.text = "วันที่: ${medication.selectedDate}"
            holder.medicationNotes.text = "หมายเหตุ: ${medication.notes}"

            // Handle click on medication item
            holder.itemView.setOnClickListener {
                onMedicationClick?.invoke(medication)
            }
        }
    }

    // Get the item count, ensuring the empty view is counted properly
    override fun getItemCount(): Int {
        return if (medicationList.isEmpty()) 1 else medicationList.size
    }

    // To update the medication list dynamically (notify adapter)
    fun updateMedicationList(newList: List<MedicationData>) {
        medicationList = newList
        notifyDataSetChanged()
    }
    // ฟังก์ชันในการตั้งค่าการคลิก
    fun setOnItemClickListener(onClickListener: (MedicationData) -> Unit) {
        this.onMedicationClick = onClickListener
    }
}