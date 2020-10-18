package com.regionaldeals.de

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.google.gson.GsonBuilder
import com.regionaldeals.de.Utils.JSONParser
import com.regionaldeals.de.adapter.ShopAdapter
import com.regionaldeals.de.entities.Shop
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Umi on 28.10.2017.
 */
class ShopActivity() : AppCompatActivity(), OnRefreshListener {
    private var toolbar: Toolbar? = null
    private var mListView: ListView? = null
    private var shopList: MutableList<Shop>? = null
    private var shopArr: JSONArray? = null
    private var context: Context? = null
    private var userId = ""
    var jsonParser = JSONParser()
    private var mAdapter: ShopAdapter? = null
    private val URL_Deals = "/web/shops/list"
    private var isFirst = true
    private var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null

    override fun onPostResume() {
        super.onPostResume()
        if (!isFirst) {
            LoadShop().execute()
        } else {
            isFirst = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_main)
        context = this
        toolbar = findViewById<View>(R.id.toolbarShop) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mListView = findViewById<View>(R.id.shop_list_view) as ListView
        swipeRefreshLayout = findViewById<View>(R.id.swipe_refresh_layout) as androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        swipeRefreshLayout?.setOnRefreshListener(this)
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            //supportFragmentManager.beginTransaction().add(AddShopFragment(), null).commit()
            val startActivityIntent = Intent(this, AddShopActivity::class.java)
            startActivity(startActivityIntent)
        }
        val prefs = getSharedPreferences(getString(R.string.sharedPredName), Context.MODE_PRIVATE)
        val restoredUser = prefs.getString("userObject", null)
        try {
            if (restoredUser != null) {
                val obj = JSONObject(restoredUser)
                userId = obj.getString("userId")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (t: Throwable) {
        }
        shopList = ArrayList()
        LoadShop().execute()
    }

    override fun onRefresh() { // swipe refresh is performed, fetch the messages again
        LoadShop().execute()
        return
    }

    internal inner class LoadShop() : AsyncTask<String?, String?, String?>() {
        /**
         * Before starting background thread Show Progress Dialog
         */
        override fun onPreExecute() {
            super.onPreExecute()
            swipeRefreshLayout!!.isRefreshing = true
        }

        override fun doInBackground(vararg params: String?): String? { // Building Parameters
            val params: MutableList<NameValuePair> = ArrayList()
            params.add(BasicNameValuePair("userid", userId))
            // getting JSON string from URL
            val json = jsonParser.makeHttpRequest(context!!.getString(R.string.apiUrl) + URL_Deals, "GET",
                    params)
            Log.d("JSON: ", "> $json")
            try {
                shopList?.clear()
                val jO = JSONObject(json)
                shopArr = jO.getJSONArray("data") as JSONArray
                shopArr?.let {
                    for (i in 0 until it.length()) {
                        val c = it.getJSONObject(i)
                        val gsonRes = GsonBuilder().create()
                        val newDeal = gsonRes.fromJson(c.toString(), Shop::class.java)
                        shopList?.add(newDeal)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        override fun onPostExecute(file_url: String?) { // dismiss the dialog after getting all albums
            swipeRefreshLayout?.isRefreshing = false
            // updating UI from Background Thread
            runOnUiThread {
                mAdapter = ShopAdapter(context, shopList)
                mListView?.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
            }
        }

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