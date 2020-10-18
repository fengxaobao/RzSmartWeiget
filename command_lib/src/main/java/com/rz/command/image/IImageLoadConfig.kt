package  com.rz.command.image

import android.graphics.drawable.ColorDrawable

/**
 * 冯小保
 * 2018/9/10.
 */
interface IImageLoadConfig {
    fun placeholder(placeId: Int) //占位符
    fun placeholder(drawable: ColorDrawable) //占位符
    fun error(errId: Int)
    fun thumbnail(thumb: Float) //设置缩略图
    fun centerCrop()
    fun circleCrop()
    fun fitCenter()
    fun override(width: Int, height: Int) //图片剪切大小
    fun closeDisCacheStrategy(disable: Boolean) //关闭图片缓存
    fun setCacheStrategy() //设置缓存策略

}
