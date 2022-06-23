package com.wisa.eOurPetshop.ui.picture

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseActivity
import com.wisa.eOurPetshop.databinding.ActivityDetailPictureBinding
import com.wisa.eOurPetshop.utils.getCallerActivity
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl

class DetailPictureActivity : BaseActivity<ActivityDetailPictureBinding>() {

    companion object{
        fun getStaredIntent( activityCaller:Any,urlImage:String){
            getCallerActivity(activityCaller)?.let {
                val intent = Intent(it.context, DetailPictureActivity::class.java)
                intent.putExtra("data",urlImage)
                it.startActivityForResult(intent,90)

            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_detail_picture

    private lateinit var binding: ActivityDetailPictureBinding

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gambar"
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        intent.getStringExtra("data")?.let {
            binding.img.loadFromUrl(it)

        }
    }
}
