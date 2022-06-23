package com.wisa.eOurPetshop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Paket(
    var uid:String="",
    var name:String="",
    var price:String="",
    var facility:ArrayList<String> = ArrayList(),
):Parcelable{
    val facilityReguler : ArrayList<String>
        get() {
            val list = ArrayList<String>()
            list.add("- Grooming")
            list.add("- normal Feed")
            list.add("- Obat Cacing")

            return list
        }

    val facilityVip : ArrayList<String>
        get() {
            val list = ArrayList<String>()
            list.add("- Grooming")
            list.add("- Premium Feed")
            list.add("- Vaksin")
            list.add("- Obat Cacing")

            return list
        }

    val listPaket : ArrayList<Paket>
        get() {
            val list = ArrayList<Paket>()
            list.add(
                Paket("1","VIP", "200000",facilityVip)
            )
            list.add(
                Paket("2","REGULAR", "150000",facilityReguler)
            )

            return list
        }
}
