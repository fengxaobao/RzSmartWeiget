package com.jetpack.base.sdk.net.cache

/**
 * 配置过期类型和时间
 *
 *
 * Created by Zero on 2017/5/30.
 */

class CacheObject
/**
 * @param data
 * @param period -1 表示永不过期，大于0表示过期的时间，单位分钟
 */
    (val `object`: Any, period: Int) {

    private val timestamp: Long
    var period = -1

    val isValid: Boolean
        get() = period == -1 || System.currentTimeMillis() < timestamp + period * 60000

    init {
        timestamp = System.currentTimeMillis()
        this.period = period
    }
}
