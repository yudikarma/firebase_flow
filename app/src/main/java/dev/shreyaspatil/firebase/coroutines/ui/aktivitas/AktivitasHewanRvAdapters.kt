package dev.shreyaspatil.firebase.coroutines.ui.aktivitas

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.textview.MaterialTextView
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.model.Aktivitas
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.ui.paket.PickPaketRvAdapters
import dev.shreyaspatil.firebase.coroutines.ui.picture.DetailPictureActivity
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl
import dev.shreyaspatil.firebase.coroutines.utils.localeDateFromTimestampFirebase
import dev.shreyaspatil.firebase.coroutines.utils.localeDateFromTimestampFirebase2

class AktivitasHewanRvAdapters(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Aktivitas>() {

        override fun areItemsTheSame(oldItem: Aktivitas, newItem: Aktivitas): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Aktivitas, newItem: Aktivitas): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    private var order:Orders? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AktivitasHewanRvAdapters.AktivitasHewanHolders(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_actity_update,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AktivitasHewanRvAdapters.AktivitasHewanHolders -> {
                order?.let { holder.bind(differ.currentList.get(position), it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Aktivitas>,orders: Orders) {
        differ.submitList(list)
        this.order = orders
    }

    class AktivitasHewanHolders
    constructor(
        itemView: View,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Aktivitas,orders: Orders?) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            findViewById<MaterialTextView>(R.id.tag_location).text = item.tanggal?.localeDateFromTimestampFirebase2() + " WIB"
            findViewById<MaterialTextView>(R.id.feed_post_caption).text = item.caption
            orders?.let {
                findViewById<TextView>(R.id.feed_profilName).text = it.namaPeliharaan
                findViewById<ImageView>(R.id.feed_imageProfil).loadFromUrl(it.fotoPeliharaan)
            }
            findViewById<ImageView>(R.id.feed_post_image).run {
                loadFromUrl(item.urlImg)
                setOnClickListener {
                    DetailPictureActivity.getStaredIntent(context,item.urlImg)
                }
            }



        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Aktivitas)
    }
}
