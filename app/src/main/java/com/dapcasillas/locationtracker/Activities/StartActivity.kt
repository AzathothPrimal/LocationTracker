package com.dapcasillas.locationtracker.Activities

import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dapcasillas.locationtracker.Data.User
import com.dapcasillas.locationtracker.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.FileDownloadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.io.File
import com.google.firebase.storage.UploadTask
import java.util.HashMap


class StartActivity : AppCompatActivity() {

    lateinit var btn_register : MaterialButton
    lateinit var btn_singin : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        initView()
        setEvents()
    }

    fun initView(){

        btn_register = findViewById(R.id.btn_register)
        btn_singin = findViewById(R.id.btn_singin)
    }

    fun setEvents(){

        btn_register.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_singin.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



}
