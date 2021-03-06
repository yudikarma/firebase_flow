package com.wisa.eOurPetshop.ui.aktivitas

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dekape.core.utils.logD
import com.lriccardo.timelineview.TimelineDecorator
import com.lriccardo.timelineview.TimelineView
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseActivity
import com.wisa.eOurPetshop.databinding.ActivityAktivitasHewanBinding
import com.wisa.eOurPetshop.model.Aktivitas
import com.wisa.eOurPetshop.model.Orders
import com.wisa.eOurPetshop.utils.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AktivitasHewanActivity : BaseActivity<ActivityAktivitasHewanBinding>(),
    AktivitasHewanRvAdapters.Interaction {

    private lateinit var binding: ActivityAktivitasHewanBinding
    private var idAktivitas :String? = null
    private var orders: Orders? = null
    private val rvAdapters : AktivitasHewanRvAdapters by lazy { AktivitasHewanRvAdapters(this) }


    companion object{
        fun getStaredIntent( activity: AppCompatActivity,idAktivitas:String,orders: Orders){
            Intent(activity, AktivitasHewanActivity::class.java)
                .putExtra("data",idAktivitas)
                .putExtra("orders",orders)
                .run { activity?.startActivity(this) }

            /*getCallerActivity(activityCaller)?.let {
                val intent = Intent(it.context, PickPaketActivity::class.java)
                it.startActivityForResult(intent,requestCode)

            }*/
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)

        idAktivitas = intent.getStringExtra("data")
        orders = intent.getParcelableExtra("orders") as Orders?

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Update Aktivitas"

        binding.toolbar.setNavigationOnClickListener { finish() }




        binding.rvAktivitas.apply {
            addItemDecoration(
                TimelineDecorator(
                indicatorSize = 10f,
                lineWidth = 3f,
                padding = 8f,
                position = TimelineDecorator.Position.Left,
                indicatorColor = Color.GRAY,
                lineColor = Color.GRAY,
                    indicatorYPosition = 0.1f,
                    indicatorStyle = TimelineView.IndicatorStyle.Filled
            )
            )
            adapter = rvAdapters
        }


        getAktivitasUpdate(idAktivitas)


    }

    private fun getAktivitasUpdate(idAktivitas: String?) {
        idAktivitas?.let {
            uiScope.launch {
                orderViewsModel.getDataAktivitas(it).collect {
                    when(it){
                        is State.Loading -> showLoadingDialog()
                        is State.Failed -> {
                            dismissLoadingDialog()
                            showErrorDialog(it.message)
                        }
                        is State.Success -> {
                            dismissLoadingDialog()
                            logD(it.toString())
                            orders?.let {
                                    order -> rvAdapters.submitList(it.data,order)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int  = R.layout.activity_aktivitas_hewan

    override fun onItemSelected(position: Int, item: Aktivitas) {

    }
}