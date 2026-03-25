package com.andforce.mdm.center

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.content.pm.ResolveInfo
import android.util.Log
import com.afwsamples.testdpc.common.Util
import com.afwsamples.testdpc.profilepolicy.permission.AppPermissionsArrayAdapter
import com.andforce.andclaw.DeviceAdminReceiver
import org.koin.java.KoinJavaComponent
import java.util.Collections

object AppUtils {

    private const val TAG = "AppUtils"

    fun getAllLauncherIntentResolversSorted(context: Context): List<ResolveInfo> {
        val packageManager = context.packageManager
        val launcherIntent = Util.getLauncherIntent(context)
        val launcherIntentResolvers: List<ResolveInfo> =
            packageManager.queryIntentActivities(launcherIntent, 0)
        Collections.sort<ResolveInfo>(
            launcherIntentResolvers, ResolveInfo.DisplayNameComparator(packageManager)
        )
        return launcherIntentResolvers
    }

    fun getAllInstalledApplicationsSorted(context: Context): List<ApplicationInfo> {
        val packageManager = context.packageManager
        val allApps =
            packageManager?.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
        if (allApps != null) {
            packageManager.let {
                Collections.sort(allApps, ApplicationInfo.DisplayNameComparator(packageManager))
            }
            return allApps
        }
        return emptyList()
    }


    fun hideApps(context: Context, pkgs: List<String>) {
        val devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        Log.i(TAG, "hideApps: ${pkgs.size}")
        for (pkg in pkgs) {
            Log.i(TAG, "hideApps: pkg: $pkg")
            devicePolicyManager.setApplicationHidden(DeviceAdminReceiver.getComponentName(context), pkg, true)
        }
    }

    fun showApps(context: Context, pkgs: List<String>) {
        val devicePolicyManager =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        Log.i(TAG, "showApps: ${pkgs.size}")
        for (pkg in pkgs) {
            devicePolicyManager.setApplicationHidden(DeviceAdminReceiver.getComponentName(context), pkg, false)
        }
    }

    fun showAllHideApps(context: Context) {
        val appViewModel: AppViewModule = KoinJavaComponent.get(AppViewModule::class.java)
        appViewModel.isDeviceOwnerRemoving = true
        showApps(context, getAllInstalledApplicationsSorted(context).map { it.packageName })
        appViewModel.isDeviceOwnerRemoving = false
    }

    fun enablePermission(context: Context, pkgName: String) {
        if (!Util.isDeviceOwner(context)) {
            return
        }

        val permissions: MutableList<String> = ArrayList()

        val packageManager = context.packageManager
        val info: PackageInfo?
        try {
            info = packageManager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Could not retrieve info about the package: $pkgName", e
            )
            return
        }

        info?.requestedPermissions?.let {
            for (requestedPerm in it) {
                try {
                    val pInfo: PermissionInfo =
                        packageManager.getPermissionInfo(requestedPerm, 0)
                    if (
                        (pInfo.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS
                    ) {
                        permissions.add(pInfo.name)
                    }
                } catch (e: Exception) {
                    Log.i(
                        TAG,
                        "Could not retrieve info about the permission: $requestedPerm"
                    )
                }
            }
        }

        val mDpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val mAdminComponent = DeviceAdminReceiver.getComponentName(context)
        val populatedPermissions: MutableList<AppPermissionsArrayAdapter.AppPermission> = java.util.ArrayList()
        for (permission in permissions) {
            val permissionState: Int =
                mDpm.getPermissionGrantState(mAdminComponent, pkgName, permission)
            val populatedPerm =
                AppPermissionsArrayAdapter.AppPermission(pkgName, permission, permissionState)
            populatedPermissions.add(populatedPerm)
        }

        populatedPermissions.filter { it.permissionState != DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED }
            .forEach {
                mDpm.setPermissionGrantState(
                    mAdminComponent,
                    pkgName,
                    it.permissionName,
                    DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED
                )
            }
    }
}