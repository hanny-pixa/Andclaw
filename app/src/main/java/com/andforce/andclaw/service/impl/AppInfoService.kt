package com.andforce.andclaw.service.impl

import android.content.Context
import com.andforce.andclaw.BuildConfig
import com.andforce.andclaw.R
import com.andforce.mdm.ext.getAppContext
import com.base.services.IAppInfoService
import com.common.commonutils.ActivityUtils
import com.common.commonutils.CommonUtils
import org.koin.java.KoinJavaComponent

class AppInfoService : IAppInfoService {

    private val context: Context = KoinJavaComponent.get(Context::class.java)

    override fun getAppName(): String = context.getString(R.string.app_name)

    override fun isSystemApp() = false

    override fun isDebug() = BuildConfig.DEBUG

    override fun getVersionCode() = BuildConfig.VERSION_CODE
    override fun getVersionName(): String = BuildConfig.VERSION_NAME

    override fun getApplicationId(): String = BuildConfig.APPLICATION_ID

    override fun bringAppToForeground() {
        ActivityUtils.bringAppToForeground(context, false)
    }

    override fun isSupportRemoteControl(): Boolean {
        return false
    }

    override fun getSerialNumber(): String = CommonUtils.getAndroidId(this.getAppContext())
}