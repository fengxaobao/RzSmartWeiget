package com.rz.utils.model

/**
 * @author Vondear
 * 功能描述：弹窗内部子类项（绘制标题和图标）
 */
class ActionItem {
    /**
     * 定义文本对象
     */
    var mTitle: CharSequence

    var mResourcesId: Int = 0

    constructor(title: CharSequence, mResourcesId: Int) {
        this.mResourcesId = mResourcesId
        this.mTitle = title
    }

    constructor(title: CharSequence) {
        this.mTitle = title
    }

}