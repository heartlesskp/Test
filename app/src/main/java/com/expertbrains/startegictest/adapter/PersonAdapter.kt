package com.expertbrains.startegictest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.database.testtable.TestTable
import kotlinx.android.synthetic.main.lay_person_list.view.*

class PersonAdapter : RecyclerView.Adapter<PersonAdapter.VHolder>() {

    private var alData = ArrayList<TestTable>()
    var onItemClick: ((isDelete: Boolean) -> Unit)? = null

    inner class VHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView() {
//            val item = alData[adapterPosition]
//            itemView.tvFirstName.text = item.firstName
//            itemView.tvLastName.text = item.lastName
//            itemView.tvDesignation.text = item.designation
            itemView.setOnClickListener {
                onItemClick?.invoke(false)
            }
            itemView.ivDelete.setOnClickListener {
                onItemClick?.invoke(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        return VHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.lay_person_list, parent, false)
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