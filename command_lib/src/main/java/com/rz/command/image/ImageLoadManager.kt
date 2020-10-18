package  com.rz.command.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout

/**
 * 冯小保
 * 2018/9/10.
 */
class ImageLoadManager {
    private var mContext: Context? = null

    private var iImageLoad: IImageLoad? = null


    fun setiImageLoad(iImageLoad: IImageLoad) {
        this.iImageLoad = iImageLoad
    }

    private object Singleton {
        internal var instance = ImageLoadManager()
    }

    fun init(context: Context, config: IImageLoadConfig?) {
        mContext = context.applicationContext
        iImageLoad!!.init(context, config)
    }

    fun onTrimMemory(level: Int) {
        if (null != iImageLoad) {
            iImageLoad!!.trimMemory(level)
        }
    }

    fun onLowMemory() {
        if (null != iImageLoad) {
            iImageLoad!!.onLowMemory()
        }
    }

    fun load(target: ImageView, url: String) {

        iImageLoad!!.load(target, url)
    }

    //高斯模糊
    fun showImageViewBlur(
        bgLayout: RelativeLayout, errorimg: Int,
        url: String
    ) {
        iImageLoad!!.showImageViewBlur(bgLayout, errorimg, url)
    }

    //    显示图片 圆角显示  ImageView
    fun showImageViewToCircle(url: String, imageView: ImageView, err: Drawable) {
        iImageLoad!!.showImageViewToCircle(url, imageView, err)
    }

    fun loadImageViewSize(
        mContext: Context,
        path: String,
        width: Int,
        height: Int,
        imageview: ImageView
    ) {
        iImageLoad!!.loadImageViewSize(mContext, path, width, height, imageview)
    }

    //设置跳过内存缓存
    fun loadImageViewSkipCache(path: String, imageview: ImageView) {
        iImageLoad!!.loadImageViewSkipCache(path, imageview)
    }

    fun loadImageViewStaticGif(path: String, imageview: ImageView) {
        iImageLoad!!.loadImageViewStaticGif(path, imageview)
    }

    fun clearAllMemoryCaches() {
//        iImageLoad!!.clearAllMemoryCaches()
    }


    fun clearDiskCache() {
        iImageLoad!!.clearDiskCache()
    }

    companion object {

        fun newInstance(): ImageLoadManager {
            return Singleton.instance
        }
    }
}
