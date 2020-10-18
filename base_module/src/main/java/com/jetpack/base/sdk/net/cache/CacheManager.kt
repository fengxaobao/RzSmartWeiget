package com.jetpack.base.sdk.net.cache

import java.util.*

/**
 * 数据请求中对cache进行管理
 * Created by Zero on 2017/5/30.
 */

object CacheManager {

    private val cacheMap = HashMap<String, CacheObject>()

    /**
     * 添加到cache
     *
     * @param key
     * @param data
     * @param period
     */
    fun addData(key: String, data: Any, period: Int) {
        var cacheObject = getData(key)
        if (cacheObject != null) {
            /**
             * 如果类型不同，便重新加入到缓存中
             */
            if (period != cacheObject.period) {
                cacheObject.period = period
                /**
                 * 移除老的value
                 */
                removeInvalidData(key)
                /**
                 * 重新putvalue
                 */
                cacheMap[key] = cacheObject
            }
        } else {
            cacheObject = CacheObject(data, period)
            cacheMap[key] = cacheObject
        }
    }

    /**
     * 获取cache
     *
     * @param key
     * @return
     */
    fun getData(key: String): CacheObject? {
        val cacheObject = cacheMap[key]
        if (cacheObject != null) {
            /**
             * 判断缓存是否过期
             */
            if (cacheObject.isValid) {
                return cacheObject
            } else {
                removeInvalidData(key)
            }
        }
        return null
    }

    /**
     * 移除过期的key
     *
     * @param key
     */
    fun removeInvalidData(key: String) {
        cacheMap.remove(key)
    }
}
