package com.wisa.eOurPetshop.ui.dashboard.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseFragment
import com.wisa.eOurPetshop.databinding.FragmentDashboardUserBinding
import com.wisa.eOurPetshop.ui.dashboard.admin.DashboardAdminActivity
import com.wisa.eOurPetshop.ui.order.CreateOrderActivity
import com.wisa.eOurPetshop.utils.onClick

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardUserFragment : BaseFragment<FragmentDashboardUserBinding>() {


    override fun getLayoutId(): Int = R.layout.fragment_dashboard_user

    private lateinit var binding: FragmentDashboardUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardUserBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOrder.onClick { CreateOrderActivity.getStaredIntent(this, CreateOrderActivity.CreateParcelRequestCode) }
        binding.btnLogout.onClick {  logout()}
        binding.btnActivityUpdate.onClick {   DashboardAdminActivity.getStaredIntent(requireActivity() as AppCompatActivity,
            DashboardAdminActivity.DashboardAdminActivityRequestCode,true) }

    }

}