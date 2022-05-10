package dev.shreyaspatil.firebase.coroutines.ui.about


import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import androidx.appcompat.widget.AppCompatButton
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentAboutxxxxBinding
import dev.shreyaspatil.firebase.coroutines.base.BaseFragment
import dev.shreyaspatil.firebase.coroutines.utils.onClick
import org.jetbrains.anko.support.v4.toast


/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : BaseFragment<FragmentAboutxxxxBinding>() {

    override fun getLayoutId(): Int  = R.layout.fragment_aboutxxxx

    private lateinit var binding: FragmentAboutxxxxBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        //binding = FragmentAboutxxxxBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btGetcode.onClick {
            showCustomDialog()
        }

    }


    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_dark)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()?.getAttributes())
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        /*((TextView) dialog.findViewById(R.id.title)).setText(p.name);
        ((CircleImageView) dialog.findViewById(R.id.image)).setImageResource(p.image);*/

        (dialog.findViewById(R.id.bt_close) as ImageButton).onClick { dialog.dismiss() }

        (dialog.findViewById(R.id.bt_follow) as AppCompatButton).onClick {
            /* Toast.makeText(getApplicationContext(), "Follow Clicked", Toast.LENGTH_SHORT).show();*/
            dialog.dismiss()
        }

        dialog.show()
        dialog.getWindow()?.setAttributes(lp)
    }

}
