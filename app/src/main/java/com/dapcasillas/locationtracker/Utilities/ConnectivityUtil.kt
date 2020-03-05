package com.dapcasillas.locationtracker.Utilities

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager

class ConnectivityUtil {


        fun isNetworkConnected(activity : Activity): Boolean {

            val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return cm.activeNetworkInfo != null
        }
}