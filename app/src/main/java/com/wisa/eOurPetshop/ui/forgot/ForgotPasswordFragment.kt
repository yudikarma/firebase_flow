package com.wisa.eOurPetshop.ui.forgot


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.wisa.eOurPetshop.R

import com.wisa.eOurPetshop.base.BaseFragment
import com.wisa.eOurPetshop.databinding.FragmentForgotPasswordBinding
import com.wisa.eOurPetshop.utils.State
import com.wisa.eOurPetshop.utils.onClick
import com.wisa.eOurPetshop.utils.showAlert
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    companion object{
        fun directionToMe(activity: FragmentActivity){
            val mainNavView = activity.findViewById<View>(R.id.main_fragment)
            Navigation.findNavController(mainNavView).navigate(R.id.forgotPasswordFragment)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_forgot_password

    private lateinit var binding: FragmentForgotPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {view?.let {
            Navigation.findNavController(it).popBackStack()
        }  }

        binding.btnResetPassword.onClick {
            val email = binding.emailforgotpassword.text.toString()
            if (email.isNullOrEmpty())
                toast("Email tidak boleh kosong")
            else{

                uiScope.launch {
                    usersviewModel.getUsersIsRegistered(email).collect { state ->
                        when(state){
                            is State.Loading -> showLoadingDialog()
                            is State.Failed -> {
                                dismissLoadingDialog()
                                showErrorDialog(state.message)
                            }
                            is State.Success -> {
                                if (!state.data){
                                    dismissLoadingDialog()
                                    showErrorDialog("Email tidak terdaftar di sistem kami.")

                                }else{
                                    usersviewModel.getAuth()?.sendPasswordResetEmail(email)?.addOnCompleteListener{ task ->
                                        if (task.isSuccessful){
                                            dismissLoadingDialog()
                                            context.showAlert(
                                                "Forgot Password Berhasil",
                                                "kami telah mengirim email reset password pada email anda."
                                            ){
                                                view?.let {
                                                    Navigation.findNavController(it).popBackStack()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }
    }


}
