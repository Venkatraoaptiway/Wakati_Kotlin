package com.example.wakati_kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakati_kotlin.model.DealerModel

class AssignSuperDealerAdapter(
    private val context: Context,
    private val list: ArrayList<DealerModel>,
    private val onClick:(DealerModel)->Unit
) : RecyclerView.Adapter<AssignSuperDealerAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView){

        val name =
            itemView.findViewById<
                    TextView>(
                android.R.id.text1
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(context)
                .inflate(
                    android.R.layout.simple_list_item_1,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.name.text =
            item.full_name

        holder.itemView.setOnClickListener {

            onClick(item)
        }
    }

    override fun getItemCount() =
        list.size
}