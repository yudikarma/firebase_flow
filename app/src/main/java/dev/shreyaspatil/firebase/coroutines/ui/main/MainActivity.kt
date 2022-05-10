package dev.shreyaspatil.firebase.coroutines.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.ActivityMainBinding
import dev.shreyaspatil.firebase.coroutines.base.BaseActivity
import dev.shreyaspatil.firebase.coroutines.ui.login.LoginFragment
import dev.shreyaspatil.firebase.coroutines.utils.getCallerActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    companion object {
        const val MainActivityRequestCode = 10
        const val MainActivityEXTRA = "MainActivityEXTRA"
        fun getStaredIntent(activityCaller: Any, isLogout: String?=null) {
            getCallerActivity(activityCaller)?.let {
                val intent = Intent(it.context, MainActivity::class.java)
                intent.putExtra(MainActivityEXTRA, isLogout)
                it.startActivity(intent)
                //it.activity?.finish()

            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    private lateinit var binding: ActivityMainBinding



    var isLogout :String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)
        isLogout = intent.getStringExtra(MainActivityEXTRA)
       setupFragmentMain()
    }

    private fun setupFragmentMain() {
        var fragment: Fragment? = null
        fragment = MainFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()


    }

    override fun onBackPressed() {
        /*supportFragmentManager.fragments.let { fragement ->
            (0 until fragement.size).filter {
                fragement[it] is MainFragment
            }.forEach {
                if(fragement[it] is MainFragment){
                    (fragement[it] as MainFragment).handleBackPres()
                }
            }
        }*/

    }




}
