package com.jetpack.base.mvvm.repository

import com.rz.utils.RxSignUtils
import java.util.*

/**
 * mike.feng
 */
open abstract class BaseNetRepository : BaseRepository() {
    abstract fun getUlrKey(): String
    protected fun getSign(map: TreeMap<String, Any>): String {
        return RxSignUtils.getSign(map, getUlrKey())
    }
}