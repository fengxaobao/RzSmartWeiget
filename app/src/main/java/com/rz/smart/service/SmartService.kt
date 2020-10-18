package com.rz.bmn.service


import com.jetpack.base.mvvm.bean.BaseResponse
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST

/**
 * 作者：iss on 2020/6/1 10:56
 * 邮箱：55921173@qq.com
 * 类备注：
 * {"Code":"3","PageNo":1,"PageCount":20,"Sign":"0E09BC183E20DAD8A0787AD7B147931F"}
 */
interface SmartService {

    @POST("Home/GetData")
    suspend fun getAllCuisine(@Field ("Code")Code:String,@Field("Parament")Parament:String): BaseResponse<List<CuisineInfo>>


    //餐卡余额
    @POST("Home/GetData")
    suspend fun UpLoadList(@Field ("Code")Code:String,@Field("Parament")Parament:String): BaseResponse<UploadMenuInfo>
}