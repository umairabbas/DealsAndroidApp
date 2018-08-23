package com.regionaldeals.de.fragment

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.regionaldeals.de.entities.GutscheinResults


class DealsViewModel : ViewModel() {

    var gutscheinLiveDataList: MutableLiveData<GutscheinResults> = MutableLiveData()

    private var dealsDataProvider = DealsDataProvider()

    init {
    }

    fun loadGutschein(url: String, params: ArrayList<Pair<String, Any?>>) {
        dealsDataProvider.getGutschein(url, params) { gutResult ->
            gutscheinLiveDataList.value?.results?.clear()
            gutscheinLiveDataList.postValue(gutResult)
        }
    }

    fun mitmachenGutschein(url: String, params: List<Pair<String, Any>>, responseHandler:(res: Boolean) -> Unit? ) {
        dealsDataProvider.setMitmachenGutschein(url, params) { gutResult ->
            responseHandler.invoke(gutResult)
        }
    }

}