package com.wisa.eOurPetshop.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.wisa.eOurPetshop.model.Pets
import com.wisa.eOurPetshop.utils.State
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.pets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class PetsRepository {
    private val mPetsCollection = FirebaseFirestore.getInstance().pets()

    /**
     * Returns Flow of [State] which retrieves all posts from cloud firestore collection.
     */
    fun getAllpets() = flow<State<List<Pets>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mPetsCollection.get().await()
        val pets = snapshot.toObjects(Pets::class.java)

        // Emit success state with data
        emit(State.success(pets))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
    get User role as information user login as a Admin or just user
     */

    fun getDataPets(id: String)  = flow<State<Pets>> {

        // Emit loading state
        emit(State.loading())

        val petsRef = mPetsCollection.document(id).get().await()

        //Check if data exists
        if (petsRef.exists()) {
            //Cast the given DocumentSnapshot to our POJO class
            var data = petsRef.toObject(Pets::class.java)!!

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
    fun addPets(pets: Pets) = flow<State<DocumentReference>> {

        // Emit loading state
        emit(State.loading())

        val postRef = mPetsCollection.add(pets).await()

        // Emit success state with post reference
        emit(State.success(postRef))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}