package com.rz.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.widget.ImageView
import java.io.*


/**
 * @author vondear
 * @date 2016/1/24
 * 图像工具类
 */

class RxImageTool {

    /**
     * 获取bitmap
     *
     * @param is 输入流
     * @return bitmap
     */
    fun getBitmap(`is`: InputStream?): Bitmap? {
        return if (`is` == null) {
            null
        } else BitmapFactory.decodeStream(`is`)
    }

    /**
     * 获取bitmap
     *
     * @param data   数据
     * @param offset 偏移量
     * @return bitmap
     */
    fun getBitmap(data: ByteArray, offset: Int): Bitmap? {
        return if (data.size == 0) {
            null
        } else BitmapFactory.decodeByteArray(data, offset, data.size)
    }

    companion object {

        /**
         * dip转px
         *
         * @param dpValue dp值
         * @return px值
         */
        fun dip2px(dpValue: Int): Int {
            return dp2px(dpValue)
        }

        /**
         * dp转px
         *
         * @param dpValue dp值
         * @return px值
         */
        fun dp2px(dpValue: Int): Int {
            val scale = RxTool.getContext().resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * px转dip
         *
         * @param pxValue px值
         * @return dip值
         */
        fun px2dip(pxValue: Float): Int {
            return px2dp(pxValue)
        }

        /**
         * px转dp
         *
         * @param pxValue px值
         * @return dp值
         */
        fun px2dp(pxValue: Float): Int {
            val scale = RxTool.getContext().resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * sp转px
         *
         * @param spValue sp值
         * @return px值
         */
        fun sp2px(spValue: Float): Int {
            val fontScale = RxTool.getContext().resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * px转sp
         *
         * @param pxValue px值
         * @return sp值
         */
        fun px2sp(pxValue: Float): Int {
            val fontScale = RxTool.getContext().resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
         *
         *
         * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
         *
         *
         * B.本地路径:url="file://mnt/sdcard/photo/image.png";
         *
         *
         * C.支持的图片格式 ,png, jpg,bmp,gif等等
         *
         * @param url
         * @return
         */
//        fun GetLocalOrNetBitmap(url: String): Bitmap? {
//            var bitmap: Bitmap? = null
//            var `in`: InputStream? = null
//            var out: BufferedOutputStream? = null
//            try {
//                `in` = BufferedInputStream(URL(url).openStream(), 1024)
//                val dataStream = ByteArrayOutputStream()
//
//                out = BufferedOutputStream(dataStream, 1024)
//
//                out.flush()
//                var data: ByteArray? = dataStream.toByteArray()
//                bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
//                data = null
//                return bitmap
//            } catch (e: IOException) {
//                e.printStackTrace()
//                return null
//            }
//
//        }

        fun getColorByInt(colorInt: Int): Int {
            return colorInt or -16777216
        }

        /**
         * 修改颜色透明度
         *
         * @param color
         * @param alpha
         * @return
         */
        fun changeColorAlpha(color: Int, alpha: Int): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            return Color.argb(alpha, red, green, blue)
        }

        fun getAlphaPercent(argb: Int): Float {
            return Color.alpha(argb) / 255f
        }

        fun alphaValueAsInt(alpha: Float): Int {
            return Math.round(alpha * 255)
        }

        fun adjustAlpha(alpha: Float, color: Int): Int {
            return alphaValueAsInt(alpha) shl 24 or (0x00ffffff and color)
        }

        fun colorAtLightness(color: Int, lightness: Float): Int {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            hsv[2] = lightness
            return Color.HSVToColor(hsv)
        }

        fun lightnessOfColor(color: Int): Float {
            val hsv = FloatArray(3)
            Color.colorToHSV(color, hsv)
            return hsv[2]
        }

        fun getHexString(color: Int, showAlpha: Boolean): String {
            val base = if (showAlpha) -0x1 else 0xFFFFFF
            val format = if (showAlpha) "#%08X" else "#%06X"
            return String.format(format, base and color).toUpperCase()
        }

        /**
         * bitmap转byteArr
         *
         * @param bitmap bitmap对象
         * @param format 格式
         * @return 字节数组
         */
        fun bitmap2Bytes(bitmap: Bitmap, format: CompressFormat): ByteArray {
            val baos = ByteArrayOutputStream()
            bitmap.compress(format, 100, baos)
            return baos.toByteArray()
        }

        /**
         * byteArr转bitmap
         *
         * @param bytes 字节数组
         * @return bitmap对象
         */
        fun bytes2Bitmap(bytes: ByteArray): Bitmap? {
            return if (bytes.size != 0) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else {
                null
            }
        }

        /**
         * drawable转bitmap
         *
         * @param drawable drawable对象
         * @return bitmap对象
         */
        fun drawable2Bitmap(drawable: Drawable): Bitmap {
            // 取 drawable 的长宽
            val w = drawable.intrinsicWidth
            val h = drawable.intrinsicHeight

            // 取 drawable 的颜色格式
            val config = if (drawable.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
            else
                Bitmap.Config.RGB_565
            // 建立对应 bitmap
            val bitmap = Bitmap.createBitmap(w, h, config)
            // 建立对应 bitmap 的画布
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, w, h)
            // 把 drawable 内容画到画布中
            drawable.draw(canvas)
            return bitmap
        }

        /**
         * bitmap转drawable
         *
         * @param res    resources对象
         * @param bitmap bitmap对象
         * @return drawable对象
         */
        fun bitmap2Drawable(res: Resources, bitmap: Bitmap?): Drawable {
            return BitmapDrawable(res, bitmap)
        }

        fun bitmap2Drawable(bitmap: Bitmap?): Drawable {
            return BitmapDrawable(bitmap)
        }

        /**
         * drawable转byteArr
         *
         * @param drawable drawable对象
         * @param format   格式
         * @return 字节数组
         */
        fun drawable2Bytes(drawable: Drawable, format: CompressFormat): ByteArray {
            val bitmap = drawable2Bitmap(drawable)
            return bitmap2Bytes(bitmap, format)
        }

        /**
         * byteArr转drawable
         *
         * @param res   resources对象
         * @param bytes 字节数组
         * @return drawable对象
         */
        fun bytes2Drawable(res: Resources, bytes: ByteArray): Drawable {
            val bitmap = bytes2Bitmap(bytes)
            return bitmap2Drawable(res, bitmap)
        }

        fun bytes2Drawable(bytes: ByteArray): Drawable {
            val bitmap = bytes2Bitmap(bytes)
            return bitmap2Drawable(bitmap)
        }

        /**
         * 计算采样大小
         *
         * @param options   选项
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return 采样大小
         */
        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            maxWidth: Int,
            maxHeight: Int
        ): Int {
            if (maxWidth == 0 || maxHeight == 0) {
                return 1
            }
            var height = options.outHeight
            var width = options.outWidth
            var inSampleSize = 1

//            while ((height = height shr 1) >= maxHeight && (width = width shr 1) >= maxWidth) inSampleSize = inSampleSize shl 1
            return inSampleSize
        }

        /**
         * 获取bitmap
         *
         * @param file 文件
         * @return bitmap
         */
        fun getBitmap(file: File?): Bitmap? {
            if (file == null) {
                return null
            }
            var `is`: InputStream? = null
            try {
                `is` = BufferedInputStream(FileInputStream(file))
                return BitmapFactory.decodeStream(`is`)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return null
            } finally {
                RxFileTool.closeIO(`is`!!)
            }
        }

        /**
         * 获取bitmap
         *
         * @param file      文件
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return bitmap
         */
        fun getBitmap(file: File?, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (file == null) {
                return null
            }
            var `is`: InputStream? = null
            try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                `is` = BufferedInputStream(FileInputStream(file))
                BitmapFactory.decodeStream(`is`, null, options)
                options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
                options.inJustDecodeBounds = false
                return BitmapFactory.decodeStream(`is`, null, options)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return null
            } finally {
                RxFileTool.closeIO(`is`!!)
            }
        }

        /**
         * 获取bitmap
         *
         * @param filePath 文件路径
         * @return bitmap
         */
        fun getBitmap(filePath: String): Bitmap? {
            return if (RxDataTool.isNullString(filePath)) {
                null
            } else BitmapFactory.decodeFile(filePath)
        }

        /**
         * 获取bitmap
         *
         * @param filePath  文件路径
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return bitmap
         */
        fun getBitmap(filePath: String, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (RxDataTool.isNullString(filePath)) {
                return null
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(filePath, options)
        }

        /**
         * 获取bitmap
         *
         * @param is        输入流
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return bitmap
         */
        fun getBitmap(`is`: InputStream?, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (`is` == null) {
                return null
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(`is`, null, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(`is`, null, options)
        }

        /**
         * 获取bitmap
         *
         * @param data      数据
         * @param offset    偏移量
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return bitmap
         */
        fun getBitmap(data: ByteArray, offset: Int, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (data.size == 0) {
                return null
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, offset, data.size, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeByteArray(data, offset, data.size, options)
        }

        /**
         * 获取bitmap
         *
         * @param resId 资源id
         * @return bitmap
         */
        fun getBitmap(resId: Int): Bitmap? {
            if (RxTool.getContext() == null) {
                return null
            }
            val `is` = RxTool.getContext().resources.openRawResource(resId)
            return BitmapFactory.decodeStream(`is`)
        }

        /**
         * 获取bitmap
         *
         * @param resId     资源id
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return bitmap
         */
        fun getBitmap(resId: Int, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (RxTool.getContext() == null) {
                return null
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val `is` = RxTool.getContext().resources.openRawResource(resId)
            BitmapFactory.decodeStream(`is`, null, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(`is`, null, options)
        }

        /**
         * 获取bitmap
         *
         * @param res 资源对象
         * @param id  资源id
         * @return bitmap
         */
        fun getBitmap(res: Resources?, id: Int): Bitmap? {
            return if (res == null) {
                null
            } else BitmapFactory.decodeResource(res, id)
        }

        /**
         * 获取bitmap
         *
         * @param res       资源对象
         * @param id        资源id
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return bitmap
         */
        fun getBitmap(res: Resources?, id: Int, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (res == null) {
                return null
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, id, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(res, id, options)
        }

        /**
         * 获取bitmap
         *
         * @param fd 文件描述
         * @return bitmap
         */
        fun getBitmap(fd: FileDescriptor?): Bitmap? {
            return if (fd == null) {
                null
            } else BitmapFactory.decodeFileDescriptor(fd)
        }

        /**
         * 获取bitmap
         *
         * @param fd        文件描述
         * @param maxWidth  最大宽度
         * @param maxHeight 最大高度
         * @return bitmap
         */
        fun getBitmap(fd: FileDescriptor?, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (fd == null) {
                return null
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fd, null, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFileDescriptor(fd, null, options)
        }

        /**
         * 缩放图片
         *
         * @param src       源图片
         * @param newWidth  新宽度
         * @param newHeight 新高度
         * @param recycle   是否回收
         * @return 缩放后的图片
         */
        @JvmOverloads
        fun scale(src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 缩放图片
         *
         * @param src         源图片
         * @param scaleWidth  缩放宽度倍数
         * @param scaleHeight 缩放高度倍数
         * @param recycle     是否回收
         * @return 缩放后的图片
         */
        @JvmOverloads
        fun scale(
            src: Bitmap,
            scaleWidth: Float,
            scaleHeight: Float,
            recycle: Boolean = false
        ): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val matrix = Matrix()
            matrix.setScale(scaleWidth, scaleHeight)
            val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 裁剪图片
         *
         * @param src     源图片
         * @param x       开始坐标x
         * @param y       开始坐标y
         * @param width   裁剪宽度
         * @param height  裁剪高度
         * @param recycle 是否回收
         * @return 裁剪后的图片
         */
        @JvmOverloads
        fun clip(
            src: Bitmap,
            x: Int,
            y: Int,
            width: Int,
            height: Int,
            recycle: Boolean = false
        ): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val ret = Bitmap.createBitmap(src, x, y, width, height)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 倾斜图片
         *
         * @param src     源图片
         * @param kx      倾斜因子x
         * @param ky      倾斜因子y
         * @param recycle 是否回收
         * @return 倾斜后的图片
         */
        fun skew(src: Bitmap, kx: Float, ky: Float, recycle: Boolean): Bitmap? {
            return skew(src, kx, ky, 0f, 0f, recycle)
        }

        /**
         * 倾斜图片
         *
         * @param src 源图片
         * @param kx  倾斜因子x
         * @param ky  倾斜因子y
         * @param px  平移因子x
         * @param py  平移因子y
         * @return 倾斜后的图片
         */
        fun skew(kx: Float, src: Bitmap, ky: Float, px: Float, py: Float): Bitmap? {
            return skew(src, kx, ky, 0f, 0f, false)
        }

        /**
         * 倾斜图片
         *
         * @param src     源图片
         * @param kx      倾斜因子x
         * @param ky      倾斜因子y
         * @param px      平移因子x
         * @param py      平移因子y
         * @param recycle 是否回收
         * @return 倾斜后的图片
         */
        @JvmOverloads
        fun skew(
            src: Bitmap,
            kx: Float,
            ky: Float,
            px: Float = 0f,
            py: Float = 0f,
            recycle: Boolean = false
        ): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val matrix = Matrix()
            matrix.setSkew(kx, ky, px, py)
            val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 旋转图片
         *
         * @param src     源图片
         * @param degrees 旋转角度
         * @param px      旋转点横坐标
         * @param py      旋转点纵坐标
         * @param recycle 是否回收
         * @return 旋转后的图片
         */
        @JvmOverloads
        fun rotate(
            src: Bitmap,
            degrees: Int,
            px: Float,
            py: Float,
            recycle: Boolean = false
        ): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            if (degrees == 0) {
                return src
            }
            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat(), px, py)
            val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 获取图片旋转角度
         *
         * @param filePath 文件路径
         * @return 旋转角度
         */
        fun getRotateDegree(filePath: String): Int {
            var degree = 0
            try {
                val exifInterface = ExifInterface(filePath)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                    else -> degree = 90
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return degree
        }

        /**
         * 转为圆形图片
         *
         * @param src     源图片
         * @param recycle 是否回收
         * @return 圆形图片
         */
        @JvmOverloads
        fun toRound(src: Bitmap, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val width = src.width
            val height = src.height
            val radius = Math.min(width, height) shr 1
            val ret = src.copy(src.config, true)
            val paint = Paint()
            val canvas = Canvas(ret)
            val rect = Rect(0, 0, width, height)
            paint.isAntiAlias = true
            paint.color = Color.TRANSPARENT
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawCircle(
                (width shr 1).toFloat(),
                (height shr 1).toFloat(),
                radius.toFloat(),
                paint
            )
            canvas.drawBitmap(src, rect, rect, paint)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 转为圆角图片
         *
         * @param src     源图片
         * @param radius  圆角的度数
         * @param recycle 是否回收
         * @return 圆角图片
         */
        @JvmOverloads
        fun toRoundCorner(src: Bitmap?, radius: Float, recycle: Boolean = false): Bitmap? {
            if (null == src) {
                return null
            }
            val width = src.width
            val height = src.height
            val ret = src.copy(src.config, true)
            val bitmapShader = BitmapShader(
                src,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
            )
            val paint = Paint()
            val canvas = Canvas(ret)
            val rectf = RectF(0f, 0f, width.toFloat(), height.toFloat())
            paint.isAntiAlias = true
            paint.shader = bitmapShader
            canvas.drawRoundRect(rectf, radius, radius, paint)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 快速模糊
         *
         * 先缩小原图，对小图进行模糊，再放大回原先尺寸
         *
         * @param src     源图片
         * @param scale   缩小倍数(0...1)
         * @param radius  模糊半径
         * @param recycle 是否回收
         * @return 模糊后的图片
         */
        @JvmOverloads
        fun fastBlur(src: Bitmap, scale: Float, radius: Float, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val width = src.width
            val height = src.height
            val scaleWidth = (width * scale + 0.5f).toInt()
            val scaleHeight = (height * scale + 0.5f).toInt()
            if (scaleWidth == 0 || scaleHeight == 0) {
                return null
            }
            var scaleBitmap: Bitmap? = Bitmap.createScaledBitmap(src, scaleWidth, scaleHeight, true)
            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas()
            val filter = PorterDuffColorFilter(
                Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP
            )
            paint.colorFilter = filter
            canvas.scale(scale, scale)
            canvas.drawBitmap(scaleBitmap!!, 0f, 0f, paint)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                scaleBitmap = renderScriptBlur(scaleBitmap, radius)
            } else {
                scaleBitmap = stackBlur(scaleBitmap, radius.toInt(), true)
            }
            if (scale == 1f) {
                return scaleBitmap
            }
            val ret = Bitmap.createScaledBitmap(scaleBitmap!!, width, height, true)
            if (scaleBitmap != null && !scaleBitmap.isRecycled) {
                scaleBitmap.recycle()
            }
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * renderScript模糊图片
         *
         * API大于17
         *
         * @param src    源图片
         * @param radius 模糊度(0...25)
         * @return 模糊后的图片
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun renderScriptBlur(src: Bitmap, radius: Float): Bitmap? {
            var radius = radius
            if (isEmptyBitmap(src)) return null
            var rs: RenderScript? = null
            try {
                rs = RenderScript.create(RxTool.getContext())
                rs!!.messageHandler = RenderScript.RSMessageHandler()
                val input = Allocation.createFromBitmap(
                    rs, src, Allocation.MipmapControl.MIPMAP_NONE, Allocation
                        .USAGE_SCRIPT
                )
                val output = Allocation.createTyped(rs, input.type)
                val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                if (radius > 25) {
                    radius = 25.0f
                } else if (radius <= 0) {
                    radius = 1.0f
                }
                blurScript.setInput(input)
                blurScript.setRadius(radius)
                blurScript.forEach(output)
                output.copyTo(src)
            } finally {
                rs?.destroy()
            }
            return src
        }

        /**
         * stack模糊图片
         *
         * @param src     源图片
         * @param radius  模糊半径
         * @param recycle 是否回收
         * @return stackBlur模糊图片
         */
        fun stackBlur(src: Bitmap, radius: Int, recycle: Boolean): Bitmap? {
            val ret: Bitmap
            if (recycle) {
                ret = src
            } else {
                ret = src.copy(src.config, true)
            }

            if (radius < 1) {
                return null
            }

            val w = ret.width
            val h = ret.height

            val pix = IntArray(w * h)
            ret.getPixels(pix, 0, w, 0, 0, w, h)

            val wm = w - 1
            val hm = h - 1
            val wh = w * h
            val div = radius + radius + 1

            val r = IntArray(wh)
            val g = IntArray(wh)
            val b = IntArray(wh)
            var rsum: Int
            var gsum: Int
            var bsum: Int
            var x: Int
            var y: Int
            var i: Int
            var p: Int
            var yp: Int
            var yi: Int
            var yw: Int
            val vmin = IntArray(Math.max(w, h))

            var divsum = div + 1 shr 1
            divsum *= divsum
            val dv = IntArray(256 * divsum)
            i = 0
            while (i < 256 * divsum) {
                dv[i] = i / divsum
                i++
            }

            yi = 0
            yw = yi

            val stack = Array(div) { IntArray(3) }
            var stackpointer: Int
            var stackstart: Int
            var sir: IntArray
            var rbs: Int
            val r1 = radius + 1
            var routsum: Int
            var goutsum: Int
            var boutsum: Int
            var rinsum: Int
            var ginsum: Int
            var binsum: Int

            y = 0
            while (y < h) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                i = -radius
                while (i <= radius) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))]
                    sir = stack[i + radius]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rbs = r1 - Math.abs(i)
                    rsum += sir[0] * rbs
                    gsum += sir[1] * rbs
                    bsum += sir[2] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    i++
                }
                stackpointer = radius

                x = 0
                while (x < w) {

                    r[yi] = dv[rsum]
                    g[yi] = dv[gsum]
                    b[yi] = dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm)
                    }
                    p = pix[yw + vmin[x]]

                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer % div]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi++
                    x++
                }
                yw += w
                y++
            }
            x = 0
            while (x < w) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                yp = -radius * w
                i = -radius
                while (i <= radius) {
                    yi = Math.max(0, yp) + x

                    sir = stack[i + radius]

                    sir[0] = r[yi]
                    sir[1] = g[yi]
                    sir[2] = b[yi]

                    rbs = r1 - Math.abs(i)

                    rsum += r[yi] * rbs
                    gsum += g[yi] * rbs
                    bsum += b[yi] * rbs

                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }

                    if (i < hm) {
                        yp += w
                    }
                    i++
                }
                yi = x
                stackpointer = radius
                y = 0
                while (y < h) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] =
                        -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w
                    }
                    p = x + vmin[y]

                    sir[0] = r[p]
                    sir[1] = g[p]
                    sir[2] = b[p]

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi += w
                    y++
                }
                x++
            }
            ret.setPixels(pix, 0, w, 0, 0, w, h)
            return ret
        }

