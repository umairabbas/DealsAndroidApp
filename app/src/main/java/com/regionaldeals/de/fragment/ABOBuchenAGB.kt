package com.regionaldeals.de.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.regionaldeals.de.Constants
import com.regionaldeals.de.MainActivity
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.PrefsHelper
import com.regionaldeals.de.entities.Plans
import kotlinx.android.synthetic.main.abo_buchen_agb.*


class ABOBuchenAGB : androidx.fragment.app.Fragment() {

    private lateinit var prefHelper: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { prefHelper = PrefsHelper.getInstance(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.abo_buchen_agb, container, false)

    private fun getSubReference() = arguments?.getString(Constants.SUB_REFERENCE) ?: ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvInfo.text = getString(R.string.Verwendungzweke) + getSubReference()

        btnAgbSubmit.setOnClickListener { _ ->
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("userEmail", prefHelper.email)
            intent.putExtra("userId", prefHelper.userId.toInt())
            intent.putExtra("subscribed", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
    }
}