/**
 * 作者：iss on 2020/6/30 09:55
 * 邮箱：55921173@qq.com
 * 类备注：
 */
package com.rz.utils

import java.util.*

class RxSignUtils {
    companion object {
        fun getSign(map: TreeMap<String, Any>, key: String): String {
            val sign = StringBuilder()
            for ((key, value) in map) {
                if ((value is String)) {
                    val values = value as String
                    if (values.isNotEmpty()) {
                        sign.append(key).append("=").append(values).append("&")
                    }
                } else {
                    sign.append(key).append("=").append(value).append("&")
                }
            }
            sign.append("Key=${key}")
            println("MD5之前的 ${sign}")
            return RxTool.md5(sign.toString()).toUpperCase()
        }
    }
}