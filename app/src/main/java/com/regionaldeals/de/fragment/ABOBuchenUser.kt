package com.regionaldeals.de.fragment

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.regionaldeals.de.Constants.*
import com.regionaldeals.de.MainActivity
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.PrefsHelper
import com.regionaldeals.de.entities.Plans
import com.regionaldeals.de.location.LocationPrediction
import com.regionaldeals.de.viewmodel.ABOViewModel
import kotlinx.android.synthetic.main.abo_buchen_user.*
import kotlinx.android.synthetic.main.item_list_address_prediction.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject

class ABOBuchenUser : Fragment() {


    private var model: ABOViewModel? = null

    private var url: String = "/web/users/updateuser"

    private lateinit var prefHelper: PrefsHelper

    private val LOCATION_REQ = 101

    private var locationIntent: Intent = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(activity!!).get(ABOViewModel::class.java)
        context?.let { prefHelper = PrefsHelper.getInstance(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.abo_buchen_user, container, false)

    private fun getSelectedPlan() = arguments?.getParcelable<Plans>(SELECTED_PLAN) ?: null

    private fun validateForm(): Boolean {
        var value = true

        if (txtFirst.text.toString().isEmpty()) {
            value = false
            inputFirst.error = " "
        }
        if (txtLast.text.toString().isEmpty()) {
            value = false
            inputLast.error = " "
        }
        if (txtPhone.text.toString().isEmpty()) {
            value = false
            inputPhone.error = " "
        }
        if (txtStreetAddress.text.toString().isEmpty()) {
            value = false
            inputStreetAddress.error = " "
        }
        if (txtAddress.text.toString().isEmpty()) {
            value = false
            inputAddress.error = " "
        }


        return value
    }

    private fun resetValidations() {

        inputFirst.error = null

        inputLast.error = null

        inputPhone.error = null

        inputStreetAddress.error = null

        inputAddress.error = null

    }

    private fun goToAGB() {
        view?.let {
            val args = android.os.Bundle().apply {
                putParcelable(com.regionaldeals.de.Constants.SELECTED_PLAN, getSelectedPlan())
            }
            androidx.navigation.Navigation.findNavController(it).navigate(com.regionaldeals.de.R.id.action_abo_buchen_user_to_abo_buchen_summary, args)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQ) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    locationIntent = it
                    txtAddress.setText("""${it.getStringExtra(LOCATION_CITY)}, ${it.getStringExtra(LOCATION_POSTAL)}, DE"""
                            , TextView.BufferType.EDITABLE)
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtAddress.isFocusable = false
        txtAddress.setOnClickListener {
            activity?.let {
                val intent = Intent(it, LocationPrediction::class.java)
                intent.putExtra(LOCATION_FROM_SUB, true)
                startActivityForResult(intent, LOCATION_REQ)
            }
        }

        btnUserSubmit.setOnClickListener {

            progressBarProcessingUser.isIndeterminate = true

            resetValidations()

            if (validateForm()) {

                doAsync {

                    val bodyJson = """
                      { "userId" : """ + prefHelper.userId.toInt() + """,
                        "firstName" : """" + txtFirst.text.toString() + """",
                        "lastName" : """" + txtLast.text.toString() + """",
                        "email" : """" + prefHelper.email + """",
                        "phone" : """" + txtPhone.text.toString() + """",
                        "mobile" : """" + txtPhone.text.toString() + """",
                        "address" : """" + txtStreetAddress.text.toString() + """",
                        "postCode" : """" + locationIntent.getStringExtra(LOCATION_POSTAL).toString() + """",
                        "city" : """" + locationIntent.getStringExtra(LOCATION_CITY).toString() + """",
                        "country" : """" + "DE" + """",
                        "billingAddress" : """" + txtStreetAddress.text.toString() + """",
                        "billingPostCode" : """" + locationIntent.getStringExtra(LOCATION_POSTAL).toString() + """",
                        "billingCity" : """" + locationIntent.getStringExtra(LOCATION_CITY).toString() + """",
                        "billingCountry" : """" + "DE" + """",
                        "merchant" : true
                      }
                    """

                    model?.updateUserData(url, bodyJson) { response ->
                        uiThread { progressBarProcessingUser.isIndeterminate = false }
                        if (!response) {
                            context?.let {
                                Toast.makeText(it, "User Data Update problem, please try later", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            context?.let {
                                Toast.makeText(it, "User Data Update Success", Toast.LENGTH_SHORT).show()
                            }

                            goToAGB()
                        }
                    }
                }
            } else {
                progressBarProcessingUser.isIndeterminate = false
            }

        }

    }

}