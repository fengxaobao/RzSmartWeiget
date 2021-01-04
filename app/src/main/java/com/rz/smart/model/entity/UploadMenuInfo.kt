package com.rz.smart.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * 作者：iss on 2020/10/18 10:42
 * 邮箱：55921173@qq.com
 * 类备注：
 * {"WarehouseID":1,
"WarehouseName ":"仓库1 ",
"Level":1,
" HigherLevel":1
}
 */
class UploadMenuInfo {
    var WarehouseID:Int = 0
    var WarehouseName:String ?= null
    var Level:Int = 0
    var HigherLevel:Int = 0
}