package com.smile.commonlib.util;

import android.text.TextUtils;

/**
 * 字符串工具类
 */
public class StringUtils {

    /**
     * 字符串是否为空
     *
     * @param data
     * @return
     */
    public static boolean judgeStringIsNull(String data) {
        if (TextUtils.isEmpty(data) || "".equals(data.trim()) || "null".equals(data.trim())) {
            return true;
        }
        return false;
    }
}
