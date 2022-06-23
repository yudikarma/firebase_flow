package com.wisa.eOurPetshop.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.wisa.eOurPetshop.model.Users
import com.wisa.eOurPetshop.repository.UsersRepository

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

    fun getDataUsersLocal(context:Context) : Users? = repository.getDataUsersLocal(context)
}