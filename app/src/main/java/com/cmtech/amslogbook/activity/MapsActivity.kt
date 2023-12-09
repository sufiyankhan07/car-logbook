package com.cmtech.amslogbook.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.cmtech.amslogbook.Permission.PermissionRequest
import com.cmtech.amslogbook.R
import com.github.florent37.runtimepermission.RuntimePermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.permissionx.guolindev.dialog.permissionMapOnQ
import kotlinx.coroutines.*
import java.util.*

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    companion object {

        const val MODE = "mode"

        const val DESTINATION = "Destination"

        const val SOURCE = "Source"

        const val LATITUDE = "latitude"

        const val LONGITUDE = "longitude"

        const val ADDRESS = "address"

    }

    private lateinit var mMap: GoogleMap

    private lateinit var job : Job
    private val serviceScope : CoroutineScope get() = CoroutineScope(Dispatchers.Main + job)

    private var destination : Marker? = null
    private var PermissionRequiest:PermissionRequest?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        PermissionRequiest= PermissionRequest(this)
        setContentView(R.layout.activity_maps)
        if (intent.getStringExtra(MODE) == DESTINATION) title = "Select Destination"
        else if (intent.getStringExtra(MODE) == SOURCE) title = "Select Source"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroy(){
        super.onDestroy()
        job.cancel()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        PermissionRequiest?.askLocationPermission(object : PermissionRequest.PermissionCallBack{
            override fun onPermissionGranted(type: String) {
                mMap.isMyLocationEnabled = true
            }

        })
//        RuntimePermission.askPermission(this,Manifest.permission.ACCESS_FINE_LOCATION).onAccepted {
//
//        }.onDenied{
//            finish()
//        }.onForeverDenied {
//            finish()
//        }.ask()
        mMap.setOnMyLocationChangeListener {
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(it.latitude
                        ,it.longitude),15f))
            if (intent.getStringExtra(MODE) == SOURCE) {
                mMap.clear()
                destination = mMap.addMarker(MarkerOptions().position(LatLng(it.latitude,it.longitude)).title(intent.getStringExtra(
                    MODE)))
            }
            mMap.setOnMyLocationChangeListener(null)
        }
        mMap.setOnMapClickListener{
            mMap.clear()
            destination = mMap.addMarker(MarkerOptions().position(it).title(intent.getStringExtra(
                MODE)))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return false
        }else if (id == R.id.select) {
            serviceScope.launch {
                saveAndExit()
            }
        }
        return false
    }

    private suspend fun saveAndExit() {
        if (destination!=null){
            showProgress()
            val intent = Intent()
            intent.putExtra(LATITUDE,destination?.position?.latitude)
            intent.putExtra(LONGITUDE,destination?.position?.longitude)
            intent.putExtra(ADDRESS,getAddress(destination?.position?.latitude,
                destination?.position?.longitude))
            setResult(Activity.RESULT_OK,intent)
            dismissProgress()
            finish()
        }else {
            Toast.makeText(this,"Please select "+intent.getStringExtra(MODE)+"!",Toast.LENGTH_SHORT).show()
        }
    }

        private suspend fun getAddress(latitude: Double?, longitude: Double?): String? = withContext(Dispatchers.IO) {
        try {
            if (latitude == null || longitude==null)  return@withContext null
            val geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0){
                val address = addresses[0].getAddressLine(0)
                return@withContext address.replace(",,", ",")
            }
        }catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
        return@withContext null
    }

}
