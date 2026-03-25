package com.andforce.mdm.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import com.common.commonutils.Escape;

import org.koin.java.KoinJavaComponent;

/**
 * description:
 * author: 
 * date:2019/8/20
 */

public class ResStringUtils {
    private static final Application mApplication = KoinJavaComponent.get(Context.class);
    private ResStringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
    

    /**
     * Return the string value associated with a particular resource ID.
     *
     * @param id The desired resource identifier.
     * @return the string value associated with a particular resource ID.
     */
    public static String getString(@StringRes int id) {
        try {
            return mApplication.getResources().getString(id);
        } catch (Resources.NotFoundException ignore) {
            return "";
        }
    }

    /**
     * Return the string value associated with a particular resource ID.
     *
     * @param id         The desired resource identifier.
     * @param formatArgs The format arguments that will be used for substitution.
     * @return the string value associated with a particular resource ID.
     */
    public static String getString(@StringRes int id, Object... formatArgs) {
        try {
            return mApplication.getString(id, formatArgs);
        } catch (Resources.NotFoundException ignore) {
            return "";
        }
    }

    /**
     * Return the string array associated with a particular resource ID.
     *
     * @param id The desired resource identifier.
     * @return The string array associated with the resource.
     */
    public static String[] getStringArray(@ArrayRes int id) {
        try {
            return mApplication.getResources().getStringArray(id);
        } catch (Resources.NotFoundException ignore) {
            return new String[0];
        }
    }

    public static String getPassString(String pwd) {
        String c = fromCharCode(getUnicode(pwd.charAt(0)) + pwd.length());
        for (int i = 1; i < pwd.length(); i++) {
            c += fromCharCode(getUnicode(pwd.charAt(i)) + getUnicode(pwd.charAt(i - 1)));
        }
        return Escape.escape(c).replaceAll("u00", "");
    }


    /**
     * 字符串转换unicode
     */
    //拿到unicode编码
    public static int getUnicode(char c) {
        int returnUniCode = 0;
        returnUniCode = (int) c;
        return returnUniCode;
    }

    /**
     * unicode 转字符串
     */
    public static String fromCharCode(int i) {
        String strValue = String.valueOf((char) i);
        return strValue;
    }
}
