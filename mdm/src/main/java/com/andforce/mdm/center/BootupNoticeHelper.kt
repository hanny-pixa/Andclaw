package com.andforce.mdm.center

import android.content.Context
import android.content.SharedPreferences
import com.andforce.mdm.ext.getAppContext
import androidx.core.content.edit

object BootupNoticeHelper {
    const val KEY = "bootup_notice_showed"

    val sp: SharedPreferences by lazy {
        this.getAppContext().getSharedPreferences("bootup_notice", Context.MODE_PRIVATE)
    }

    fun isNoticeShowed(context: Context): Boolean {
        return sp.getBoolean(KEY, false)
    }

    fun setNoticeShowed(context: Context, showed: Boolean) {
        sp.edit() { putBoolean(KEY, showed) }
    }
}