        /**
         * 添加颜色边框
         *
         * @param src         源图片
         * @param borderWidth 边框宽度
         * @param color       边框的颜色值
         * @return 带颜色边框图
         */
        fun addFrame(src: Bitmap, borderWidth: Int, color: Int): Bitmap {
            return addFrame(src, borderWidth, color)
        }

        /**
         * 添加颜色边框
         *
         * @param src         源图片
         * @param borderWidth 边框宽度
         * @param color       边框的颜色值
         * @param recycle     是否回收
         * @return 带颜色边框图
         */
        fun addFrame(src: Bitmap, borderWidth: Int, color: Int, recycle: Boolean): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val newWidth = src.width + borderWidth shr 1
            val newHeight = src.height + borderWidth shr 1
            val ret = Bitmap.createBitmap(newWidth, newHeight, src.config)
            val canvas = Canvas(ret)
            val rec = canvas.clipBounds
            val paint = Paint()
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth.toFloat()
            canvas.drawRect(rec, paint)
            canvas.drawBitmap(src, borderWidth.toFloat(), borderWidth.toFloat(), null)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 添加倒影
         *
         * @param src              源图片的
         * @param reflectionHeight 倒影高度
         * @param recycle          是否回收
         * @return 带倒影图片
         */
        @JvmOverloads
        fun addReflection(src: Bitmap, reflectionHeight: Int, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val REFLECTION_GAP = 0
            val srcWidth = src.width
            val srcHeight = src.height
            if (0 == srcWidth || srcHeight == 0) {
                return null
            }
            val matrix = Matrix()
            matrix.preScale(1f, -1f)
            val reflectionBitmap = Bitmap.createBitmap(
                src, 0, srcHeight - reflectionHeight,
                srcWidth, reflectionHeight, matrix, false
            ) ?: return null
            val ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.config)
            val canvas = Canvas(ret)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.drawBitmap(reflectionBitmap, 0f, (srcHeight + REFLECTION_GAP).toFloat(), null)
            val paint = Paint()
            paint.isAntiAlias = true
            val shader = LinearGradient(
                0f, srcHeight.toFloat(), 0f,
                (ret.height + REFLECTION_GAP).toFloat(),
                0x70FFFFFF, 0x00FFFFFF, Shader.TileMode.MIRROR
            )
            paint.shader = shader
            paint.xfermode = PorterDuffXfermode(
                PorterDuff.Mode.DST_IN
            )
            canvas.save()
            canvas.drawRect(
                0f, srcHeight.toFloat(), srcWidth.toFloat(),
                (ret.height + REFLECTION_GAP).toFloat(), paint
            )
            canvas.restore()
            if (!reflectionBitmap.isRecycled) {
                reflectionBitmap.recycle()
            }
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 添加文字水印
         *
         * @param src      源图片
         * @param content  水印文本
         * @param textSize 水印字体大小
         * @param color    水印字体颜色
         * @param alpha    水印字体透明度
         * @param x        起始坐标x
         * @param y        起始坐标y
         * @param recycle  是否回收
         * @return 带有文字水印的图片
         */
        @JvmOverloads
        fun addTextWatermark(
            src: Bitmap,
            content: String?,
            textSize: Int,
            color: Int,
            alpha: Int,
            x: Float,
            y: Float,
            recycle: Boolean = false
        ): Bitmap? {
            if (isEmptyBitmap(src) || content == null) {
                return null
            }
            val ret = src.copy(src.config, true)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas(ret)
            paint.alpha = alpha
            paint.color = color
            paint.textSize = textSize.toFloat()
            val bounds = Rect()
            paint.getTextBounds(content, 0, content.length, bounds)
            canvas.drawText(content, x, y, paint)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 添加图片水印
         *
         * @param src       源图片
         * @param watermark 图片水印
         * @param x         起始坐标x
         * @param y         起始坐标y
         * @param alpha     透明度
         * @param recycle   是否回收
         * @return 带有图片水印的图片
         */
        @JvmOverloads
        fun addImageWatermark(
            src: Bitmap,
            watermark: Bitmap,
            x: Int,
            y: Int,
            alpha: Int,
            recycle: Boolean = false
        ): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val ret = src.copy(src.config, true)
            if (!isEmptyBitmap(watermark)) {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                val canvas = Canvas(ret)
                paint.alpha = alpha
                canvas.drawBitmap(watermark, x.toFloat(), y.toFloat(), paint)
            }
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 转为alpha位图
         *
         * @param src 源图片
         * @return alpha位图
         */
        fun toAlpha(src: Bitmap): Bitmap {
            return toAlpha(src)
        }

        /**
         * 转为alpha位图
         *
         * @param src     源图片
         * @param recycle 是否回收
         * @return alpha位图
         */
        fun toAlpha(src: Bitmap, recycle: Boolean?): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val ret = src.extractAlpha()
            if (recycle!! && !src.isRecycled) {
                src.recycle()
            }
            return ret
        }

