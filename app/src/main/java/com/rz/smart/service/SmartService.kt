package com.rz.bmn.service


import com.jetpack.base.mvvm.bean.BaseResponse
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 作者：iss on 2020/6/1 10:56
 * 邮箱：55921173@qq.com
 * 类备注：
 * {"Code":"3","PageNo":1,"PageCount":20,"Sign":"0E09BC183E20DAD8A0787AD7B147931F"}
 */
interface SmartService {

    @POST("Home/GetData")
    suspend fun getAllCuisine(
        @Query("Code") Code: String,
        @Query("Parament") Parament: Map<String, String>
    ): BaseResponse<List<CuisineInfo>>

    @POST("Home/GetData")
    suspend fun UpLoadList(
        @Query("Code") Code: String,
        @Query("Parament") Parament: String
    ): BaseResponse<List<UploadMenuInfo>>
}