package com.rz.smart.utils;

/**
 * 作者：iss on 2020/10/18 18:15
 * 邮箱：55921173@qq.com
 * 类备注：
 */
public class RxStringUtils {
    public static String replaceAll(String str) {
        return str.trim().replaceAll(" ", "");
    }
    public static String substring(String str) {
        return str.substring(1, str.length());
    }
}
