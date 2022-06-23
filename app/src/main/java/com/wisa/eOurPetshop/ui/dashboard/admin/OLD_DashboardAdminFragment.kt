package com.wisa.eOurPetshop.ui.dashboard.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseFragment
import com.wisa.eOurPetshop.databinding.FragmentDashboardAdminBinding

class OLD_DashboardAdminFragment : BaseFragment<FragmentDashboardAdminBinding>() {


    override fun getLayoutId(): Int  = R.layout.fragment_dashboard_admin
    private lateinit var binding: FragmentDashboardAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashboardAdminBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}