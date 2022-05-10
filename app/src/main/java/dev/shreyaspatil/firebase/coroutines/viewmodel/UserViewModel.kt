package dev.shreyaspatil.firebase.coroutines.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.repository.UsersRepository
import dev.shreyaspatil.firebase.coroutines.utils.Firebase
import dev.shreyaspatil.firebase.coroutines.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow

class UserViewModel(private val repository: UsersRepository) : ViewModel() {

    fun getAllUsers() = repository.getAllUsers()

    fun getUsersIsRegistered(email:String) = repository.getUsersIsRegistered(email)

    fun getCurrentUser() : FirebaseUser? = repository.getCurrentUser()

    fun getAuth() : FirebaseAuth? = repository.getAuth()

    fun getProfileStorageRef() : StorageReference? = repository.getProfileStorageRef()

    fun logOut()  = repository.logOut()

    fun addUsers(users: Users) = repository.addUser(users)

    fun getDataUser(userId:String) = repository.getDataUser(userId)

    fun setDataUsersLocal(context: Context, users: Users?) = repository.setDataUsersLocal(context,users)

    fun getDataUsersLocal(context:Context) : Users ? = repository.getDataUsersLocal(context)
}