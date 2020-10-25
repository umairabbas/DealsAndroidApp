package com.regionaldeals.de.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.PrefsHelper.Companion.getInstance
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Umi on 11.10.2017.
 */
class Login : Fragment() {
    private var viewPager: ViewPager? = null
    private val URL_Login = "/web/users/login"
    private var email = ""
    private var password = ""
    private var isSuccess = false
    private var name = ""
    private var progressDialog: ProgressDialog? = null
    private var message = ""
    private var jObject: JSONObject? = null
    private var _emailText: EditText? = null
    private var _passwordText: EditText? = null
    private var _loginButton: Button? = null
    private var _signupLink: TextView? = null
    private var _passowrdLink: TextView? = null
    private val dealsDataProvider = DealsDataProvider()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.login_fragment, container, false)
        _emailText = v.findViewById(R.id.input_email)
        _passwordText = v.findViewById(R.id.input_password)
        _loginButton = v.findViewById(R.id.btn_login)
        _signupLink = v.findViewById(R.id.link_signup)
        _passowrdLink = v.findViewById(R.id.link_passowrd)
        viewPager = activity!!.findViewById(R.id.viewpager)
        _loginButton?.setOnClickListener { login() }
        _signupLink?.setOnClickListener { viewPager?.currentItem = 1 }
        _passowrdLink?.setOnClickListener { createDialogue() }
        return v
    }

    private fun createDialogue() {
        context?.let {
            val mBuilder = AlertDialog.Builder(it)
            val mView = layoutInflater.inflate(R.layout.dialogue_password, null)
            val mEmailText = mView.findViewById<EditText>(R.id.input_email)
            mBuilder.setTitle(R.string.passwort_vergessen)
            mBuilder.setView(mView)
            mBuilder.setPositiveButton(getString(R.string.submit)) { dialogInterface, i ->
                if (mEmailText.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmailText.text.toString()).matches()) {
                    Toast.makeText(getContext(), getString(R.string.valid_email), Toast.LENGTH_SHORT).show()
                } else {
                    dialogInterface.dismiss()
                    val email: Pair<String, Any> = Pair("email", mEmailText.text.toString())
                    val formData: MutableList<Pair<String, Any>> = ArrayList()
                    formData.add(email)
                    dealsDataProvider.postPasswordReset("/web/users/forgetpassword", formData.toList())
                    Toast.makeText(it, getString(R.string.email_reset_ins), Toast.LENGTH_SHORT).show()
                }
            }
            val mDialog = mBuilder.create()
            mDialog.show()
        }
    }

    private fun login() {
        if (!validate()) {
            onLoginFailed()
            return
        }
        _loginButton?.isEnabled = false
        progressDialog = ProgressDialog(context)
        progressDialog?.isIndeterminate = true
        progressDialog?.setMessage("Authenticating...")
        progressDialog?.show()
        email = _emailText?.text.toString()
        password = _passwordText?.text.toString()
        LoginCall().execute()
    }

    internal inner class LoginCall : AsyncTask<String?, String?, String?>() {

        override fun doInBackground(vararg params: String?): String? {
            try {
                message = ""
                val url = URL(context!!.getString(R.string.apiUrl) + URL_Login)
                val conn = url.openConnection() as HttpsURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.doInput = true
                conn.connect()
                val jsonParam = JSONObject()
                jsonParam.put("email", email)
                jsonParam.put("password", password)
                Log.i("JSON", jsonParam.toString())
                val os = DataOutputStream(conn.outputStream)
                os.writeBytes(jsonParam.toString())
                os.flush()
                os.close()
                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)
                val `in`: BufferedReader
                `in` = if (conn.responseCode in 200..299) {
                    BufferedReader(InputStreamReader(conn.inputStream))
                } else {
                    BufferedReader(InputStreamReader(conn.errorStream))
                }
                var inputLine: String?
                val res = StringBuffer()
                while (`in`.readLine().also { inputLine = it } != null) {
                    res.append(inputLine)
                }
                `in`.close()
                jObject = JSONObject(res.toString())
                message = jObject?.getString("message") ?: ""
                conn.disconnect()
                if (message == getString(R.string.LOGIN_OK)) {
                    isSuccess = true
                    val firstName = jObject?.getString("firstName") ?: ""
                    val lastName = jObject?.getString("lastName") ?: ""
                    name = "$firstName $lastName"
                } else if (message == getString(R.string.LOGIN_ERR_INVALID_CREDENTIALS)) {
                    isSuccess = false
                    message = "Invalid Credentials"
                } else {
                    isSuccess = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(file_url: String?) {
            progressDialog?.dismiss()
            _loginButton?.isEnabled = true
            activity?.runOnUiThread {
                context?.let {
                    if (isSuccess) {
                        val prefHelper = getInstance(it)
                        prefHelper.updateUser(it, jObject.toString())
                        Toast.makeText(it, resources.getString(R.string.welcome) + " " + name, Toast.LENGTH_LONG).show()
                        try {
                            val intent = activity!!.intent
                            intent.putExtra("userEmail", jObject?.getString("email"))
                            intent.putExtra("userId", jObject?.getInt("userId"))
                            activity?.setResult(Activity.RESULT_OK, intent)
                            activity?.finish()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(it, """ ${resources.getString(R.string.login_failed)} $message """.trimIndent(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun onLoginFailed() {
        _loginButton?.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true
        val email = _emailText?.text.toString()
        val password = _passwordText?.text.toString()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText?.error = getString(R.string.valid_email)
            valid = false
        } else {
            _emailText?.error = null
        }
        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText?.error = getString(R.string.valid_pass)
            valid = false
        } else {
            _passwordText?.error = null
        }
        return valid
    }
}