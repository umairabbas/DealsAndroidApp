package com.regionaldeals.de.entities

import android.content.Context

import com.regionaldeals.de.R

import java.io.Serializable

class GutscheineObject : Serializable {

    var gutscheinId: Int = 0
    var shop: Shop? = null
    var createDate: Long? = 0
    var publishDate: Long? = 0
    var expiryDate: Long? = 0
    var timezone: Int? = 0
    var gutscheinTitle: String? = ""
    private var gutscheinImageUrl: String? = ""
    var gutscheinDescription: String? = ""
    var gutscheinPrice: Long? = 0
    var currency: String? = ""
    var isGutscheinAvailed: Boolean = false
    var isGutscheinWin: Long? = 0
    var category: CategoryObject? = null
    var gutscheinImageCount: Int? = 0
    var gutscheinCode: String? = ""

    fun getGutscheinImageUrl(c: Context): String {
        return c.getString(R.string.apiUrl) + "/mobile/api/gutschein/gutscheinimage?gutscheinid=" + gutscheinId
    }
}
