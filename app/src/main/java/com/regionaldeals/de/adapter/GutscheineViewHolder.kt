package com.regionaldeals.de.adapter


import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.regionaldeals.de.R
import com.regionaldeals.de.entities.GutscheineObject


class GutscheineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var dealTitle: TextView
    var dealDescription: TextView
    var gut_price: TextView
    var dealCoverUrl: ImageView
    var mitMachenBtn: ImageView
    var availText: TextView

    init {
        dealTitle = itemView.findViewById<View>(R.id.deal_title) as TextView
        dealDescription = itemView.findViewById<View>(R.id.deal_description) as TextView
        gut_price = itemView.findViewById<View>(R.id.gut_price) as TextView
        dealCoverUrl = itemView.findViewById<View>(R.id.card_image_gut) as ImageView
        mitMachenBtn = itemView.findViewById<View>(R.id.imageView_mitmachen) as ImageView
        availText = itemView.findViewById<View>(R.id.mit_text) as TextView

    }

}
