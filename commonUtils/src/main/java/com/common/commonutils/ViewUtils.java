package com.common.commonutils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author pingni
 * view类工具类
 * 主要是view的显示和隐藏
 */
public class ViewUtils {
    /**
     * 在非显示状态下
     * 对view进行显示
     *
     * @param view
     */
    public static void showView(View view) {
        if (view != null && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 在显示状态下，(View.INVISIBLE)隐藏view
     *
     * @param view
     */
    public static void hideView(View view) {
        if (view != null && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 在显示状态下，(View.GONE))隐藏view
     *
     * @param view
     */
    public static void goneView(View view) {
        if (view != null && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setViewsVisibility(int visibility, View... view) {
        if (view.length > 0) {
            for (View v : view) {
                v.setVisibility(visibility);
            }
        }
    }

    /**
     * 获取当前activity的布局view
     *
     * @param context
     * @return
     */
    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 扩大View的触摸和点击响应范围,最大不超过其父View范围
     * <p>
     * - 若View的自定义触摸范围超出Parent的大小，则超出的那部分无效。
     * - 一个Parent只能设置一个View的TouchDelegate，设置多个时只有最后设置的生效。
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public static void expandViewTouchDelegate(View view, int top, int bottom, int left, int right) {
        ((View) view.getParent()).post(() -> {
            Rect bounds = new Rect();
            view.setEnabled(true);
            view.getHitRect(bounds);
            bounds.top -= top;
            bounds.bottom += bottom;
            bounds.left -= left;
            bounds.right += right;

            TouchDelegate touchDelegate = new TouchDelegate(bounds, view);
            if (View.class.isInstance(view.getParent())) {
                ((View) view.getParent()).setTouchDelegate(touchDelegate);
            }
        });

    }

    public static void setText(TextView textView, String s) {
        if (textView == null) return;
        if (s == null) s = "";
        textView.setText(s);
    }

    public static void setHtmlText(TextView textView, String s) {
        if (textView == null) return;
        if (s == null) s = "";
        UiUtils.setFromHtml(textView, s);
    }
}
