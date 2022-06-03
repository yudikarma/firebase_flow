package dev.shreyaspatil.firebase.coroutines.ui.registrasi


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.dekape.core.utils.goToPermissionSetting
import com.dekape.core.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageMetadata
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import permissions.dispatcher.*
import dev.shreyaspatil.firebase.coroutines.R
import java.io.File
import java.util.*
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentRegistrasiBinding
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.base.BaseFragment
import dev.shreyaspatil.firebase.coroutines.ui.picture.DetailPictureActivity
import dev.shreyaspatil.firebase.coroutines.utils.*
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.*


/**
 * A simple [Fragment] subclass.
 */
@RuntimePermissions
class RegistrasiFragment : BaseFragment<FragmentRegistrasiBinding>() {

    companion object {

        const val CAMERA_PERMISSION_CODE = 909
        fun directionToMe(activity: FragmentActivity, rule: String) {
            val mainNavView = activity.findViewById<View>(R.id.main_fragment)
            var bundle = bundleOf("rule" to rule)
            Navigation.findNavController(mainNavView).navigate(R.id.registrasiFragment, bundle)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_registrasi

    private lateinit var binding : FragmentRegistrasiBinding


    private var file_photo_profil: File? = null
    private var file_photo_profil_url :String = ""
    private var userRule :String? = null

    private val easyImage: EasyImage by lazy {
        EasyImage.Builder(context)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .build()
    }

    private var lastRequestCode = CAMERA_PERMISSION_CODE

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

        val safeArgs: RegistrasiFragmentArgs by navArgs()
        userRule = safeArgs.rule


        binding.registBtn.onClick { validateDataregister() }
        binding.btnLogin.onClick { activity.onBackPressed() }

        binding.btnCamera.onClick {
            pickFotoWithPermissionCheck(lastRequestCode)
         }

        getView()!!.isFocusableInTouchMode = true
        getView()!!.requestFocus()

        getView()!!.setOnKeyListener(object : View.OnKeyListener {
            override
            fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.getAction() === KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Navigation.findNavController(v).popBackStack()
                        return true
                    }
                }
                return false
            }
        })

    }


    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun pickFoto(requestCode:Int) {
        this.lastRequestCode = requestCode
        easyImage.openChooser(this)

    }

    @OnShowRationale(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onShowRationaleDialogCamera(request: PermissionRequest) {
       activity.goToPermissionSetting(CAMERA_PERMISSION_CODE,getString(R.string.msg_permission_camera))
    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionCameraDenied() {
       context.showAlert("Permission Help",getString(R.string.msg_permission_camera)){
           pickFoto(lastRequestCode)
       }
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionCameraNeverAskAgain() {
        context.showAlert("Permission Help",getString(R.string.msg_permission_camera)){
            activity.goToPermissionSetting(lastRequestCode,getString(R.string.msg_permission_camera))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode,resultCode,data,activity,object : DefaultCallback(){
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                val file_name = imageFiles[0].file
                file_name?.let {
                    when(lastRequestCode){

                        CAMERA_PERMISSION_CODE -> {
                            file_photo_profil = file_name
                            file_photo_profil?.let { uploadToFirebase(it) }
                        }
                        else ->{
                            //do nothing
                        }
                    }
                }


            }

            override fun onImagePickerError(error: Throwable, source: MediaSource) {
               showErrorDialog("${error.message}")
            }


        })
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun validateDataregister() {
        val email = binding.regEmail?.text.toString()
        val password = binding.regPassword?.text.toString()
        val passwordConfirm = binding.etConfirmPassword?.text.toString()
        val name = binding.regDisplayName?.text.toString()
        val noHp = binding.regNoHp.text.toString()


        if (email.isEmpty() || password.isEmpty() ||
            passwordConfirm.isEmpty() ||
            name.isEmpty()  ||
            noHp.isEmpty() ||
            userRule == null
        ) {

            when {
                email.isEmpty() -> {
                    binding.regEmail.error = "Required"
                    return
                }
                password.isEmpty() -> {
                    binding.regPassword.error = "Required"
                    return
                }
                passwordConfirm.isEmpty() -> {
                    binding.regPassword.error = "Required"
                    return
                }
                name.isEmpty() -> {
                    binding.regDisplayName.error = "Required"
                    return
                }
                noHp.isEmpty() -> {
                    binding.regNoHp.error = "Required"
                    return
                }else ->{
                    toast("Please Check Your Rule User")
                return
                }
            }


        } else {

            if(password.length <= 5){
                toast("password harus 6 karakter")
            } else if (!password.equals(passwordConfirm)){
                toast("Password harus sama dengan confirm password")
            }else if (!email.isValidEmail()){
                context?.showAlert("Email Tidak Valid")
            }
            else{
                //call function to register
                registerUser()
            }
        }
    }

    private fun registerUser() {

        val email = binding.regEmail.text.toString()
        val password = binding.regPassword.text.toString()
        val name = binding.regDisplayName.text.toString()
        val noHp = binding.regNoHp.text.toString()

        val profilPict = file_photo_profil_url

        uiScope.launch {
            usersviewModel.getUsersIsRegistered(email).collect { state ->
                when(state){
                    is State.Loading -> showLoadingDialog()
                    is State.Failed -> {
                        dismissLoadingDialog()
                        showErrorDialog(state.message)
                    }
                    is State.Success ->{
                            if (state.data){
                                dismissLoadingDialog()
                                showErrorDialog("Email Sudah Terdaftar Pada Sistem Kami, Silahkan Login")
                            }else{
                                    usersviewModel.getAuth()?.createUserWithEmailAndPassword(email, password)
                                        ?.addOnCompleteListener { taskAuth ->
                                            if (taskAuth.isSuccessful) {
                                                var uid = usersviewModel.getAuth()?.uid.toString()
                                                val user = Users(uid = uid,email = email.toLowerCase(),name = name,nohp = noHp,password = password,rule = userRule?:Constants.RULE_USER,urlPict = profilPict)

                                                uiScope.launch {
                                                    usersviewModel.addUsers(user).collect { state ->
                                                        when(state){
                                                            is State.Failed -> {
                                                                dismissLoadingDialog()
                                                                showErrorDialog(state.message)
                                                            }
                                                            is State.Success -> {
                                                                dismissLoadingDialog()
                                                                context.showAlert(
                                                                    "",
                                                                    "Registrasi berhasil. Silahkan login kembali"
                                                                ){
                                                                    view?.let {
                                                                        Navigation.findNavController(it).popBackStack()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }



                                            } else {
                                                //fail create user to firebase auth
                                                //fail create user to firebase auth
                                                if (taskAuth.exception?.message.equals("The email address is already in use by another account.")){
                                                    usersviewModel.getAuth()?.signInWithEmailAndPassword(email,password)
                                                        ?.addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                val user = FirebaseAuth.getInstance().currentUser
                                                                user?.delete()
                                                                    ?.addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {
                                                                            registerUser()
                                                                        }
                                                                    }

                                                            }
                                                        }
                                                }else{
                                                    showErrorDialog(taskAuth.exception?.message)
                                                    dismissLoadingDialog()
                                                }
                                            }
                                }
                            }
                    }
                }
            }
        }

    }

    private fun uploadToFirebase(file: File) {

        showLoadingDialog()

        val fileUri = Uri.fromFile(file)
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        val randomNameImage = UUID.randomUUID().toString()
        val uploadTask = usersviewModel.getProfileStorageRef()?.child("$randomNameImage")?.putFile(fileUri, metadata)
        uploadTask?.addOnFailureListener { exception ->

            exception.message?.let { message -> alert(message) { positiveButton("OK") {} }.show() }
            dismissLoadingDialog()

        }?.addOnSuccessListener { succes ->
            usersviewModel?.getProfileStorageRef()?.child("$randomNameImage")?.downloadUrl
                ?.addOnSuccessListener {
                    //success
                    dismissLoadingDialog()
                    file_photo_profil_url = it.toString()

                    //show image
                    binding.avatar.loadFromUrl(it.toString())

                    binding.avatar.setOnClickListener {
                        DetailPictureActivity.getStaredIntent(context,it.toString())
                    }

                }?.addOnFailureListener { exception ->
                    //failure
                    exception.message?.let { message -> alert(message) { positiveButton("OK") {} }.show() }
                    dismissLoadingDialog()

                }
        }
    }



}
