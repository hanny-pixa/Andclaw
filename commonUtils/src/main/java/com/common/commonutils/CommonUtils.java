package com.common.commonutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

public class CommonUtils {
    /**
     * 获取androidId
     */
    public static String getAndroidId(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取厂商
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取App包 信息版本号
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 获取App包名
     *
     * @return PackageName
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取版本号
     *
     * @return versionCode
     */
    public static int getAppVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * 获取版本名称
     *
     * @return versionName
     */
    public static String getAppVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 获取安卓版本
     *
     * @return 安卓版本
     */
    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }
}
