package com.rz.smart.repository


import com.jetpack.base.mvvm.bean.BaseResponse
import com.jetpack.base.mvvm.bean.Results
import com.jetpack.base.mvvm.repository.BaseNetRepository
import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.rz.bmn.service.SmartService
import com.rz.smart.R
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import com.rz.smart.model.request.GoodDataRequest
import com.rz.smart.model.request.GoodsWeightRequest
import com.rz.smart.model.request.LoginRequest
import com.rz.smart.service.BMNRetrofitClient
import com.rz.smart.service.BmnBaseUrlConfig
import com.rz.smart.utils.CacheDataUtils
import com.rz.utils.RxDeviceTool


class SmartRepository : BaseNetRepository() {
    var service: SmartService = BMNRetrofitClient.service

    suspend fun GetWarehouseData(): Results<BaseResponse<List<UploadMenuInfo>>> {
        return safeApiCall(
                call = { requestGetWarehouseData() },
                errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }


    private suspend fun requestGetWarehouseData(): Results<BaseResponse<List<UploadMenuInfo>>> {
        val hash = getMutableMaps()
        hash.put("Code", "04")
        hash.put("EquipmentID", RxDeviceTool.serialNumber!!)
        hash.put("Token", CacheDataUtils.TOKEN!!)
        val sign = getSign(map = hash)
        val response = service.GetWarehouseData(GoodDataRequest("04", RxDeviceTool.serialNumber!!, sign, CacheDataUtils.TOKEN!!))
        return executeAnyResponse(response)
    }


    suspend fun getGoodsData(): Results<BaseResponse<List<CuisineInfo>>> {
        return safeApiCall(
                call = { requestGetGoodsData() },
                errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }

    /**
    @Query("EquipmentID") EquipmentID: String,
    @Query("Sign") Sign: String
     */

    private suspend fun requestGetGoodsData(): Results<BaseResponse<List<CuisineInfo>>> {
        val hash = getMutableMaps()
        hash.put("Code", "04")
        hash.put("EquipmentID", RxDeviceTool.serialNumber!!)
        hash.put("Token", CacheDataUtils.TOKEN!!)
        val sign = getSign(map = hash)
        val response = service.GetGoodsData(
                GoodDataRequest("04", RxDeviceTool.serialNumber!!, sign, CacheDataUtils.TOKEN!!)
        )
        return executeAnyResponse(response)
    }


    /**
     *   @Query("Code") Code: String,
    @Query("EquipmentID") EquipmentID: String,
    @Query("OperatorName") OperatorName: String,
    @Query("GoodsID") GoodsID: String,
    @Query("SupplierID") SupplierID: String,
    @Query("GoodsWeight") GoodsWeight: String,
    @Query("WarehouseID") WarehouseID: String,
    @Query("Remark") Remark: String,
    @Query("Sign") Sign: String
     */
    suspend fun setGoodsWeight(
            EquipmentID: String,
            OperatorName: String,
            GoodsID: String,
            SupplierID: String,
            GoodsWeight: String,
            GoodsAmount: String,
            WarehouseID: String,
            Remark: String
    ): Results<BaseResponse<Any>> {
        return safeApiCall(
                call = {
                    requestSetGoodsWeight(
                            EquipmentID,
                            OperatorName,
                            GoodsID,
                            SupplierID,
                            GoodsWeight,
                            GoodsAmount,
                            WarehouseID,
                            Remark
                    )
                },
                errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }

    /**
    @Query("EquipmentID") EquipmentID: String,
    @Query("Sign") Sign: String
     */

    private suspend fun requestSetGoodsWeight(
            EquipmentID: String,
            OperatorName: String,
            GoodsID: String,
            SupplierID: String,
            GoodsWeight: String,
            GoodsAmount: String,
            WarehouseID: String,
            Remark: String
    ): Results<BaseResponse<Any>> {
        val hash = getMutableMaps()
        hash.put("Code", "04")
        hash.put("EquipmentID", EquipmentID)
        hash.put("OperatorName", OperatorName)
        hash.put("GoodsID", GoodsID)
        hash.put("SupplierID", SupplierID)
        hash.put("GoodsWeight", GoodsWeight)
        hash.put("GoodsAmount", GoodsAmount)
        hash.put("WarehouseID", WarehouseID)
        hash.put("Remark", Remark)
        val sign = getSign(map = hash)
        val response = service.SetGoodsWeight(
                GoodsWeightRequest(
                        "04",
                        EquipmentID,
                        OperatorName,
                        GoodsID,
                        SupplierID,
                        GoodsWeight,
                        GoodsAmount,
                        WarehouseID,
                        Remark,
                        sign,
                        CacheDataUtils.TOKEN!!
                )
        )
        return executeAnyResponse(response)
    }


    suspend fun SysUserLogin(
            Operator1Name: String, Password1: String, Operator2Name: String,
            Password2: String
    ): Results<BaseResponse<Any>> {
        return safeApiCall(
                call = { requestSysUserLogin(Operator1Name, Password1, Operator2Name, Password2) },
                errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }

    private suspend fun requestSysUserLogin(
            Operator1Name: String, Password1: String, Operator2Name: String,
            Password2: String
    ): Results<BaseResponse<Any>> {
        val hash = getMutableMaps()
        hash["Code"] = "04"
        hash["Operator1Name"] = Operator1Name
        hash["Password1"] = Password1
        hash["Operator2Name"] = Operator2Name
        hash["Password2"] = Password2
        val sign = getSign(map = hash)
        val response =
                service.SysUserLogin(
                        LoginRequest(
                                "04", Operator1Name, Password1, Operator2Name,
                                Password2, sign
                        )
                )
        return executeAnyResponse(response)
    }

    override fun getUlrKey(): String = BmnBaseUrlConfig.UrlKey()

}

