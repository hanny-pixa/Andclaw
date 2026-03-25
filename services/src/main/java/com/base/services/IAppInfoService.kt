package com.base.services

interface IAppInfoService {

    fun getAppName(): String

    fun isSystemApp(): Boolean

    fun isDebug(): Boolean

    fun getVersionCode(): Int

    fun getVersionName(): String

    fun getApplicationId(): String

    fun bringAppToForeground()

    fun isSupportRemoteControl(): Boolean

    fun getSerialNumber(): String
}