package com.regionaldeals.de.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.regionaldeals.de.Constants
import com.regionaldeals.de.MainActivity
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.PrefsHelper
import com.regionaldeals.de.entities.Plans
import com.regionaldeals.de.viewmodel.ABOViewModel
import kotlinx.android.synthetic.main.abo_buchen_summary.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject

class ABOBuchenSummary : Fragment() {

    private var model: ABOViewModel? = null

    private var url: String = "/mobile/api/subscriptions/update_subscription"
    private var urlSub: String = "/mobile/api/subscriptions/subscription?userid="
    private var subReference: String = ""

    private lateinit var prefHelper: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(activity!!).get(ABOViewModel::class.java)
        context?.let { prefHelper = PrefsHelper.getInstance(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.abo_buchen_summary, container, false)

    private fun getSelectedPlan() = arguments?.getParcelable<Plans>(Constants.SELECTED_PLAN) ?: null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPlan.text = getString(R.string.gewahlter_tariff) + getSelectedPlan()?.planName

        tvDesc.text = "Email: " + prefHelper.email + "\n" + getString(R.string.zahlung_text) + "\n"

        tvInfo.text = getString(R.string.zahlung_info_text) + "\n"

        btnAgbSubmit.setOnClickListener { v ->
            resetValidations()
            if (validateForm()) {
                progressBarProcessing.isIndeterminate = true
                doAsync {
                    val formData = listOf("userid" to prefHelper.userId.toInt(), "billing_plan" to getSelectedPlan()?.planShortName)
                    model?.buyPlan(url, formData) {
                        if (it.statusCode == 200) {
                            model?.updateSubscription(urlSub + prefHelper.userId) { subRes ->
                                progressBarProcessing.isIndeterminate = false
                                val obj = JSONObject(String(subRes.data))
                                val msg = obj.getString("message")
                                if (msg == "PLANS_SUBSCRIPTIONS_OK") {
                                    Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()
                                    val data = obj.getJSONObject("data")
                                    context?.let { context ->
                                        prefHelper.updateSubscription(data.toString(), context)
                                    }
                                }
                                activity?.runOnUiThread {
                                    val args = android.os.Bundle().apply {
                                        putParcelable(com.regionaldeals.de.Constants.SELECTED_PLAN, getSelectedPlan())
                                        putString(com.regionaldeals.de.Constants.SUB_REFERENCE, prefHelper.getSubReference(context!!))

                                    }
                                    androidx.navigation.Navigation.findNavController(v).navigate(com.regionaldeals.de.R.id.action_abo_buchen_summary_to_abo_buchen_agb, args)
                                }
                            }
                        } else {
                            Toast.makeText(context, "FAILED", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                //androidx.navigation.Navigation.findNavController(v).navigate(com.regionaldeals.de.R.id.action_abo_buchen_user_to_abo_buchen_agb)
            }
        }
    }

    private fun validateForm(): Boolean {
        var value = true

        if (!checkBoxTerms.isChecked) {
            value = false
            checkBoxTerms.error = " "
        }
        if (!checkBoxABG.isChecked) {
            value = false
            checkBoxABG.error = " "
        }
        return value
    }

    private fun resetValidations() {

        checkBoxTerms.error = null

        checkBoxABG.error = null

    }
}