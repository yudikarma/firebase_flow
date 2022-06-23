package com.wisa.eOurPetshop.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dekape.core.utils.parseDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.alert
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*


object Utils {


    const val DATE_FORMAT = "yyyy-MM-dd"
    const val DATE_FORMAT_FULL = "dd-MM-yyyy HH:mm"
    const val DATE_FORMAT_YEAR = "yyyy"
    const val DATE_FORMAT_MONTH = "MM"
    const val DATE_FORMAT_MONTH_NAME = "MMMM"
    const val DATE_FORMAT_DAY = "dd"
    const val MUTATION_DATE_FORMAT = "dd/MM/yy"

    fun getCurrentDate(patern:String = DATE_FORMAT):String{
        var result = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(patern)
            result = current.format(formatter)
        } else {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat(patern)
            result  = df.format(c)
        }
        return  result
    }


    fun getTimeStampFromDate(cal:Calendar):Double{
        return  cal.timeInMillis.toDouble()
    }


    fun getTimeStampFromDateStr(cal:String?):Long{
        val formater = SimpleDateFormat(MUTATION_DATE_FORMAT, Locale.US)
        val date = formater.parse(cal)
        val output = date.time / 1000L
        val str = java.lang.Long.toString(output)
        return date.time
    }


    val currentDate: String
        get() = SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date())

    val currentYears: String
        get() = SimpleDateFormat(DATE_FORMAT_YEAR, Locale.US).format(Date())

    val currentMonth: String
        get() = SimpleDateFormat(DATE_FORMAT_MONTH, Locale.US).format(Date())

    val currentMonthName: String
        get() = SimpleDateFormat(DATE_FORMAT_MONTH, Locale.US).format(Date())

    val currentday: String
        get() = SimpleDateFormat(DATE_FORMAT_DAY, Locale.US).format(Date())


    fun generateKeywords(name: String): List<String> {
        val keywords = mutableListOf<String>()
        for (i in 0 until name.length) {
            for (j in (i+1)..name.length) {
                if (i < 1)
                    keywords.add(name.toLowerCase().slice(i until j))
            }
        }
        return keywords
    }

    fun Long.getDayCount(): Long {
        val secsInMils = 1_000L
        val minInMils = secsInMils * 60L
        val hourInMils = minInMils * 60L
        val dayInMils = hourInMils * 24L
        return this / dayInMils
        /*val hours = (this % dayInMils) / hourInMils
        val minutes = (this % dayInMils % hourInMils) / minInMils
        val secs = (this % dayInMils % hourInMils % minInMils) / secsInMils
        //val millis = this % dayInMils % hourInMils % minInMils % secsInMils
        val stringBuilder =  StringBuilder()
            // .append(days.toString().padStart(2, '0')).append("d ")
            .append(hours.toString().padStart(2, '0')).append(":")
            .append(minutes.toString().padStart(2, '0')).append(":")
            .append(secs.toString().padStart(2, '0')).append("")
            //.append(millis.toString().padStart(3, '0'))
            .toString()

        return stringBuilder*/
    }



}

