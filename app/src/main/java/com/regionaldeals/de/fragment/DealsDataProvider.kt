package com.regionaldeals.de.fragment

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpUpload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.regionaldeals.de.entities.GutscheinResults
import com.regionaldeals.de.entities.GutscheineObject

class DealsDataProvider {

    private val baseUrl: String = "https://www.regionaldeals.de"

    fun getGutschein(subUrl: String, params: List<Pair<String, Any?>>, responseHandler: (result: GutscheinResults?) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpGet(params).header(Pair("cache-control","no-store"),Pair("Content-Type", "application/json"))
                .responseObject(GutDataDeserializer()) { _, response, result ->

                    if (response.httpStatusCode != 200) {
                        if (response.httpStatusCode == 204) {
                            //Return Empty
                            responseHandler.invoke(GutscheinResults())
                        } else {
                            //Server issue
                            responseHandler.invoke(null)
                        }
                    }

                    val (data, _) = result
                    if (data != null) {
                        val deals = GutscheinResults()
                        deals.results.addAll(data)
                        responseHandler.invoke(deals)
                    }
                }
    }

    fun setMitmachenGutschein(subUrl: String, formData: List<Pair<String, Any>>, responseHandler: (res: Boolean) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpUpload(parameters = formData)
                .dataParts { _, url -> listOf() }
                .responseString { _, response, result ->
                    if (response.httpStatusCode != 200) {
                        responseHandler.invoke(false)
                    } else {
                        responseHandler.invoke(true)
                    }
                }

    }

    class GutDataDeserializer : ResponseDeserializable<List<GutscheineObject>> {
        override fun deserialize(content: String): List<GutscheineObject> = Gson().fromJson(content, object : TypeToken<List<GutscheineObject>>() {}.type)
    }
}
