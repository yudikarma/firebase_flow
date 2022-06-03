package dev.shreyaspatil.firebase.coroutines.repository

import com.dekape.core.utils.logD
import com.dekape.core.utils.toCalendar
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.shreyaspatil.firebase.coroutines.model.Aktivitas
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.utils.Constants
import dev.shreyaspatil.firebase.coroutines.utils.State
import dev.shreyaspatil.firebase.coroutines.utils.Utils
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.aktivitas
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.order
import dev.shreyaspatil.firebase.coroutines.utils.serializeToMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class OrdersRepository {
    private val mOrdersCollection = FirebaseFirestore.getInstance().order()
    private val mAktivitasCollection = FirebaseFirestore.getInstance().aktivitas()
    private  var imageOrdersReference: StorageReference? = null
    private  var storage: FirebaseStorage ? = null


    init {
        //init firebase storage
        storage = FirebaseStorage.getInstance()
        storage?.let {
            imageOrdersReference = it.reference.child(Constants.COLLECTION_ORDERS+"/")
        }
    }

    fun getOrderStorageRef() : StorageReference? = imageOrdersReference

    /**
     * Returns Flow of [State] which retrieves all posts from cloud firestore collection.
     */
    fun getAllOrders() = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mOrdersCollection.get().await()
        val orders = snapshot.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllOrdersFilterDate(dateFrom:Date,dateTo:Date) = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())

        logD("date from is ${dateFrom}")
        logD("date to is ${dateTo}")


        val snapshot = mOrdersCollection
            .whereGreaterThanOrEqualTo("timeCheckIn", dateFrom)
            .whereLessThanOrEqualTo("timeCheckIn",dateTo)

        val task = Tasks.await(snapshot.get())
        val orders = task.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllOrdersFilterUser(uidUser:String) = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mOrdersCollection
            .whereEqualTo("uidUsers", uidUser)

        val task = Tasks.await(snapshot.get())
        val orders = task.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllOrdersFilterUserAndKeyword(uidUser:String,keyword: String) = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mOrdersCollection
            .whereEqualTo("uidUsers", uidUser)
            .whereArrayContains("keyword",keyword.trim()).limit(20)

        val task = Tasks.await(snapshot.get())
        val orders = task.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllOrdersFilterKewword(keyword :String) = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())



        val snapshot = mOrdersCollection
            .whereArrayContains("keyword",keyword.trim()).limit(20)

        val task = Tasks.await(snapshot.get())
        val orders = task.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllOrdersFilterUserAndDate(uidUser:String,dateFrom:Date,dateTo:Date) = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mOrdersCollection
            .whereEqualTo("uidUsers", uidUser)
            .whereGreaterThan("timeCheckIn", dateFrom)
            .whereLessThan("timeCheckIn",dateTo)

        val task = Tasks.await(snapshot.get())
        val orders = task.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllOrdersFilterUserDateAndKeyword(keyword: String, uidUser:String, dateFrom:Date, dateTo:Date) = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())



        val snapshot = mOrdersCollection
            .whereEqualTo("uidUsers", uidUser)
            .whereGreaterThan("timeCheckIn", dateFrom)
            .whereLessThan("timeCheckIn",dateFrom)
            .whereArrayContains("keyword",keyword.trim()).limit(20)

        val task = Tasks.await(snapshot.get())
        val orders = task.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllOrdersFilterKeywordAndDate(keyword: String, dateFrom:Date, dateTo:Date) = flow<State<List<Orders>>> {

        // Emit loading state
        emit(State.loading())


        val snapshot = mOrdersCollection
            .whereArrayContains("keyword",keyword.trim()).limit(20)
            .whereGreaterThan("timeCheckIn", dateFrom)
            .whereLessThan("timeCheckIn",dateTo)

        val task = Tasks.await(snapshot.get())
        val orders = task.toObjects(Orders::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
    get User role as information user login as a Admin or just user
     */

    fun getDataOrders(id: String)  = flow<State<Orders>> {

        // Emit loading state
        emit(State.loading())

        val orderRef = mOrdersCollection.document(id).get().await()

        //Check if data exists
        if (orderRef.exists()) {
            //Cast the given DocumentSnapshot to our POJO class
            var data = orderRef.toObject(Orders::class.java)!!

            //Task successful
            emit(State.success(data))
        }else{
            emit(State.failed("Data Null"))
        }



    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getDataAktivitas(idAktivitas: String)  = flow<State<List<Aktivitas>>> {

        // Emit loading state
        emit(State.loading())



        val snapshot = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_aktivitas+"/$idAktivitas/$idAktivitas").orderBy("tanggal",
            Query.Direction.DESCENDING)


        val data = Tasks.await(snapshot.get())

        var model = data.toObjects(Aktivitas::class.java)


        emit(State.success(model))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    /**
     * Adds post [users] into the cloud firestore collection.
     * @return The Flow of [State] which will store state of current action.
     */
    fun addOrders(orders: Orders) = flow {

        // Emit loading state
        emit(State.loading())

        val orderId = mOrdersCollection.document().id
        orders.uid = orderId
        orders.idAktivitas = orderId

        val task = Tasks.await(mOrdersCollection.document(orderId).set(orders))

        // Emit success state with post reference
        emit(State.success(task))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun updateOrders(orders: Orders) = flow<State<Orders>> {

        // Emit loading state
        emit(State.loading())
        val snapshot = mOrdersCollection.document(orders.uid)

        Tasks.await(snapshot.set(orders))


        // Emit success state with post reference
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun addAktivitas(orderId:String,aktivitas: Aktivitas) = flow<State<Aktivitas>> {

        // Emit loading state
        emit(State.loading())
        val newId = mAktivitasCollection.document(orderId).id
        aktivitas.id = newId
        aktivitas.tanggal = Timestamp.now().toDate()
        val userPublicProfileRef = mAktivitasCollection.document(orderId).collection(newId)
        //Use the id for whatever you need
        val task = Tasks.await(userPublicProfileRef.add(aktivitas))
        // Emit success state with post reference
        if (task.get().exception != null)
            emit(State.failed(task.get().exception?.message.toString()))
        else
            emit(State.success(aktivitas))


    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}