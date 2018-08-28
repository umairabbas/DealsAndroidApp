package com.regionaldeals.de.fragment

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.regionaldeals.de.entities.GutscheinResults


class DealsViewModel : ViewModel() {

    var gutscheinLiveDataList: MutableLiveData<GutscheinResults> = MutableLiveData()

    private var dealsDataProvider = DealsDataProvider()

    init {
    }

    fun loadGutschein(url: String, params: ArrayList<Pair<String, Any?>>, responseHandler: (status: Boolean) -> Unit?) {
        dealsDataProvider.getGutschein(url, params) { gutResult ->
            if (gutResult != null) {
                gutscheinLiveDataList.value?.results?.clear()
                gutscheinLiveDataList.postValue(gutResult)
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

}