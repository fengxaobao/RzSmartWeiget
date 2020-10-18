package com.rz.command.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout

/**
 * 冯小保
 * 2018/9/10.
 */
interface IImageLoad {
    fun init(context: Context, config: IImageLoadConfig?)

    fun trimMemory(level: Int)
    fun clear(imageView: ImageView) //取消下载

    fun clearAllMemoryCaches()


    fun clearDiskCache()

    fun onLowMemory()

    fun load(imageView: ImageView, url: String)

    //高斯模糊
    fun showImageViewBlur(
        bgLayout: RelativeLayout, errorimg: Int,
        url: String
    )

    //    显示图片 圆角显示  ImageView
    fun showImageViewToCircle(url: String, imageView: ImageView, err: Drawable)

    fun loadImageViewSize(
        mContext: Context,
        path: String,
        width: Int,
        height: Int,
        mImageView: ImageView
    )

    //设置跳过内存缓存
    fun loadImageViewSkipCache(path: String, mImageView: ImageView)

    fun loadImageViewStaticGif(path: String, mImageView: ImageView)
}
