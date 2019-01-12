package com.regionaldeals.de.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.regionaldeals.de.Constants
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.PrefsHelper
import com.regionaldeals.de.entities.Plans
import com.regionaldeals.de.viewmodel.ABOViewModel
import kotlinx.android.synthetic.main.abo_buchen_user.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ABOBuchenUser : Fragment() {


    private var model: ABOViewModel? = null

    private var url: String = "/mobile/api/users/updateuser"

    private lateinit var prefHelper: PrefsHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(activity!!).get(ABOViewModel::class.java)
        context?.let { prefHelper = PrefsHelper.getInstance(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.abo_buchen_user, container, false)

    private fun getSelectedPlan() = arguments?.getParcelable<Plans>(Constants.SELECTED_PLAN) ?: null

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
        if (txtEmail.text.toString().isEmpty()) {
            value = false
            inputEmail.error = " "
        }
        if (txtPhone.text.toString().isEmpty()) {
            value = false
            inputPhone.error = " "
        }
        if (txtMobile.text.toString().isEmpty()) {
            value = false
            inputMobile.error = " "
        }
        if (txtStreetAddress.text.toString().isEmpty()) {
            value = false
            inputStreetAddress.error = " "
        }
        if (txtPostal.text.toString().isEmpty()) {
            value = false
            inputPostal.error = " "
        }
        if (txtCity.text.toString().isEmpty()) {
            value = false
            inputCity.error = " "
        }
        if (txtCountry.text.toString().isEmpty()) {
            value = false
            inputCountry.error = " "
        }

        return value
    }

    private fun resetValidations() {

        inputFirst.error = null

        inputLast.error = null

        inputEmail.error = null

        inputPhone.error = null

        inputMobile.error = null

        inputStreetAddress.error = null

        inputPostal.error = null

        inputCity.error = null

        inputCountry.error = null

    }

    private fun goToAGB() {
        view?.let {
            val args = android.os.Bundle().apply {
                putParcelable(com.regionaldeals.de.Constants.SELECTED_PLAN, getSelectedPlan())
            }
            androidx.navigation.Navigation.findNavController(it).navigate(com.regionaldeals.de.R.id.action_abo_buchen_user_to_abo_buchen_agb, args)
        }
    }

    private fun setEmail() {
        txtEmail.setText(prefHelper.email, TextView.BufferType.EDITABLE)
        txtEmail.isEnabled = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEmail()

        btnUserSubmit.setOnClickListener {

            progressBarProcessing.isIndeterminate = true

            resetValidations()

            if (validateForm()) {

                doAsync {

                    val bodyJson = """
                      { "userId" : """ + prefHelper.userId.toInt() + """,
                        "firstName" : """" + txtFirst.text.toString() + """",
                        "lastName" : """" + txtLast.text.toString() + """",
                        "email" : """" + txtEmail.text.toString() + """",
                        "phone" : """" + txtPhone.text.toString() + """",
                        "mobile" : """" + txtMobile.text.toString() + """",
                        "address" : """" + txtStreetAddress.text.toString() + """",
                        "postCode" : """" + txtPostal.text.toString() + """",
                        "city" : """" + txtCity.text.toString() + """",
                        "country" : """" + txtCountry.text.toString() + """",
                        "billingAddress" : """" + txtStreetAddress.text.toString() + """",
                        "billingPostCode" : """" + txtPostal.text.toString() + """",
                        "billingCity" : """" + txtCity.text.toString() + """",
                        "billingCountry" : """" + txtCountry.text.toString() + """",
                        "shopKeeper" : true
                      }
                    """

                    model?.updateUserData(url, bodyJson) { response ->
                        uiThread { progressBarProcessing.isIndeterminate = false }
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
                progressBarProcessing.isIndeterminate = false
            }

        }

    }
}