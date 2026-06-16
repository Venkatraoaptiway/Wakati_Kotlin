package com.example.wakati_kotlin.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wakati_kotlin.R
import com.example.wakati_kotlin.model.DealerModel
import com.example.wakati_kotlin.reciever.UsersDetailsCardActivity

class DealerAdapter(
    private val context: Context,
    private val dealerList: ArrayList<DealerModel>,
    private val flag: String
) : RecyclerView.Adapter<DealerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvName: TextView =
            itemView.findViewById(R.id.tvName)

        val tvCount: TextView =
            itemView.findViewById(R.id.tvCount )

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)




        val tvUser: View? =
            itemView.findViewById(R.id.user_details)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context)
            .inflate(
                R.layout.row_dealer,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = dealerList[position]

        holder.tvName.text =
            item.full_name ?: ""

        holder.tvCount.text =
            (item.assigned_dealers_count ?: "").toString()

        holder.tvStatus.text =
            item.status ?: ""





        holder.tvUser!!.setOnClickListener {

            Log.d("FLAG_2", flag ?: "")


            val intent = Intent(context, UsersDetailsCardActivity::class.java)

            intent.putExtra("flag", flag)


            intent.putExtra("user_id", item.user_id  ) // Current logged in user
            intent.putExtra("user_type", item.user_type)
            intent.putExtra("full_name", item.full_name)
            context.startActivity(intent)
        }
    }




    override fun getItemCount(): Int {
        return dealerList.size
    }

    fun updateData(
        list: ArrayList<DealerModel>
    ) {

        dealerList.clear()

        dealerList.addAll(list)

        notifyDataSetChanged()
    }




}