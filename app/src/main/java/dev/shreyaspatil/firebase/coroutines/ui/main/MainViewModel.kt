package dev.shreyaspatil.firebase.coroutines.ui.main

import androidx.lifecycle.ViewModel
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.repository.UsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainViewModel(private val repository: UsersRepository) : ViewModel() {

    fun getAllPosts() = repository.getAllUsers()

    fun addPost(users: Users) = repository.addUser(users)
}