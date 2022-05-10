package dev.shreyaspatil.firebase.coroutines.model

import android.os.Parcel
import android.os.Parcelable
import com.dekape.core.utils.parseDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import dev.shreyaspatil.firebase.coroutines.utils.Utils
import dev.shreyaspatil.firebase.coroutines.utils.localeDateFromTimestampFirebase
import dev.shreyaspatil.firebase.coroutines.utils.readDate
import dev.shreyaspatil.firebase.coroutines.utils.writeDate
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
