package com.MyPobmor

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class MedicationData(

    var medicineName: String? = null,
    var quantity: String? = null,
    var medicineType: String? = null,
    var selectedTime: String? = null,
    var selectedDate: String? = null,
    var notes: String? = null,
    val id: String = "" // เพิ่ม id ที่นี่

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(medicineName)
        parcel.writeString(quantity)
        parcel.writeString(medicineType)
        parcel.writeString(selectedTime)
        parcel.writeString(selectedDate)
        parcel.writeString(notes)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MedicationData> {
        override fun createFromParcel(parcel: Parcel): MedicationData {
            return MedicationData(parcel)
        }

        override fun newArray(size: Int): Array<MedicationData?> {
            return arrayOfNulls(size)
        }
    }
}
