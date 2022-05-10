package dev.shreyaspatil.firebase.coroutines.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.shreyaspatil.firebase.coroutines.repository.PetsRepository
import dev.shreyaspatil.firebase.coroutines.repository.UsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PetsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PetsRepository::class.java)
            .newInstance(PetsRepository())
    }
}