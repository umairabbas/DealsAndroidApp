package com.regionaldeals.de.entities

import android.content.Context

import com.regionaldeals.de.R

import java.io.Serializable

/**
 * Created by Umi on 25.09.2017.
 */

class DealObject() : Serializable {
    var originalPrice: Double? = 0.toDouble()
    var dealPrice: Double? = 0.toDouble()
    var shop: Shop? = null
    var favourite: Boolean? = false
    var dealType: String? = ""
    var dealImageCount: Int? = 0
    var dealUrl: String? = ""
    var dealId: Int? = 0
    var dealTitle: String? = ""
    var dealDescription: String? = ""
    var createDate: Long? = 0.toLong()
    var publishDate: Long? = 0.toLong()
    var expiryDate: Long? = 0.toLong()
    var timezone: String? = ""
    var currency: String? = ""

    init {
        this.originalPrice = originalPrice?.toDouble()
        this.dealPrice = dealPrice?.toDouble()
        this.shop = shop
    }

    fun getDealImageUrl(c: Context): String {
        return c.getString(R.string.apiUrl) + "/mobile/api/deals/dealimage?dealid=" + dealId + "&" + "dealtype=" + dealType
    }

    fun getDateExpire(): Long? {
        return expiryDate
    }

}
