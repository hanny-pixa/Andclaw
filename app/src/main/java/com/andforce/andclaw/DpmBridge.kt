package com.andforce.andclaw

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Build
import android.util.Log
import com.afwsamples.testdpc.DevicePolicyManagerGateway
import com.afwsamples.testdpc.DevicePolicyManagerGatewayImpl
import com.afwsamples.testdpc.common.PackageInstallationUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

data class DpmResult(
    val success: Boolean,
    val message: String
)

enum class DpmSafetyLevel { SAFE, CONFIRM, DANGEROUS }

class DpmBridge(private val context: Context) {

    companion object {
        private const val TAG = "DpmBridge"
    }

    private val gateway: DevicePolicyManagerGateway = DevicePolicyManagerGatewayImpl(context)

    fun getSafetyLevel(action: String): DpmSafetyLevel = when (action) {
        "lockNow", "setCameraDisabled", "setStatusBarDisabled",
        "setKeyguardDisabled", "setLocationEnabled",
        "setGlobalSetting", "setSecureSetting",
        "setOrganizationName", "setDeviceOwnerLockScreenInfo",
        "setSecurityLoggingEnabled", "setNetworkLoggingEnabled",
        "getUserRestrictions", "requestBugreport",
        "getInstalledPackages", "getPackageInfo", "getAppDpmStatus",
        "getLockTaskPackages", "getUserControlDisabledPackages"
        -> DpmSafetyLevel.SAFE

        "installPackage", "uninstallPackage"
        -> DpmSafetyLevel.CONFIRM

        "wipeData", "reboot", "removeUser"
        -> DpmSafetyLevel.DANGEROUS

        else -> DpmSafetyLevel.CONFIRM
    }

