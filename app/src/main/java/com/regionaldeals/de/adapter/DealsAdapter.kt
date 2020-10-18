package com.regionaldeals.de.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.ColorUtility
import com.regionaldeals.de.entities.DealObject
import com.squareup.picasso.Picasso

class DealsAdapter(private val isFromFav: Boolean, private val skipFavBtn: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<DealsViewHolder>() {

    private lateinit var context: Context
    var allDeals = arrayListOf<DealObject>()
    private var mClickListener: ItemClickListener? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dealslist_layout, parent, false)
        context = view.context
        return DealsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealsViewHolder, position: Int) {

        val deals = allDeals[position]
        holder.dealTitle.text = deals.dealTitle
        deals.shop?.let {shop ->
            shop.shopCity?.let{city ->
                holder.dealDescription.text = shop.shopName + ", " + city.substring(0, 1).toUpperCase() + city.substring(1)
            }
        }
        holder.dealOldPrice.setText(java.lang.Double.toString(deals.originalPrice!!) + "€")
        holder.dealPrice.setText(java.lang.Double.toString(deals.dealPrice!!) + "€")
        val imgUrl = deals.getDealImageUrl(context) + "&imagecount=1&res=470x320"
        Picasso.with(context).load(imgUrl).placeholder(ColorUtility.getColorFromPosition(position)).resize(470, 320).onlyScaleDown().into(holder.dealCoverUrl)

        if (!skipFavBtn) {
            if (deals.favourite == null || deals.favourite == false) {
                holder.favoriteImageButton.setImageResource(R.drawable.not_favorite)
            } else if (deals.favourite == true) {
                holder.favoriteImageButton.setImageResource(R.drawable.favorite)
            }
        } else {
            holder.favoriteImageButton.setVisibility(View.GONE)
        }

        holder.itemView.setOnClickListener {
            mClickListener?.onItemClick(allDeals[position])
        }

        holder.favoriteImageButton.setOnClickListener {
            mClickListener?.onFavouriteClick(allDeals[position], isFromFav, position)
        }

    }

    override fun getItemCount(): Int {
        return allDeals.size
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(obj: DealObject)
        fun onFavouriteClick(obj: DealObject, isFromFav: Boolean, pos: Int)
    }
}
