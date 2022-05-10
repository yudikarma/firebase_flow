package dev.shreyaspatil.firebase.coroutines.viewmodel

import androidx.lifecycle.ViewModel
import dev.shreyaspatil.firebase.coroutines.model.Kandang
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.model.Pets
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.repository.KandangRepository
import dev.shreyaspatil.firebase.coroutines.repository.OrdersRepository
import dev.shreyaspatil.firebase.coroutines.repository.PetsRepository
import dev.shreyaspatil.firebase.coroutines.repository.UsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

class KandangViewModel(private val repository: KandangRepository) : ViewModel() {

    fun getAllKandang() = repository.getAllKandang()

    fun addKandang(kandang: Kandang) = repository.addKandang(kandang)

    fun getDataKandang(id:String) = repository.getDataKandang(id)
}