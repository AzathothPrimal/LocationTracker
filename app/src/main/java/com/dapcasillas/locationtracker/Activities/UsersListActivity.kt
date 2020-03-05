package com.dapcasillas.locationtracker.Activities

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dapcasillas.locationtracker.Activities.Adapters.UsersAdapter
import com.dapcasillas.locationtracker.Data.User
import com.dapcasillas.locationtracker.FireBase.FireBaseData
import com.dapcasillas.locationtracker.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class UsersListActivity : AppCompatActivity() {


    lateinit var db: FirebaseFirestore
    lateinit var sharedPreferences: SharedPreferences
    lateinit var email : String
    var usersList : MutableList<User> = mutableListOf<User>()
    lateinit private var usersAdapter: UsersAdapter
    lateinit var users_recycler: androidx.recyclerview.widget.RecyclerView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    // 1
    private lateinit var locationCallback: LocationCallback
    // 2
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        // 3
        private const val REQUEST_CHECK_SETTINGS = 2

        private const val PLACE_PICKER_REQUEST = 3

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)

        initViews()

    }

    fun initViews(){
        users_recycler = findViewById(R.id.users_recycler)
        //readBase()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                usersList.clear()
                FireBaseData().readData(this@UsersListActivity){
                    usersList.clear()
                    usersList = it
                    initUrlAdapter()
                }
            }
        }
        createLocationRequest()


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
                    e.startResolutionForResult(this@UsersListActivity,
                        UsersListActivity.REQUEST_CHECK_SETTINGS
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
                UsersListActivity.LOCATION_PERMISSION_REQUEST_CODE
            )

            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }


    fun readBase(){
        db = FirebaseFirestore.getInstance()
        db.collection(getString(R.string.collections_users))
            .whereEqualTo("type", "user")
            .get()
            .addOnSuccessListener { documents ->

                usersList.clear()

                for (document in documents) {
                    val geoPoint = document.get(getString(R.string.location_field)) as GeoPoint
                    usersList.add(User(document.get(getString(R.string.name_field)).toString(),
                        document.get(getString(R.string.email_field)).toString(),
                        geoPoint,
                        document.get(getString(R.string.type_field)).toString()
                        )
                    )
                }
                initUrlAdapter()

            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,  "Error getting documents: ",  Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun initUrlAdapter(){
        try {
            if(usersList.size>0) {

                users_recycler.layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(
                    1,
                    androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
                )
                usersAdapter = UsersAdapter(this, R.layout.row_user, usersList,lastLocation)
                users_recycler.adapter = usersAdapter
                usersAdapter.setOnItemClickListener(onUserClickListener)

                //usersAdapter.notifyDataSetChanged()
                usersAdapter.notifyItemRangeChanged(0, usersAdapter.getItemCount())
                Toast.makeText(this@UsersListActivity, "List Updated",
                    Toast.LENGTH_SHORT
                ).show()
            }




        } catch (ex : Exception){
            ex.printStackTrace()

        }
    }

    private val onUserClickListener = object : UsersAdapter.OnItemClickListener {
        override fun onItemClick(view: View, name: String?, email: String?, location: GeoPoint?){

            val intent = Intent(this@UsersListActivity, MapsActivity::class.java)
            intent.putExtra(getString(R.string.name_field), name)
            intent.putExtra(getString(R.string.email_field), email)
            intent.putExtra(getString(R.string.latitude_field), location?.latitude)
            intent.putExtra(getString(R.string.longitude_field), location?.longitude)
            intent.putExtra(getString(R.string.supervisor_latitude), lastLocation.latitude)
            intent.putExtra(getString(R.string.supervisor_longitude), lastLocation.longitude)

            startActivity(intent)
            //finish()
        }
    }
}
