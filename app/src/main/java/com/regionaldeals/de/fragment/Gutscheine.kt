package com.regionaldeals.de.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.regionaldeals.de.Constants.LOCATION_KEY
import com.regionaldeals.de.Constants.USER_OBJECT_KEY
import com.regionaldeals.de.LoginActivity
import com.regionaldeals.de.MainActivity
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.SharedPreferenceUtils
import com.regionaldeals.de.adapter.GutscheineAdapter
import com.regionaldeals.de.entities.GutscheineObject
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Umi on 28.08.2017.
 */

class Gutscheine : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val URL_Deals = "/mobile/api/gutschein/list"
    private var gutRecyclerView: RecyclerView? = null
    private lateinit var mAdapter: GutscheineAdapter
    private var locationLat: Double? = 50.781203
    private var locationLng: Double? = 6.078068
    private var maxDistance = 50
    private var userId = ""
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var myReceiver: MyReceiver? = null
    private var filter: IntentFilter? = null
    private var model: DealsViewModel? = null

    inner class MyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            maxDistance = intent.getIntExtra("distance", maxDistance)
            loadDeals()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model?.gutscheinLiveDataList?.observe(activity!!, Observer { gutscheinResults ->
            mAdapter.allDeals.clear()
            mAdapter.allDeals.addAll(gutscheinResults!!.results)
            activity?.runOnUiThread {
                mAdapter.notifyDataSetChanged()
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(activity!!).get(DealsViewModel::class.java)
    }

    override fun onPause() {
        context!!.unregisterReceiver(myReceiver)
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gutRecyclerView = view.findViewById<View>(R.id.song_list) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(activity)
        gutRecyclerView?.layoutManager = linearLayoutManager
        gutRecyclerView?.setHasFixedSize(true)

        locationLat = Main.latitude
        locationLng = Main.longitude

        val restoredText = SharedPreferenceUtils.getInstance(activity).getStringValue(LOCATION_KEY, null)
        val restoredUser = SharedPreferenceUtils.getInstance(activity).getStringValue(USER_OBJECT_KEY, null)

        try {
            if (locationLat == 0.0 || locationLng == 0.0) {
                if (restoredText != null) {
                    val obj = JSONObject(restoredText)
                    val Lat = obj.getString("lat")
                    val Lng = obj.getString("lng")
                    if (!Lat.isEmpty() && !Lng.isEmpty()) {
                        locationLat = java.lang.Double.parseDouble(Lat)
                        locationLng = java.lang.Double.parseDouble(Lng)
                    }
                }
            }
            if (restoredUser != null) {
                val obj = JSONObject(restoredUser)
                userId = obj.getString("userId")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (t: Throwable) {
        }

        swipeRefreshLayout = view.findViewById<View>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout?.setOnRefreshListener(this)

        mAdapter = GutscheineAdapter(false) { mitmachenClick(it) }
        gutRecyclerView?.adapter = mAdapter

        swipeRefreshLayout?.post { loadDeals() }

        filter = IntentFilter("BroadcastReceiver")
        myReceiver = MyReceiver()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gutscheine, container, false)
        activity?.title = resources.getString(R.string.headerText)
        return view
    }


    override fun onResume() {
        super.onResume()
        context?.registerReceiver(myReceiver, filter)
        //TODO: make seperate shouldrefresh bool for gut and deals etc
        if ((activity as MainActivity).shouldRefresh) {
            val restoredUser = SharedPreferenceUtils.getInstance(activity).getStringValue(USER_OBJECT_KEY, null)
            try {
                if (restoredUser != null) {
                    val obj = JSONObject(restoredUser)
                    userId = obj.getString("userId")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (t: Throwable) {
            }

            onRefresh()
            (this.activity as MainActivity).shouldRefresh = false
        }
    }

    override fun onRefresh() {
        loadDeals()
    }

    private fun loadDeals() {

        swipeRefreshLayout?.isRefreshing = true

        val requestParams = arrayListOf<Pair<String, Any?>>()
        requestParams.add(Pair("userid", userId))
        requestParams.add(Pair("lat", locationLat))
        requestParams.add(Pair("long", locationLng))
        requestParams.add(Pair("radius", maxDistance * 1000))

        model?.loadGutschein(URL_Deals, requestParams)

    }

    private fun mitmachenClick(deals: GutscheineObject) {
        var suburl = "/mobile/api/gutschein/gutscheinclick"
        val gutId = deals.gutscheinId

        if (userId.isNotEmpty()) {
            val formData = listOf("userid" to userId, "gutscheinid" to gutId)
            model?.mitmachenGutschein(suburl, formData) {
                if (it) {
                    for (curr: GutscheineObject in mAdapter.allDeals) {
                        if (curr.gutscheinId == gutId) {
                            curr.isGutscheinAvailed = true
                        }
                    }
                    activity?.runOnUiThread {
                        mAdapter.notifyDataSetChanged()
                    }
                } else {

                }
            }
        } else {
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
            (activity as MainActivity).shouldRefresh = true
        }
    }

}