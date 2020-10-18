package com.jetpack.base.sdk.net.http


/**
 * mike.feng
 * 2018/10/26.
 */
open abstract class BaseUrlConfig {
    abstract fun UrlKey(): String

    lateinit var URL_SERVER: String

    fun initServerHost(serverCfg: String) {
        if (serverCfg == "test") {
            URL_SERVER = getTestUrl()

        } else if (serverCfg == "format") {
            URL_SERVER = getFormatUrl()
        }
    }

    abstract fun getFormatUrl(): String

    abstract fun getTestUrl(): String

}
