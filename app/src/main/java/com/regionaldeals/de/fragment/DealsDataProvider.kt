package com.regionaldeals.de.fragment

import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpUpload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.regionaldeals.de.entities.GutscheinResults
import com.regionaldeals.de.entities.GutscheineObject
import kotlinx.android.synthetic.main.signup_fragment.*
import java.io.Reader
import java.lang.reflect.Type

class DealsDataProvider {

    val baseUrl: String = "https://www.regionaldeals.de"

    fun getGutschein(subUrl: String, params: List<Pair<String, Any?>>, responseHandler: (result: GutscheinResults) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpGet(params).header(Pair("Content-Type", "application/json"))
                .responseObject(GutscheineDataDeserializer()) { _, response, result ->

                    if (response.httpStatusCode != 200) {

                    }

                    val (data, _) = result
                    if (data != null) {
                        var deals = GutscheinResults()
                        deals.results.addAll(data as ArrayList<GutscheineObject>)
                        responseHandler.invoke(deals)
                    }
                }
    }

    fun setMitmachenGutschein(subUrl: String, formData: List<Pair<String, Any>>, responseHandler: (res: Boolean) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        //val formData = listOf("Email" to "mail@example.com", "Name" to "Joe Smith")
        mainUrl.httpUpload(parameters = formData)
                .dataParts { request, url -> listOf<DataPart>() }
                .responseString { request, response, result ->
                    if (response.httpStatusCode != 200) {
                        responseHandler.invoke(false)
                        //throw Exception("Unable to avail coupon")
                    } else {
                        responseHandler.invoke(true)
                    }
                }

    }

    class GutscheineDataDeserializer : ResponseDeserializable<Any> {
        override fun deserialize(reader: String) = Gson().fromJson<List<GutscheineObject>>(reader, object : TypeToken<List<GutscheineObject>>() {}.type)
    }
}
