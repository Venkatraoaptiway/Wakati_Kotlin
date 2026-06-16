package com.example.wakati_kotlin.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wakati_kotlin.model.Document
import com.example.wakati_kotlin.R

class DocumentAdapter(var activity: Activity, var documentList: List<Document>) :
    RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_document, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = documentList[position]
        holder.txtDocName.text = document.documentTypeLabel
        holder.itemView.setOnClickListener { v: View? ->
            val url = document.documentUrl ?: return@setOnClickListener

            // IMAGE
            if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                val dialog = Dialog(activity)
                dialog.setContentView(R.layout.dialog_image_preview)
                val imageView = dialog.findViewById<ImageView>(R.id.previewImage)
                Glide.with(activity).load(url).into(imageView)
                dialog.show()
            } else {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(browserIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtDocName: TextView
        var imgDoc: ImageView

        init {
            txtDocName = itemView.findViewById(R.id.txtDocName)
            imgDoc = itemView.findViewById(R.id.imgDoc)
        }
    }
}