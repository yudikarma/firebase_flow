package com.wisa.eOurPetshop.model

import android.os.Parcelable
import com.wisa.eOurPetshop.utils.Utils
import com.wisa.eOurPetshop.utils.localeDateFromTimestampFirebase
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Orders(
    var uid:String="",
    var uidUsers:String="",
    var uidPaket:String="",
    var shipAddress:String="",
    var shipLat:String="",
    var shipLng:String="",
    var price:String = "",
    var timeCheckIn:Date? = null,
    var timeCheckOut:Date? = null,
    var namaPeliharaan :String = "",
    var jenisPeliharaan:String = "",
    var umurPeliharaan :String = "",
    var fotoPeliharaan :String = "",
    var tandaSpesialPeliharaan :String = "",
    val catatan :String = "",

    val noHpPengirim  :String = "",
    var namaPelangan :String = "",
    val namaPaket :String = "",
    var idAktivitas:String = "",
    var status:String = "Progress",
    var keyword:List<String> = emptyList()
) : Parcelable{

    val localeDateCheckIn get() = timeCheckIn?.localeDateFromTimestampFirebase()
    val localeDateNoMonthNameCheckIn get() = timeCheckIn?.localeDateFromTimestampFirebase(Utils.MUTATION_DATE_FORMAT)
    val localeDateCheckOut get() = timeCheckOut?.localeDateFromTimestampFirebase()
    val localeDateddNoMonthNameCheckOut get() = timeCheckOut?.localeDateFromTimestampFirebase(Utils.MUTATION_DATE_FORMAT)


}
