package com.base.services

import java.io.File

interface IDeviceOwner {
    fun isDeviceOwner(): Boolean

    fun installAPK(file: File) : Boolean

    fun uninstallAPK(packageName: String) : Boolean

    fun isAllowInstall(pkgName: String): Boolean
}