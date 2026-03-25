package com.afwsamples.testdpc.policy.locktask.viewmodule

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object KioskViewModuleHelper {


    var lastFetchConfigSuccessTime = 0L

    private var _launcherDefaultApp = MutableStateFlow("")
    val launcherDefaultApp: StateFlow<String> = _launcherDefaultApp
    fun updateLauncherDefaultApp(app: String) {
        _launcherDefaultApp.value = app
    }

    private var _configVersionFlow = MutableStateFlow(-1)
    val configVersionFlow: StateFlow<Int> = _configVersionFlow
    fun updateConfigVersion(version: Int) {
        _configVersionFlow.value = version
    }

    private var _deviceOwnerStateFlow = MutableStateFlow(false)
    val deviceOwnerStateFlow: StateFlow<Boolean> = _deviceOwnerStateFlow
    fun updateDeviceOwnerState(enabled: Boolean) {
        _deviceOwnerStateFlow.value = enabled
    }

    fun isDeviceOwnerEnabled(): Boolean {
        return _deviceOwnerStateFlow.value
    }

    fun isAppEnabled(packageName: String): Boolean {
        return true
    }
}