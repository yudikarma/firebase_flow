package dev.shreyaspatil.firebase.coroutines.utils.ekstension

import com.google.firebase.firestore.FirebaseFirestore
import dev.shreyaspatil.firebase.coroutines.utils.Constants

fun FirebaseFirestore.user() = collection(Constants.COLLECTION_USERS)
fun FirebaseFirestore.order() = collection(Constants.COLLECTION_ORDERS)
fun FirebaseFirestore.kandang() = collection(Constants.COLLECTION_KANDANG)
fun FirebaseFirestore.pets() = collection(Constants.COLLECTION_PETS)
fun FirebaseFirestore.aktivitas() = collection(Constants.COLLECTION_aktivitas)
//fun FirebaseFirestore.publicProfileDoc(uid: String) = publicProfile().document(uid)