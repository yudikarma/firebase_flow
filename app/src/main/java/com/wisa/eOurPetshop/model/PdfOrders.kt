package com.wisa.eOurPetshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PdfOrders(
    var no:String="",
    var timeCheckIn: String = "",
    var timeCheckOut: String = "",
    var namaPelangan :String = "",
    var namaHewan :String = "",
    val noHpPengirim  :String = "",
    val catatan :String = "",
    val namaPaket :String = "",
    var price:String = "",
    var status:String = "Progress"
) : Parcelable {

}