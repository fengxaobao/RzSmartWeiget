package com.jetpack.base.mvvm.bean

/**
 * mike.feng
 * {
"Status":"0",
"Msg":"ok",
"Sign":"3AE65B3606169D797DCB6F75EEF4ACAD",
"Count":100,
"Data":[{
"EmpName":"0014",
"EmpCode":"0001",
"EmpId":1,
"CardNo":"201700",
"Status":0,
}
]
}
 */

data class BaseResponse<out T>(
    val Status: Int,
    val Message: String,
    val Data: T? = null,
    val Sign: String? = null
)