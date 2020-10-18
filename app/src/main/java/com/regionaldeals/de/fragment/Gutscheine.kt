package com.regionaldeals.de.fragment

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.regionaldeals.de.*
import com.regionaldeals.de.Utils.PrefsHelper
import com.regionaldeals.de.adapter.GutscheineAdapter
import com.regionaldeals.de.entities.GutscheineObject
import kotlinx.android.synthetic.main.fragment_gutscheine.*
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by Umi on 28.08.2017.
 */

class Gutscheine : androidx.fragment.app.Fragment(), androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, GutscheineAdapter.ItemClickListener {

    private val mUrlDeals = "/web/gutschein/list"
    private val mMitMachenUrl = "/web/gutschein/gutscheinclick"
    private var gutRecyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private lateinit var mAdapter: GutscheineAdapter
    private var maxDistance = 50
    private var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    private var myReceiver: MyReceiver? = null
    private var filter: IntentFilter? = null
    private var model: DealsViewModel? = null
    private lateinit var prefHelper: PrefsHelper

    inner class MyReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            maxDistance = intent.getIntExtra("distance", maxDistance)
            val handler = Handler()
            handler.postDelayed({ loadDeals() }, 300)

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            model?.gutscheinLiveDataList?.observe(it, Observer { gutscheinResults ->
                it?.runOnUiThread {
                    mAdapter.allDeals.clear()
                    mAdapter.allDeals.addAll(gutscheinResults!!.results)
                    mAdapter.notifyDataSetChanged()
                }
            })
        }
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

        prefHelper = PrefsHelper.getInstance(context!!)

        this.gutRecyclerView = view.findViewById<View>(R.id.rV_gutschein) as androidx.recyclerview.widget.RecyclerView
        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        gutRecyclerView?.layoutManager = linearLayoutManager
        gutRecyclerView?.setHasFixedSize(true)

        swipeRefreshLayout = view.findViewById<View>(R.id.swipe_refresh_layout) as androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        swipeRefreshLayout?.setOnRefreshListener(this)

        mAdapter = GutscheineAdapter(false)
        gutRecyclerView?.adapter = mAdapter

        mAdapter.setClickListener(this)

        filter = IntentFilter("BroadcastReceiver")
        myReceiver = MyReceiver()

        swipeRefreshLayout?.isRefreshing = true

        swipeRefreshLayout?.postDelayed({ loadDeals() }, 700) ?: loadDeals()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gutscheine, container, false)
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(myReceiver, filter)
        //TODO: make seperate shouldrefresh bool for gut and deals etc
        if ((activity as MainActivity).shouldRefresh) {
            prefHelper.syncUserId(context!!)
            onRefresh()
            (this.activity as MainActivity).shouldRefresh = false
        }
    }

    override fun onRefresh() {
        loadDeals()
    }

    override fun onItemClick(obj: GutscheineObject) {
        onDealClick(obj)
    }

    override fun onItemMitmachenClick(obj: GutscheineObject) {
        mitmachenClick(obj)
    }

    private fun loadDeals() {

        swipeRefreshLayout?.isRefreshing = true

        doAsync {

            val requestParams = arrayListOf<Pair<String, Any?>>()
            requestParams.add(Pair("userid", prefHelper.userId))
            requestParams.add(Pair("lat", prefHelper.locationLat))
            requestParams.add(Pair("long", prefHelper.locationLng))
            requestParams.add(Pair("radius", maxDistance * 1000))

            model?.loadGutschein(mUrlDeals, requestParams) {
                activity?.runOnUiThread {
                    swipeRefreshLayout?.isRefreshing = false
                    if (it) {
                        rV_gutschein?.visibility = View.VISIBLE
                        tvStatus?.visibility = View.GONE
                    } else {
                        rV_gutschein?.visibility = View.GONE
                        tvStatus?.visibility = View.VISIBLE
                        tvStatus?.text = getString(R.string.error_dealsList)
                    }
                }
            }
        }

    }

    private fun onDealClick(deal: GutscheineObject) {
        val intent = Intent(activity, DealsDetailActivity::class.java)
        intent.putExtra(Constants.DEALS_OBJECT, deal)
        intent.putExtra("deleteEnable", false)
        intent.putExtra("isGutschein", true)
        activity?.startActivity(intent)
    }

    private fun mitmachenClick(deal: GutscheineObject) {
        if (prefHelper.userId.isNotEmpty()) {
            doAsync {
                val formData = listOf("userid" to prefHelper.userId, "gutscheinid" to deal.gutscheinId)
                model?.mitmachenGutschein(mMitMachenUrl, formData) {
                    if (it) {
                        for (curr: GutscheineObject in mAdapter.allDeals) {
                            if (curr.gutscheinId == deal.gutscheinId) {
                                curr.gutscheinAvailed = true
                            }
                        }
                        activity?.runOnUiThread {
                            mAdapter.notifyDataSetChanged()
                        }
                    } else {

                    }
                }
            }
        } else {
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.startActivity(intent)
            (activity as MainActivity).shouldRefresh = true
        }
    }

}