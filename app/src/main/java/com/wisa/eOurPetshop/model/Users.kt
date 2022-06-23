package com.wisa.eOurPetshop.model

import android.os.Parcelable
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
