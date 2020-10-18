package com.rz.utils

import android.view.View

/**
 * Author:冯小保
 * 2019/6/11
 *
 * @descriptio：
 */

object RxClickUtil {

    /**
     * 最近一次点击的时间
     */
    private var mLastClickTime: Long = 0

    /**
     * 最近一次点击的控件ID
     */
    private var mLastClickViewId: Int = 0

    /**
     * 是否是快速点击
     *
     * @param v              点击的控件
     * @param intervalMillis 时间间期（毫秒）
     * @return true:是，false:不是
     */
    fun isFastDoubleClick(v: View, intervalMillis: Long): Boolean {
        val viewId = v.id
        val time = System.currentTimeMillis()
        val timeInterval = Math.abs(time - mLastClickTime)
        if (timeInterval < intervalMillis && viewId == mLastClickViewId) {
            return true
        } else {
            mLastClickTime = time
            mLastClickViewId = viewId
            return false
        }
    }
}