package com.expertbrains.startegictest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.model.CountryItem
import kotlinx.android.synthetic.main.lay_dropdown_item.view.*

class CountryDropDownAdapter : RecyclerView.Adapter<CountryDropDownAdapter.VHolder>() {

    private var alData = ArrayList<CountryItem>()
    var onItemClick: ((item: CountryItem) -> Unit)? = null

    inner class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView() {
            val item = alData[adapterPosition]
            itemView.tvName.text = item.countryName
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lay_dropdown_item, parent, false)
        )
    }

    override fun getItemCount(): Int = alData.size

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.bindView()
    }

    fun addData(alData: ArrayList<CountryItem>) {
        this.alData = alData
    }
}