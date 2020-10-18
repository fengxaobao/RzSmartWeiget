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
    val F_No: String? = null

    @Expose
    @SerializedName("F_UnitPrice")
    val F_UnitPrice = 0

    @Expose
    @SerializedName("F_Money")
    val F_Money = 0.0

    @Expose
    @SerializedName("F_Weight")
    val F_Weight = 0.0

    @Expose
    @SerializedName("F_Name")
    val F_Name: String? = null

    @Expose
    @SerializedName("F_GoodId")
    val F_GoodId: Long = 0L

}