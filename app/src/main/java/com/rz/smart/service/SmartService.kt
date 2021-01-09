package com.rz.bmn.service


import com.jetpack.base.mvvm.bean.BaseResponse
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import com.rz.smart.model.request.GoodDataRequest
import com.rz.smart.model.request.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 作者：iss on 2020/6/1 10:56
 * 邮箱：55921173@qq.com
 * 类备注：
 * {"Code":"3","PageNo":1,"PageCount":20,"Sign":"0E09BC183E20DAD8A0787AD7B147931F"}
 */
interface SmartService {

    /**
     * Code	String	*
    EquipmentID	String		设备编号
    Sign	String		Token标识

     */
    @POST("GetGoodsData")
    suspend fun GetGoodsData(
            @Body data: GoodDataRequest
    ): BaseResponse<List<CuisineInfo>>


    //3.获取库房数据接口
    @POST("GetWarehouseData")
    suspend fun GetWarehouseData(
            @Body data: GoodDataRequest
    ): BaseResponse<List<UploadMenuInfo>>

    /**
    上传称重数据接口

     */
    @POST("SetGoodsWeight")
    suspend fun SetGoodsWeight(
            @Query("Code") Code: String,
            @Query("EquipmentID") EquipmentID: String,
            @Query("OperatorName") OperatorName: String,
            @Query("GoodsID") GoodsID: String,
            @Query("SupplierID") SupplierID: String,
            @Query("GoodsWeight") GoodsWeight: String,
            @Query("WarehouseID") WarehouseID: String,
            @Query("Remark") Remark: String,
            @Query("Sign") Sign: String

    ): BaseResponse<Any>

    @POST("SysUserLogin")
    suspend fun SysUserLogin(@Body login: LoginRequest): BaseResponse<Any>
}