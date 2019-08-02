package com.sujitmo.googlemap_realtimenotification

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissions {

    companion object {
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1

        public fun checkAndRequestPermissions(context: Context): Boolean {

            val internet = ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET)
            val location = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
            val locationCoarse =
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            val network =
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)

            val listPermissionsNeeded: ArrayList<String> = ArrayList()

            if (internet != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.INTERNET)
            }

            if (location != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (locationCoarse != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            if (network != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE)
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    context as Activity, listPermissionsNeeded.toTypedArray
                        (), REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                return false
            }
            return true
        }
    }
}