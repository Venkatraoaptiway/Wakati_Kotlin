package com.example.wakati_kotlin.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.example.wakati_kotlin.R

object LoaderUtils {
    private var dialog: Dialog? = null

    // Show Loader
    fun showLoader(context: Context?) {
        if (dialog != null && dialog!!.isShowing) {
            return
        }
        dialog = Dialog(context!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.loading_dialog)
        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
        }
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    // Hide Loader
    fun hideLoader() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }
}
