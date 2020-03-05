package com.dapcasillas.locationtracker.Utilities

import com.google.firebase.firestore.GeoPoint

class CreateDocumentsUtil {

    fun createUserFile(email : String, name: String, type: String) : HashMap<String, Any> {
        val geopunto = GeoPoint(0.0, 0.0)
        return hashMapOf(
            "email" to email,
            "location" to geopunto,
            "name" to name,
            "type" to type
        )

    }

    fun createSupervisorFile(email : String, name: String, type: String): HashMap<String, Any> {
        return hashMapOf(
            "email" to email,
            "name" to name,
            "type" to type
        )
    }

}