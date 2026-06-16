package com.example.wakati_kotlin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wakati_kotlin.model.TransactionData
import com.example.wakati_kotlin.R
import java.util.Locale

class TransactionAdapter(
    var context: Context, var list: List<TransactionData>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>(), Filterable {
    var filteredList: MutableList<TransactionData>

    init {
        filteredList = ArrayList(list)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.transaction_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        val item = filteredList[position]
        if ("IN" == item.direction) {
            holder.txtTitle.text = "Received from"
            holder.txtName.text = item.receiverName
            holder.txtMobile.text = item.senderMobile
            holder.txtAmount.text = "+ " + item.transactionAmount + " KMF"
            holder.txtAmount.setTextColor(
                Color.parseColor("#2E7D32")
            )
        } else {
            holder.txtTitle.text = "Transferred to"
            holder.txtName.text = item.receiverName
            holder.txtMobile.text = item.receiverMobile
            holder.txtAmount.text = "- " + item.transactionAmount + " KMF"
            holder.txtAmount.setTextColor(
                Color.parseColor("#B00020")
            )
        }
        holder.txtDate.text = item.createdAt
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: TextView
        var txtName: TextView
        var txtMobile: TextView
        var txtAmount: TextView
        var txtDate: TextView

        init {
            txtTitle = itemView.findViewById(R.id.txtType)
            txtName = itemView.findViewById(R.id.txtName)
            txtMobile = itemView.findViewById(R.id.txtPhone)
            txtAmount = itemView.findViewById(R.id.txtAmount)
            txtDate = itemView.findViewById(R.id.txtDate)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(
                constraint: CharSequence
            ): FilterResults {
                val searchText =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                val tempList: MutableList<TransactionData> = ArrayList()
                if (searchText.isEmpty()) {
                    tempList.addAll(list)
                } else {
                    for (item in list) {
                        var mobileNumber: String?

                        // SAME MOBILE NUMBER WHICH SHOWING IN UI
                        mobileNumber = if ("IN" == item.direction) {
                            item.senderMobile
                        } else {
                            item.receiverMobile
                        }
                        if (mobileNumber != null && mobileNumber.lowercase(Locale.getDefault())
                                .contains(searchText)
                        ) {
                            tempList.add(item)
                        }
                    }
                }
                val results = FilterResults()
                results.values = tempList
                return results
            }

            override fun publishResults(
                constraint: CharSequence, results: FilterResults
            ) {
                filteredList.clear()
                filteredList.addAll(
                    (results.values as List<TransactionData>)
                )
                notifyDataSetChanged()
            }
        }
    }
}