package com.wisa.eOurPetshop.viewmodel

import androidx.lifecycle.ViewModel
import com.wisa.eOurPetshop.model.Pets
import com.wisa.eOurPetshop.repository.PetsRepository

class PetsViewModel(private val repository: PetsRepository) : ViewModel() {

    fun getAllpets() = repository.getAllpets()

    fun addPets(pets: Pets) = repository.addPets(pets)

    fun getDataPets(id:String) = repository.getDataPets(id)
}