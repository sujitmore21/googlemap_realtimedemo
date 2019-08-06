package com.sujitmo.googlemap_realtimenotification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {
    var locationManager: LocationManager? =null
    var locationListner: LocationListener? =null
    var slat:Double ? =0.0
    var slog:Double ? =0.0
    var dlat:Double ? =0.0
    var dlog:Double ? =0.0
    private var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        destinationTxt.setOnClickListener {
            getAddress()
        }

        button.setOnClickListener {
            if (destinationTxt.text.isNotEmpty() && sourceTxt.text.isNotEmpty()) {
                val budle=Bundle()
                 budle.putDouble("sLat",slat!!)
                budle.putDouble("sLog",slog!!)
                budle.putDouble("dLat",dlat!!)
                budle.putDouble("dLog",dlog!!)

                startActivity(Intent(this@SearchActivity, MapsActivity::class.java)
                    .putExtras(budle))
            }
            else{
                Toast.makeText(this@SearchActivity,"Enter Source Or destination address",Toast.LENGTH_SHORT).show()
            }
            }

        Permissions.checkAndRequestPermissions(this@SearchActivity)
        locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0L,0F,mLocationListener)
    }

    private val mLocationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
            Toast.makeText(this@SearchActivity,provider.toString(), Toast.LENGTH_LONG).show()
        }

        override fun onProviderDisabled(provider: String?) {
            Toast.makeText(this@SearchActivity,provider.toString(), Toast.LENGTH_LONG).show()

        }

        override fun onLocationChanged(location: Location) {
             slat=location.latitude
            slog=location.longitude
            getAddressFromLocation(location.latitude, location.longitude,this@SearchActivity)
            Toast.makeText(this@SearchActivity,"Lat : ${location!!.latitude}\n" +
                    "Long : ${location.longitude}", Toast.LENGTH_LONG).show()
        }
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double,
                               context: Context
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())
        var result: String? = null

        val addressList = geocoder.getFromLocation(
            latitude, longitude, 1)
        if (addressList != null && addressList.size > 0) {
            val address = addressList[0]
            val sb = StringBuilder()
            for (i in 0 until address.maxAddressLineIndex) {
                sb.append(address.getAddressLine(i)).append("\n")
            }

            sb.append(address.locality).append("\n")
            sb.append(address.postalCode).append("\n")
            sb.append(address.countryName)
            sourceTxt.text = address.subLocality

            Toast.makeText(this@SearchActivity,address.toString(), Toast.LENGTH_LONG).show()

        }


    }
    private fun getAddress() {
        try {

            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                .build(this@SearchActivity)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
// TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
// TODO: Handle the error.
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Log.i("@glen", "Permission Required For Location==========>")

            for (item in 0 until permissions.size) {
                if (grantResults[item] == PackageManager.PERMISSION_DENIED)
                    Log.i("@glen", "Permission Required For Location==========>")
            }

        } else {
            Log.i("@glen", "Permission ==========>")
        }
    }

    private var place: Place? = null

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this@SearchActivity, data)
                Log.i("@Place", "Place: " + place!!.getName())
                Log.i("@Place", "add: " + place!!.getAddress()!!)
                Log.i("@Place", "local: " + place!!.getLocale())

                val coordinates = place!!.getLatLng() // Get the coordinates from your place
                val geocoder = Geocoder(this@SearchActivity, Locale.getDefault())

                var addresses: List<Address>? = ArrayList() // Only retrieve 1 address
                try {
                    addresses = geocoder.getFromLocation(
                        coordinates.latitude,
                        coordinates.longitude,
                        1
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (addresses!!.isNotEmpty()) {
                    Log.i("@Place", "addresses: ${Gson().toJson(addresses)}")
                    Log.i("@Place", "place: " + Locale.getDefault())

                    Log.i("@place", "sub locality : ${addresses.get(0).subLocality}")
                    Log.i("@place", "address get : " + addresses[0])
                    Log.i("@place", "latitude : ${addresses[0].latitude}")
                    Log.i("@place", "longitude : ${addresses[0].longitude}")
                    Log.i("@place", "state : ${addresses[0].adminArea}")
                    Log.i("@place", "city : ${addresses[0].locality}")
                    Log.i("@place", "pin code : ${addresses[0].postalCode}")
                    Log.i("@place", "country : ${addresses[0].countryName}")
                    Log.i("@place", "locality : ${addresses[0].locality}")

                    dlat= addresses[0].latitude
                    dlog = addresses[0].longitude

                    if (addresses.get(0).subLocality != null) {
                        destinationTxt.text = addresses.get(0).subLocality
                    }


                }
            }
        }
    }
}
