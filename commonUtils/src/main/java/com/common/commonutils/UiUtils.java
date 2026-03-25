package com.common.commonutils;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

/**
 * TODO
 *
 * @author dev.liang <a href="mailto:dev.liang@outlook.com">Contact me.</a>
 * @version 1.0.0
 * @since 2022/05/11 15:52
 */
public class UiUtils {


    public static void setTextSpannable(TextView tv) {
        SpannableString spannableString = new SpannableString(tv.getText());
        spannableString.setSpan(new AbsoluteSizeSpan(40, true), tv.getText().length() - 1, tv.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
    }

    /**
     * 设置文本颜色大小（一个字段大字小字的样式）
     *
     * @param textView
     */
    public static void setTextSpannable(Context mContext, int color, TextView... textView) {
        for (TextView tv : textView) {
            SpannableString spannableString = new SpannableString(tv.getText());
            spannableString.setSpan(new RelativeSizeSpan(1.7f), 0, tv.getText().length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(14, true), tv.getText().length() - 1, tv.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(color)), tv.getText().length() - 1, tv.getText().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tv.setText(spannableString);
        }
    }

    public static void setFromHtml(TextView textView, String str) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(str));
        }
    }

    public static void setNotEmptyText(TextView textView, String str) {
        if (textView != null && !StringUtils.isEmpty(str)) {
            textView.setText(str);
        }
    }
}
