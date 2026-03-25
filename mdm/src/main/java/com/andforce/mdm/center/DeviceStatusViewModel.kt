package com.andforce.mdm.center

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class DeviceStatusViewModel : ViewModel() {

    fun isNetworkConnected(connectivityManager: ConnectivityManager?): Boolean {
        return connectivityManager?.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
        } ?: false
    }

    fun observeNetworkState(connectivityManager: ConnectivityManager): Flow<Boolean> =
        callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(true)
                }

                override fun onLost(network: Network) {
                    trySend(false)
                }
            }

            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

            // 发送初始状态
            val isConnected = connectivityManager.activeNetwork?.let { network ->
                connectivityManager.getNetworkCapabilities(network)?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
            } ?: false
            trySend(isConnected)

            awaitClose {
                runCatching {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            }
        }.distinctUntilChanged()

    /**
     * 监听屏幕状态（锁屏/开屏）
     * @param context Context
     * @return Flow<Boolean> true表示屏幕开启，false表示屏幕关闭
     */
    fun observeScreenState(context: Context): Flow<Boolean> = callbackFlow {
        val screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_USER_PRESENT -> trySend(true)
                    Intent.ACTION_SCREEN_OFF -> trySend(false)
                }
            }
        }

        val filter = IntentFilter().apply {
//            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }

        // 发送初始状态（默认为true，因为应用启动时屏幕必然是亮着的）
        trySend(false)
        
        context.registerReceiver(screenReceiver, filter)

        awaitClose {
            runCatching {
                context.unregisterReceiver(screenReceiver)
            }
        }
    }.distinctUntilChanged()
}