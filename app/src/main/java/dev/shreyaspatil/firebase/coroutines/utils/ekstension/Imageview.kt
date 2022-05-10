package dev.shreyaspatil.firebase.coroutines.utils.ekstension

import android.widget.ImageView
import com.bumptech.glide.Glide
import dev.shreyaspatil.firebase.coroutines.utils.Constants
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil


inline fun ImageView.loadFromUrl(url:String){
    Glide.with(this)
        .setDefaultRequestOptions(Constants.placeholderRequest)
        .load(url).into(this)


}

fun String?.toPriceFormat(): String = try {
    if (this.isNullOrEmpty()) "Rp 0"
    else {
        val inp = this.toDouble()
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        "Rp " + numberFormat.format(ceil(inp).toLong())
    }
} catch (e: Exception) {
    "Rp 0"
}