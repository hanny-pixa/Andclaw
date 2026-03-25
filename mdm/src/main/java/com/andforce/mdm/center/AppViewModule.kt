package com.andforce.mdm.center

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.andforce.mdm.basemodule.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModule : BaseViewModel() {

    companion object {
        const val TAG = "AppViewModule"
    }

    private val _mutableAppChangeFlow = MutableStateFlow<AppChange?>(null)
    val appChangeFlow: StateFlow<AppChange?> = _mutableAppChangeFlow

    var isDeviceOwnerRemoving = false

    fun startObserve(context: Context) {

        // 注册广播接收器
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        context.registerReceiver(packageChangeReceiver, filter)
    }

    private val packageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val packageName = intent.data?.schemeSpecificPart
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    if (!isDeviceOwnerRemoving) {
//                        Toast.makeText(
//                            context,
//                            "新应用已安装: $packageName",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        packageName?.let {
                            //viewModel.onAppInstalled(it)
                            _mutableAppChangeFlow.value = AppChange(Intent.ACTION_PACKAGE_ADDED, it)
                        }
                    }
                }

                Intent.ACTION_PACKAGE_REMOVED -> {
//                    Toast.makeText(
//                        context,
//                        "应用已卸载: $packageName",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    packageName?.let {
                        //viewModel.onAppUninstalled(it)
                        _mutableAppChangeFlow.value = AppChange(Intent.ACTION_PACKAGE_REMOVED, it)
                    }
                }

                Intent.ACTION_PACKAGE_REPLACED -> {
//                    Toast.makeText(
//                        context,
//                        "应用已更新: $packageName",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    packageName?.let {
                        //viewModel.onAppUpdated(it)
                        _mutableAppChangeFlow.value = AppChange(Intent.ACTION_PACKAGE_REPLACED, it)
                    }
                }
            }
        }
    }


}