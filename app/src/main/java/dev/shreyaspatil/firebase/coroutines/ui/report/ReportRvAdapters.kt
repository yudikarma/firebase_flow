package dev.shreyaspatil.firebase.coroutines.ui.report

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.model.Paket

import android.annotation.SuppressLint
import dev.shreyaspatil.firebase.coroutines.databinding.ItemListReportBinding

class ReportRvAdapters(
    private var callback: ((Orders) -> Unit)? = null
): RecyclerView.Adapter<ReportRvAdapters.Holder>() {

    private var items = ArrayList<Orders>()

    fun set(items : ArrayList<Orders>){
        this.items = items
        notifyDataSetChanged()
    }
    fun clear(){
        this.items.clear()
        notifyDataSetChanged()
    }

    fun add(item : Orders){
        this.items.add(item)
        notifyDataSetChanged()
    }

    inner class Holder(private val binding : ItemListReportBinding) :
        RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(item: Orders) = with(binding){
            no.text = (adapterPosition + 1).toString()
            dateTitip.text = item.localeDateCheckIn.toString()
            dateAmbil.text = item.localeDateCheckOut.toString()
            pelanggan.text = item.namaPelangan
            //peliharaan.text = item.namaPeliharaan
            price.text = item.price
            //imgProfile.loadImage(url = item.photo ?: "")
            //studyName.text = item.name
            //txtEducation.text = "${item.student_study?.name ?: ""} - ${item.student_study_path?.name ?: ""}"
            itemView.setOnClickListener {
                callback?.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(ItemListReportBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
