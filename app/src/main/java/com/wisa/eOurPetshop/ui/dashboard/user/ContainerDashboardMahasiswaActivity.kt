package com.wisa.eOurPetshop.ui.dashboard.user


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.wisa.eOurPetshop.R

import de.hdodenhof.circleimageview.CircleImageView
import com.wisa.eOurPetshop.model.Users
import com.wisa.eOurPetshop.base.BaseActivity
import com.wisa.eOurPetshop.databinding.FragmentContainerDashboardBinding
import com.wisa.eOurPetshop.ui.picture.DetailPictureActivity
import com.wisa.eOurPetshop.utils.getCallerActivity
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl

/**
 * A simple [Fragment] subclass.
 */
class ContainerDashboardMahasiswaActivity : BaseActivity<FragmentContainerDashboardBinding>() {

    companion object{
        fun getStaredIntent( activityCaller:Any){
            getCallerActivity(activityCaller)?.let {
                val intent = Intent(it.context, ContainerDashboardMahasiswaActivity::class.java)
                it.startActivity(intent)
                //it.activity?.finish()

            }
        }
    }
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var display_profil_cirle: CircleImageView
    private lateinit var display_name: TextView
    private lateinit var display_job:TextView

    private var user: Users? = Users()

    override fun getLayoutId(): Int = R.layout.fragment_container_dashboard

    private lateinit var binding: FragmentContainerDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        //init navcontroller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.client_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph,null)

        //setup nav controller to toolbar
        //NavigationUI.setupActionBarWithNavController(this,navController)

        //setup navigation controller
        NavigationUI.setupWithNavController(binding.navigationMenu, navController)

        /*val logout :MenuItem = binding.navigationMenu.menu.findItem(R.id.logout)
        logout.setOnMenuItemClickListener {
            logout()
            true
        }*/

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboard_client -> {
                    binding.toolbar.navigationIcon = null
                }
                R.id.profil_client -> {
                    binding.toolbar.gone()
                }
                else -> {
                    binding.toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back)
                    binding.toolbar.visible()
                }
            }
        }


        getUserInfo()

    }

    private fun getUserInfo() {
        /*mUserDatabaseReffrence.child(mUser?.uid?:"").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p: DatabaseError) {
                showErrorDialog("${p.message}")
            }

            override fun onDataChange(p: DataSnapshot) {
                user = p.getValue(User::class.java)
                Log.d("data snapshot","${p.value}")
                Log.d("data uuid","${mUser?.uid?:""}")
                Log.d("user info","$user")
                showUserInfo(user)
            }

        })*/
    }

    private fun showUserInfo(user: Users? = Users()) {
        //init
//        val headerView = binding.navigationMenu.getHeaderView(0)
//        display_profil_cirle = headerView.findViewById(R.id.display_profil_cirle)
//        display_name = headerView.findViewById(R.id.display_name)
//        display_job = headerView.findViewById(R.id.display_job)

        //set data
        display_profil_cirle.loadFromUrl(user?.urlPict?:"")
        display_profil_cirle.setOnClickListener {
            DetailPictureActivity.getStaredIntent(this,user?.urlPict?:"")
        }
        display_name.text = user?.name
        display_job.text = user?.rule
    }

    override fun onSupportNavigateUp(): Boolean {
       /* return NavigationUI.navigateUp(navController,appBarConfiguration)*/
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search ->{
                DashboardAdminActivity.getStaredIntent(this, DashboardAdminActivity.DashboardAdminActivityRequestCode)
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    /*override fun onBackPressed() {
        super.onBackPressed()
    }*/



}
