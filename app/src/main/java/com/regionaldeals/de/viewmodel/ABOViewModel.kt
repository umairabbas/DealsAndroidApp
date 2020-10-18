package com.regionaldeals.de.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.core.Response
import com.regionaldeals.de.entities.PlansResults
import com.regionaldeals.de.entities.UserObject

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

    fun updateUserData(url: String, params:  String, responseHandler: (status: Boolean) -> Unit?) {
        subDataProvider.updateUser(url, params) { result ->
            if (result != null) {
                responseHandler.invoke(true)
            } else {
                responseHandler.invoke(false)
            }
        }
    }



    fun updateSubscription(url: String, responseHandler: (status: Response) -> Unit?) {
        subDataProvider.updateSubscription(url) { result ->
            responseHandler.invoke(result)
        }
    }

    fun buyPlan(url: String, params: List<Pair<String, Any?>>, responseHandler: (status: Response) -> Unit?) {
        subDataProvider.buyPlan(url, params) { result ->
            responseHandler.invoke(result)
        }
    }
}