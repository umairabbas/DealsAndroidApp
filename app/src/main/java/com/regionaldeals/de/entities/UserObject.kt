package com.regionaldeals.de.entities

import java.io.Serializable

data class UserObject(val userId: Int? = 0): Serializable {

    val userName: String? = ""

    val firstName: String? = ""

    val lastName: String? = ""

    val email: String? = ""

    val phone: String? = ""

    val mobile: String? = ""

    val address: String? = ""

    val postCode: String? = ""

    val city: String? = ""

    val country: String? = ""

    val billingAddress: String? = ""

    val billingPostCode: String? = ""

    val billingCity: String? = ""

    val billingCountry: String? = ""

    val shopKeeper: Boolean? = false

}