package com.regionaldeals.de.location

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.regionaldeals.de.R

class AddressPredictionAdapter(private val clickListener: (data) -> Unit) : RecyclerView.Adapter<AddressPredictionViewHolder>() {

    private var placesList = mutableListOf<data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AddressPredictionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_address_prediction, parent, false))

    override fun getItemCount() = placesList.size

    override fun onBindViewHolder(holder: AddressPredictionViewHolder, position: Int) {
        holder.updateWithPlaces(placesList[position])
        holder.itemView.setOnClickListener {
            clickListener(placesList[position])
        }
    }

    fun updateAdapter(places: List<data>) {
        placesList.clear()
        placesList.addAll(places)
        notifyDataSetChanged()
    }
}