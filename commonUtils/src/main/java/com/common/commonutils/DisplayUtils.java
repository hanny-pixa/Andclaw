package com.common.commonutils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;


/**
 * @author pingni
 */
public class DisplayUtils {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 返回手机当前屏幕宽带
     *
     * @return < 0 - 失败
     */
    public static int getScreenWidth(Context context) {
        return getDispInfo(context)[0];
    }



    /**
     * 返回手机当前屏幕高度
     *
     * @return < 0 - 失败
     */
    public static int getScreenHeight(Context context) {
        return getDispInfo(context)[1];
    }

    private static int[] getDispInfo(Context context) {
        if (context == null) {
            return new int[]{-1, -1};
        }
        Display dm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (dm == null) {
            return new int[]{-1, -1};
        }
        if (Build.VERSION.SDK_INT < 14) {
            return new int[]{dm.getWidth(), dm.getHeight(), dm.getRotation()};
        } else {
            try {
                Point size = new Point();
                Method method = dm.getClass().getMethod("getRealSize", Point.class);
                method.invoke(dm, size);
                return new int[]{size.x, size.y, dm.getRotation()};
            } catch (Exception e) {
                return new int[]{-1, -1};
            }
        }
    }

    public static int getDpi(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        int density = dm.densityDpi;
        return density;
    }
}
