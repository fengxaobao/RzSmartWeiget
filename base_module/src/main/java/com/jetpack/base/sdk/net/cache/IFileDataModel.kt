package com.jetpack.base.sdk.net.cache

import com.jetpack.base.mvvm.bean.BaseEntity

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
interface IFileDataModel<T> {

    fun <D : BaseEntity> saveFileContext(d: D, filePath: String): Boolean

    fun getFileContext(filePath: String): T

}
