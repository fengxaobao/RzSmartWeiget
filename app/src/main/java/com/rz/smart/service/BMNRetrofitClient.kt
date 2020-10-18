package com.rz.smart.service

import com.jetpack.base.sdk.net.http.RetrofitClient
import com.rz.bmn.service.SmartService

object BMNRetrofitClient : RetrofitClient() {
    val service by lazy { getService(SmartService::class.java, BmnBaseUrlConfig.URL_SERVER) }
}