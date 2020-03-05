package com.dapcasillas.locationtracker.FireBase

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class FireBaseData {

    lateinit var db: FirebaseFirestore


    fun deleteFromBase(fireTable: String, key: String){

        try {
            db = FirebaseFirestore.getInstance()

            db.collection(fireTable).document(key)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }catch (ex :Exception){Log.w(TAG, "Error deleting document", ex)}

    }



    fun addOrder(fireTable: String, idItem: Int){
        db = FirebaseFirestore.getInstance()
        val pedido = HashMap<String, Any>()
        pedido.put("idPedido", idItem)

        // Add a new order with a generated ID
        db.collection(fireTable)
                .add(pedido)
                .addOnSuccessListener { documentReference -> Log.d("THIS", "DocumentSnapshot added with ID: " + documentReference.id) }
                .addOnFailureListener { e -> Log.w("THIS", "Error adding document", e) }
    }




}