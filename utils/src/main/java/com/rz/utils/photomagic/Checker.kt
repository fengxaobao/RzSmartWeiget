package com.rz.utils.module.photomagic

import android.text.TextUtils
import java.io.File
import java.util.*

internal object Checker {
    private val format = ArrayList<String>()
    private val JPG = "jpg"
    private val JPEG = "jpeg"
    private val PNG = "png"
    private val WEBP = "webp"
    private val GIF = "gif"

    init {
        format.add(JPG)
        format.add(JPEG)
        format.add(PNG)
        format.add(WEBP)
        format.add(GIF)
    }

    fun isImage(path: String): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }

        val suffix = path.substring(path.lastIndexOf(".") + 1, path.length)
        return format.contains(suffix.toLowerCase())
    }

    fun isJPG(path: String): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }

        val suffix = path.substring(path.lastIndexOf("."), path.length).toLowerCase()
        return suffix.contains(JPG) || suffix.contains(JPEG)
    }

    fun checkSuffix(path: String): String {
        return if (TextUtils.isEmpty(path)) {
            ".jpg"
        } else path.substring(path.lastIndexOf("."), path.length)

    }

    fun isNeedCompress(leastCompressSize: Int, path: String): Boolean {
        if (leastCompressSize > 0) {
            val source = File(path)
            return if (!source.exists()) {
                false
            } else source.length() > leastCompressSize shl 10

        }
        return true
    }
}
