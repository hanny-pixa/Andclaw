package com.afwsamples.testdpc

import android.app.Application
import com.andforce.mdm.center.AppViewModule
import com.andforce.mdm.center.DeviceStatusViewModel
import com.andforce.mdm.center.TimeTickViewModel
import com.andforce.mdm.center.DeviceOwnerImpl
import com.afwsamples.testdpc.policy.locktask.viewmodule.KioskViewModule
import com.base.services.IDeviceOwner
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

open class KoinApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApplication)
            modules(mdmKoinModule())
        }
    }

    open fun mdmKoinModule() = module {
        viewModel { KioskViewModule() }
        single { AppViewModule() }
        single { DeviceStatusViewModel() }
        single { TimeTickViewModel() }
        single { DeviceOwnerImpl() } bind IDeviceOwner::class
    }
}