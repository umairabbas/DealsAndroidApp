package com.regionaldeals.de.Utils

import android.content.Context
import com.regionaldeals.de.Constants
import com.regionaldeals.de.service.LocationStatic
import org.json.JSONException
import org.json.JSONObject

//data class LocationCordinates(val latitude: Double, val longitude: Double)

class PrefsHelper private constructor(context: Context) {

    var locationLat: Double = 50.781203
    var locationLng: Double = 6.078068
    var userId: String = ""

    companion object {

        private var mInstance: PrefsHelper? = null

        @Synchronized
        fun getInstance(context: Context): PrefsHelper {

            if (mInstance == null)
                mInstance = PrefsHelper(context)

            return mInstance!!
        }
    }

    init {

        val restoredUser = SharedPreferenceUtils.getInstance(context).getStringValue(Constants.USER_OBJECT_KEY, null)

        restoredUser?.let { getUserIdfromPrefs(restoredUser) }

        locationLat = LocationStatic.latitude
        locationLng = LocationStatic.longitude

        if (locationLat == 0.0 || locationLng == 0.0) {
            val restoredText = SharedPreferenceUtils.getInstance(context).getStringValue(Constants.LOCATION_KEY, null)
            restoredText?.let { getCordinatesfromPrefs(restoredText) }
        }
    }

    fun syncUserId(context:Context){
        val restoredUser = SharedPreferenceUtils.getInstance(context).getStringValue(Constants.USER_OBJECT_KEY, null)
        getUserIdfromPrefs(restoredUser)
    }

    private fun getCordinatesfromPrefs(restoredJson: String) {
        try {
            if (restoredJson != null) {
                val obj = JSONObject(restoredJson)
                if (!obj.isNull("lat") && !obj.isNull("lng")) {
                    val mLat = obj.getString("lat")
                    val mLng = obj.getString("lng")
                    if (!mLat.isEmpty() && !mLng.isEmpty()) {
                        locationLat = java.lang.Double.parseDouble(mLat)
                        locationLng = java.lang.Double.parseDouble(mLng)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (t: Throwable) {
            t.toString()
        }
    }


    private fun getUserIdfromPrefs(restoredJson: String) {
        try {
            if (restoredJson != null) {
                val obj = JSONObject(restoredJson)
                userId = obj.getString("userId")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (t: Throwable) {
        }
    }

}