package com.regionaldeals.de.location

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_list_address_prediction.view.*

class AddressPredictionViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    fun updateWithPlaces(place: data) {
        itemView.textAddress.text = place.cityName + ", " + place.postCode + ", " + place.stateAbbrv
    }
}