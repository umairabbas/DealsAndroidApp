package com.regionaldeals.de.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.jetbrains.anko.doAsync

class AddressViewModel : ViewModel() {

    private val addressDataProvider = AddressDataProvider()

    var predictionsLiveDataList: MutableLiveData<ArrayList<data>> = MutableLiveData()


    fun fetchAddressPrediction(query: String, callback: (ArrayList<data>) -> Unit) {
        doAsync {
            try {
                addressDataProvider.getAddressPredictions(query) {
                    it.data?.let { predictionsLiveDataList.value?.clear()
                        predictionsLiveDataList.postValue(it)
                        callback(it)
                    }
                }
            } catch (e: Exception) {
                //Show proper error message to user
            }
        }
    }
}