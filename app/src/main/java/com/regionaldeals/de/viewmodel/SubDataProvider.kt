package com.regionaldeals.de.viewmodel

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.regionaldeals.de.entities.Plans
import com.regionaldeals.de.entities.PlansResults
import org.json.JSONObject

class SubDataProvider {

    private val baseUrl: String = "https://www.regionaldeals.de"

    fun getPlans(subUrl: String, responseHandler: (result: PlansResults?) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpGet().header(Pair("Content-Type", "application/json"))
                .responseObject(PlansDataDeserializer()) { _, response, result ->

                    if (response.statusCode != 200) {
                        if (response.statusCode == 204) {
                            //Return Empty
                            responseHandler.invoke(PlansResults())
                        } else {
                            //Server issue
                            responseHandler.invoke(null)
                        }
                    }

                    val (data, _) = result
                    if (data != null) {
                        val deals = PlansResults()
                        deals.results.addAll(data)
                        responseHandler.invoke(deals)
                    }
                }
    }

    class PlansDataDeserializer : ResponseDeserializable<List<Plans>> {
        override fun deserialize(content: String): List<Plans> = Gson().fromJson(JSONObject(content).getString("data"), object : TypeToken<List<Plans>>() {}.type)
    }
}