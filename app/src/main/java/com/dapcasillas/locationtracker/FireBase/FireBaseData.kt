package com.dapcasillas.locationtracker.FireBase

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.dapcasillas.locationtracker.Data.User
import com.dapcasillas.locationtracker.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.util.*


class FireBaseData {

    lateinit var db: FirebaseFirestore
    var usersList : MutableList<User> = mutableListOf<User>()

    fun getUser(context: Context, user: String, myUserCallback: (User) -> Unit) {
        db = FirebaseFirestore.getInstance()

        db.collection(context.getString(R.string.collections_users)).document(user)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var geoPoint = GeoPoint(0.0, 0.0)
                    if(task.result?.get(context.getString(R.string.type_field)).toString().equals(context.getString(R.string.user)))
                        geoPoint = task.result?.get(context.getString(R.string.location_field)) as GeoPoint


                    myUserCallback(User(task.result?.get(context.getString(R.string.name_field)).toString(),
                                task.result?.get(context.getString(R.string.email_field)).toString(),
                                geoPoint,
                                task.result?.get(context.getString(R.string.type_field)).toString()
                            ))
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,  "Error getting documents: ",  Toast.LENGTH_SHORT
                ).show()
            }

    }


    fun readData(context: Context, myUsersListCallback: (MutableList<User>) -> Unit) {
        db = FirebaseFirestore.getInstance()

        db.collection(context.getString(R.string.collections_users))
            .whereEqualTo(context.getString(R.string.type_field), context.getString(R.string.user))
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    usersList.clear()
                    for (document in task.result!!) {
                        val geoPoint = document.get(context.getString(R.string.location_field)) as GeoPoint
                        usersList.add(
                            User(document.get(context.getString(R.string.name_field)).toString(),
                                document.get(context.getString(R.string.email_field)).toString(),
                                geoPoint,
                                document.get(context.getString(R.string.type_field)).toString()
                            )
                        )
                    }
                    myUsersListCallback(usersList)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,  "Error getting documents: ",  Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun updateUserLocation(context: Context,location: Location, email: String){
        db = FirebaseFirestore.getInstance()

        val docRef = db.collection(context.getString(R.string.collections_users)).document(email)
        val geopunto = GeoPoint(location.latitude, location.longitude)
        val update = docRef.update(context.getString(R.string.location_field), geopunto)

        Toast.makeText(context, "updated location"
                +"\n" + location.latitude.toString()+","+ location.longitude.toString(),
            Toast.LENGTH_SHORT
        ).show()

    }

}