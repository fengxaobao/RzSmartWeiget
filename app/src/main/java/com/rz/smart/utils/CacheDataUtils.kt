package com.rz.smart.utils

import com.rz.smart.model.entity.UploadMenuInfo

object CacheDataUtils{
    var TOKEN:String?=null

//    var WarehouseName
    /**
     * 供应商集合
     */
    var WARE_HOUSE_NAME_LIST: List<UploadMenuInfo> = mutableListOf()

    var USERNAME1: String? = null
    var PASSWORD1: String? = null

    var USERNAME2: String? = null

}