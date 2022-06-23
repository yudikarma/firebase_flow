package com.wisa.eOurPetshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pets(
    var uid:String="",
    var name:String = "",
    var urlPict:String = "",
    var userUid : String = "",
    var urlActivity : String = "",
    var lastUpdate:String = "",
    var gender : String = "",
    var kindAnimal :String = "",
) : Parcelable