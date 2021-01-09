package com.rz.smart.service

import com.jetpack.base.sdk.net.http.BaseUrlConfig
import com.rz.smart.BuildConfig

/**
 * mike.feng
 * 2018/10/26.
 */
object BmnBaseUrlConfig : BaseUrlConfig() {
    //闪送员text环境 web接口
    init {
        initServerHost(BuildConfig.SERVER_CFG)
    }

    override fun UrlKey(): String = "O9bXoVrIXd1498617048B10VuB7K88F"

    override fun getFormatUrl(): String {
        return "http://40.72.98.199:8081/API/EWCApi/"
    }

    override fun getTestUrl(): String {
        return "http://40.72.98.199:8081/API/EWCApi/"
    }
}
