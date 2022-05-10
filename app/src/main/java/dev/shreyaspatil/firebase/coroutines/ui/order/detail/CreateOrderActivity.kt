package dev.shreyaspatil.firebase.coroutines.ui.order.detail


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dekape.core.utils.currentDateMutation
import com.dekape.core.utils.toCalendar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.StorageMetadata
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentCreateParcelBinding
import dev.shreyaspatil.firebase.coroutines.model.MapsModel
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.model.Paket
import dev.shreyaspatil.firebase.coroutines.base.BaseActivity
import dev.shreyaspatil.firebase.coroutines.ui.history.HistoryFragment
import dev.shreyaspatil.firebase.coroutines.ui.history.HistoryFragment.Companion.DATE_CODE_FROM
import dev.shreyaspatil.firebase.coroutines.ui.history.HistoryFragment.Companion.DATE_CODE_TO
import dev.shreyaspatil.firebase.coroutines.ui.maps.PickMaps
import dev.shreyaspatil.firebase.coroutines.ui.maps.PickMapsActivity
import dev.shreyaspatil.firebase.coroutines.ui.paket.PickPaketActivity
import dev.shreyaspatil.firebase.coroutines.ui.picture.DetailPictureActivity
import dev.shreyaspatil.firebase.coroutines.utils.*
import dev.shreyaspatil.firebase.coroutines.utils.Firebase.uploadToFirebase
import dev.shreyaspatil.firebase.coroutines.utils.Utils.generateKeywords
import dev.shreyaspatil.firebase.coroutines.utils.Utils.getCurrentDate
import dev.shreyaspatil.firebase.coroutines.utils.Utils.getTimeStampFromDateStr
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.loadFromUrl
import dev.shreyaspatil.firebase.coroutines.utils.ekstension.toPriceFormat
import kotlinx.android.synthetic.main.fragment_show_parcel.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.alert
import permissions.dispatcher.*
import pl.aprilapps.easyphotopicker.*
import java.io.File
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

@RuntimePermissions
class CreateOrderActivity : BaseActivity<FragmentCreateParcelBinding>() , DatePickerDialog.OnDateSetListener{

    companion object{
        const val CAMERA_PERMISSION_CODE = 909
        const val LOCATION_PERMISSION_CODE = 1000
        const val CreateParcelRequestCode = 1032
        fun getStaredIntent( activityCaller:Any,requestCode: Int){
            getCallerActivity(activityCaller)?.let {
                val intent = Intent(it.context, CreateOrderActivity::class.java)
                it.startActivityForResult(intent,requestCode)
                //it.activity?.finish()

            }
        }
    }

    private var selectedDateFrom = getCurrentDate().toCalendar()
    private var selectedDateFromTimeStamp :Date? = null
    private var selectedDateFromString = ""
    private var selectedDateTo = getCurrentDate().toCalendar()
    private var selectedDateToTimeStamp:Date? = null
    private var selectedDateToString = ""
    private var minimumDate = getCurrentDate().toCalendar()
    private var maximumDate = getCurrentDate().toCalendar()


    private var lasDateCode = HistoryFragment.DATE_CODE_FROM;

    private var file_photo_barang: File? = null
    private var file_photo_barang_url :String = ""

    private var paket:Paket? = null
    private var mapsModel: MapsModel? = null

    private val easyImage: EasyImage by lazy {
        EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .build()
    }

    private var lastRequestCode = CAMERA_PERMISSION_CODE

    override fun getLayoutId(): Int = R.layout.fragment_create_parcel
    private lateinit var binding: FragmentCreateParcelBinding

    init {
        selectedDateTo.set(Calendar.DAY_OF_MONTH,selectedDateFrom.get(Calendar.DAY_OF_MONTH)+1)
        maximumDate.set(Calendar.DAY_OF_MONTH,maximumDate.get(Calendar.DAY_OF_MONTH)+1)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)

