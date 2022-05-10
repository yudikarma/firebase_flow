package dev.shreyaspatil.firebase.coroutines.ui.paket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.ActivityPickKabupatenBinding
import dev.shreyaspatil.firebase.coroutines.model.Paket
import dev.shreyaspatil.firebase.coroutines.base.BaseActivity
import dev.shreyaspatil.firebase.coroutines.ui.maps.PickMapsActivity
import dev.shreyaspatil.firebase.coroutines.utils.getCallerActivity

class PickPaketActivity : BaseActivity<ActivityPickKabupatenBinding>(), PickPaketRvAdapters.Interaction {
    override fun onItemSelected(position: Int, item: Paket) {
        callFinish(item)
    }

    private var rvAdapters:PickPaketRvAdapters = PickPaketRvAdapters(this)

    companion object{
        const val PickKabupatenIntentCode = 909
        fun getStaredIntent( activity: AppCompatActivity,requestCode:Int){
            Intent(activity, PickPaketActivity::class.java)
                .run { activity.startActivityForResult(this,requestCode) }
        }
    }

    private lateinit var binding:ActivityPickKabupatenBinding

    override fun getLayoutId(): Int = R.layout.activity_pick_kabupaten

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pilih Paket"

        val dummyData = Paket()
        rvAdapters.submitList(dummyData.listPaket)
        binding.rvMobbil.adapter = rvAdapters
    }


    fun callFinish(item:Paket){
        val intent = Intent()
        intent.putExtra("data",item)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}
