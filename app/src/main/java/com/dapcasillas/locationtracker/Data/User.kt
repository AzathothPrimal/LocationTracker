package com.dapcasillas.locationtracker.Data

import android.location.Geocoder
import android.location.Location
import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User(
                @SerializedName("name")
                @Expose var name: String?, @SerializedName("fathersLastName")
                @Expose var email: String?, @SerializedName("mothersLastName")
                @Expose var location: GeoPoint?, @SerializedName("profile")
                @Expose var type: String?
        ) {


}