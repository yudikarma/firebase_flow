package com.wisa.eOurPetshop.ui.splash


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseFragment
import com.wisa.eOurPetshop.databinding.FragmentSpalshBinding
import com.wisa.eOurPetshop.ui.dashboard.admin.ContaienrDahsboardDosenActivity
import com.wisa.eOurPetshop.ui.dashboard.user.ContainerDashboardMahasiswaActivity
import com.wisa.eOurPetshop.ui.login.LoginFragment
import com.wisa.eOurPetshop.utils.Constants
import com.wisa.eOurPetshop.utils.State
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity


/**
 * A simple [Fragment] subclass.
 */
class SplashFragment : BaseFragment<FragmentSpalshBinding>() {

    companion object{
        private const val SPLASH_DELAY = 100L

        fun directToMe(fragmentActivity: FragmentActivity){
            val mainNavView = fragmentActivity.findViewById<View>(R.id.main_fragment)
            Navigation.findNavController(mainNavView).navigate(R.id.spalshFragment)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_spalsh

     var binding : FragmentSpalshBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            if (usersviewModel.getCurrentUser() == null){
                Log.d("SplashScreen","User unAuthenticate")
                //not yet login
                activity?.let { LoginFragment.directionToMe(view,
                    SplashFragmentDirections.actionSpalshFragment2ToLoginFragment()
                ) }
            }else{
                //user login
                Log.d("SplashScreen","User Login Success")
                uiScope.launch {
                    getUserRole()
                }
            }
        }, SPLASH_DELAY)

    }

    private suspend fun getUserRole() {

        usersviewModel.getCurrentUser()?.uid?.let { userId ->
            Log.d("SplashScreen",userId)
            usersviewModel.getDataUser(userId).collect { state ->
                when (state) {
                    is State.Loading -> {
                        showLoadingDialog()
                    }

                    is State.Success -> {
                        dismissLoadingDialog()

                        when (state.data.rule) {
                            Constants.RULE_ADMIN -> {
                                   if (!isLogout()) {
                                            val intent =
                                                Intent(activity, ContaienrDahsboardDosenActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            activity.startActivity(intent)
                                            activity.finish()
                                        }else{
                                            activity?.let { view?.let { LoginFragment.directionToMe(it,
                                                SplashFragmentDirections.actionSpalshFragment2ToLoginFragment()
                                            ) } }
                                        }

                            }
                            else -> {
                                context?.startActivity<ContainerDashboardMahasiswaActivity>()
                                activity?.finish()

                            }
                        }
                    }

                    is State.Failed -> {
                        dismissLoadingDialog()
                        Log.d("Splashscreeen",state.toString())
                        showErrorDialog(state.message)
                    }
                }
            }
        }

    }

    private fun isLogout():Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(Constants.IS_LOGOUT, false)

    }




}
