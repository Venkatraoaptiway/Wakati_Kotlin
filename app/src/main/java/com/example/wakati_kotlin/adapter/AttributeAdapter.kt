package com.example.wakati_kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakati_kotlin.model.Attribute
import com.example.wakati_kotlin.R

class AttributeAdapter(var context: Context, var attributeList: List<Attribute>) :
    RecyclerView.Adapter<AttributeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (attributeLabel, value) = attributeList[position]

        // Hide null / empty values
        if (value == null || value == "null" || value.trim { it <= ' ' }.isEmpty()) {
            holder.itemView.visibility = View.GONE
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.height = 0
            params.width = 0
            holder.itemView.layoutParams = params
        } else {
            holder.itemView.visibility = View.VISIBLE
            holder.txtLabel.text = attributeLabel
            holder.txtValue.text = value
        }
    }

    override fun getItemCount(): Int {
        return attributeList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtLabel: TextView
        var txtValue: TextView

        init {
            txtLabel = itemView.findViewById(R.id.txtLabel)
            txtValue = itemView.findViewById(R.id.txtValue)
        }
    }
}