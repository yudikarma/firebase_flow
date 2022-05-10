package dev.shreyaspatil.firebase.coroutines.repository

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.dekape.core.utils.logD
import com.dekape.core.utils.toJson
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dev.shreyaspatil.firebase.coroutines.utils.Constants
import dev.shreyaspatil.firebase.coroutines.utils.State
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.user
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.lang.Exception

/**
 * Repository for the data of posts.
 * This will be a single source of data throughout the application.
 */
@ExperimentalCoroutinesApi
class UsersRepository {

    private val mUsersCollection = FirebaseFirestore.getInstance().user()

    private var mAuth : FirebaseAuth? = null
    private var mUser: FirebaseUser? = null
    private  var storage: FirebaseStorage ? = null
    private  var imageProfilReference: StorageReference? = null
    init {
        mAuth = FirebaseAuth.getInstance()
        mUser = FirebaseAuth.getInstance().currentUser

        //init firebase storage
        storage = FirebaseStorage.getInstance()
        storage?.let {
            imageProfilReference = it.reference.child(Constants.COLLECTION_USERS+"/")
        }
    }

    fun getCurrentUser() : FirebaseUser? = if (mUser == null)FirebaseAuth.getInstance().currentUser else mUser

    fun getAuth() : FirebaseAuth? = if (mAuth == null)FirebaseAuth.getInstance() else mAuth

    fun getProfileStorageRef() : StorageReference? = if (mAuth != null) imageProfilReference else null

    fun logOut() =  if (mAuth == null)  FirebaseAuth.getInstance().signOut() else mAuth?.signOut()

    /**
     * Returns Flow of [State] which retrieves all posts from cloud firestore collection.
     */
    fun getAllUsers() = flow<State<List<Users>>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mUsersCollection.get().await()
        val posts = snapshot.toObjects(Users::class.java)

        // Emit success state with data
        emit(State.success(posts))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getUsersIsRegistered(email:String) = flow<State<Boolean>> {

        // Emit loading state
        emit(State.loading())

        val snapshot = mUsersCollection.whereEqualTo("email",email.toLowerCase()).get().await()

        val posts = snapshot.toObjects(Users::class.java)

        logD("data user "+posts.toJson())

       if (posts.size >= 1){
            emit(State.success(true))
        }else
           emit(State.success(false))


    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    /**
    get User role as information user login as a Admin or just user
     */

    fun getDataUser(userId: String)  = flow<State<Users>> {

        // Emit loading state
        emit(State.loading())

        val userRef = mUsersCollection.document(userId).get().await()

        //Check if data exists
        if (userRef.exists()) {
            //Cast the given DocumentSnapshot to our POJO class
            var data = userRef.toObject(Users::class.java)!!

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
    fun addUser(users: Users) = flow<State<Users>> {

        // Emit loading state
        emit(State.loading())

        val userPublicProfileRef = mUsersCollection.document(users.uid)
        //Use the id for whatever you need
        Tasks.await(userPublicProfileRef.set(users))
        // Emit success state with post reference
        emit(State.success(users))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun setDataUsersLocal(context:Context,users: Users?) : Boolean  {

        return try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putString(Constants.COLLECTION_USERS, users?.toJson().toString())
            editor.commit()
            true
        }catch (e:Exception){
            false
        }
    }

    fun getDataUsersLocal(context:Context) : Users ?{
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val users = prefs.getString(Constants.COLLECTION_USERS,null)
        val gson = Gson()
        return try {
             gson.fromJson(users,Users::class.java)
        }catch (e: Exception){
            Log.d(this.javaClass.name,e.message.toString())
            return null
        }

    }
}
