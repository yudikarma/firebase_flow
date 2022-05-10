package dev.shreyaspatil.firebase.coroutines.viewmodel

import androidx.lifecycle.ViewModel
import dev.shreyaspatil.firebase.coroutines.model.Pets
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.repository.PetsRepository
import dev.shreyaspatil.firebase.coroutines.repository.UsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PetsViewModel(private val repository: PetsRepository) : ViewModel() {

    fun getAllpets() = repository.getAllpets()

    fun addPets(pets: Pets) = repository.addPets(pets)

    fun getDataPets(id:String) = repository.getDataPets(id)
}