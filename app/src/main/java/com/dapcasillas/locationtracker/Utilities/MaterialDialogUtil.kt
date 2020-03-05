package com.dapcasillas.locationtracker.Utilities

import android.app.Activity
import android.content.Intent
import com.dapcasillas.locationtracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MaterialDialogUtil {


     fun ShowCenterTitleMaterialDialog(context: Activity, title: String, message: String, positiveText: String){
         MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme_Center)
             .setIcon(R.drawable.android)
             .setTitle(title)
             .setMessage(message)
             .setPositiveButton(positiveText,null)
             .show().setCancelable(false)
     }

    fun ShowCenterIntentMaterialDialog(context: Activity, intent: Intent, title: String, message: String, positiveText: String){
        MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme_Center)
            .setIcon(R.drawable.android)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText){dialog, which ->
                context.startActivity(intent)
                context.finish()
            }
            .show().setCancelable(false)
    }

    fun ShowCenterNoFIntentMaterialDialog(context: Activity, intent: Intent, title: String, message: String, positiveText: String){
        MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme_Center)
            .setIcon(R.drawable.android)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText){dialog, which ->
                context.startActivity(intent)
            }
            .show()
    }
}