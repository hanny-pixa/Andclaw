package com.andforce.mdm.center

import android.app.Activity
import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import com.afwsamples.testdpc.R
import com.afwsamples.testdpc.common.Util

class BootupNoticeActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bootup_notice)

        findViewById<Button>(R.id.notice_button).setOnClickListener {
            BootupNoticeHelper.setNoticeShowed(this, true)
            stopLockTask()
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        // start lock task mode if it's not already active
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        // ActivityManager.getLockTaskModeState api is not available in pre-M.
        if (Util.SDK_INT < Build.VERSION_CODES.M) {
            if (!am.isInLockTaskMode) {
                startLockTask()
            }
        } else {
            if (am.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask()
            }
        }
    }
}