package com.rz.smart.event

import org.greenrobot.eventbus.EventBus

/**
 * Created by zhou on 2021/1/11 18:43.
 */
open class BaseEvent {

    fun post(){
        EventBus.getDefault().post(this)
    }

    open fun postSticky() {
        EventBus.getDefault().postSticky(this)
    }

    open fun removeSticky() {
        EventBus.getDefault().removeStickyEvent(this)
    }

}