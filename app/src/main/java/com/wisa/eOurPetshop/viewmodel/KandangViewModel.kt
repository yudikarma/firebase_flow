package com.wisa.eOurPetshop.viewmodel

import androidx.lifecycle.ViewModel
import com.wisa.eOurPetshop.model.Kandang
import com.wisa.eOurPetshop.repository.KandangRepository

class KandangViewModel(private val repository: KandangRepository) : ViewModel() {

    fun getAllKandang() = repository.getAllKandang()

    fun addKandang(kandang: Kandang) = repository.addKandang(kandang)

    fun getDataKandang(id:String) = repository.getDataKandang(id)
}