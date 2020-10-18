package com.rz.command.image.glide.effect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.renderscript.RSRuntimeException
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource


import java.security.MessageDigest

class GlideBlurTransformation : Transformation<Bitmap> {

    private var mContext: Context
    private var mBitmapPool: BitmapPool

    private var mRadius: Int = 0
    private var mSampling: Int = 0

    constructor(context: Context, radius: Int) : this(
        context, Glide.get(context).bitmapPool, radius,
        DEFAULT_DOWN_SAMPLING
    )

    @JvmOverloads
    constructor(
        context: Context,
        pool: BitmapPool = Glide.get(context).bitmapPool,
        radius: Int = MAX_RADIUS,
        sampling: Int = DEFAULT_DOWN_SAMPLING
    ) {
        mContext = context
        mBitmapPool = pool
        mRadius = radius
        mSampling = sampling
    }

    constructor(context: Context, radius: Int, sampling: Int) {
        mContext = context
        mBitmapPool = Glide.get(context).bitmapPool
        mRadius = radius
        mSampling = sampling
    }


    override fun transform(
        context: Context,
        resource: Resource<Bitmap>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap> {
        val source = resource.get()

        val width = source.width
        val height = source.height
        val scaledWidth = width / mSampling
        val scaledHeight = height / mSampling

        var bitmap: Bitmap? = mBitmapPool.get(
            scaledWidth, scaledHeight,
            Bitmap.Config.ARGB_8888
        )
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(
                scaledWidth, scaledHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap!!)
        canvas.scale(1 / mSampling.toFloat(), 1 / mSampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(source, 0f, 0f, paint)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                bitmap = RSBlur.blur(this.mContext, bitmap, mRadius)
            } catch (e: RSRuntimeException) {
                bitmap = FastBlur.blur(bitmap, mRadius, true)
            }

        } else {
            bitmap = FastBlur.blur(bitmap, mRadius, true)
        }

        return BitmapResource.obtain(bitmap, mBitmapPool)!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }

    companion object {

        private val MAX_RADIUS = 25
        private val DEFAULT_DOWN_SAMPLING = 1
    }
}