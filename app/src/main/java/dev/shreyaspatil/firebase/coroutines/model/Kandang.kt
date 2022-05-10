package dev.shreyaspatil.firebase.coroutines.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kandang(
    var uid:String="",
    var uidHewan:String="",
    var name:String = "",
    var capacity:Int = 10,
    var booked:Int = 0,
    var price : Int = 0,
) : Parcelable