    fun execute(dpmAction: String, params: Map<String, Any>?): DpmResult {
        Log.d(TAG, "execute: $dpmAction, params=$params")
        return try {
            when (dpmAction) {
                "lockNow" -> callVoid { s, e -> gateway.lockNow(s, e) }
                "reboot" -> callVoid { s, e -> gateway.reboot(s, e) }
                "wipeData" -> {
                    val flags = params.intParam("flags", 0)
                    callVoid { s, e -> gateway.wipeData(flags, s, e) }
                }
                "requestBugreport" -> callVoid { s, e -> gateway.requestBugreport(s, e) }

                "setCameraDisabled" -> {
                    val disabled = params.boolParam("disabled", true)
                    callVoid { s, e -> gateway.setCameraDisabled(disabled, s, e) }
                }
                "setStatusBarDisabled" -> {
                    val disabled = params.boolParam("disabled", true)
                    callVoid { s, e -> gateway.setStatusBarDisabled(disabled, s, e) }
                }
                "setKeyguardDisabled" -> {
                    val disabled = params.boolParam("disabled", true)
                    callVoid { s, e -> gateway.setKeyguardDisabled(disabled, s, e) }
                }
                "setLocationEnabled" -> {
                    val enabled = params.boolParam("enabled", true)
                    callVoid { s, e -> gateway.setLocationEnabled(enabled, s, e) }
                }
                "setUsbDataSignalingEnabled" -> {
                    val enabled = params.boolParam("enabled", true)
                    callVoid { s, e -> gateway.setUsbDataSignalingEnabled(enabled, s, e) }
                }

                "setApplicationHidden" -> {
                    val pkg = params.stringParam("package_name") ?: return paramMissing("package_name")
                    val hidden = params.boolParam("hidden", true)
                    callVoid { s, e -> gateway.setApplicationHidden(pkg, hidden, s, e) }
                }
                "setPackagesSuspended" -> {
                    val pkgs = params.stringListParam("packages") ?: return paramMissing("packages")
                    val suspended = params.boolParam("suspended", true)
                    try {
                        gateway.setPackagesSuspended(pkgs.toTypedArray(), suspended, { _ -> }, { e -> throw e })
                        DpmResult(true, "OK")
                    } catch (e: Exception) {
                        DpmResult(false, e.message ?: "Unknown error")
                    }
                }
                "setUninstallBlocked" -> {
                    val pkg = params.stringParam("package_name") ?: return paramMissing("package_name")
                    val blocked = params.boolParam("blocked", true)
                    callVoid { s, e -> gateway.setUninstallBlocked(pkg, blocked, s, e) }
                }
                "enableSystemApp" -> {
                    val pkg = params.stringParam("package_name") ?: return paramMissing("package_name")
                    callVoid { s, e -> gateway.enableSystemApp(pkg, s, e) }
                }
                "setUserControlDisabledPackages" -> {
                    val pkgs = params.stringListParam("packages") ?: return paramMissing("packages")
                    callVoid { s, e -> gateway.setUserControlDisabledPackages(pkgs, s, e) }
                }
                "setPermissionGrantState" -> {
                    val pkg = params.stringParam("package_name") ?: return paramMissing("package_name")
                    val permission = params.stringParam("permission") ?: return paramMissing("permission")
                    val grantState = params.intParam("grant_state", 1) // GRANTED=1
                    callVoid { s, e -> gateway.setPermissionGrantState(pkg, permission, grantState, s, e) }
                }

                "setUserRestriction" -> {
                    val restriction = params.stringParam("restriction") ?: return paramMissing("restriction")
                    val enabled = params.boolParam("enabled", true)
                    callVoid { s, e -> gateway.setUserRestriction(restriction, enabled, s, e) }
                }
                "getUserRestrictions" -> {
                    val restrictions = gateway.userRestrictions
                    DpmResult(true, "Active restrictions: ${restrictions.joinToString()}")
                }

                "setLockTaskPackages" -> {
                    val pkgs = params.stringListParam("packages") ?: return paramMissing("packages")
                    callVoid { s, e -> gateway.setLockTaskPackages(pkgs.toTypedArray(), s, e) }
                }
                "setLockTaskFeatures" -> {
                    val flags = params.intParam("flags", 0)
                    callVoid { s, e -> gateway.setLockTaskFeatures(flags, s, e) }
                }

                "setPasswordQuality" -> {
                    val quality = params.intParam("quality", 0)
                    callVoid { s, e -> gateway.setPasswordQuality(quality, s, e) }
                }
                "setRequiredPasswordComplexity" -> {
                    val complexity = params.intParam("complexity", 0)
                    callVoid { s, e -> gateway.setRequiredPasswordComplexity(complexity, s, e) }
                }
                "setMaximumFailedPasswordsForWipe" -> {
                    val max = params.intParam("max", 0)
                    callVoid { s, e -> gateway.setMaximumFailedPasswordsForWipe(max, s, e) }
                }

                "setGlobalSetting" -> {
                    val setting = params.stringParam("setting") ?: return paramMissing("setting")
                    val value = params.stringParam("value") ?: return paramMissing("value")
                    callVoid { s, e -> gateway.setGlobalSetting(setting, value, s, e) }
                }
                "setSecureSetting" -> {
                    val setting = params.stringParam("setting") ?: return paramMissing("setting")
                    val value = params.stringParam("value") ?: return paramMissing("value")
                    callVoid { s, e -> gateway.setSecureSetting(setting, value, s, e) }
                }
                "setOrganizationName" -> {
                    val name = params.stringParam("name") ?: return paramMissing("name")
                    callVoid { s, e -> gateway.setOrganizationName(name, s, e) }
                }
                "setDeviceOwnerLockScreenInfo" -> {
                    val info = params.stringParam("info") ?: return paramMissing("info")
                    callVoid { s, e -> gateway.setDeviceOwnerLockScreenInfo(info, s, e) }
                }

                "setSecurityLoggingEnabled" -> {
                    val enabled = params.boolParam("enabled", true)
                    callVoid { s, e -> gateway.setSecurityLoggingEnabled(enabled, s, e) }
                }
                "setNetworkLoggingEnabled" -> {
                    val enabled = params.boolParam("enabled", true)
                    callVoid { s, e -> gateway.setNetworkLoggingEnabled(enabled, s, e) }
                }

                "setMeteredDataDisabledPackages" -> {
                    val pkgs = params.stringListParam("packages") ?: return paramMissing("packages")
                    DpmResult(true, "setMeteredDataDisabledPackages").also {
                        gateway.setMeteredDataDisabledPackages(pkgs, { _ -> }, { _ -> })
                    }
                }

                "installPackage" -> {
                    val filePath = params.stringParam("file_path") ?: return paramMissing("file_path")
                    val file = File(filePath)
                    if (!file.exists()) return DpmResult(false, "File not found: $filePath")
                    try {
                        file.inputStream().use { inputStream ->
                            PackageInstallationUtils.installPackage(context, inputStream, null)
                        }
                        DpmResult(true, "Install session committed: ${file.name}")
                    } catch (e: Exception) {
                        DpmResult(false, "Install failed: ${e.message}")
                    }
                }

                "uninstallPackage" -> {
                    val pkg = params.stringParam("package_name") ?: return paramMissing("package_name")
                    try {
                        PackageInstallationUtils.uninstallPackage(context, pkg)
                        DpmResult(true, "Uninstall requested: $pkg")
                    } catch (e: Exception) {
                        DpmResult(false, "Uninstall failed: ${e.message}")
                    }
                }

                "logoutUser" -> {
                    try {
                        gateway.logoutUser({ _ -> }, { e -> throw e })
                        DpmResult(true, "OK")
                    } catch (e: Exception) {
                        DpmResult(false, e.message ?: "Unknown error")
                    }
                }

                "getInstalledPackages" -> queryInstalledPackages(params)
                "getPackageInfo" -> {
                    val pkg = params.stringParam("package_name") ?: return paramMissing("package_name")
                    queryPackageInfo(pkg)
                }
                "getAppDpmStatus" -> {
                    val pkg = params.stringParam("package_name") ?: return paramMissing("package_name")
                    queryAppDpmStatus(pkg)
                }
                "getLockTaskPackages" -> {
                    val pkgs = gateway.lockTaskPackages
                    DpmResult(true, pkgs.joinToString(", ").ifEmpty { "(none)" })
                }
                "getUserControlDisabledPackages" -> {
                    val pkgs = gateway.userControlDisabledPackages
                    DpmResult(true, pkgs.joinToString(", ").ifEmpty { "(none)" })
                }

                else -> DpmResult(false, "Unknown DPM action: $dpmAction")
            }
        } catch (e: Exception) {
            Log.e(TAG, "DPM execute error: $dpmAction", e)
            DpmResult(false, "DPM error: ${e.message}")
        }
    }

