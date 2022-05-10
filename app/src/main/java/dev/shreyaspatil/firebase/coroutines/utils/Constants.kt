package dev.shreyaspatil.firebase.coroutines.utils

import com.bumptech.glide.request.RequestOptions
import dev.shreyaspatil.firebase.coroutines.R

object Constants {

    val placeholderRequest = RequestOptions.placeholderOf(R.drawable.default_avatar)
    const val COLLECTION_USERS = "users"
    const val COLLECTION_ORDERS = "orders"
    const val COLLECTION_KANDANG = "kandang"
    const val COLLECTION_PETS = "pets"
    const val COLLECTION_aktivitas = "aktivitas"


    const val RULE_USER = "USER"
    const val RULE_ADMIN = "ADMIN"
    const val STATUS_ORDER_PROGRESS = "PROGRESS"
    const val STATUS_ORDER_DONE = "DONE"


    const val IS_LOGOUT = "isLogout"

}