package dev.shreyaspatil.firebase.coroutines.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.shreyaspatil.firebase.coroutines.ui.dialog.LoadingDialogFragment
import dev.shreyaspatil.firebase.coroutines.ui.main.MainActivity
import dev.shreyaspatil.firebase.coroutines.ui.main.MainViewModelFactory
import dev.shreyaspatil.firebase.coroutines.utils.Constants
import dev.shreyaspatil.firebase.coroutines.utils.showAlert
import dev.shreyaspatil.firebase.coroutines.viewmodel.OrdersViewModel
import dev.shreyaspatil.firebase.coroutines.viewmodel.OrdersViewModelFactory
import dev.shreyaspatil.firebase.coroutines.viewmodel.UserViewModel
import dev.shreyaspatil.firebase.coroutines.viewmodel.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseFragment<T : ViewDataBinding>:Fragment(), LoadingDialogFragment.LoadingDialogFragmentListener {

    private val loadingDialog: LoadingDialogFragment? by lazy { LoadingDialogFragment(this) }

    internal lateinit var context: Context
    internal lateinit var activity : FragmentActivity

    lateinit var usersviewModel: UserViewModel

    lateinit var orderViewsModel: OrdersViewModel


    // Coroutine Scope
    val uiScope = CoroutineScope(Dispatchers.Main)

    private lateinit var viewDataBinding: T

    @LayoutRes
    abstract fun getLayoutId(): Int

    protected fun getViewDataBinding(): T = viewDataBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        performDataBinding(inflater,container)
        return viewDataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getContext()?.let {
            context = it
        }
        getActivity()?.let {
            activity = it
        }
        usersviewModel = ViewModelProvider(this, UserViewModelFactory())
            .get(UserViewModel::class.java)

        orderViewsModel = ViewModelProvider(this, OrdersViewModelFactory())
            .get(OrdersViewModel::class.java)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun performDataBinding(inflater: LayoutInflater,
                                   container: ViewGroup?) {
        /*viewDataBinding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.executePendingBindings()*/
    }

    override fun onDismis(dialog: BottomSheetDialogFragment) {
        dismissLoadingDialog()
    }

    fun showLoadingDialog(){
        if (!(loadingDialog?.isAdded?:true)){
            loadingDialog?.isCancelable = false
            fragmentManager?.let {
                loadingDialog?.show(it,"loadingDialog")
            }
        }else{
            loadingDialog?.onDetach()
        }
    }

    fun showErrorDialog(errorMessage:String? = ""){
        errorMessage?.let { context.showAlert(it) }
    }

    fun dismissLoadingDialog(){
        if (loadingDialog?.isAdded?:false)
            loadingDialog?.dismiss()
    }

    fun View.isVisible(): Boolean = visibility == View.VISIBLE

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun logout() {
        val dialog : AlertDialog = AlertDialog.Builder(context)
            .setTitle("Keluar")
            .setMessage("Apakah kamu yakin ingin keluar ?")
            .setNeutralButton("CANCEL"){
                    dialog, which -> dialog.dismiss()
            }
            .setPositiveButton("OK"){
                    dialog, which ->

                usersviewModel.logOut()
                startActivity(Intent(activity,MainActivity::class.java))
                activity.finish()
                true
            }

            .create()
        dialog.setOnShowListener(object : DialogInterface.OnShowListener{
            override fun onShow(arg0: DialogInterface?) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#BDBDBD"))
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00bcd4"))
            } }
        )
        dialog.show()

    }

    fun setLogout(isLogout:Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(Constants.IS_LOGOUT, isLogout)
        editor.commit()
    }

}