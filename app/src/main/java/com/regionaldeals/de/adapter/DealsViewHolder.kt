package com.regionaldeals.de.adapter

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.regionaldeals.de.R

class DealsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var dealTitle: TextView
    var dealDescription: TextView
    var dealOldPrice: TextView
    var dealPrice: TextView
    var dealCoverUrl: ImageView
    var favoriteImageButton: ImageButton

    init {
        dealTitle = itemView.findViewById<View>(R.id.deal_title) as TextView
        dealDescription = itemView.findViewById<View>(R.id.deal_description) as TextView
        dealOldPrice = itemView.findViewById<View>(R.id.deal_old_price) as TextView
        dealOldPrice.paintFlags = dealOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        dealPrice = itemView.findViewById<View>(R.id.deal_price) as TextView
        dealCoverUrl = itemView.findViewById<View>(R.id.card_image_gut) as ImageView
        favoriteImageButton = itemView.findViewById<View>(R.id.favorite_button) as ImageButton

    }

}