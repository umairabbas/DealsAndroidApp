package com.regionaldeals.de.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.regionaldeals.de.entities.PlansResults

class ABOViewModel : ViewModel() {

    var plansRes: MutableLiveData<PlansResults> = MutableLiveData()

    private var subDataProvider = SubDataProvider()


    init {
    }

    fun loadPlans(url: String, responseHandler: (status: Boolean) -> Unit?) {
        subDataProvider.getPlans(url) { result ->
            if (result != null) {
                plansRes.value?.results?.clear()
                plansRes.postValue(result)
                responseHandler.invoke(true)
            } else {
                responseHandler.invoke(false)
            }
        }
    }
}