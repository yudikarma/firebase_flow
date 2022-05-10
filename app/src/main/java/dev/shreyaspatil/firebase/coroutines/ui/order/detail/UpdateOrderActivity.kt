package dev.shreyaspatil.firebase.coroutines.ui.order.detail


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dekape.core.dialog.showMessageDialog
import com.dekape.core.utils.currentDate
import com.dekape.core.utils.toCalendar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.StorageMetadata

import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentShowParcelBinding
import dev.shreyaspatil.firebase.coroutines.base.BaseActivity
import dev.shreyaspatil.firebase.coroutines.model.*
import dev.shreyaspatil.firebase.coroutines.ui.aktivitas.AktivitasHewanActivity
import dev.shreyaspatil.firebase.coroutines.ui.picture.DetailPictureActivity
import dev.shreyaspatil.firebase.coroutines.utils.*
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import permissions.dispatcher.*
import pl.aprilapps.easyphotopicker.*
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
@RuntimePermissions
class UpdateOrderActivity : BaseActivity<FragmentShowParcelBinding>() {
    companion object{
        const val ShowParcelRequestCode = 1033
        const val CAMERA_PERMISSION_CODE = 909

        fun getStaredIntent(activity: AppCompatActivity,  data: Orders,requestCode:Int){
            Intent(activity, UpdateOrderActivity::class.java)
                .putExtra("data",data)
                .run { activity.startActivityForResult(this,requestCode) }
        }

        fun getStaredIntent(activityCaller:Any, data: Orders, requestCode: Int){
            getCallerActivity(activityCaller)?.let {
                val intent = Intent(it.context, UpdateOrderActivity::class.java)
                intent.putExtra("data",data)
                it.startActivityForResult(intent,requestCode)
                //it.activity?.finish()

            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_show_parcel
    private lateinit var orders: Orders
    private  var users: Users?  = null
    private lateinit var binding: FragmentShowParcelBinding
    private var file_photo_barang: File? = null
    private var file_photo_barang_url :String = ""
    private var lastRequestCode = CreateOrderActivity.CAMERA_PERMISSION_CODE

    private val easyImage: EasyImage by lazy {
        EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = getViewDataBinding()
        setContentView(binding.root)

        orders = intent?.getParcelableExtra("data") ?: Orders()

        users = userViewModel.getDataUsersLocal(this)
        if (users?.rule.equals(Constants.RULE_USER)){
            binding.containerLihatAktivitas.visible()
            binding.cardviewPassFoto.gone()
            binding.containerCaption.gone()
            binding.containerBtn.gone()
        }else{
            binding.cardviewPassFoto.visible()
            binding.containerCaption.visible()
            binding.containerBtn.visible()

        }

        setupViewParcel(orders)

    }

    private fun setupViewParcel(data:Orders?) {

        data?.let { order ->

            binding.apply {
                namaPeliharaan.text = order.namaPeliharaan
                jenisHewan.text = order.jenisPeliharaan
                tglMasuk.text = order.localeDateCheckIn.toString()
                tglKeluar.text = order.localeDateCheckOut.toString()

                with(mapView) {
                    // Initialise the MapView
                    onCreate(null)
                    getMapAsync {
                        MapsInitializer.initialize(context)

                        order?.shipLat?.let { lat ->
                            order?.shipLng?.let { lng ->
                                setupMapView(LatLng(lat.toDouble(),lng.toDouble()),it)
                            }
                        }
                    }
                }

                if (order.status.equals(Constants.STATUS_ORDER_DONE)){
                    cardviewPassFoto.gone()
                    containerLihatAktivitas.visible()
                    containerCaption.gone()
                    btnSave.gone()
                    btnDone.gone()
                }

                cardviewPassFoto.setOnClickListener {
                    pickFotoWithPermissionCheck(CAMERA_PERMISSION_CODE)
                }


                //save update activitas
                btnSave.onClick {

                    val caption = catatan.text.toString()

                    if (caption.isEmpty() ||  file_photo_barang_url.isEmpty())
                        showMessageDialog("Harap masukkan foto aktivitas dan berikan keterangan terkait.")
                    else
                        updateAktivias(Aktivitas(tanggal = Calendar.getInstance().time, caption = caption, urlImg = file_photo_barang_url))

                }

                btnDone.onClick {
                    order.status = Constants.STATUS_ORDER_DONE
                    uiScope.launch {
                        orderViewsModel.updateOrders(order).collect { state ->
                            when(state){
                                is State.Loading -> showErrorDialog()
                                is State.Failed ->{
                                    dismissLoadingDialog()
                                    showErrorDialog(state.message)
                                }
                                is State.Success ->{
                                    dismissLoadingDialog()
                                    alert("Berhasil Mengubah Status Order") { positiveButton("OK") {
                                        callToFinish()
                                    } }.show()

                                }
                            }
                        }
                    }
                }

                containerLihatAktivitas.onClick {
                    AktivitasHewanActivity.getStaredIntent(this@UpdateOrderActivity,order.idAktivitas,orders)
                }
                pickPaket.onClick {
                    AktivitasHewanActivity.getStaredIntent(this@UpdateOrderActivity,order.idAktivitas,orders)
                }


            }
        }




    }

    fun setupMapView(destinationLatlng: LatLng, map: GoogleMap) {
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatlng, 13f))
            addMarker(MarkerOptions().position(destinationLatlng))
            mapType = GoogleMap.MAP_TYPE_NORMAL
            /*setOnMapClickListener {

            }*/

        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    private fun updateAktivias(aktivitas:Aktivitas?) {

        aktivitas?.let {
            uiScope.launch {
                orderViewsModel.addAktivitas(orders.uid, it).collect { state ->
                    when(state){
                        is State.Loading -> showLoadingDialog()
                        is State.Failed -> {
                            dismissLoadingDialog()
                            showErrorDialog(state.message)
                        }
                        is State.Success ->{
                            showAlert(
                                "Success",
                                "Berhasil Mengupdate Data Aktivitas"
                            ) {
                                callToFinish()
                            }
                        }
                    }
                }
            }
        }

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
        this.goToPermissionSetting(CreateOrderActivity.CAMERA_PERMISSION_CODE,this.getString(R.string.msg_permission_camera))
    }



    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionCameraDenied() {
        showAlert("Permission Help",getString(R.string.msg_permission_camera)){
            pickFoto(lastRequestCode)
        }
    }



    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionCameraNeverAskAgain() {
        showAlert("Permission Help",getString(R.string.msg_permission_camera)){
            this.goToPermissionSetting(lastRequestCode,getString(R.string.msg_permission_camera))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode,resultCode,data,this,object : DefaultCallback(){

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                val file_name = imageFiles[0].file
                file_name?.let {
                    when(lastRequestCode){

                        UpdateOrderActivity.CAMERA_PERMISSION_CODE -> {
                            file_photo_barang = file_name
                            file_photo_barang?.let {
                                uploadFotoHewan(it)

                            }

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

    private fun uploadFotoHewan(file: File) {
        showLoadingDialog()

        val fileUri = Uri.fromFile(file)
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        val randomNameImage = UUID.randomUUID().toString()
        val uploadTask = orderViewsModel.getOrderStorageRef()?.child("$randomNameImage")?.putFile(fileUri, metadata)
        uploadTask?.addOnFailureListener { exception ->
            dismissLoadingDialog()
            exception.message?.let { message -> showAlert("Succes Upload Foto") }

        }?.addOnSuccessListener { succes ->
            orderViewsModel.getOrderStorageRef()?.child("$randomNameImage")?.downloadUrl
                ?.addOnSuccessListener {
                    //success
                    dismissLoadingDialog()
                    file_photo_barang_url = it.toString()

                    //show image
                    binding.urlImg.loadFromUrl(it.toString())
                    binding.urlImg.setOnClickListener {
                        DetailPictureActivity.getStaredIntent(this,it.toString())
                    }
                    binding.urlImg.visible()

                }?.addOnFailureListener { exception ->
                    //failure
                    exception.message?.let { message -> alert(message) { positiveButton("OK") {} }.show() }
                    dismissLoadingDialog()

                }
        }
    }



    fun callToFinish(){
        val intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()
    }


}
