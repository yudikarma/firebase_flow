package dev.shreyaspatil.firebase.coroutines.ui.dashboard.admin


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import de.hdodenhof.circleimageview.CircleImageView
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentContaienrDahsboardLoketBinding
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.base.BaseActivity
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl
import kotlinx.android.synthetic.main.fragment_dashboard_supir.*


/**
 * A simple [Fragment] subclass.
 */
class ContaienrDahsboardDosenActivity : BaseActivity<FragmentContaienrDahsboardLoketBinding>() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    private var user:Users? = Users()

    override fun getLayoutId(): Int = R.layout.fragment_contaienr_dahsboard_loket

    private lateinit var binding  : FragmentContaienrDahsboardLoketBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        binding.toolbar.inflateMenu(R.menu.menu_main_admin)


        //init navcontroller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.admin_fragment) as NavHostFragment
        navController = navHostFragment.navController


        //init appbarconfiguration
        appBarConfiguration = AppBarConfiguration(navController.graph, null)

        /*val logout :MenuItem = binding.navigationMenu.menu.findItem(R.id.about_admin)
        logout.setOnMenuItemClickListener {
            logout()
            true
        }*/

        //setup nav controller to toolbar
        //NavigationUI.setupActionBarWithNavController(this,navController)

        //setup navigation controller
        NavigationUI.setupWithNavController(binding.navigationMenu, navController)



        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboard_admin -> {
                    binding.toolbar.navigationIcon = null
                }
                R.id.profil_admin -> {
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
        userViewModel.getDataUsersLocal(this)?.let {
            user = it

        }
    }



    override fun onSupportNavigateUp(): Boolean {
        /*return NavigationUI.navigateUp(navController,appBarConfiguration)*/
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

   /* override fun onBackPressed() {

        super.onBackPressed()

    }*/

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search ->{
                DashboardAdminActivity.getStaredIntent(this,DashboardAdminActivity.DashboardAdminActivityRequestCode)
            }
        }
        return super.onOptionsItemSelected(item)
    }*/




}
