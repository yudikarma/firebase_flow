package dev.shreyaspatil.firebase.coroutines.viewmodel

import androidx.lifecycle.ViewModel
import dev.shreyaspatil.firebase.coroutines.model.Aktivitas
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.model.Pets
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.repository.OrdersRepository
import dev.shreyaspatil.firebase.coroutines.repository.PetsRepository
import dev.shreyaspatil.firebase.coroutines.repository.UsersRepository
import dev.shreyaspatil.firebase.coroutines.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import java.util.*

class OrdersViewModel(private val repository: OrdersRepository) : ViewModel() {

    fun getAllOrders() = repository.getAllOrders()

    fun getOrderStorageRef() = repository.getOrderStorageRef()

    fun getAllOrdersFilterDate(dateFrom:Date,dateTo:Date) = repository.getAllOrdersFilterDate(dateFrom,dateTo)

    fun getAllOrdersFilterUser(uidUser:String) = repository.getAllOrdersFilterUser(uidUser)
    fun getAllOrdersFilterUserAndKeyword(uidUser:String,keyword: String) = repository.getAllOrdersFilterUserAndKeyword(uidUser,keyword)

    fun getAllOrdersFilterUserAndDate(uidUser:String,dateFrom:Date,dateTo:Date) = repository.getAllOrdersFilterUserAndDate(uidUser,dateFrom,dateTo)
    fun getAllOrdersFilterUserDateAndKeyword(keyword: String, uidUser:String, dateFrom:Date, dateTo:Date) = repository.getAllOrdersFilterUserDateAndKeyword(keyword,uidUser,dateFrom,dateTo)


    fun getAllOrdersFilterKewword(keyword :String)  = repository.getAllOrdersFilterKewword(keyword)

    fun getAllOrdersFilterKeywordAndDate(keyword: String, dateFrom:Date, dateTo:Date) = repository.getAllOrdersFilterKeywordAndDate(keyword,dateFrom,dateTo)

    fun addOrders(order: Orders) = repository.addOrders(order)

    fun updateOrders(order: Orders) = repository.updateOrders(order)

    fun addAktivitas(orderId:String,aktivitas: Aktivitas) = repository.addAktivitas(orderId,aktivitas)

    fun getDataAktivitas(id:String) = repository.getDataAktivitas(id)


    fun getDataOrders(id:String) = repository.getDataOrders(id)
}