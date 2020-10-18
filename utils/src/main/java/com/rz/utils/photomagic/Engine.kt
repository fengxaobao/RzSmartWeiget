package com.rz.utils.module.photomagic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Responsible for starting compress and managing active and cached resources.
 */
internal class Engine @Throws(IOException::class)
constructor(private val srcImg: String, private val tagImg: File) {
    private var srcExif: ExifInterface? = null
    private var srcWidth: Int = 0
    private var srcHeight: Int = 0

    init {
        if (Checker.isJPG(srcImg)) {
            this.srcExif = ExifInterface(srcImg)
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 1

        BitmapFactory.decodeFile(srcImg, options)
        this.srcWidth = options.outWidth
        this.srcHeight = options.outHeight
    }

    private fun computeSize(): Int {
        srcWidth = if (srcWidth % 2 == 1) srcWidth + 1 else srcWidth
        srcHeight = if (srcHeight % 2 == 1) srcHeight + 1 else srcHeight

        val longSide = Math.max(srcWidth, srcHeight)
        val shortSide = Math.min(srcWidth, srcHeight)

        val scale = shortSide.toFloat() / longSide
        return if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                2
            } else if (longSide >= 1664 && longSide < 4990) {
                4
            } else if (longSide > 4990 && longSide < 10240) {
                8
            } else {
                if (longSide / 1280 == 0) 1 + 1 else longSide / 1280 + 1
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (longSide / 1280 == 0) 1 + 1 else longSide / 1280 + 1
        } else {
            Math.ceil(longSide / (1280.0 / scale)).toInt() + 1
        }
    }

    private fun rotatingImage(bitmap: Bitmap): Bitmap {
        if (srcExif == null) {
            return bitmap
        }

        val matrix = Matrix()
        var angle = 0
        val orientation = srcExif!!.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> angle = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> angle = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> angle = 270
            else -> {
            }
        }

        matrix.postRotate(angle.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @Throws(IOException::class)
    fun compress(): File {
        val options = BitmapFactory.Options()
        options.inSampleSize = computeSize()

        var tagBitmap = BitmapFactory.decodeFile(srcImg, options)
        val stream = ByteArrayOutputStream()

        tagBitmap = rotatingImage(tagBitmap)
        tagBitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream)
        tagBitmap.recycle()

        val fos = FileOutputStream(tagImg)
        fos.write(stream.toByteArray())
        fos.flush()
        fos.close()
        stream.close()

        return tagImg
    }
}