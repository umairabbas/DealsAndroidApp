package com.regionaldeals.de.entities

import android.os.Parcelable

import java.io.Serializable

import kotlinx.android.parcel.Parcelize

/**
 * Created by Umi on 03.01.2018.
 */
@Parcelize
class Plans : Parcelable {
    var id: Int? = null

    var planName: String? = null

    var planShortName: String? = null

    var planDescription: String? = null

    var planPrice: Double? = null

    var billingCycle: Int? = null

    var numberBillingCycles: Int? = null

    var currency: String? = null

    var planOffer: String? = null
}
