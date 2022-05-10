package dev.shreyaspatil.firebase.coroutines.ui.maps
import android.app.Activity.RESULT_OK
import android.content.Intent

object PickMaps {
    const val INTENT_PICKER = 7911
    const val EXTRA_LAT = "lat"
    const val EXTRA_LONG = "lng"
    const val EXTRA_ADRESS = "address"
    fun handleActivityResult(
        request: Int, result: Int, data: Intent?,
        callback: (lat: Double?, lng: Double?,address :String?) -> Unit
        ) {
        if (request == INTENT_PICKER && result == RESULT_OK) {
            callback.invoke(
                data?.getDoubleExtra(EXTRA_LAT, 0.0),
                data?.getDoubleExtra(EXTRA_LONG, 0.0),
                data?.getStringExtra(EXTRA_ADRESS)

            )
        }
    }
}
