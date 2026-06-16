package com.example.wakati_kotlin.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View

object StatusBarUtils {
    @JvmStatic
    fun setStatusBar(activity: Activity, isLightBackground: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            activity.window.statusBarColor = Color.TRANSPARENT

            // BLACK ICONS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isLightBackground) {
                    activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {

                    // WHITE ICONS
                    activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
            }
        }
    }
}