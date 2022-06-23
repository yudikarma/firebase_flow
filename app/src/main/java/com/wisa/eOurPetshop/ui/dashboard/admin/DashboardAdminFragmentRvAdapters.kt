package com.wisa.eOurPetshop.ui.dashboard.admin

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.dekape.core.utils.toJson
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.model.Orders
import com.wisa.eOurPetshop.ui.picture.DetailPictureActivity
import com.wisa.eOurPetshop.utils.onClick
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.toPriceFormat

class DashboardAdminFragmentRvAdapters(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Orders>() {

        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
           return oldItem.uid == newItem.uid
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.uid == newItem.uid
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return DashboardSupirFragmentViewHolders(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_parcel,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DashboardSupirFragmentViewHolders -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Orders?>) {
       /* if(differ.currentList.isNotEmpty()){
            val oldList : MutableList<BebasProdi> =   differ.currentList.toMutableList()
            differ.currentList.removeAll(oldList)
        }
*/
        differ.submitList(list)
    }

    class DashboardSupirFragmentViewHolders
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Orders) = with(itemView) {
            itemView.onClick {
                interaction?.onItemSelected(adapterPosition, item)
            }

            Log.d("data bebas prodi","${item.toJson()}")


            itemView.findViewById<AppCompatImageView>(R.id.img_paket).loadFromUrl(item.fotoPeliharaan)
            itemView.findViewById<AppCompatImageView>(R.id.img_paket).setOnClickListener {
                DetailPictureActivity.getStaredIntent(context,item.fotoPeliharaan)
            }
            itemView.findViewById<AppCompatTextView>(R.id.nama_paket).setText(item.namaPeliharaan)
            itemView.findViewById<AppCompatTextView>(R.id.facility_paket).setText(item.localeDateCheckIn)
            itemView.findViewById<AppCompatTextView>(R.id.desc_task).setText(item.catatan)
            itemView.findViewById<AppCompatTextView>(R.id.harga_paket).setText(item.price.toPriceFormat())

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Orders)
    }
}
