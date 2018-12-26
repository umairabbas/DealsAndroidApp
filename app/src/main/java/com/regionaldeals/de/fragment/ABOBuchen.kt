package com.regionaldeals.de.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.regionaldeals.de.Constants.SELECTED_PLAN
import com.regionaldeals.de.R
import com.regionaldeals.de.adapter.ABOAdapter
import com.regionaldeals.de.entities.Plans
import com.regionaldeals.de.viewmodel.ABOViewModel
import kotlinx.android.synthetic.main.abo_buchen.*

class ABOBuchen : Fragment(), ABOAdapter.ItemClickListener {

    private val mUrlPlans = "/mobile/api/subscriptions/plans"
    private lateinit var mAdapter: ABOAdapter
    private var model: ABOViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(activity!!).get(ABOViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model?.plansRes?.observe(activity!!, Observer { results ->
            mAdapter.allPlans.clear()
            mAdapter.allPlans.addAll(results!!.results)
            activity?.runOnUiThread {
                mAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.abo_buchen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(activity)
        rVAbo?.layoutManager = linearLayoutManager
        rVAbo?.setHasFixedSize(true)

        mAdapter = ABOAdapter()
        rVAbo?.adapter = mAdapter

        mAdapter.setClickListener(this)


        model?.loadPlans(mUrlPlans) {
            activity?.runOnUiThread {
                if (it) {
                } else {
                    Toast.makeText(context, getString(R.string.error_plansList), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onItemClick(obj: Plans) {
        view?.let {
            val args = Bundle().apply {
                putString(SELECTED_PLAN, obj.planShortName)
            }
            Navigation.findNavController(it).navigate(R.id.action_abo_buchen_to_abo_buchen_user, args)
        }
    }

}