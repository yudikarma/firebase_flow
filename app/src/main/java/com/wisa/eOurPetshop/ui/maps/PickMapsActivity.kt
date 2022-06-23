package com.wisa.eOurPetshop.ui.maps

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dekape.core.utils.toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.base.BaseActivity
import com.wisa.eOurPetshop.databinding.ActivityPickMapsBinding
import com.wisa.eOurPetshop.utils.MapUtil
import com.wisa.eOurPetshop.utils.onClick
import java.io.IOException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class PickMapsActivity : BaseActivity<ActivityPickMapsBinding>(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    OnMapReadyCallback{

    private var googleMap: GoogleMap? = null
    private val mGoogleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
    }

    private val placeFields by lazy {
        listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS
        )
    }

    private var mCurrentLocation: Location? = null
    private var fromMap = true
    var latitude: Double? = null
    var longitude: Double? = null
    var apiKey: String? = null
    private lateinit var binding: ActivityPickMapsBinding

    override fun getLayoutId(): Int = R.layout.activity_pick_maps


    companion object {
        fun start(activity: Activity,requestCode: Int) {
            Intent(activity, PickMapsActivity::class.java)
                .run { activity.startActivityForResult(this,requestCode) }
        }

        private const val REQUEST_SELECT_PLACE = 101
    }



    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)
        //toolbar?.setNavigationOnClickListener { finish() }

       // presenter.apiKey = getString(R.string.maps_key)
        com.google.android.libraries.places.api.Places.initialize(this, resources.getString(R.string.maps_key))

        with(binding.mapLocation){
            onCreate(savedInstanceState)
            getMapAsync(this@PickMapsActivity)
            startLocationUpdates()
        }
    }



    fun startLocationUpdates() {
        mGoogleApiClient.connect()
    }

    override fun onStart() {
        super.onStart()
        mGoogleApiClient.connect()
    }

    override fun onResume() {
        super.onResume()
        binding.mapLocation?.onResume()
        binding.btnClickMarker.onClick{
            Intent().apply {
                val lat = googleMap?.cameraPosition?.target?.latitude
                val lng = googleMap?.cameraPosition?.target?.latitude

                putExtra(PickMaps.EXTRA_LAT, googleMap?.cameraPosition?.target?.latitude)
                putExtra(PickMaps.EXTRA_LONG, googleMap?.cameraPosition?.target?.longitude)


                var list : ArrayList<Address> = arrayListOf()
                val geoCoder = Geocoder(this@PickMapsActivity, Locale.getDefault())
                val sb = StringBuilder()
                try {
                   lat?.let { it1 -> lng?.let { it2 ->
                       list.addAll(geoCoder.getFromLocation(it1, it2,1))

                   } }
                }catch (e : IOException){
                    Log.d("PICKMAPS",e.message.toString())
                }
                if (list.size > 0) {
                    val address: String = list.get(0).getAddressLine(0)
                    val city: String = list.get(0).getLocality()
                    val state: String = list.get(0).getAdminArea()
                    val zip: String = list.get(0).getPostalCode()
                    val country: String = list.get(0).getCountryName()

                    sb.append(address+city+state+zip+country)
                    Log.d("adress : ",sb.toString())
                    putExtra(PickMaps.EXTRA_ADRESS, sb.toString())



                }


            }.run {
                setResult(Activity.RESULT_OK, this)
                finish()
            }
        }
    }

    override fun onPause() {
        binding.mapLocation?.onPause()
        mGoogleApiClient.disconnect()
        super.onPause()
    }

    override fun onStop() {
        mGoogleApiClient.disconnect()
        super.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapLocation?.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            googleMap.isMyLocationEnabled = true
            googleMap.setOnMapClickListener {
                fromMap = true
            }
            googleMap.setOnCameraIdleListener {
                val cameraPosition = googleMap.cameraPosition
                if (cameraPosition != null) {
                    googleMap.clear()

                    if (fromMap) getLocationGeocode(
                        cameraPosition.target.latitude,
                        cameraPosition.target.longitude
                    )
                    else fromMap = true
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        this.googleMap = googleMap

    }

    private fun getLocationGeocode(lat: Double, lng: Double) {
        latitude = lat
        longitude = lng
    }

    fun showLoading() {
        binding.btnClickMarker?.visibility = View.GONE
        binding.pbCenterPoi?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.btnClickMarker?.visibility = View.VISIBLE
        binding.pbCenterPoi?.visibility = View.GONE
    }



    override fun onConnected(bundle: Bundle?) {
        try {
            val providerClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
            providerClient.lastLocation.addOnSuccessListener { location ->
                mCurrentLocation = location
                mCurrentLocation?.let {
                    MapUtil.setCamera(LatLng(it.latitude, it.longitude), googleMap)
                }
            }.addOnFailureListener { exception ->
                toast(exception.message ?: "Gagal mendapatkan lokasi anda")
                startLocationUpdates()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onConnectionSuspended(p0: Int) = Unit

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution())
            try {
                connectionResult.startResolutionForResult(this, 61124)
            } catch (e: IntentSender.SendIntentException) {
                mGoogleApiClient.connect()
                e.printStackTrace()
            }
        else toast(connectionResult.errorMessage ?: "Gagal mendapatkan lokasi")
    }





}
