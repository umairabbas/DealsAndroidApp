package com.regionaldeals.de.location

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.regionaldeals.de.Constants
import com.regionaldeals.de.Constants.*
import com.regionaldeals.de.MainActivity
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.SharedPreferenceUtils
import kotlinx.android.synthetic.main.location_prediction.*

class LocationPrediction : AppCompatActivity() {

    private lateinit var addressPredictionAdapter: AddressPredictionAdapter
    private var REQUEST_LOCATION_CODE = 101
    private lateinit var viewModel: AddressViewModel
    private var googleApiClient: GoogleApiClient? = null

    private fun isLocationFromSubscription() = intent.getBooleanExtra(Constants.LOCATION_FROM_SUB, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_prediction)
        viewModel = ViewModelProviders.of(this).get(AddressViewModel::class.java)

        if (googleApiClient == null) {

            googleApiClient = GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build()

        }
        checkGPSEnabled()

        addressPredictionAdapter = AddressPredictionAdapter {
            onItemClick(it)
        }

        buttonLocation.setOnClickListener { getLocationCordinates() }

        recyclerViewAddress.setHasFixedSize(true)
        recyclerViewAddress.layoutManager = LinearLayoutManager(this)
        recyclerViewAddress.adapter = addressPredictionAdapter

        queryAddress()

        viewModel.predictionsLiveDataList.observe(this, Observer
        {
            it?.let {
                addressPredictionAdapter.updateAdapter(it)
            }
        })


    }


    private fun onItemClick(address: data) {

        if(isLocationFromSubscription()) {


            intent.putExtra(LOCATION_POSTAL, address.postCode)
            intent.putExtra(LOCATION_CITY, address.cityName)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            val placeJson = "{\"Name\":\"${address.cityName}\", \"lat\": \"${address.cityLat}\", \"lng\":\"${address.cityLong}\"}"
            SharedPreferenceUtils.getInstance(this).setValue(LOCATION_KEY, placeJson)

            val startActivityIntent = Intent(this, MainActivity::class.java)
            startActivityIntent.putExtra("userCity", address.cityName)
            startActivityIntent.putExtra("updateCity", true)
            startActivity(startActivityIntent)
            finish()
        }
    }

    private fun queryAddress() {
        textInputAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length > 2) {
                    viewModel.fetchAddressPrediction(s.toString()) {
                        //                    it?.let {
//                        addressPredictionAdapter.updateAdapter(it.predictions)
//                    }
                    }
                }
            }
        })
    }

    private fun getLocationCordinates() {
        if (!this.checkGPSEnabled()) {
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = LocationServices.getFusedLocationProviderClient(this)
            if (location != null) {
                location.lastLocation?.addOnCompleteListener {
                    it.result?.let {
                        val extractAddress = GeocodeAddress(this).geocodeAddress(location = it)
                        textInputAddress.setText(extractAddress, TextView.BufferType.NORMAL)
                        textInputAddress.text?.let { text ->
                            textInputAddress.setSelection(text.length)
                        }
                    }
                }
            } else {
                startLocationUpdates()
            }

        } else {
            checkLocationPermission()
        }

    }

    private fun startLocationUpdates() {

        val locationRequest = LocationRequest().apply {
            interval = 5000
            fastestInterval = 2500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest) {
                val extractAddress = GeocodeAddress(this).geocodeAddress(it)
                textInputAddress.setText(extractAddress, TextView.BufferType.NORMAL)
                textInputAddress.text?.let { text ->
                    textInputAddress.setSelection(text.length)
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        googleApiClient?.connect()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient?.disconnect()
    }

    private fun checkGPSEnabled(): Boolean {
        if (!isLocationEnabled())
            showAlert()
        return isLocationEnabled()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.enable_location))
                .setMessage(getString(R.string.enable_location_message))
                .setPositiveButton(getString(R.string.location_setting)) { _, _ ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                }
                .setNegativeButton("Cancel") { _, _ -> }
        dialog.show()
    }

    private fun checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    AlertDialog.Builder(this)
                            .setTitle(getString(R.string.location_permission_title))
                            .setMessage(getString(R.string.location_permission_message))
                            .setPositiveButton(getString(R.string.ok_button)) { _, _ ->
                                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                                        REQUEST_LOCATION_CODE)
                            }
                            .create()
                            .show()

                } else requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_LOCATION_CODE)
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.location_granted), Toast.LENGTH_LONG).show()
                    getLocationCordinates()
                }

            } else {
                Toast.makeText(this, getString(R.string.location_denied), Toast.LENGTH_LONG).show()
            }
            return
        }
    }
}