        /**
         * 可以对该图的非透明区域着色
         *
         *
         * 有多种使用场景，常见如 Button 的 pressed 状态，View 的阴影状态等
         *
         * @param iv
         * @param src
         * @param radius
         * @param color
         * @return
         */
        private fun getDropShadow(iv: ImageView, src: Bitmap, radius: Float, color: Int): Bitmap {

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = color

            val width = src.width
            val height = src.height
            val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(dest)
            val alpha = src.extractAlpha()
            canvas.drawBitmap(alpha, 0f, 0f, paint)

            val filter = BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER)
            paint.maskFilter = filter
            canvas.drawBitmap(alpha, 0f, 0f, paint)
            iv.setImageBitmap(dest)

            return dest
        }

        /**
         * 转为灰度图片
         *
         * @param src     源图片
         * @param recycle 是否回收
         * @return 灰度图
         */
        @JvmOverloads
        fun toGray(src: Bitmap, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val grayBitmap = Bitmap.createBitmap(
                src.width,
                src.height, Bitmap.Config.RGB_565
            )
            val canvas = Canvas(grayBitmap)
            val paint = Paint()
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
            paint.colorFilter = colorMatrixColorFilter
            canvas.drawBitmap(src, 0f, 0f, paint)
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return grayBitmap
        }

        /**
         * 保存图片
         *
         * @param src      源图片
         * @param filePath 要保存到的文件路径
         * @param format   格式
         * @return `true`: 成功<br></br>`false`: 失败
         */
        fun save(src: Bitmap, filePath: String, format: CompressFormat): Boolean {
            return save(src, RxFileTool.getFileByPath(filePath), format, false)
        }

        /**
         * 保存图片
         *
         * @param src      源图片
         * @param filePath 要保存到的文件路径
         * @param format   格式
         * @param recycle  是否回收
         * @return `true`: 成功<br></br>`false`: 失败
         */
        fun save(src: Bitmap, filePath: String, format: CompressFormat, recycle: Boolean): Boolean {
            return save(src, RxFileTool.getFileByPath(filePath), format, recycle)
        }

        /**
         * 保存图片
         *
         * @param src     源图片
         * @param file    要保存到的文件
         * @param format  格式
         * @param recycle 是否回收
         * @return `true`: 成功<br></br>`false`: 失败
         */
        @JvmOverloads
        fun save(
            src: Bitmap,
            file: File?,
            format: CompressFormat,
            recycle: Boolean = false
        ): Boolean {
            if (isEmptyBitmap(src) || !RxFileTool.createOrExistsFile(file)) {
                return false
            }
            println(src.width.toString() + ", " + src.height)
            var os: OutputStream? = null
            var ret = false
            try {
                os = BufferedOutputStream(FileOutputStream(file!!))
                ret = src.compress(format, 100, os)
                if (recycle && !src.isRecycled) {
                    src.recycle()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                RxFileTool.closeIO(os!!)
            }
            return ret
        }

        /**
         * 根据文件名判断文件是否为图片
         *
         * @param file 　文件
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isImage(file: File?): Boolean {
            return file != null && isImage(file.path)
        }

        /**
         * 根据文件名判断文件是否为图片
         *
         * @param filePath 　文件路径
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isImage(filePath: String): Boolean {
            val path = filePath.toUpperCase()
            return (path.endsWith(".PNG") || path.endsWith(".JPG")
                    || path.endsWith(".JPEG") || path.endsWith(".BMP")
                    || path.endsWith(".GIF"))
        }

        /**
         * 获取图片类型
         *
         * @param filePath 文件路径
         * @return 图片类型
         */
        fun getImageType(filePath: String): String? {
            return getImageType(RxFileTool.getFileByPath(filePath))
        }

        /**
         * 获取图片类型
         *
         * @param file 文件
         * @return 图片类型
         */
        fun getImageType(file: File?): String? {
            if (file == null) return null
            var `is`: InputStream? = null
            try {
                `is` = FileInputStream(file)
                return getImageType(`is`)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                RxFileTool.closeIO(`is`!!)
            }
        }

        /**
         * 流获取图片类型
         *
         * @param is 图片输入流
         * @return 图片类型
         */
        fun getImageType(`is`: InputStream?): String? {
            if (`is` == null) return null
            try {
                val bytes = ByteArray(8)
                return if (`is`.read(bytes, 0, 8) != -1) getImageType(bytes) else null
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取图片类型
         *
         * @param bytes bitmap的前8字节
         * @return 图片类型
         */
        fun getImageType(bytes: ByteArray): String? {
            if (isJPEG(bytes)) {
                return "JPEG"
            }
            if (isGIF(bytes)) {
                return "GIF"
            }
            if (isPNG(bytes)) {
                return "PNG"
            }
            return if (isBMP(bytes)) {
                "BMP"
            } else null
        }

        private fun isJPEG(b: ByteArray): Boolean {
            return (b.size >= 2
                    && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte())
        }

        private fun isGIF(b: ByteArray): Boolean {
            return (b.size >= 6
                    && b[0] == 'G'.toByte() && b[1] == 'I'.toByte()
                    && b[2] == 'F'.toByte() && b[3] == '8'.toByte()
                    && (b[4] == '7'.toByte() || b[4] == '9'.toByte()) && b[5] == 'a'.toByte())
        }

        private fun isPNG(b: ByteArray): Boolean {
            return b.size >= 8 && (b[0] == 137.toByte() && b[1] == 80.toByte()
                    && b[2] == 78.toByte() && b[3] == 71.toByte()
                    && b[4] == 13.toByte() && b[5] == 10.toByte()
                    && b[6] == 26.toByte() && b[7] == 10.toByte())
        }

        private fun isBMP(b: ByteArray): Boolean {
            return (b.size >= 2
                    && b[0].toInt() == 0x42 && b[1].toInt() == 0x4d)
        }

        /**
         * 判断bitmap对象是否为空
         *
         * @param src 源图片
         * @return `true`: 是<br></br>`false`: 否
         */
        private fun isEmptyBitmap(src: Bitmap?): Boolean {
            return src == null || src.width == 0 || src.height == 0
        }

        /**
         * 按缩放压缩
         *
         * @param src       源图片
         * @param newWidth  新宽度
         * @param newHeight 新高度
         * @return 缩放压缩后的图片
         */
        fun compressByScale(src: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
            return scale(src, newWidth, newHeight, false)
        }

        /**
         * 按缩放压缩
         *
         * @param src       源图片
         * @param newWidth  新宽度
         * @param newHeight 新高度
         * @param recycle   是否回收
         * @return 缩放压缩后的图片
         */
        fun compressByScale(src: Bitmap, newWidth: Int, newHeight: Int, recycle: Boolean): Bitmap? {
            return scale(src, newWidth, newHeight, recycle)
        }

        /******************************~~~~~~~~~ 下方和压缩有关 ~~~~~~~~~ */

        /**
         * 按缩放压缩
         *
         * @param src         源图片
         * @param scaleWidth  缩放宽度倍数
         * @param scaleHeight 缩放高度倍数
         * @return 缩放压缩后的图片
         */
        fun compressByScale(src: Bitmap, scaleWidth: Float, scaleHeight: Float): Bitmap? {
            return scale(src, scaleWidth, scaleHeight, false)
        }

        /**
         * 按缩放压缩
         *
         * @param src         源图片
         * @param scaleWidth  缩放宽度倍数
         * @param scaleHeight 缩放高度倍数
         * @param recycle     是否回收
         * @return 缩放压缩后的图片
         */
        fun compressByScale(
            src: Bitmap,
            scaleWidth: Float,
            scaleHeight: Float,
            recycle: Boolean
        ): Bitmap? {
            return scale(src, scaleWidth, scaleHeight, recycle)
        }

        /**
         * 按质量压缩
         *
         * @param src     源图片
         * @param quality 质量
         * @param recycle 是否回收
         * @return 质量压缩后的图片
         */
        @JvmOverloads
        fun compressByQuality(src: Bitmap, quality: Int, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src) || quality < 0 || quality > 100) {
                return null
            }
            val baos = ByteArrayOutputStream()
            src.compress(CompressFormat.JPEG, quality, baos)
            val bytes = baos.toByteArray()
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * 按质量压缩
         *
         * @param src         源图片
         * @param maxByteSize 允许最大值字节数
         * @param recycle     是否回收
         * @return 质量压缩压缩过的图片
         */
        @JvmOverloads
        fun compressByQuality(src: Bitmap, maxByteSize: Long, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src) || maxByteSize <= 0) {
                return null
            }
            val baos = ByteArrayOutputStream()
            var quality = 100
            src.compress(CompressFormat.JPEG, quality, baos)
            while (baos.toByteArray().size > maxByteSize && quality >= 0) {
                baos.reset()
                quality = quality - 5
                src.compress(CompressFormat.JPEG, quality, baos)
            }
            if (quality < 0) {
                return null
            }
            val bytes = baos.toByteArray()
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * 按采样大小压缩
         *
         * @param src        源图片
         * @param sampleSize 采样率大小
         * @param recycle    是否回收
         * @return 按采样率压缩后的图片
         */
        @JvmOverloads
        fun compressBySampleSize(src: Bitmap, sampleSize: Int, recycle: Boolean = false): Bitmap? {
            if (isEmptyBitmap(src)) {
                return null
            }
            val options = BitmapFactory.Options()
            options.inSampleSize = sampleSize
            val baos = ByteArrayOutputStream()
            src.compress(CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            if (recycle && !src.isRecycled) {
                src.recycle()
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        }

        /**
         * 缩略图工具类，
         * 可以根据本地视频文件源、
         * Bitmap 对象生成缩略图
         *
         * @param filePath
         * @param kind
         * @return
         */
        fun getThumb(filePath: String, kind: Int): Bitmap {
            return ThumbnailUtils.createVideoThumbnail(filePath, kind)
        }

        fun getThumb(source: Bitmap, width: Int, height: Int): Bitmap {
            return ThumbnailUtils.extractThumbnail(source, width, height)
        }

        fun zoomImage(bgimage: Bitmap, newWidth: Double, newHeight: Double): Bitmap {
            // 获取到bitmap的宽
            val width = bgimage.width.toFloat()

            val height = bgimage.height.toFloat()
            //
            val matrix = Matrix()
            // 设置尺寸
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height

            matrix.postScale(scaleWidth, scaleHeight)
            val bitmap = Bitmap.createBitmap(
                bgimage, 0, 0, width.toInt(),
                height.toInt(), matrix, true
            )
            Log.e("tag", (bitmap.height + bitmap.width).toString() + "d")
            return bitmap
        }

        /**
         * Resize the bitmap
         *
         * @param bitmap 图片引用
         * @param width  宽度
         * @param height 高度
         * @return 缩放之后的图片引用
         */
        fun zoomBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
            val w = bitmap.width
            val h = bitmap.height
            val matrix = Matrix()
            val scaleWidth = width.toFloat() / w
            val scaleHeight = height.toFloat() / h
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
        }

        /**
         * 绘制 9Path
         *
         * @param c
         * @param bmp
         * @param rect
         */
        fun drawNinePath(c: Canvas, bmp: Bitmap, rect: Rect) {
            val patch = NinePatch(bmp, bmp.ninePatchChunk, null)
            patch.draw(c, rect)
        }

        /**
         * 创建的包含文字的图片，背景为透明
         *
         * @param source              图片
         * @param txtSize             文字大小
         * @param innerTxt            显示的文字
         * @param textColor           文字颜色Color.BLUE
         * @param textBackgroundColor 文字背景板颜色 Color.parseColor("#FFD700")
         * @return
         */
        fun createTextImage(
            source: Bitmap,
            txtSize: Int,
            innerTxt: String,
            textColor: Int,
            textBackgroundColor: Int
        ): Bitmap {
            val bitmapWidth = source.width
            val bitmapHeight = source.height

            val textWidth = txtSize * innerTxt.length
            val textHeight = txtSize

            val width: Int


            if (bitmapWidth > textWidth) {
                width = bitmapWidth + txtSize * innerTxt.length
            } else {
                width = txtSize * innerTxt.length
            }
            val height = bitmapHeight + txtSize

            //若使背景为透明，必须设置为Bitmap.Config.ARGB_4444
            val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
            val canvas = Canvas(bm)
            //把图片画上来
            val bitmapPaint = Paint()
            canvas.drawBitmap(source, ((width - bitmapWidth) / 2).toFloat(), 0f, bitmapPaint)


            val paint = Paint()
            paint.color = textColor
            paint.textSize = txtSize.toFloat()
            paint.isAntiAlias = true


            //计算得出文字的绘制起始x、y坐标
            val posX = (width - txtSize * innerTxt.length) / 2
            val posY = height / 2

            val textX = posX + txtSize * innerTxt.length / 4

            val paint1 = Paint()
            paint1.color = textBackgroundColor
            paint1.strokeWidth = 3f
            paint1.style = Paint.Style.FILL_AND_STROKE

            val r1 = RectF()
            r1.left = posX.toFloat()
            r1.right = (posX + txtSize * innerTxt.length).toFloat()
            r1.top = posY.toFloat()
            r1.bottom = (posY + txtSize).toFloat()
            canvas.drawRoundRect(r1, 10f, 10f, paint1)
            canvas.drawText(innerTxt, textX.toFloat(), (posY + txtSize - 2).toFloat(), paint)

            return bm
        }

        //保存文件到指定路径
        fun saveImageToGallery(context: Context, bmp: Bitmap): Boolean {
            // 首先保存图片
            val storePath =
                Environment.getExternalStorageDirectory().absolutePath + File.separator + context.packageName
            val appDir = File(storePath)
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val file = File(appDir, fileName)
            try {
                val fos = FileOutputStream(file)
                //通过io流的方式来压缩保存图片
                val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos)
                fos.flush()
                fos.close()

                //把文件插入到系统图库
                //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

                //保存图片后发送广播通知更新数据库
                val uri = Uri.fromFile(file)
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                return isSuccess
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return false
        }
    }


}

