package com.dapcasillas.locationtracker.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dapcasillas.locationtracker.FireBase.FireBaseData
import com.dapcasillas.locationtracker.R
import com.dapcasillas.locationtracker.Utilities.ConnectivityUtil
import com.dapcasillas.locationtracker.Utilities.MaterialDialogUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class LoginActivity : AppCompatActivity() {

    lateinit var et_user_name : EditText
    lateinit var et_password : EditText
    lateinit var btn_login : MaterialButton
    lateinit var sharedPreferences: SharedPreferences
    val MY_LOCATION = 0

    private var mAuth: FirebaseAuth? = null


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.getCurrentUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)

        initView()
        setEvents()
    }

    fun initView(){

        mAuth = FirebaseAuth.getInstance()
        et_user_name = findViewById(R.id.et_user_name)
        et_password = findViewById(R.id.et_password)
        btn_login = findViewById(R.id.btn_longin)
    }

    fun setEvents(){

        btn_login.setOnClickListener {

            validate()

        }

    }

    fun validate(){
        if (!et_user_name.text.toString().isNullOrEmpty() && !et_password.text.toString().isNullOrEmpty()){

            askPermissons()
        } else {
            MaterialDialogUtil().ShowCenterTitleMaterialDialog(
                this,
                getString(R.string.alert_generic_title),
                getString(R.string.validate_login),
                getString(R.string.alert_ok)
            )
        }

    }

    fun login() {
        if (ConnectivityUtil().isNetworkConnected(this@LoginActivity)) {
            mAuth?.signInWithEmailAndPassword(
                et_user_name.text.toString(),
                et_password.text.toString()
            )
                ?.addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {

                        val user = mAuth?.getCurrentUser()
                        if (user != null) {

                            var name = getString(R.string.general_name)
                            val email = user.email

                            var intent = Intent(this, UsersListActivity::class.java)
                            FireBaseData().getUser(this@LoginActivity, email?:""){
                                name = it.name.toString()

                                val editor = sharedPreferences.edit()
                                editor.putString(getString(R.string.pref_email), email)
                                editor.putString(getString(R.string.type_field), it.type.toString())
                                editor.apply()

                                if(it.type.equals(getString(R.string.user))){
                                    intent = Intent(this, MapsActivity::class.java)
                                }

                                val alertWelcome = getString(R.string.alert_welcome_title) + " "+ name

                                MaterialDialogUtil().ShowCenterIntentMaterialDialog(this@LoginActivity,
                                    intent,
                                    getString(R.string.alert_login_title),
                                    alertWelcome,
                                    getString(R.string.get_in))

                            }

                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this@LoginActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

        } else {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)

            MaterialDialogUtil().ShowCenterNoFIntentMaterialDialog(
                this,
                intent,
                getString(R.string.alert_generic_title),
                getString(R.string.no_internet),
                getString(R.string.connect)
            )

        }
    }


    fun askPermissons() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            login()

        } else {

            val PermissionsNeeded = ArrayList<String>()

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {

                PermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (!PermissionsNeeded.isEmpty()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.alert_generic_title))
                    .setMessage(getString(R.string.alert_ask_permision))
                    .setPositiveButton(getString(R.string.alert_accept_permision)) { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this,
                            PermissionsNeeded.toTypedArray(),
                            MY_LOCATION
                        )
                    }
                    .setNegativeButton(getString(R.string.alert_ok)) { dialog, which ->
                    }
                    .show().setCancelable(false)

            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_LOCATION -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    login()
                } else {

                    runOnUiThread {
                        val i = Intent()
                        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        i.addCategory(Intent.CATEGORY_DEFAULT)
                        i.data =
                            Uri.parse("package:" + packageName)
                        MaterialDialogUtil().ShowCenterNoFIntentMaterialDialog(
                            this,
                            i,
                            getString(R.string.alert_generic_title),
                            getString(R.string.alert_ask_again),
                            getString(R.string.connect)
                        )

                    }
                }
                return
            }
        }
    }


}
