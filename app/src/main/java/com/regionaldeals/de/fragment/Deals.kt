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
import com.regionaldeals.de.Constants
import com.regionaldeals.de.DealsDetailActivity
import com.regionaldeals.de.MainActivity
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.PrefsHelper
import com.regionaldeals.de.adapter.DealsAdapter
import com.regionaldeals.de.entities.DealObject
import kotlinx.android.synthetic.main.fragment_gutscheine.*
import org.jetbrains.anko.doAsync

/**
 * Created by Umi on 28.08.2017.
 */

class Deals : Fragment(), SwipeRefreshLayout.OnRefreshListener, DealsAdapter.ItemClickListener {

    private val mUrlDeals = "/mobile/api/deals/list"
    private var dealsRecyclerView: RecyclerView? = null
    private lateinit var mAdapter: DealsAdapter
    private var maxDistance = 50
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var myReceiver: Deals.MyReceiver? = null
    private var filter: IntentFilter? = null
    private var model: DealsViewModel? = null
    private lateinit var prefHelper: PrefsHelper

    inner class MyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            maxDistance = intent.getIntExtra("distance", maxDistance)
            loadAllDeals()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model?.dealLiveDataList?.observe(activity!!, Observer { results ->
            mAdapter.allDeals.clear()
            mAdapter.allDeals.addAll(results!!.results)
            activity?.runOnUiThread {
                mAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(activity!!).get(DealsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefHelper = PrefsHelper.getInstance(context!!)

        this.dealsRecyclerView = view.findViewById<View>(R.id.rV_gutschein) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(activity)
        dealsRecyclerView?.layoutManager = linearLayoutManager
        dealsRecyclerView?.setHasFixedSize(true)

        swipeRefreshLayout = view.findViewById<View>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout?.setOnRefreshListener(this)

        mAdapter = DealsAdapter(false, false)
        dealsRecyclerView?.adapter = mAdapter

        mAdapter.setClickListener(this)

        swipeRefreshLayout?.post { loadAllDeals() }

        filter = IntentFilter("BroadcastReceiver")
        myReceiver = MyReceiver()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gutscheine, container, false)
    }

    override fun onPause() {
        context?.unregisterReceiver(myReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(myReceiver, filter)
        if ((this.activity as MainActivity).shouldRefresh) {
            prefHelper.syncUserId(context!!)
            onRefresh()
            (this.activity as MainActivity).shouldRefresh = false
        }
    }

    override fun onRefresh() {
        loadAllDeals()
    }

    override fun onItemClick(obj: DealObject) {
        val intent = Intent(activity, DealsDetailActivity::class.java)
        intent.putExtra(Constants.DEALS_OBJECT, obj)
        intent.putExtra("deleteEnable", false)
        intent.putExtra("isGutschein", false)
        activity?.startActivity(intent)
    }

    override fun onFavouriteClick(obj: DealObject, isFromFav: Boolean) {

    }

    private fun loadAllDeals() {

        swipeRefreshLayout?.isRefreshing = true

        doAsync {

            val requestParams = arrayListOf<Pair<String, Any?>>()
            requestParams.add(Pair("userid", prefHelper.userId))
            requestParams.add(Pair("lat", prefHelper.locationLat))
            requestParams.add(Pair("long", prefHelper.locationLng))
            requestParams.add(Pair("dealtype", "TYPE_DEALS"))
            requestParams.add(Pair("userid", prefHelper.userId))
            requestParams.add(Pair("radius", maxDistance * 1000))

            model?.loadDeals(mUrlDeals, requestParams) {
                activity?.runOnUiThread {
                    swipeRefreshLayout?.isRefreshing = false
                    if (it) {
                        rV_gutschein.visibility = View.VISIBLE
                        tvStatus.visibility = View.GONE
                    } else {
                        rV_gutschein.visibility = View.GONE
                        tvStatus.visibility = View.VISIBLE
                        tvStatus.text = getString(R.string.error_dealsList)
                    }
                }
            }
        }
    }

}
