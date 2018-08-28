package com.regionaldeals.de.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.ColorUtility
import com.regionaldeals.de.entities.GutscheineObject
import com.squareup.picasso.Picasso

class GutscheineAdapter(private val isEdit: Boolean) : RecyclerView.Adapter<GutscheineViewHolder>() {

    private lateinit var context: Context
    var allDeals = arrayListOf<GutscheineObject>()
    private var mClickListener: ItemClickListener? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GutscheineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gutscheinelist_layout, parent, false)
        context = view.context
        return GutscheineViewHolder(view, allDeals, isEdit)
    }

    override fun onBindViewHolder(holder: GutscheineViewHolder, position: Int) {
        val deals = allDeals[position]
        holder.dealTitle.text = deals.gutscheinTitle
        holder.dealDescription.text = deals.shop?.shopName + ", " + deals.shop?.shopCity?.substring(0, 1)?.toUpperCase() + deals.shop?.shopCity?.substring(1)
        holder.gut_price.text = deals.gutscheinPrice.toString() + "€"
        val imgUrl = deals.getGutscheinImageUrl(context) + "&imagecount=1&res=470x320"
        Picasso.with(context).load(imgUrl).placeholder(ColorUtility.getColorFromPosition(position)).into(holder.dealCoverUrl)

        if (!isEdit) {
            if (deals.isGutscheinAvailed) {
                holder.mitMachenBtn.isEnabled = false
                holder.mitMachenBtn.setColorFilter(context.resources.getColor(R.color.colorAccent))
            } else {
                holder.mitMachenBtn.setOnClickListener {
                    mClickListener?.onItemMitmachenClick(allDeals[position])
                }
            }
        } else {
            holder.mitMachenBtn.visibility = View.GONE
            holder.availText.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { v ->
            mClickListener?.onItemClick(allDeals[position])
        }

    }

    override fun getItemCount(): Int {
        return allDeals.size
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(obj: GutscheineObject)
        fun onItemMitmachenClick(obj: GutscheineObject)
    }
}
