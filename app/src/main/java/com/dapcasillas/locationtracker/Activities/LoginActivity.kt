package com.dapcasillas.locationtracker.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.dapcasillas.locationtracker.R
import com.dapcasillas.locationtracker.Utilities.ConnectivityUtil
import com.dapcasillas.locationtracker.Utilities.MaterialDialogUtil


class LoginActivity : AppCompatActivity() {

    lateinit var et_user_name : EditText
    lateinit var et_password : EditText
    lateinit var btn_login : MaterialButton
    lateinit var sharedPreferences: SharedPreferences


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
            login()
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
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth?.getCurrentUser()
                        if (user != null) {
                            // Name, email address, and profile photo Url
                            val name = user.displayName
                            val email = user.email
                            val photoUrl = user.photoUrl

                            // Check if user's email is verified
                            val emailVerified = user.isEmailVerified

                            // The user's ID, unique to the Firebase project. Do NOT use this value to
                            // authenticate with your backend server, if you have one. Use
                            // FirebaseUser.getIdToken() instead.
                            val uid = user.uid
                            Toast.makeText(
                                this@LoginActivity, "Authentication Succesfull " + email,
                                Toast.LENGTH_SHORT
                            ).show()
                            val editor = sharedPreferences.edit()
                            editor.putString(getString(R.string.pref_email), email)
                            editor.apply()

                            val intent = Intent(this, UsersListActivity::class.java)
                            startActivity(intent)
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


}
