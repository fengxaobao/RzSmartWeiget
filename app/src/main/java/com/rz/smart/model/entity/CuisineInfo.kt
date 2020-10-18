package com.rz.smart.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 作者：iss on 2020/10/18 10:05
 * 邮箱：55921173@qq.com
 * 类备注：
 */
 class CuisineInfo {
    @Expose
    @SerializedName("F_DELETEFLAG")
    var F_DELETEFLAG = 0

    @Expose
    @SerializedName("F_DELETEDATE")
    var F_DELETEDATE: String? = null

    @Expose
    @SerializedName("F_DELETEUSERNAME")
    var F_DELETEUSERNAME: String? = null

    @Expose
    @SerializedName("F_DELETEUSERID")
    var F_DELETEUSERID = 0

    @Expose
    @SerializedName("F_UPDATEDATE")
    var F_UPDATEDATE: String? = null

    @Expose
    @SerializedName("F_UPDATEUSERNAME")
    var F_UPDATEUSERNAME: String? = null

    @Expose
    @SerializedName("F_UPDATEUSERID")
    var F_UPDATEUSERID = 0

    @Expose
    @SerializedName("F_INSERTDATE")
    var F_INSERTDATE: String? = null

    @Expose
    @SerializedName("F_INSERTUSERNAME")
    var F_INSERTUSERNAME: String? = null

    @Expose
    @SerializedName("F_INSERTUSERID")
    var F_INSERTUSERID = 0

    @Expose
    @SerializedName("F_ORDER")
    var F_ORDER = 0

    @Expose
    @SerializedName("F_MEMO")
    var F_MEMO: String? = null

    @Expose
    @SerializedName("F_Money")
    var F_Money = 0

    @Expose
    @SerializedName("F_Weight")
    var F_Weight = 0

    @Expose
    @SerializedName("F_TypeID")
    var F_TypeID = 0

    @Expose
    @SerializedName("F_ComparyID")
    var F_ComparyID = 0

    @Expose
    @SerializedName("F_ALIAS")
    var F_ALIAS: String? = null

    @Expose
    @SerializedName("F_NAME")
    var F_NAME: String? = null

    @Expose
    @SerializedName("F_CODE")
    var F_CODE: String? = null

    @Expose
    @SerializedName("F_ID")
    var F_ID = 0
}