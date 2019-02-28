package com.regionaldeals.de.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.util.*

class GeocodeAddress(context: Context) {

    private var geocoder: Geocoder

    init {
        geocoder = Geocoder(context, Locale.getDefault())
    }

    fun geocodeAddress(location: Location): String {
        var addressList = listOf<Address>()
        try {
            addressList = geocoder.getFromLocation(
                    location.latitude, location.longitude, 1)
            return extractAddress(addressList)

        } catch (e: Exception) {

        }
        return ""
    }

    private fun extractAddress(addressList: List<Address>): String {
        return if (addressList.isNotEmpty()) {
            val address = addressList[0]
            address.postalCode.toString()
        } else {
            ""
        }
    }
}