package dev.shreyaspatil.firebase.coroutines.model

import android.os.Parcelable
import dev.shreyaspatil.firebase.coroutines.utils.localeDateFromTimestampFirebase
import kotlinx.android.parcel.Parcelize
import java.util.*
/*
* pdfTextView.setText("NO")
        pdfTextView.setText("Tanggal Penitipan")
        pdfTextView.setText("Tanggal Keluar")
        pdfTextView.setText("Nama Pelanggan")
        pdfTextView.setText("No Handphone ")
        pdfTextView.setText("Jenis Peliharaan ")
        pdfTextView.setText("Catatan medis ")
        pdfTextView.setText("Paket ")
        pdfTextView.setText("Harga")
        pdfTextView.setText("Status Order")*/
@Parcelize
data class PdfOrders(
    var no:String="",
    var timeCheckIn: String = "",
    var timeCheckOut: String = "",
    var namaPelangan :String = "",
    var namaHewan :String = "",
    val noHpPengirim  :String = "",
    val catatan :String = "",
    val namaPaket :String = "",
    var price:String = "",
    var status:String = "Progress"
) : Parcelable {

}