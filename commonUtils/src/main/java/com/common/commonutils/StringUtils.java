package com.common.commonutils;

import static android.graphics.Typeface.BOLD;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.text.DecimalFormat;

/**
 * description:
 * author: 
 * date:2019/8/20
 */

public class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getText(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    /**
     * Return whether the string is null or 0-length.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0 || s.equals("null");
    }

    /**
     * Return whether the string is null or whitespace.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isTrimEmpty(final String s) {
        return (s == null || s.trim().length() == 0);
    }

    /**
     * Return whether the string is null or white space.
     *
     * @param s The string.
     * @return {@code true}: yes<br> {@code false}: no
     */
    public static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return whether string1 is equals to string2.
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2) {
            return true;
        }
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether string1 is equals to string2, ignoring case considerations..
     *
     * @param s1 The first string.
     * @param s2 The second string.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean equalsIgnoreCase(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * Return {@code ""} if string equals null.
     *
     * @param s The string.
     * @return {@code ""} if string equals null
     */
    public static String null2Length0(final String s) {
        return s == null ? "" : s;
    }

    /**
     * Return the length of string.
     *
     * @param s The string.
     * @return the length of string
     */
    public static int length(final CharSequence s) {
        return s == null ? 0 : s.length();
    }

    /**
     * Set the first letter of string upper.
     *
     * @param s The string.
     * @return the string with first letter upper.
     */
    public static String upperFirstLetter(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * Set the first letter of string lower.
     *
     * @param s The string.
     * @return the string with first letter lower.
     */
    public static String lowerFirstLetter(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * Reverse the string.
     *
     * @param s The string.
     * @return the reverse string.
     */
    public static String reverse(final String s) {
        if (s == null) {
            return "";
        }
        int len = s.length();
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * Convert string to DBC.
     *
     * @param s The string.
     * @return the DBC string
     */
    public static String toDBC(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * Convert string to SBC.
     *
     * @param s The string.
     * @return the SBC string
     */
    public static String toSBC(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    //获取带空格手机号
    public static String getSpacePhone(String number) {
        try {
            number = number.substring(0, 3) + "  " + number.substring(3, 7) + "  " + number.substring(7, 11);
            return number;
        } catch (Exception e) {
            return "";
        }
    }


    //个位数前添加0
    public static String get0Format(int number) {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(number);
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

    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static SpannableString changeColorAndSize(String notChangeS, String wholeIntroduce, int size, String color) {
        if (notChangeS == null) notChangeS = "";
        int start = notChangeS.length();
        int end = wholeIntroduce.length();
        SpannableString spannable = new SpannableString(wholeIntroduce);
        spannable.setSpan(new AbsoluteSizeSpan(size, true), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
        spannable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static SpannableString setColorAndSize(String changeS, String wholeIntroduce, int size, String color) {
        if (changeS == null) changeS = "";
        int start = wholeIntroduce.indexOf(changeS);
        int end = start + changeS.length();
        SpannableString spannable = new SpannableString(wholeIntroduce);
        spannable.setSpan(new AbsoluteSizeSpan(size, true), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
        spannable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static SpannableString setColor(String changeS, String wholeIntroduce, String color) {
        if (changeS == null) changeS = "";
        int start = wholeIntroduce.indexOf(changeS);
        int end = start + changeS.length();
        SpannableString spannable = new SpannableString(wholeIntroduce);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(color));
        spannable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}
