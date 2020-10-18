package com.jetpack.base.sdk.net.cache

/**
 * mike.feng
 * 2018/11/23.
 * 一般数的来源有五种
 * 1.网络请求（push pull两种方式）
 * 2.数据库读取
 * 3.sp
 * 4.File
 * 5.本地资源
 */
interface ISpDataModel<T> {

    fun getSp(mode: String): T

    fun setSp(mode: String)

}
