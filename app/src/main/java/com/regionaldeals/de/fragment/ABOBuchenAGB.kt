package com.regionaldeals.de.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.regionaldeals.de.Constants
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.PrefsHelper
import com.regionaldeals.de.entities.Plans
import kotlinx.android.synthetic.main.abo_buchen_agb.*


class ABOBuchenAGB : Fragment() {

    private lateinit var prefHelper: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { prefHelper = PrefsHelper.getInstance(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.abo_buchen_agb, container, false)

    private fun getSelectedPlan() = arguments?.getParcelable<Plans>(Constants.SELECTED_PLAN) ?: null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvInfo.text = "Verwendungzweke: " + prefHelper.email

        btnAgbSubmit.setOnClickListener { v ->
            val args = android.os.Bundle().apply {
                putParcelable(com.regionaldeals.de.Constants.SELECTED_PLAN, getSelectedPlan())
            }
            androidx.navigation.Navigation.findNavController(v).navigate(com.regionaldeals.de.R.id.action_abo_buchen_agb_to_abo_buchen_summary, args)

        }
    }
}