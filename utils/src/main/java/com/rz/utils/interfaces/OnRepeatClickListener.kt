package com.rz.utils.interfaces

import android.view.View

import com.rz.utils.RxTool


/**
 * @author Vondear
 * @date 2017/7/24
 * 重复点击的监听器
 */

abstract class OnRepeatClickListener : View.OnClickListener {

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private val MIN_CLICK_DELAY_TIME = 1000

    abstract fun onRepeatClick(v: View)

    override fun onClick(v: View) {
        if (!RxTool.isFastClick(MIN_CLICK_DELAY_TIME)) {
            onRepeatClick(v)
        }
    }

}
