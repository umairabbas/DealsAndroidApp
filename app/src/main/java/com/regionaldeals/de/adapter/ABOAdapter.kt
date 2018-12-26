package com.regionaldeals.de.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import com.regionaldeals.de.R
import com.regionaldeals.de.entities.Plans
import kotlinx.android.synthetic.main.abo_buchen_list.view.*

class ABOAdapter() : RecyclerView.Adapter<ABOViewHolder>() {

    private lateinit var context: Context
    var allPlans = arrayListOf<Plans>()
    private var mClickListener: ItemClickListener? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ABOViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.abo_buchen_list, parent, false)
        context = view.context
        return ABOViewHolder(view)
    }

    override fun onBindViewHolder(holder: ABOViewHolder, position: Int) {

        val plans = allPlans[position]
        holder.itemView.planName.text = plans.planName
        holder.itemView.planDesc.text = Html.fromHtml(plans.planDescription)
        holder.itemView.subPrice.text = plans.planPrice.toString() + " " + plans.currency
        holder.itemView.planDesc.movementMethod = LinkMovementMethod.getInstance()
        holder.itemView.subMonat.text =  plans.numberBillingCycles.toString() + " Monat"

        holder.itemView.setOnClickListener {
            mClickListener?.onItemClick(allPlans[position])
        }

        holder.itemView.planDesc.setOnClickListener {
            mClickListener?.onItemClick(allPlans[position])
        }
    }

    override fun getItemCount(): Int {
        return allPlans.size
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(obj: Plans)
    }
}