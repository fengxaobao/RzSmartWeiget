package com.rz.smart.repository


import com.jetpack.base.mvvm.bean.BaseResponse
import com.jetpack.base.mvvm.bean.Results
import com.jetpack.base.mvvm.repository.BaseNetRepository
import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.rz.bmn.service.SmartService
import com.rz.smart.R
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import com.rz.smart.service.BMNRetrofitClient
import com.rz.smart.service.BmnBaseUrlConfig


class SmartRepository : BaseNetRepository() {
    var service: SmartService = BMNRetrofitClient.service

    suspend fun GetWarehouseData(EquipmentID: String): Results<BaseResponse<List<UploadMenuInfo>>> {
        return safeApiCall(
            call = { requestGetWarehouseData(EquipmentID) },
            errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }


    private suspend fun requestGetWarehouseData(EquipmentID: String): Results<BaseResponse<List<UploadMenuInfo>>> {
        val hash = getMutableMaps()
        hash.put("EquipmentID", EquipmentID)
        val sign = getSign(map = hash)
        val response = service.GetWarehouseData("04", EquipmentID, sign)
        return executeAnyResponse(response)
    }


    suspend fun getGoodsData(EquipmentID: String): Results<BaseResponse<List<CuisineInfo>>> {
        return safeApiCall(
            call = { requestGetGoodsData(EquipmentID) },
            errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }

    /**
    @Query("EquipmentID") EquipmentID: String,
    @Query("Sign") Sign: String
     */

    private suspend fun requestGetGoodsData(EquipmentID: String): Results<BaseResponse<List<CuisineInfo>>> {
        val hash = getMutableMaps()
        hash.put("EquipmentID", EquipmentID)
        val sign = getSign(map = hash)
        val response = service.GetGoodsData("04", EquipmentID, sign)
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
    suspend fun setGoodsWeight(EquipmentID: String,OperatorName:String,GoodsID:String
    ,SupplierID:String,GoodsWeight:String,WarehouseID:String,Remark:String): Results<BaseResponse<Any>> {
        return safeApiCall(
            call = { requestSetGoodsWeight(EquipmentID,OperatorName,GoodsID,SupplierID,GoodsWeight,WarehouseID,Remark) },
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
        WarehouseID: String,
        Remark: String
    ): Results<BaseResponse<Any>> {
        val hash = getMutableMaps()
        hash.put("EquipmentID", EquipmentID)
        hash.put("OperatorName", OperatorName)
        hash.put("GoodsID", GoodsID)
        hash.put("SupplierID", SupplierID)
        hash.put("GoodsWeight", GoodsWeight)
        hash.put("WarehouseID", WarehouseID)
        hash.put("Remark", Remark)
        val sign = getSign(map = hash)
        val response = service.SetGoodsWeight("04", EquipmentID, OperatorName,GoodsID,SupplierID,GoodsWeight
        ,WarehouseID,Remark,sign)
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
        hash.put("Code", "04")
        hash.put("Operator1Name", Operator1Name)
        hash.put("Password1", Password1)
        hash.put("Operator2Name", Operator2Name)
        hash.put("Password2", Password2)
        val sign = getSign(map = hash)
        val response =
            service.SysUserLogin("04", Operator1Name, Password1, Operator2Name, Password2, sign)
        return executeAnyResponse(response)
    }

    override fun getUlrKey(): String = BmnBaseUrlConfig.UrlKey()

}

