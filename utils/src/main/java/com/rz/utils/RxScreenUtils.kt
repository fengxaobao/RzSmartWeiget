package com.rz.utils

import android.content.Context


/**
 * ScreenUtils
 *
 * **Convert between dp and sp**
 *  * [RxScreenUtils.dpToPx]
 *  * [RxScreenUtils.pxToDp]
 *
 *
 * @author [Trinea](http://www.trinea.cn) 2014-2-14
 */
class RxScreenUtils private constructor() {

    init {
        throw AssertionError()
    }

    companion object {

        fun dpToPx(context: Context?, dp: Float): Float {
            return if (context == null) {
                -1f
            } else dp * context.resources.displayMetrics.density
        }

        fun pxToDp(context: Context?, px: Float): Float {
            return if (context == null) {
                -1f
            } else px / context.resources.displayMetrics.density
        }

        fun dpToPxInt(context: Context, dp: Float): Int {
            return (dpToPx(context, dp) + 0.5f).toInt()
        }

        fun pxToDpCeilInt(context: Context, px: Float): Int {
            return (pxToDp(context, px) + 0.5f).toInt()
        }
    }
}
