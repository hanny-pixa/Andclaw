package com.andforce.mdm.ext

import android.app.Application
import android.content.Context
import com.base.services.IAppInfoService
import org.koin.java.KoinJavaComponent

private var appContext: Application = KoinJavaComponent.get(Context::class.java)
private var appInfoService: IAppInfoService = KoinJavaComponent.get(IAppInfoService::class.java)

fun Any.getAppContext(): Application {
    return appContext
}

fun Any.getAppInfoService(): IAppInfoService {
    return appInfoService
}