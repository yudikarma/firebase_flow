package dev.shreyaspatil.firebase.coroutines.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    var uid:String="",
    var email:String = "",
    var password:String = "",
    var name:String = "",
    var urlPict:String = "",
    var rule:String = "",
    var nohp :String = "",
    var petsUid : String = "",
    var uDocId : String = "",
) : Parcelable
