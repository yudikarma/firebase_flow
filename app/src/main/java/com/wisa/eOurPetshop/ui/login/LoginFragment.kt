package com.wisa.eOurPetshop.ui.login


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseFragment
import com.wisa.eOurPetshop.databinding.FragmentLoginBinding
import com.wisa.eOurPetshop.ui.forgot.ForgotPasswordFragment
import com.wisa.eOurPetshop.ui.main.MainActivity
import com.wisa.eOurPetshop.ui.registrasi.RegistrasiFragment
import com.wisa.eOurPetshop.utils.Constants
import com.wisa.eOurPetshop.utils.State
import com.wisa.eOurPetshop.utils.onClick
import com.wisa.eOurPetshop.utils.showAlert
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    companion object{
        fun directionToMe(view: View,destination: NavDirections){
           // val mainNavView = activity.findViewById<View>(R.id.main_fragment)
           // Navigation.findNavController(mainNavView).navigate(R.id.loginFragment)
            view?.let {
                with(findNavController(it)) {
                    currentDestination?.getAction(destination.actionId)
                        ?.let { navigate(destination) }
                }
                //Navigation.findNavController(it).navigate(R.id.action_spalshFragment2_to_loginFragment)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_login

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnForgotPassword.onClick {
            //direc to forgot password
            ForgotPasswordFragment.directionToMe(activity)
        }

        binding.petugas.onClick {
                RegistrasiFragment.directionToMe(activity, Constants.RULE_USER) }
        binding.supir.onClick { RegistrasiFragment.directionToMe(activity, Constants.RULE_ADMIN) }

        binding.btnLogin.onClick { validateLogin() }
    }

    private fun validateLogin() {
        val valueEmail = binding.email.text.toString()
        val valuePassword =  binding.password.text.toString()

        if (valueEmail.isEmpty() || valuePassword.isEmpty()){
            if (valueEmail.isEmpty())
                binding.email.error = "Required"
            if (valuePassword.isEmpty())
                binding.password.error = "Required"
        }else{
            showLoadingDialog()
            usersviewModel?.getAuth()?.signInWithEmailAndPassword(valueEmail,valuePassword)?.addOnCompleteListener { task ->
                if (task.isSuccessful){

                    //get data user
                        uiScope.launch {
                            usersviewModel.getCurrentUser()?.uid?.let {
                                usersviewModel.getDataUser(it).collect { state ->
                                    when(state){
                                        is State.Success ->{
                                            setLogout(false)

                                            usersviewModel.setDataUsersLocal(context,state.data)
                                            dismissLoadingDialog()

                                            val intent = Intent(activity, MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            startActivity(intent)
                                            activity.finish()
                                        }
                                        is State.Failed ->{
                                            dismissLoadingDialog()
                                            showErrorDialog(state.message)
                                        }
                                        else -> {
                                            dismissLoadingDialog()
                                        }
                                    }
                                }
                            }
                        }
                    /*activity?.let {
                        SplashFragment.directToMe(it)
                    }*/
                }else{
                    dismissLoadingDialog()
                    context.showAlert(
                        "Login Gagal",
                        task.exception?.localizedMessage?:"Periksa Kembali Email atau Password anda"
                    )
                }
            }
        }
    }



}
