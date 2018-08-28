package com.regionaldeals.de.entities

import java.io.Serializable

/**
 * Created by Umi on 29.09.2017.
 */
class Shop(var shopId: Int, var shopName: String?) : Serializable {
    var shopAddress: String? = ""
    var shopCountry: String? = ""
    var shopDetails: String? = ""
    var taxNumber: String? = ""
    var shopLocationLat: String? = ""
    var shopLocationLong: String? = ""
    var shopContact: String? = ""
    var shopCity: String? = ""
    var active: Boolean? = true
    var shopCategories: String? = ""
}
