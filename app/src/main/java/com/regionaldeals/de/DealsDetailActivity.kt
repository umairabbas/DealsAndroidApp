package com.regionaldeals.de

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.regionaldeals.de.entities.DealObject
import com.regionaldeals.de.entities.GutscheineObject
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.deals_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Umi on 13.09.2017.
 */

class DealsDetailActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback, BaseSliderView.OnSliderClickListener {

    private var mMap: GoogleMap? = null
    private var LOCATIONCORDINATE = LatLng(-50.7753, 6.0839)
    private var title: String? = null
    private var mPerth: Marker? = null
    private var mDemoSlider: SliderLayout? = null

    //For deal deletion
    private var delBtn: Button? = null
    private var progressDialog: ProgressDialog? = null
    private var message: String? = null
    private var isSuccess: Boolean? = false
    private var URL_DealDel = "/mobile/api/deals/deactivate"
    private var dealId: Int? = null
    private var shopId: Int? = null
    private var userId: String? = null

    //for gutscheien del
    private var isGutschein: Boolean = false
    private var dealURL: String = ""

    private var images: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deals_detail)
        setSupportActionBar(deal_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var arguments: Bundle = intent.extras

        val collapsingToolbar = collapsing_toolbar as CollapsingToolbarLayout
        delBtn = btn_deal_del as Button
        val mExpDate = exp_date as TextView
        val mPlaceUrl = place_url as TextView
        val mTitleUrl = urlTitle as TextView
        val shopNameText = shopName as TextView
        val contactText = shop_contact as TextView
        val AddressText = shop_address as TextView
        val placeDetail = place_detail as TextView
        mDemoSlider = image as SliderLayout

        var enableDeleteBtn: Boolean = false

        enableDeleteBtn = arguments.getBoolean("deleteEnable", false)
        isGutschein = arguments.getBoolean("isGutschein", false)


        var coverUrl: String? = ""
        var locationLat: Double? = -50.7753
        var locationLong: Double? = 6.0839
        var imgCount: Int = 1

        //If gutschein, else deal
        if (isGutschein) {
            val gutscheinObject = arguments.get(Constants.DEALS_OBJECT) as GutscheineObject
            dealId = gutscheinObject.gutscheinId
            shopId = gutscheinObject.shop?.shopId
            mExpDate.text = DateFormat.format("dd/MM/yyyy", Date(gutscheinObject.expiryDate!!)).toString()
            title = gutscheinObject.gutscheinTitle
            placeDetail.text = gutscheinObject.gutscheinDescription

            coverUrl = gutscheinObject.getGutscheinImageUrl(this)
            if (gutscheinObject.gutscheinImageCount != null)
                imgCount = gutscheinObject.gutscheinImageCount!!

            shopNameText.text = gutscheinObject.shop?.shopName
            contactText.text = gutscheinObject.shop?.shopContact
            AddressText.text = gutscheinObject.shop?.shopAddress
            locationLat = gutscheinObject.shop?.shopLocationLat!!.toDouble()
            locationLong = gutscheinObject.shop?.shopLocationLong!!.toDouble()

        } else {
            val dealObject = arguments.get(Constants.DEALS_OBJECT) as DealObject
            dealId = dealObject.dealId
            shopId = dealObject.shop?.shopId
            mExpDate.text = DateFormat.format("dd/MM/yyyy", Date(dealObject.expiryDate!!)).toString()
            title = dealObject.dealTitle
            placeDetail.text = dealObject.dealDescription

            coverUrl = dealObject.getDealImageUrl(this)
            if (dealObject.dealImageCount != null)
                imgCount = dealObject.dealImageCount!!

            shopNameText.text = dealObject.shop?.shopName
            contactText.text = dealObject.shop?.shopContact
            AddressText.text = dealObject.shop?.shopAddress
            locationLat = dealObject.shop?.shopLocationLat!!.toDouble()
            locationLong = dealObject.shop?.shopLocationLong!!.toDouble()

            if (dealObject.dealUrl != null) {
                dealURL = dealObject.dealUrl!!
                mTitleUrl.visibility = View.VISIBLE
                mPlaceUrl.text = dealURL
                mPlaceUrl.visibility = View.VISIBLE
                mPlaceUrl.setOnClickListener {
                    if (!dealURL.startsWith("http://") && !dealURL.startsWith("https://")) {
                        dealURL = "http://$dealURL"
                    }
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(dealURL))
                    startActivity(browserIntent)
                }
            }
        }


        if (enableDeleteBtn) {
            delBtn!!.visibility = View.VISIBLE
            val prefs = getSharedPreferences(getString(R.string.sharedPredName), Context.MODE_PRIVATE)
            val restoredUser = prefs?.getString("userObject", null)
            try {
                if (restoredUser != null) {
                    val obj = JSONObject(restoredUser)
                    userId = obj.getString("userId")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (t: Throwable) {
            }

            delBtn!!.setOnClickListener {
                progressDialog = ProgressDialog(this,
                        R.style.ThemeOverlay_AppCompat_Dialog)
                progressDialog!!.isIndeterminate = true
                progressDialog!!.setMessage("Removing...")
                progressDialog!!.show()
                delBtn!!.isEnabled = false
                DealDeleteCall().execute()
            }
        }



        LOCATIONCORDINATE = LatLng(locationLat, locationLong)
        collapsingToolbar.title = title

        val url_maps = HashMap<String, String>()
        val imgTitle = arrayOf(" ", "  ", "   ", "    ", "     ")
        for (a in 1..imgCount) {
            url_maps[imgTitle[a - 1]] = coverUrl + "&imagecount=" + Integer.toString(a) + "&res=470x320"
        }

        images = ArrayList()
        for (name in url_maps.keys) {
            images!!.add(url_maps[name]!!)
            val textSliderView = TextSliderView(this)
            textSliderView
                    .description(name)
                    .image(url_maps[name])
                    .setOnSliderClickListener(this).scaleType = BaseSliderView.ScaleType.Fit
            //add your extra information
            textSliderView.bundle(Bundle())
            textSliderView.bundle
                    .putString("extra", name)
            mDemoSlider!!.addSlider(textSliderView)
            mDemoSlider!!.stopAutoCycle()

        }

        mDemoSlider!!.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        if (imgCount == 1) {
            mDemoSlider!!.setCustomAnimation(DescriptionAnimation())
            mDemoSlider!!.setDuration(600000)
        }

        Fresco.initialize(this)

        val mapFragment = supportFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        //set height of map
        val displaymetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels
        val params = mapFragment.view?.layoutParams
        params?.height = height / 2
        mapFragment.view?.layoutParams = params
    }

    override fun onSliderClick(slider: BaseSliderView) {
        ImageViewer.Builder(this, images)
                .setStartPosition(0)
                .show()
    }

    /**
     * Called when the map is ready.
     */
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        val cp = CameraPosition.Builder()
                .target(LOCATIONCORDINATE)      // Sets the center of the map to Mountain View
                .zoom(11.0f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                .build()
        mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cp))

        // Add some markers to the map, and add a data object to each marker.
        mPerth = mMap!!.addMarker(MarkerOptions()
                .position(LOCATIONCORDINATE)
                .title(title))
        mPerth!!.tag = 0

        mMap!!.setMinZoomPreference(6.0f)
        mMap!!.setMaxZoomPreference(14.0f)
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(10.0f))
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isScrollGesturesEnabled = false
        // Set a listener for marker click.
        mMap!!.setOnMarkerClickListener(this)
    }

    /**
     * Called when the user clicks a marker.
     */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val clickCount = marker.tag as Int?

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    internal inner class DealDeleteCall : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg args: String): String? {
            try {
                message = ""
                val url: URL
                if (isGutschein!!) {
                    URL_DealDel = "/mobile/api/gutschein/deactivate"
                    url = URL(getString(R.string.apiUrl) + URL_DealDel + "?gutscheinid=" + dealId + "&userid=" + userId +
                            "&shopid=" + shopId)
                } else {
                    url = URL(getString(R.string.apiUrl) + URL_DealDel + "?dealid=" + dealId + "&userid=" + userId +
                            "&shopid=" + shopId)
                }
                val conn = url.openConnection() as HttpsURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true

                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)

                val response = conn.responseMessage
                val mIn = BufferedReader(InputStreamReader(
                        conn.inputStream))
                val res = StringBuffer()

                while (mIn.readLine() != null) {
                    res.append(mIn.readLine())
                }
                mIn.close()

                val jObject = JSONObject(res.toString())
                message = jObject.getString("message")

                if (message == getString(R.string.DEALS_REMOVE_OK) || message == getString(R.string.GUTSCHEIN_REMOVE_OK)) {
                    isSuccess = true
                } else if (message == getString(R.string.DEALS_REMOVE_ERR) || message == getString(R.string.GUTSCHEIN_REMOVE_ERR)) {
                    isSuccess = false
                    message = "Cannot remove deal"
                } else {
                    isSuccess = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        override fun onPostExecute(file_url: String) {
            progressDialog!!.dismiss()
            delBtn?.isEnabled = true
            runOnUiThread {
                if (isSuccess!!) {
                    Toast.makeText(applicationContext, "Deal Removed\n", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Failed\n" + message!!, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}