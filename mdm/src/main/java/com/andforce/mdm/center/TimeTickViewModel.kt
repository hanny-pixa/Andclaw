package com.andforce.mdm.center

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged


class TimeTickViewModel : ViewModel() {

    fun observeTimeChange(context: Context): Flow<Long> = callbackFlow {
        val screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_TIME_TICK -> trySend(System.currentTimeMillis())
                    Intent.ACTION_TIME_CHANGED -> trySend(System.currentTimeMillis())
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
        }

        trySend(System.currentTimeMillis())

        context.applicationContext.registerReceiver(screenReceiver, filter)

        awaitClose {
            runCatching {
                context.applicationContext.unregisterReceiver(screenReceiver)
            }
        }
    }.distinctUntilChanged()

}