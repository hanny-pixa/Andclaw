package com.andforce.mdm.utils;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.common.commonutils.DisplayUtils;
import com.common.commonutils.ViewUtils;

import org.koin.java.KoinJavaComponent;

public class MyDoubleBarViewUtlis {
    private static final Application mApplication = KoinJavaComponent.get(Context.class);
    public static ViewGroup.LayoutParams onDraw(View textView, Float percents) {
        if (percents < 5 && percents > 0) {
            percents = 5f;
        }
        if (percents > 100) {
            percents = 100f;
        }
        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
        int p = percents.intValue();
//        Logger.t(ConstantUtils.TAG).i("hhhhhhhhhhh:" + p + "");
        if (p > 0) {
            ViewUtils.showView(textView);
            layoutParams.height = DisplayUtils.dip2px(mApplication, percents / 100 * 374);
            layoutParams.width = 40;
        } else {
            ViewUtils.hideView(textView);
        }
        return layoutParams;
    }
}