package com.wisa.eOurPetshop.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Aktivitas(
    var id:String="",
    @ServerTimestamp
    var tanggal: Date? =null,
    var caption:String = "",
    var urlImg:String = "",
) : Parcelable
