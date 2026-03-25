package com.andforce.mdm.center

import android.util.Log
import com.afwsamples.testdpc.common.PackageInstallationUtils
import com.afwsamples.testdpc.common.Util
import com.afwsamples.testdpc.policy.locktask.viewmodule.KioskViewModule
import com.andforce.mdm.ext.getAppContext
import com.base.services.IDeviceOwner
import org.koin.java.KoinJavaComponent
import java.io.File
import java.io.IOException

class DeviceOwnerImpl: IDeviceOwner {
    companion object {
        private const val TAG = "IDeviceOwner"
    }

    override fun isDeviceOwner(): Boolean {
        return Util.isDeviceOwner(this.getAppContext())
    }

    override fun installAPK(file: File): Boolean {
        try {
            file.inputStream().use {
                PackageInstallationUtils.installPackage(this.getAppContext(), it, null)
            }
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open APK file", e)
            return false
        }
    }

    override fun uninstallAPK(packageName: String): Boolean {
        try {
            PackageInstallationUtils.uninstallPackage(this.getAppContext(), packageName)
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to uninstall package", e)
            return false
        }
    }

    private val kioskViewModel: KioskViewModule by lazy {
        KoinJavaComponent.get(KioskViewModule::class.java)
    }

    override fun isAllowInstall(pkgName: String): Boolean {
        return true
    }

}