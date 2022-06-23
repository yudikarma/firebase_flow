package com.wisa.eOurPetshop.ui.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseFragment
import com.wisa.eOurPetshop.databinding.FragmentProfilSupirBinding
import com.wisa.eOurPetshop.model.Users
import com.wisa.eOurPetshop.ui.picture.DetailPictureActivity
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl

/**
 * A simple [Fragment] subclass.
 */
class ProfilFragment : BaseFragment<FragmentProfilSupirBinding>() {


    override fun getLayoutId(): Int = R.layout.fragment_profil_supir

    private lateinit var binding: FragmentProfilSupirBinding

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

        getUserInfo()
    }

    private fun getUserInfo() {

        val user = usersviewModel.getDataUsersLocal(context)
        user?.let {
            showUserInfo(it)
        }

    }

    private fun showUserInfo(users: Users){
        binding.run {
            tvName.text = users.name
            phone.text = users.nohp
            rule.text = users.rule
            email.text = users.email
            profilPict.loadFromUrl(users.urlPict)
            profilPict.setOnClickListener {
                DetailPictureActivity.getStaredIntent(context,users.urlPict)
            }

        }

    }

}
