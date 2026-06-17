package com.example.wakati_kotlin.utils

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.wakati_kotlin.R

object GlobalToast {

    fun show(
        activity: Activity,
        message: String,
        type: ToastType = ToastType.DEFAULT
    ) {
        activity.runOnUiThread {

            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.layout_global_toast, null)

//            val tvMessage = view.findViewById<TextView>(R.id.tvMessage)

            val tvMessage = view.findViewById<TextView>(R.id.tvMessage)

            tvMessage.text = message

            tvMessage.typeface =
                ResourcesCompat.getFont(activity, R.font.montserrat_bold)

            tvMessage.text = message

            when (type) {
                ToastType.SUCCESS -> view.setBackgroundColor(Color.parseColor("#4CAF50"))
                ToastType.ERROR -> view.setBackgroundColor(Color.parseColor("#730101"))
                ToastType.DEFAULT -> view.setBackgroundColor(Color.parseColor("#333333"))
            }

            Toast(activity).apply {
                duration = Toast.LENGTH_SHORT
                setGravity(Gravity.BOTTOM, 0, 100)
                this.view = view
            }.show()
        }
    }
}

enum class ToastType {
    SUCCESS,
    ERROR,
    DEFAULT
}