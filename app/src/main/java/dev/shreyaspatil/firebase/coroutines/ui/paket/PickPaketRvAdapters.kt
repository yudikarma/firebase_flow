package dev.shreyaspatil.firebase.coroutines.ui.paket

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.model.Paket
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.toPriceFormat

class PickPaketRvAdapters(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Paket>() {

        override fun areItemsTheSame(oldItem: Paket, newItem: Paket): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Paket, newItem: Paket): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PickKabupatenViewHolders(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_pick_mobil,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PickKabupatenViewHolders -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Paket>) {
        differ.submitList(list)
    }

    class PickKabupatenViewHolders
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Paket) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            findViewById<AppCompatTextView>(R.id.nama_paket).text = item.name
            var newString = ""
            if (adapterPosition == 0){
                item.facilityVip.forEach {
                    newString  +=  it.plus("\n")
                }
            }else{
                item.facilityReguler.forEach {
                    newString  +=  it.plus("\n")
                }
            }
            findViewById<AppCompatTextView>(R.id.facility_paket).text = newString
            findViewById<AppCompatTextView>(R.id.harga_paket).text = "Harga : ${item.price.toPriceFormat()} /hari"



        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Paket)
    }
}
