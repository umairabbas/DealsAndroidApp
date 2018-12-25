package com.regionaldeals.de

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
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
import kotlinx.android.synthetic.main.deals_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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
    private var locationCordinates = LatLng(-50.7753, 6.0839)
    private var title: String? = null
    private var mPerth: Marker? = null

    private var message: String = ""
    private var isSuccess: Boolean = false
    private var urlDealsDeactivate = "/mobile/api/deals/deactivate"
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

        var locationLat: Double? = -50.7753
        var locationLong: Double? = 6.0839
        var imgCount = 1
        var enableDeleteBtn = false
        var coverUrl: String? = ""


        intent.extras?.let {
            val arguments: Bundle = it

            enableDeleteBtn = arguments.getBoolean("deleteEnable", false)
            isGutschein = arguments.getBoolean("isGutschein", false)


            //If gutschein, else deal
            if (isGutschein) {
                val gutscheinObject = arguments.get(Constants.DEALS_OBJECT) as GutscheineObject
                dealId = gutscheinObject.gutscheinId
                shopId = gutscheinObject.shop?.shopId
                gutscheinObject.expiryDate?.let { date ->
                    exp_date.text = DateFormat.format("dd/MM/yyyy", Date(date)).toString()
                }
                collapsing_toolbar.title = gutscheinObject.gutscheinTitle
                place_detail.text = gutscheinObject.gutscheinDescription

                coverUrl = gutscheinObject.getGutscheinImageUrl(this)
                gutscheinObject.gutscheinImageCount?.let { count ->
                    imgCount = count
                }
                shopName.text = gutscheinObject.shop?.shopName
                shop_contact.text = gutscheinObject.shop?.shopContact
                shop_address.text = gutscheinObject.shop?.shopAddress
                locationLat = gutscheinObject.shop?.shopLocationLat?.toDouble()
                locationLong = gutscheinObject.shop?.shopLocationLong?.toDouble()

            } else {
                val dealObject = arguments.get(Constants.DEALS_OBJECT) as DealObject
                dealId = dealObject.dealId
                shopId = dealObject.shop?.shopId
                dealObject.expiryDate?.let { date ->
                    exp_date.text = DateFormat.format("dd/MM/yyyy", Date(date)).toString()
                }
                collapsing_toolbar.title = dealObject.dealTitle
                place_detail.text = dealObject.dealDescription

                coverUrl = dealObject.getDealImageUrl(this)
                dealObject.dealImageCount?.let { count ->
                    imgCount = count
                }
                shopName.text = dealObject.shop?.shopName
                shop_contact.text = dealObject.shop?.shopContact
                shop_address.text = dealObject.shop?.shopAddress
                locationLat = dealObject.shop?.shopLocationLat?.toDouble()
                locationLong = dealObject.shop?.shopLocationLong?.toDouble()

                dealObject.dealUrl?.let { url ->
                    dealURL = url
                    urlTitle.visibility = View.VISIBLE
                    place_url.text = dealURL
                    place_url.visibility = View.VISIBLE
                    place_url.setOnClickListener { _ ->
                        if (!dealURL.startsWith("http://") && !dealURL.startsWith("https://")) {
                            dealURL = "http://$dealURL"
                        }
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(dealURL))
                        startActivity(browserIntent)
                    }
                }
            }

        }


        if (enableDeleteBtn) {
            btn_deal_del?.visibility = View.VISIBLE
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

            btn_deal_del?.setOnClickListener {
                //                progressDialog = ProgressDialog(this,
//                        R.style.ThemeOverlay_AppCompat_Dialog)
//                progressDialog!!.isIndeterminate = true
//                progressDialog!!.setMessage("Removing...")
//                progressDialog!!.show()
                btn_deal_del?.isEnabled = false
                removeDealTask()

            }
        }


        if (locationLat != null && locationLong != null)
            locationCordinates = LatLng(locationLat as Double, locationLong as Double)

        val urlMaps = HashMap<String, String>()
        val imgTitle = arrayOf(" ", "  ", "   ", "    ", "     ")
        for (a in 1..imgCount) {
            urlMaps[imgTitle[a - 1]] = coverUrl + "&imagecount=" + Integer.toString(a) + "&res=470x320"
        }

        images = ArrayList()
        for (name in urlMaps.keys) {
            urlMaps[name]?.let {
                images?.add(it)
            }
            val textSliderView = TextSliderView(this)
            textSliderView
                    .description(name)
                    .image(urlMaps[name])
                    .setOnSliderClickListener(this).scaleType = BaseSliderView.ScaleType.Fit
            //add your extra information
            textSliderView.bundle(Bundle())
            textSliderView.bundle
                    .putString("extra", name)
            image?.addSlider(textSliderView)
            image?.stopAutoCycle()

        }

        image?.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        if (imgCount == 1) {
            image?.setCustomAnimation(DescriptionAnimation())
            image?.setDuration(600000)
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

    private fun removeDealTask() {
        doAsync {
            try {
                message = ""
                val url: URL
                if (isGutschein) {
                    urlDealsDeactivate = "/mobile/api/gutschein/deactivate"
                    url = URL(getString(R.string.apiUrl) + urlDealsDeactivate + "?gutscheinid=" + dealId + "&userid=" + userId +
                            "&shopid=" + shopId)
                } else {
                    url = URL(getString(R.string.apiUrl) + urlDealsDeactivate + "?dealid=" + dealId + "&userid=" + userId +
                            "&shopid=" + shopId)
                }
                val conn = url.openConnection() as HttpsURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true

                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)

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

            uiThread { _ ->
                btn_deal_del?.isEnabled = true
                runOnUiThread {
                    if (isSuccess) {
                        Toast.makeText(applicationContext, "Deal Removed\n", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Failed\n" + message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onSliderClick(slider: BaseSliderView) {
//        ImageViewer.Builder(this, images)
//                .setStartPosition(0)
//                .show()
    }

    /**
     * Called when the map is ready.
     */
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        val cp = CameraPosition.Builder()
                .target(locationCordinates)      // Sets the center of the map to Mountain View
                .zoom(11.0f)                   // Sets the zoom
                .bearing(90f)                // Sets the orientation of the camera to east
                .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                .build()
        mMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cp))

        mPerth = mMap?.addMarker(MarkerOptions()
                .position(locationCordinates)
                .title(title))
        mPerth?.tag = 0

        mMap?.setMinZoomPreference(6.0f)
        mMap?.setMaxZoomPreference(14.0f)
        mMap?.moveCamera(CameraUpdateFactory.zoomTo(10.0f))
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.uiSettings?.isScrollGesturesEnabled = false
        mMap?.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
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

}