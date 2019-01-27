package com.regionaldeals.de.location

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import java.io.Reader

class AddressDataProvider {

    private val baseUrl: String = "https://www.regionaldeals.de"

    private val placeUrl: String = "$baseUrl/mobile/api/device/citieslist"

    fun getAddressPredictions(query: String, responseHandler: (result: LocationRoot) -> Unit?) {
        val params: MutableList<Pair<String, Any?>> = mutableListOf()
        params.add(Pair("query", query))

        placeUrl.httpGet(params)
                .responseObject(AddressDataDeserializer()) { _, response, result ->

                    if (response.httpStatusCode != 200) {
                        responseHandler.invoke(LocationRoot())
                        //throw Exception("Unable to get predictions")
                    }
                    val (x, _) = result
                    x?.let{
                        responseHandler.invoke(it)
                    }

                }
    }

    class AddressDataDeserializer : ResponseDeserializable<LocationRoot> {
        override fun deserialize(reader: Reader) = Gson().fromJson(reader, LocationRoot::class.java)
    }
}