        with(binding){

            btnSave.onClick { validateData() }

            btnFotoHewan.onClick {
                pickFotoWithPermissionCheck(CAMERA_PERMISSION_CODE)

            }

            tglMasuk.onClick {
                openDatePicker(DATE_CODE_FROM)
            }

            tglKeluar.onClick {
                openDatePicker(DATE_CODE_TO)

            }

            pickPaket.setOnClickListener{
                PickPaketActivity.getStaredIntent(this@CreateOrderActivity,PickPaketActivity.PickKabupatenIntentCode)

            }

            with(mapView) {
                // Initialise the MapView
                onCreate(savedInstanceState)
                getMapAsync {
                    MapsInitializer.initialize(context)

                    setupMapView(LatLng(-6.298281,106.895478),it)
                }
            }

            pickMaps.setOnClickListener{
                callPickMapsWithPermissionCheck(LOCATION_PERMISSION_CODE)
            }

            selectedDateFrom = minimumDate
            //selectedDateTo.set(2030,11,28,23,59,59)
            val mFrom = (selectedDateFrom.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val dFrom = (selectedDateFrom.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')

            val mTo = (selectedDateTo.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val dTo = (selectedDateTo.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
            selectedDateFromString = String.format("%s/%s/%d", dFrom,mFrom,selectedDateFrom.get(Calendar.YEAR))
            selectedDateToString = String.format("%s/%s/%d", dTo,mTo,selectedDateTo.get(Calendar.YEAR))

            selectedDateFromTimeStamp = selectedDateFrom.time
            selectedDateToTimeStamp = selectedDateTo.time
            tglMasuk.setText(selectedDateFromString)
            tglKeluar.setText(selectedDateToString)
        }

    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
    fun callPickMaps(requestCode: Int){
        this.lastRequestCode = requestCode
        PickMapsActivity.start(this, PickMaps.INTENT_PICKER)
    }

    private fun validateData() {
        val user = userViewModel.getDataUsersLocal(this)
        var uid:String=""
        var uidUsers = user?.uid ?: ""
        var shipLat = mapsModel?.latitude?:0.0
        var shipLng = mapsModel?.longitude ?: 0.0
        var shipAddress = binding.adress.text.toString()
        var timeCheckIn = binding.tglMasuk.text.toString()
        var timeCheckOut = binding.tglKeluar.text.toString()
        var namaPeliharaan = binding.namaPeliharaan.text.toString()
        var jenisPeliharaan = binding.jenisHewan.text.toString()
        var umurPeliharaan = binding.umurHewan.text.toString()
        var tandaSpesialPeliharaan = binding.tandaKhususHewan.text.toString()
        val catatan = binding.catatanHewan.text.toString()

        val noHpPengirim  = user?.nohp ?: ""
        var namaPelangan = user?.name ?: ""
        var uidPaket = paket?.uid?:""
        val namaPaket = paket?.name ?: ""
        var price = paket?.price ?: "0"
        if (!shipAddress.isNullOrBlank() || namaPeliharaan.isNullOrBlank() || catatan.isNullOrBlank()
                    || timeCheckIn.isNullOrBlank() || timeCheckOut.isNullOrBlank()
                    || paket == null || mapsModel == null || timeCheckIn == null || user == null){

                /*if (!file_photo_barang_url.isNullOrBlank()){
                    if (paket != null){*/
                        //save data
                        showLoadingDialog()

                        val millionSeconds = selectedDateToTimeStamp?.time?.minus(selectedDateFromTimeStamp?.time!!)
                        val daysDifference = (millionSeconds?.div((1000 * 60 * 60 * 24)))?.toInt()
                       val totalOngkir =  (price?.toInt()!! * daysDifference!!).toString()


                        val orders = Orders(
                            uidPaket = uidPaket,
                            catatan = catatan,
                            jenisPeliharaan = jenisPeliharaan,
                            namaPaket = namaPaket,
                            namaPelangan = namaPelangan,
                            namaPeliharaan = namaPeliharaan.toLowerCase(),
                            noHpPengirim = noHpPengirim,
                            price = totalOngkir,
                            shipAddress = shipAddress,
                            shipLat = shipLat.toString(),
                            shipLng = shipLng.toString(),
                            tandaSpesialPeliharaan = tandaSpesialPeliharaan,
                            timeCheckIn = selectedDateFromTimeStamp,
                            timeCheckOut = selectedDateToTimeStamp,
                            uidUsers = uidUsers,
                            umurPeliharaan = umurPeliharaan,
                            fotoPeliharaan = file_photo_barang_url,
                            keyword = generateKeywords(namaPeliharaan)
                        )

                        uiScope.launch {
                            orderViewsModel.addOrders(orders).collect { state ->
                                when (state) {
                                    is State.Loading -> showLoadingDialog()
                                    is State.Failed -> {
                                        dismissLoadingDialog()
                                        showErrorDialog(state.message)
                                    }
                                    is State.Success -> {
                                        dismissLoadingDialog()
                                        showAlert(
                                            "",
                                            message = "Pemesanan Berhasil"
                                        ) {
                                            callToFinish()
                                        }
                                    }


                                }
                            }
                        }
            }else{
                showAlert("Harap Isi Semua data")
            }
        }




    fun callToFinish(){
        val intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()
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
        this.goToPermissionSetting(CAMERA_PERMISSION_CODE,this.getString(R.string.msg_permission_camera))
    }

    fun onShowRationaleDialogLocation(){
        this.goToPermissionSetting(LOCATION_PERMISSION_CODE,getString(R.string.msg_permission_location))
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

    @OnPermissionDenied(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun onPermissionLocationDenied() {
        showAlert("Permission Help",getString(R.string.msg_permission_location)){
            callPickMapsWithPermissionCheck(lastRequestCode)
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

    @OnNeverAskAgain(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun onPermissionLocationNeverAskAgain() {
        showAlert("Permission Help",getString(R.string.msg_permission_location)){
            this.goToPermissionSetting(lastRequestCode,getString(R.string.msg_permission_location))
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            /*ListMobilActivity.ListMobilIntentCode ->{
                pets = data?.getParcelableExtra("data") as? Mobil
                if (pets != null)
                    pick_mobil.text = pets?.jenis
            }*/

            PickPaketActivity.PickKabupatenIntentCode -> {
                paket  = data?.getParcelableExtra("data") as? Paket
                if (paket != null)
                    showViewKabupaten(paket)
            }

            PickMaps.INTENT_PICKER -> {
                PickMaps.handleActivityResult(requestCode,resultCode,data){lat, lng,adress ->
                    mapsModel = MapsModel(lat,lng)

                    binding.mapView.getMapAsync {
                        setupMapView(LatLng(mapsModel?.latitude?:0.0,mapsModel?.longitude?:0.0),it)
                        binding.mapView.visible()

                    }

                }
            }
        }
        easyImage.handleActivityResult(requestCode,resultCode,data,this,object : DefaultCallback(){

            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                val file_name = imageFiles[0].file
                file_name?.let {
                    when(lastRequestCode){

                        CAMERA_PERMISSION_CODE -> {
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
                    binding.avatar.loadFromUrl(it.toString())
                    binding.avatar.setOnClickListener {
                        DetailPictureActivity.getStaredIntent(this,it.toString())
                    }

                }?.addOnFailureListener { exception ->
                    //failure
                    exception.message?.let { message -> alert(message) { positiveButton("OK") {} }.show() }
                    dismissLoadingDialog()

                }
        }
    }

    fun showViewKabupaten(data:Paket?){
        //estimasi.text = data?.estimasiPengiriman
        val millionSeconds = selectedDateToTimeStamp?.time?.minus(selectedDateFromTimeStamp?.time!!)
        val daysDifference = (millionSeconds?.div((1000 * 60 * 60 * 24)))?.toInt()
        binding.ongkir.text = (data?.price?.toInt()!! * daysDifference!!).toString().toPriceFormat()
        binding.txtTitleHarga.text = "Total Harga /$daysDifference Hari"
        binding.pickPaket.text = data?.name
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    fun setupMapView(destinationLatlng: LatLng, map: GoogleMap) {
        with(map) {
            clear()
            moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatlng, 13f))
            addMarker(MarkerOptions().position(destinationLatlng))
            mapType = GoogleMap.MAP_TYPE_NORMAL
            setOnMapClickListener {
                //toast("click marker")
            }

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

    private fun openDatePicker(lastDateCode:Int){
        this.lasDateCode = lastDateCode
        val now = if(lasDateCode == HistoryFragment.DATE_CODE_FROM) selectedDateFrom else selectedDateTo
        val dpd: DatePickerDialog = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )
        dpd.show(supportFragmentManager, "Datepickerdialog");
        /*parentFragmentManager?.let {
            DatePickerDialog
                .setTitle("Pilih Tanggal")
                .setButtonText("Pilih")
                .setMaxDate(maximumDate)
                .setMinDate(minimumDate)
                .setSelectedDate(if(lasDateCode == DATE_CODE_FROM) selectedDateFrom else selectedDateTo)
                .build(this)
                .show(it, "picker")
        }*/
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        if(lasDateCode == HistoryFragment.DATE_CODE_FROM){
            selectedDateFrom.set(year, monthOfYear, dayOfMonth,1,1,1)
            val mFrom = (selectedDateFrom.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val dFrom = (selectedDateFrom.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
            selectedDateFromString = String.format("%s/%s/%d", dFrom,mFrom,selectedDateFrom.get(Calendar.YEAR))
            selectedDateFromTimeStamp = selectedDateFrom.time

            binding.tglMasuk?.setText(selectedDateFromString)

        } else{
            selectedDateTo.set(year, monthOfYear, dayOfMonth,23,59,59)
            val mTo = (selectedDateTo.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val dTo = (selectedDateTo.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
            selectedDateToString = String.format("%s/%s/%d", dTo,mTo,selectedDateTo.get(Calendar.YEAR))
            selectedDateToTimeStamp = selectedDateTo.time
            binding.tglKeluar.setText(selectedDateToString)


        }

    }


}
