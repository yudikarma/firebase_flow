package dev.shreyaspatil.firebase.coroutines.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dev.shreyaspatil.firebase.coroutines.model.Kandang
import dev.shreyaspatil.firebase.coroutines.utils.State
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.kandang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class KandangRepository {
    private val mKandangCollection = FirebaseFirestore.getInstance().kandang()

    /**
     * Returns Flow of [State] which retrieves all posts from cloud firestore collection.
     */
    fun getAllKandang() = flow<State<List<Kandang>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mKandangCollection.get().await()
        val orders = snapshot.toObjects(Kandang::class.java)

        // Emit success state with data
        emit(State.success(orders))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
    get User role as information user login as a Admin or just user
     */

    fun getDataKandang(id: String)  = flow<State<Kandang>> {

        // Emit loading state
        emit(State.loading())

        val kandangRef = mKandangCollection.document(id).get().await()

        //Check if data exists
        if (kandangRef.exists()) {
            //Cast the given DocumentSnapshot to our POJO class
            var data = kandangRef.toObject(Kandang::class.java)!!

            //Task successful
            emit(State.success(data))
        }else{
            emit(State.failed("Data Null"))
        }



    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    /**
     * Adds post [users] into the cloud firestore collection.
     * @return The Flow of [State] which will store state of current action.
     */
    fun addKandang(kandang: Kandang) = flow<State<DocumentReference>> {

        // Emit loading state
        emit(State.loading())

        val kandangRef = mKandangCollection.add(kandang).await()

        // Emit success state with post reference
        emit(State.success(kandangRef))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}