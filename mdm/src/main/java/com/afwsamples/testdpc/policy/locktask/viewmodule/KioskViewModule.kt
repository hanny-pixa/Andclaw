package com.afwsamples.testdpc.policy.locktask.viewmodule

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andforce.mdm.basemodule.BaseViewModel
import com.base.services.IAppInfoService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class KioskViewModule : BaseViewModel() {
    companion object {
        const val TAG = "KioskViewModule"
    }

    private val appInfo: IAppInfoService by inject()


    val launcherDefaultApp: StateFlow<String> = KioskViewModuleHelper.launcherDefaultApp

    val configVersionFlow: StateFlow<Int> = KioskViewModuleHelper.configVersionFlow

    val deviceOwnerStateFlow: StateFlow<Boolean> = KioskViewModuleHelper.deviceOwnerStateFlow

    fun updateDeviceOwnerState(enabled: Boolean) {
        Log.d(TAG, "updateDeviceOwnerState: $enabled")
        KioskViewModuleHelper.updateDeviceOwnerState(enabled)
    }

    fun isDeviceOwner(): Boolean {
        //return _deviceOwnerStateFlow.value
        return KioskViewModuleHelper.isDeviceOwnerEnabled()
    }

    private fun isAppEnabled(packageName: String): Boolean {
        //return _enableApps.value.any { it.pkg == packageName }
        return KioskViewModuleHelper.isAppEnabled(packageName)
    }

    fun onAppInstalled(packageName: String) {
        viewModelScope.launch {
            if (isDeviceOwner()) {
            } else {
                Log.i(TAG, "非设备管理员，无法隐藏应用: $packageName")
            }
        }
    }

    fun onAppUninstalled(packageName: String) {
        viewModelScope.launch {
            // 处理应用卸载事件
        }
    }

    fun onAppUpdated(packageName: String) {
        viewModelScope.launch {
            // 处理应用更新事件
        }
    }
}