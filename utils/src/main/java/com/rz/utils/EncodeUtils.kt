package com.rz.utils

import android.os.Build
import android.text.Html
import android.util.Base64

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Created by wangjj on 2018/1/4.
 */

class EncodeUtils private constructor() {

    @JvmOverloads
    fun urlEncode(input: String, charset: String = "UTF-8"): String {
        try {
            return URLEncoder.encode(input, charset)
        } catch (e: UnsupportedEncodingException) {
            return input
        }

    }

    @JvmOverloads
    fun urlDecode(input: String, charset: String = "UTF-8"): String {
        try {
            return URLDecoder.decode(input, charset)
        } catch (e: UnsupportedEncodingException) {
            return input
        }

    }

    /**
     * Base64 编码
     * String -> byte[]
     */
    fun base64Encode(input: String): ByteArray {
        return Base64.encode(input.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Base64 编码
     * byte[] -> String
     */
    fun base64Encode(input: ByteArray): String {
        return Base64.encodeToString(input, Base64.NO_WRAP)
    }

    /**
     * Base64 安全编码
     */
    fun base64UrlSafeEncode(input: String): ByteArray {
        return Base64.encode(input.toByteArray(), Base64.URL_SAFE)
    }

    /**
     * Base64 解码
     */
    fun base64Decode(input: String): ByteArray {
        return Base64.decode(input, Base64.NO_WRAP)
    }


    /**
     * Html 编码
     */
    fun htmlEncode(input: CharSequence): String {
        val sb = StringBuilder()
        var c: Char
        var i = 0
        val len = input.length
        while (i < len) {
            c = input[i]
            when (c) {
                '<' -> sb.append("&lt;") //$NON-NLS-1$
                '>' -> sb.append("&gt;") //$NON-NLS-1$
                '&' -> sb.append("&amp;") //$NON-NLS-1$
                '\'' ->
                    //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was
                    // introduced in XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead of &apos; to work as expected in HTML 4
                    // user agents.
                    sb.append("&#39;") //$NON-NLS-1$
                '"' -> sb.append("&quot;") //$NON-NLS-1$
                else -> sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    /**
     * Html 解码
     */
    fun htmlDecode(input: String): CharSequence {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(input)
        }
    }

    companion object {

        private var instance: EncodeUtils? = null

        fun getInstance(): EncodeUtils {

            if (instance == null) {
                instance = EncodeUtils()
            }
            return instance as EncodeUtils
        }
    }


}
/**
 * URL 编码
 */
/**
 * URL 解码
 */