    private fun callVoid(
        block: (Consumer<Void>, Consumer<Exception>) -> Unit
    ): DpmResult {
        val latch = CountDownLatch(1)
        var result = DpmResult(false, "Timeout")
        block(
            { _ ->
                result = DpmResult(true, "OK")
                latch.countDown()
            },
            { e ->
                result = DpmResult(false, e.message ?: "Unknown error")
                latch.countDown()
            }
        )
        latch.await(10, TimeUnit.SECONDS)
        return result
    }

    private fun queryInstalledPackages(params: Map<String, Any>?): DpmResult {
        val filter = params.stringParam("filter") ?: "all"
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val filtered = when (filter) {
            "user" -> apps.filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            "system" -> apps.filter { it.flags and ApplicationInfo.FLAG_SYSTEM != 0 }
            else -> apps
        }
        val lines = filtered
            .sortedBy { pm.getApplicationLabel(it).toString().lowercase() }
            .joinToString("\n") { info ->
                val label = pm.getApplicationLabel(info).toString()
                val type = if (info.flags and ApplicationInfo.FLAG_SYSTEM != 0) "system" else "user"
                "${info.packageName} | $label | $type"
            }
        return DpmResult(true, "Installed apps (${filtered.size}):\n$lines")
    }

    private fun queryPackageInfo(packageName: String): DpmResult {
        val pm = context.packageManager
        return try {
            val pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val appInfo = pkgInfo.applicationInfo
            val label = appInfo?.let { pm.getApplicationLabel(it).toString() } ?: packageName
            val isSystem = appInfo != null && appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                pkgInfo.longVersionCode else @Suppress("DEPRECATION") pkgInfo.versionCode.toLong()

            val sb = StringBuilder()
            sb.appendLine("App: $label")
            sb.appendLine("Package: $packageName")
            sb.appendLine("Version: ${pkgInfo.versionName ?: "?"} ($versionCode)")
            sb.appendLine("Type: ${if (isSystem) "system" else "user"}")
            sb.appendLine("Target SDK: ${appInfo?.targetSdkVersion ?: "?"}")
            sb.appendLine("Installed: ${dateFormat.format(Date(pkgInfo.firstInstallTime))}")
            sb.appendLine("Updated: ${dateFormat.format(Date(pkgInfo.lastUpdateTime))}")
            sb.appendLine("Enabled: ${appInfo?.enabled ?: "?"}")

            val perms = pkgInfo.requestedPermissions
            if (!perms.isNullOrEmpty()) {
                sb.appendLine("Permissions (${perms.size}): ${perms.joinToString(", ")}")
            }
            DpmResult(true, sb.toString().trim())
        } catch (e: PackageManager.NameNotFoundException) {
            DpmResult(false, "Package not found: $packageName")
        }
    }

