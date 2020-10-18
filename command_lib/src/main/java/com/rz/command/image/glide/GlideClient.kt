package com.rz.command.image.glide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rz.command.image.IImageLoad
import com.rz.command.image.IImageLoadConfig

/**
 * 冯小保
 * 2018/9/10.
 */
class GlideClient : IImageLoad {
    private var mContext: Context? = null

    override fun init(context: Context, config: IImageLoadConfig?) {
        this.mContext = context
        val builder = builderGlideBuilder(config)
        Glide.init(context, builder)
    }


    private fun builderGlideBuilder(config: IImageLoadConfig?): GlideBuilder {
        return GlideBuilder()
    }

    override fun trimMemory(level: Int) {
        Glide.get(mContext!!).trimMemory(level)
    }

    override fun clear(imageView: ImageView) {
        Glide.with(mContext!!).clear(imageView)
    }

    override fun clearAllMemoryCaches() {
        Glide.get(mContext!!).clearDiskCache()
        Glide.get(mContext!!).clearMemory()

    }

    override fun clearDiskCache() {
        Glide.get(mContext!!).clearDiskCache()
    }

    override fun onLowMemory() {
        Glide.get(mContext!!).onLowMemory()

    }

    override fun load(imageView: ImageView, url: String) {
        Glide.with(imageView.context).load(url).into(imageView)
    }

    override fun showImageViewBlur(bgLayout: RelativeLayout, errorimg: Int, url: String) {
        //        GlideApp.with(mContext).load(url).error(errorimg)
        ////                // 设置错误图片
        ////                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        ////                // 缓存修改过的图片
        ////                .transform(new GlideBlurTransformation(mContext))// 高斯模糊处理
        ////                // 设置占位图
        ////                .(new SimpleTarget<Bitmap>() {
        ////                    @Override
        ////                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        ////                        BitmapDrawable bd = new BitmapDrawable(resource);
        ////                        bgLayout.setBackground(bd);
        ////                    }
        ////                });
        Glide.with(bgLayout).load(url).into(object : SimpleTarget<Drawable>() {
            @SuppressLint("NewApi")
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                bgLayout.background = resource
            }
        })

    }

    override fun showImageViewToCircle(url: String, imageView: ImageView, drawable: Drawable) {
        var options = RequestOptions.circleCropTransform()
        if (null != drawable) {
            options.error(drawable)
        }
        Glide.with(imageView).load(url).apply(options).into(imageView)

    }

    override fun loadImageViewSize(
        mContext: Context,
        path: String,
        width: Int,
        height: Int,
        mImageView: ImageView
    ) {
    }

    override fun loadImageViewSkipCache(path: String, imageView: ImageView) {

        Glide.with(mContext!!).load(path).into(imageView)
    }

    override fun loadImageViewStaticGif(path: String, imageView: ImageView) {
        Glide.with(mContext!!).asBitmap().load(path).into(imageView)
    }
}
