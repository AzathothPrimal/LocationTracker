package com.dapcasillas.locationtracker.Activities

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.dapcasillas.locationtracker.FireBase.FireBaseData
import com.dapcasillas.locationtracker.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class SupervisorsMapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {
    override fun onMarkerClick(p0: Marker?): Boolean  = false

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    lateinit var db: FirebaseFirestore
    lateinit var sharedPreferences: SharedPreferences
    lateinit var email : String
    lateinit var type : String
    private var UserMarker: Marker? = null


    var userName : String = "UserName"
    var userEmail : String = "userEmail"
    lateinit var userLocation : Location
    lateinit var supervisorLocation : Location




    // 1
    private lateinit var locationCallback: LocationCallback
    // 2
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        // 3
        private const val REQUEST_CHECK_SETTINGS = 2

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@SupervisorsMapsActivity, UsersListActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)

        try {
            userName = intent.getStringExtra(getString(R.string.name_field)) ?: ""
            userEmail = intent.getStringExtra(getString(R.string.email_field)) ?: ""
            userLocation = Location("UserLocastions")
            supervisorLocation = Location("SupervisorLocation")
            userLocation.latitude = intent.getDoubleExtra(getString(R.string.latitude_field), 0.0)
            userLocation.longitude = intent.getDoubleExtra(getString(R.string.longitude_field), 0.0)
            supervisorLocation.latitude = intent.getDoubleExtra(getString(R.string.supervisor_latitude), 0.0)
            supervisorLocation.longitude = intent.getDoubleExtra(getString(R.string.supervisor_longitude), 0.0)

        } catch (ex: Exception){
            ex.printStackTrace()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@SupervisorsMapsActivity)


        try {
            initViews()
        }catch (ex: Exception){
            ex.printStackTrace()
        }

    }

    private fun initViews(){

        email = sharedPreferences.getString(getString(R.string.pref_email),"") ?: ""
        type = sharedPreferences.getString(getString(R.string.type_field),"") ?: ""


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@SupervisorsMapsActivity)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                supervisorLocation = lastLocation

            }
        }

        createLocationRequest()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setUpMap()

        mMap.getUiSettings().setZoomControlsEnabled(true)
        mMap.setOnMarkerClickListener(this)


        val userMarkerTitle = userName + "\n" + userEmail
        val userMarker = LatLng(userLocation.latitude, userLocation.longitude)
        mMap.addMarker(
            MarkerOptions().position(userMarker)
                .title(userMarkerTitle)
                .snippet("Distancia: " + supervisorLocation.distanceTo(userLocation).toString() + " metros")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userMarker))

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true


        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //placeMarkerOnMap(currentLatLng)
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = (10*1000)
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@SupervisorsMapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )

            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }


}
