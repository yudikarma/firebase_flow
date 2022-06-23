package com.wisa.eOurPetshop.ui.history

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.model.Orders
import com.wisa.eOurPetshop.utils.onClick

class HistoryFragmentRvAdapters(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Orders>() {

        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.uid == newItem.uid
        }

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



            /*title_task.text = item.name
            desc_task.text = item.remark
            date_task.text = item.nim
            btn_status.text = item.createOn

            ic_task.loadFromUrl(item.urlPict?:"")*/
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Orders)
    }
}
