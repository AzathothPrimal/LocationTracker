package com.dapcasillas.locationtracker.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.dapcasillas.locationtracker.R
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dapcasillas.locationtracker.Utilities.CreateDocumentsUtil
import com.dapcasillas.locationtracker.Utilities.MaterialDialogUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.selector


class RegisterActivity : AppCompatActivity() {

    lateinit var et_type : EditText
    lateinit var et_name : EditText
    lateinit var et_email : EditText
    lateinit var et_password : EditText
    lateinit var btn_register: MaterialButton
    val userTypes = listOf("Usuario", "Supervisor")
    var type = ""
    val MY_LOCATION = 0
    lateinit var sharedPreferences: SharedPreferences



    private var mAuth: FirebaseAuth? = null
    lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)

        initView()
        setEvents()
    }

    fun initView(){
        et_type = findViewById(R.id.et_type)
        et_name = findViewById(R.id.et_name)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        btn_register = findViewById(R.id.btn_register)
    }

    fun setEvents(){

        btn_register.setOnClickListener {
            validate()
        }

        et_type.setOnClickListener {
            selector(getString(R.string.user_types), userTypes, { dialogInterface, i ->
                et_type.setText(userTypes[i])
                if (userTypes[i].equals("Usuario")) type = getString(R.string.user)
                else type = getString(R.string.supervisor)
            })
        }

    }

    fun validate(){
        if (!et_type.text.toString().isNullOrEmpty()
            && !et_name.text.toString().isNullOrEmpty()
            && !et_email.text.toString().isNullOrEmpty()
            && !et_password.text.toString().isNullOrEmpty()

        ){
            createUser()
        } else {
            MaterialDialogUtil().ShowCenterTitleMaterialDialog(
                this,
                getString(R.string.alert_generic_title),
                getString(R.string.validate_register),
                getString(R.string.alert_ok)
            )
        }

    }

    fun createUser(){
        mAuth?.createUserWithEmailAndPassword(et_email.text.toString(), et_password.text.toString())
            ?.addOnCompleteListener(this,
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth?.getCurrentUser()
                        Toast.makeText(
                            this, "Register Successful.",
                            Toast.LENGTH_SHORT
                        ).show()
                        saveUserInFB()
                    } else {
                        // If sign in fails, display a message to the user.

                        Toast.makeText(
                            this, "Register failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // ...
                })
    }

    fun saveUserInFB(){

        var newUser = HashMap<String, Any>()
        if(type.equals(getString(R.string.user)))
            newUser = CreateDocumentsUtil().createUserFile(et_email.text.toString(),et_name.text.toString(),type)
        else
            newUser = CreateDocumentsUtil().createSupervisorFile(et_email.text.toString(),et_name.text.toString(),type)

        db = FirebaseFirestore.getInstance()

        db.collection(getString(R.string.collections_users)).document(et_email.text.toString())
            .set(newUser)
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Write Succesfull ",
                    Toast.LENGTH_SHORT
                ).show()
                askPermissons()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this, "Write Fail ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        val editor = sharedPreferences.edit()
        editor.putString(getString(R.string.pref_email), et_email.text.toString())
        editor.apply()

    }

    fun askPermissons() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)

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
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)

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
