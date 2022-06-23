package com.wisa.eOurPetshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeneralModel(
    var id:Int=0,
    var name:String = ""
) : Parcelable