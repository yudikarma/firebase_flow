package dev.shreyaspatil.firebase.coroutines.ui.main


import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment

import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* //init navcontroller
        navController=  NavHostFragment.findNavController(this)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.e("MAIN FRAGMENT", "onDestinationChanged: "+destination.label);
            handleBackPres()

        }*/
    }


    fun getLayoutId(): Int = R.layout.fragment_main

    private lateinit var binding: FragmentMainBinding

    fun handleBackPres(){
        /*if ( navController.currentDestination?.id == R.id.loginFragment)
        activity?.finish()
        else
        navController.navigate(R.id.loginFragment)*/
    }

}
