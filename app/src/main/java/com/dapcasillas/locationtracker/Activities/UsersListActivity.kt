package com.dapcasillas.locationtracker.Activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import com.dapcasillas.locationtracker.Activities.Adapters.UsersAdapter
import com.dapcasillas.locationtracker.Data.User
import com.dapcasillas.locationtracker.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class UsersListActivity : AppCompatActivity() {


    lateinit var db: FirebaseFirestore
    lateinit var sharedPreferences: SharedPreferences
    lateinit var email : String
    var usersList : MutableList<User> = mutableListOf<User>()
    lateinit private var usersAdapter: UsersAdapter
    lateinit var users_recycler: androidx.recyclerview.widget.RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)

        initViews()

    }

    fun initViews(){
        users_recycler = findViewById(R.id.users_recycler)
        readBase()
    }


    fun readBase(){
        db = FirebaseFirestore.getInstance()
        db.collection(getString(R.string.collections_users))
            .whereEqualTo("type", "user")
            .get()
            .addOnSuccessListener { documents ->

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
                usersAdapter = UsersAdapter(this, R.layout.row_user, usersList)
                users_recycler.adapter = usersAdapter
                usersAdapter.setOnItemClickListener(onUserClickListener)

                usersAdapter.notifyDataSetChanged()
            }




        } catch (ex : Exception){
            ex.printStackTrace()

        }
    }

    private val onUserClickListener = object : UsersAdapter.OnItemClickListener {
        override fun onItemClick(view: View){//, idSitioWeb: Int?, idUsuario: Int?, sitioWeb: String?) {

//            val editor = sharedPreferences.edit()
//            editor.putInt("idSitioWeb",idSitioWeb!!)
//            editor.putString("sitioWeb", sitioWeb)
//            editor.apply()
//            loadFragment(AddUrlFragment())
        }
    }
}
