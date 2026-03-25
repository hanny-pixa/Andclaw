package com.common.commonutils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils

object ActivityUtils {
    fun isForeground(context: Context, className: String): Boolean {
        if (TextUtils.isEmpty(className)) {
            return false
        }
        runCatching {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list = am.getRunningTasks(1)
            if (list.isNotEmpty()) {
                val cpn = list[0].topActivity ?: return false
                if (className == cpn.className) {
                    return true
                }
            }
        }

        return false
    }


    /**
     * 重启app
     *
     * @param context
     */
    fun bringAppToForeground(context: Context, clearTop: Boolean = false) {
        val packageManager = context.packageManager ?: return
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        if (intent != null) {
            if (clearTop) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }
    }
}