fun String.toDate(currentFormat: String): Date {
    return try {
        if (this.isEmpty()) {
            Date()
        } else {
            SimpleDateFormat(currentFormat, Locale.getDefault()).parse(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        //If It can not be parsed, return today's date instead of null. So return value of this method does not create null pointer exception.
        Date()
    }
}

fun Date?.localeDateFromTimestampFirebase(newFormatPattern:String = "dd-MMMM-yyyy HH:MM") : String ?{
   try {
       val anu = SimpleDateFormat("MMM dd, yyyy h:mm:ss a",Locale.US).format(this)
      return anu.parseDate("MMM dd, yyyy h:mm:ss a",newFormatPattern)
   }catch (e : Exception){
    return null
   }
}

fun Date?.localeDateFromTimestampFirebase2(newFormatPattern:String = "dd MMMM yyyy HH:mm") : String ?{
    /*
    * Thu Jun 02 11:47:04 GMT+07:00 2022
    * */
   try {
       val anu = SimpleDateFormat("EEEE MMM dd HH:mm:ss zzzz yyyy",Locale.US).format(this)
      return anu.parseDate("EEEE MMM dd HH:mm:ss zzzz yyyy",newFormatPattern)
   }catch (e : Exception){
    return null
   }
}

fun Context.showAlert(message: String) {
    val dialog = alert(message) { positiveButton("OK") {} }.build()
    dialog.setCancelable(false)
    dialog.show()
}

fun Context.showAlert(title: String?, message: String) {
    val dialog = alert(message, title) { positiveButton("OK") {} }.build()
    dialog.setCancelable(false)
    dialog.show()
}

fun Context.showAlert(
    title: String?,
    message: String,
    onClicked: (dialog: DialogInterface) -> Unit
) {
    val dialog = alert(message, title) { positiveButton("OK", onClicked) }.build()
    dialog.setCancelable(false)
    dialog.show()
}

fun Context.showConfirmation(
    title: String?,
    message: String,
    onClicked: (dialog: DialogInterface) -> Unit
) {
    val dialog = alert(message, title) {
        positiveButton("YES", onClicked)
        negativeButton("NO") {}
    }.build()

    dialog.setCancelable(false)
    dialog.show()
}

fun AppCompatActivity.goToPermissionSetting(requestCode: Int, message: String) {
    val dialog = alert(message) {
        positiveButton("YES") {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, requestCode)
        }

        negativeButton("NO") { finish() }
    }.build()

    dialog.setCancelable(false)
    dialog.show()
}

/**
 * Activity Caller
 */
class ActivityCaller(
    val fragment: Fragment? = null,
    val activity: Activity? = null,
    val deprecatedFragment: android.app.Fragment? = null
) {
    val context: Context
        get() = (activity ?: fragment?.activity ?: deprecatedFragment?.activity)!!

    fun startActivityForResult(intent: Intent, chooser: Int) {
        activity?.startActivityForResult(intent, chooser)
            ?: fragment?.startActivityForResult(intent, chooser)
            ?: deprecatedFragment?.startActivityForResult(intent, chooser)
    }
    fun startActivity(intent: Intent) {
        activity?.startActivity(intent)
            ?: fragment?.startActivity(intent)
            ?: deprecatedFragment?.startActivity(intent)
    }
}

fun getCallerActivity(caller: Any): ActivityCaller? = when (caller) {
    is Activity -> ActivityCaller(activity = caller)
    is Fragment -> ActivityCaller(fragment = caller)
    is android.app.Fragment -> ActivityCaller(
        deprecatedFragment = caller
    )
    else -> null
}


fun View.onClick(onNext: (() -> Unit)) {
    return setOnClickListener {
        onNext.invoke()
    }
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}


//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val gson = Gson()
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}
fun Long.parseDuration(): String {
    val secsInMils = 1_000L
    val minInMils = secsInMils * 60L
    val hourInMils = minInMils * 60L
    val dayInMils = hourInMils * 24L
    val days = this / dayInMils
    val hours = (this % dayInMils) / hourInMils
    val minutes = (this % dayInMils % hourInMils) / minInMils
    val secs = (this % dayInMils % hourInMils % minInMils) / secsInMils
    //val millis = this % dayInMils % hourInMils % minInMils % secsInMils
    val stringBuilder =  StringBuilder()
        // .append(days.toString().padStart(2, '0')).append("d ")
        .append(hours.toString().padStart(2, '0')).append(":")
        .append(minutes.toString().padStart(2, '0')).append(":")
        .append(secs.toString().padStart(2, '0')).append("")
        //.append(millis.toString().padStart(3, '0'))
        .toString()

    return stringBuilder
}

fun Parcel.writeDate(date: Date?) {
    writeLong(date?.time ?: -1)
}

fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}
