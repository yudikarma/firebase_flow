package dev.shreyaspatil.firebase.coroutines.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeneralModel(
    var id:Int=0,
    var name:String = ""
) : Parcelable