package dev.shreyaspatil.firebase.coroutines.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data  class MapsModel(var latitude:Double? = 0.0,var longitude:Double? = 0.0):Parcelable