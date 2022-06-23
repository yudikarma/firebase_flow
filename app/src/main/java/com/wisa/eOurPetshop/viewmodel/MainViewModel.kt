package com.wisa.eOurPetshop.viewmodel

import androidx.lifecycle.ViewModel
import com.wisa.eOurPetshop.model.Users
import com.wisa.eOurPetshop.repository.UsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainViewModel(private val repository: UsersRepository) : ViewModel() {

    fun getAllPosts() = repository.getAllUsers()

    fun addPost(users: Users) = repository.addUser(users)
}