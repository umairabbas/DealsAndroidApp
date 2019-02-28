package com.regionaldeals.de.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import com.regionaldeals.de.R
import com.regionaldeals.de.Utils.URLImageParser
import com.regionaldeals.de.entities.Plans
import kotlinx.android.synthetic.main.abo_buchen_list.view.*
import android.text.Spanned



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
        val p = URLImageParser(holder.itemView.planDesc, context)
        val htmlSpan = Html.fromHtml(plans.planDescription, p, null)
        holder.itemView.planDesc.text = htmlSpan
        holder.itemView.subPrice.text = plans.planPrice.toString() + " " + plans.currency + " Netto"
        holder.itemView.planDesc.movementMethod = LinkMovementMethod.getInstance()
        holder.itemView.subMonat.text =  plans.numberBillingCycles.toString() + " Monate"

        holder.itemView.btnPlan.setOnClickListener {
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
