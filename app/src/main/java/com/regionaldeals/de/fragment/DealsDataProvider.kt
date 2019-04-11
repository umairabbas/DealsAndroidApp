package com.regionaldeals.de.fragment

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPut
import com.github.kittinunf.fuel.httpUpload
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.regionaldeals.de.entities.DealObject
import com.regionaldeals.de.entities.DealResults
import com.regionaldeals.de.entities.GutscheinResults
import com.regionaldeals.de.entities.GutscheineObject

class DealsDataProvider {

    private val baseUrl: String = "https://api.regionaldeals.de"

    fun getDeals(subUrl: String, params: List<Pair<String, Any?>>, responseHandler: (result: DealResults?) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpGet(params).header(Pair("Content-Type", "application/json"))
                .responseObject(DealsDataDeserializer()) { _, response, result ->

                    if (response.statusCode != 200) {
                        if (response.statusCode == 204) {
                            //Return Empty
                            responseHandler.invoke(DealResults())
                        } else {
                            //Server issue
                            responseHandler.invoke(null)
                        }
                    }

                    val (data, _) = result
                    if (data != null) {
                        val deals = DealResults()
                        deals.results.addAll(data)
                        responseHandler.invoke(deals)
                    }
                }
    }

    fun getGutschein(subUrl: String, params: List<Pair<String, Any?>>, responseHandler: (result: GutscheinResults?) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpGet(params).header(Pair("Content-Type", "application/json"))
                .responseObject(GutDataDeserializer()) { _, response, result ->

                    if (response.statusCode != 200) {
                        if (response.statusCode == 204) {
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
                .dataParts { _, _ -> listOf() }
                .responseString { _, response, _ ->
                    if (response.statusCode != 200) {
                        responseHandler.invoke(false)
                    } else {
                        responseHandler.invoke(true)
                    }
                }

    }

    fun postPasswordReset(subUrl: String, formData: List<Pair<String, Any>>) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpUpload(parameters = formData)
                .dataParts { _, _ -> listOf() }
                .responseString { _, response, _ ->
                    if (response.statusCode != 200) {

                    } else {

                    }
                }

    }

    fun setFavourite(subUrl: String, responseHandler: (res: Response) -> Unit?) {
        val mainUrl = baseUrl + subUrl
        mainUrl.httpPut().header(Pair("Accept", "application/json"))
                .responseString { _, response, _ ->
                    responseHandler.invoke(response)
                }

    }

    class DealsDataDeserializer : ResponseDeserializable<List<DealObject>> {
        override fun deserialize(content: String): List<DealObject> = Gson().fromJson(content, object : TypeToken<List<DealObject>>() {}.type)
    }

    class GutDataDeserializer : ResponseDeserializable<List<GutscheineObject>> {
        override fun deserialize(content: String): List<GutscheineObject> = Gson().fromJson(content, object : TypeToken<List<GutscheineObject>>() {}.type)
    }
}
