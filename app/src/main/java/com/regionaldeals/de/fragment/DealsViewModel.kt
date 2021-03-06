package com.regionaldeals.de.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.regionaldeals.de.entities.DealResults
import com.regionaldeals.de.entities.GutscheinResults
import com.github.kittinunf.fuel.core.Response



class DealsViewModel : ViewModel() {

    var gutscheinLiveDataList: MutableLiveData<GutscheinResults> = MutableLiveData()
    var dealLiveDataList: MutableLiveData<DealResults> = MutableLiveData()


    private var dealsDataProvider = DealsDataProvider()

    init {
    }

    fun loadDeals(url: String, params: ArrayList<Pair<String, Any?>>, responseHandler: (status: Boolean) -> Unit?) {
        dealsDataProvider.getDeals(url, params) { result ->
            if (result != null) {
                dealLiveDataList.value?.results?.clear()
                dealLiveDataList.postValue(result)
                responseHandler.invoke(true)
            } else {
                responseHandler.invoke(false)
            }
        }
    }

    fun loadGutschein(url: String, params: ArrayList<Pair<String, Any?>>, responseHandler: (status: Boolean) -> Unit?) {
        dealsDataProvider.getGutschein(url, params) { result ->
            if (result != null) {
                gutscheinLiveDataList.value?.results?.clear()
                gutscheinLiveDataList.postValue(result)
                responseHandler.invoke(true)
            } else {
                responseHandler.invoke(false)
            }
        }
    }

    fun mitmachenGutschein(url: String, params: List<Pair<String, Any>>, responseHandler: (res: Boolean) -> Unit?) {
        dealsDataProvider.setMitmachenGutschein(url, params) { gutResult ->
            responseHandler.invoke(gutResult)
        }
    }

    fun addToFav(url: String, responseHandler: (res: Response) -> Unit?) {
        dealsDataProvider.setFavourite(url) { result ->
            responseHandler.invoke(result)
        }
    }

}