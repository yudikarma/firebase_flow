package dev.shreyaspatil.firebase.coroutines.base

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.shreyaspatil.firebase.coroutines.ui.dialog.LoadingDialogFragment
import dev.shreyaspatil.firebase.coroutines.ui.main.MainActivity
import dev.shreyaspatil.firebase.coroutines.ui.main.MainViewModelFactory
import dev.shreyaspatil.firebase.coroutines.utils.Constants
import dev.shreyaspatil.firebase.coroutines.viewmodel.OrdersViewModel
import dev.shreyaspatil.firebase.coroutines.viewmodel.OrdersViewModelFactory
import dev.shreyaspatil.firebase.coroutines.viewmodel.UserViewModel
import dev.shreyaspatil.firebase.coroutines.viewmodel.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.anko.alert

abstract class BaseActivity<T : ViewDataBinding>:AppCompatActivity(), LoadingDialogFragment.LoadingDialogFragmentListener {

    val loadingDialog: LoadingDialogFragment? by lazy { LoadingDialogFragment(this) }

    lateinit var userViewModel: UserViewModel
    lateinit var orderViewsModel: OrdersViewModel

    // Coroutine Scope
    val uiScope = CoroutineScope(Dispatchers.Main)

    private lateinit var viewDataBinding: T

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this, UserViewModelFactory())
            .get(UserViewModel::class.java)

        orderViewsModel = ViewModelProvider(this,OrdersViewModelFactory())
            .get(OrdersViewModel::class.java)

        performDataBinding()

    }

    protected fun getViewDataBinding(): T = viewDataBinding



    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.executePendingBindings()
    }


    override fun onDismis(dialog: BottomSheetDialogFragment) {
        dismissLoadingDialog()
    }

    fun showLoadingDialog(){
        if (!(loadingDialog?.isAdded?:false)){
            loadingDialog?.isCancelable = false
            supportFragmentManager?.let {
                loadingDialog?.show(it,"loadingDialog")
            }
        }
    }

    fun showErrorDialog(errorMessage:String? = ""){
        baseContext.alert("$errorMessage","Terjadi Kesalahan")
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

    fun callToFinish_(finishWithResfresh:Boolean){
        if (finishWithResfresh){
            val intent = Intent()
            setResult(Activity.RESULT_OK,intent)
            finish()
        }else
            finish()
    }


    fun callToFinish_(){
        val intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    fun logout() {
        val dialog : AlertDialog = AlertDialog.Builder(this)
            .setTitle("Keluar")
            .setMessage("Apakah kamu yakin ingin keluar ?")
            .setNeutralButton("CANCEL"){
                    dialog, which -> dialog.dismiss()
            }
            .setPositiveButton("OK"){
                    dialog, which ->

                if (userViewModel.getCurrentUser() == null){
                    finish()
                }else {
                    userViewModel.logOut()
                    setLogout(true)
                    MainActivity.getStaredIntent(this,"true")
                    finish()
                }


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

    private fun setLogout(isLogout:Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putBoolean(Constants.IS_LOGOUT, isLogout)
        editor.commit()
    }



}