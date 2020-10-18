package com.rz.smart.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 作者：iss on 2020/10/18 10:42
 * 邮箱：55921173@qq.com
 * 类备注：
 */
 class UploadMenuInfo {
    @Expose
    @SerializedName("F_No")
    private val F_No: String? = null

    @Expose
    @SerializedName("F_UnitPrice")
    private val F_UnitPrice = 0

    @Expose
    @SerializedName("F_Money")
    private val F_Money = 0

    @Expose
    @SerializedName("F_Weight")
    private val F_Weight: String? = null

    @Expose
    @SerializedName("F_Name")
    private val F_Name: String? = null

    @Expose
    @SerializedName("F_GoodId")
    private val F_GoodId: String? = null
}