    private fun queryAppDpmStatus(packageName: String): DpmResult {
        return try {
            val sb = StringBuilder()
            sb.appendLine("Package: $packageName")

            try {
                sb.appendLine("Hidden: ${gateway.isApplicationHidden(packageName)}")
            } catch (_: Exception) {
                sb.appendLine("Hidden: unknown")
            }
            try {
                sb.appendLine("Suspended: ${gateway.isPackageSuspended(packageName)}")
            } catch (_: Exception) {
                sb.appendLine("Suspended: unknown")
            }
            sb.appendLine("Uninstall Blocked: ${gateway.isUninstallBlocked(packageName)}")

            val pm = context.packageManager
            try {
                val pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                val dangerousPerms = pkgInfo.requestedPermissions?.filter { perm ->
                    try {
                        val pi = pm.getPermissionInfo(perm, 0)
                        (pi.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) ==
                            PermissionInfo.PROTECTION_DANGEROUS
                    } catch (_: Exception) { false }
                } ?: emptyList()

                if (dangerousPerms.isNotEmpty()) {
                    sb.appendLine("Dangerous Permissions:")
                    dangerousPerms.forEach { perm ->
                        val state = gateway.getPermissionGrantState(packageName, perm)
                        val stateStr = when (state) {
                            0 -> "default"
                            1 -> "granted"
                            2 -> "denied"
                            else -> "unknown($state)"
                        }
                        val shortName = perm.substringAfterLast('.')
                        sb.appendLine("  $shortName: $stateStr")
                    }
                }
            } catch (_: PackageManager.NameNotFoundException) { }

            DpmResult(true, sb.toString().trim())
        } catch (e: Exception) {
            DpmResult(false, "Query failed: ${e.message}")
        }
    }

    private fun paramMissing(name: String) = DpmResult(false, "Missing required param: $name")
}

private fun Map<String, Any>?.stringParam(key: String): String? =
    this?.get(key)?.toString()

private fun Map<String, Any>?.boolParam(key: String, default: Boolean): Boolean =
    when (val v = this?.get(key)) {
        is Boolean -> v
        is String -> v.equals("true", ignoreCase = true)
        else -> default
    }

private fun Map<String, Any>?.intParam(key: String, default: Int): Int =
    when (val v = this?.get(key)) {
        is Number -> v.toInt()
        is String -> v.toIntOrNull() ?: default
        else -> default
    }

@Suppress("UNCHECKED_CAST")
private fun Map<String, Any>?.stringListParam(key: String): List<String>? {
    val v = this?.get(key) ?: return null
    return when (v) {
        is List<*> -> v.map { it.toString() }
        is String -> v.split(",").map { it.trim() }
        else -> null
    }
}
