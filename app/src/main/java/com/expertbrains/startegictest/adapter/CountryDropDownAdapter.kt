package com.expertbrains.startegictest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.database.testtable.TestTable
import kotlinx.android.synthetic.main.lay_dropdown_item.view.*

class CountryDropDownAdapter : RecyclerView.Adapter<CountryDropDownAdapter.VHolder>() {

    private var alData = ArrayList<TestTable>()
    var onItemClick: (() -> Unit)? = null

    inner class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView() {
            itemView.tvName.text = "Kaushal"
            itemView.setOnClickListener {
                onItemClick?.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lay_dropdown_item, parent, false)
        )
    }

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.bindView()
    }

    fun addData(alData: ArrayList<TestTable>) {
        this.alData = alData
    }
}