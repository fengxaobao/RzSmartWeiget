package com.rz.smart.repository


import com.google.gson.JsonObject
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

    suspend fun getAllCuisine(): Results<BaseResponse<List<CuisineInfo>>> {
        return safeApiCall(
            call = { requestAllCuisine() },
            errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }


    private suspend fun requestAllCuisine(): Results<BaseResponse<List<CuisineInfo>>> {
        val hashMap = HashMap<String,String>()
        val response = service.getAllCuisine("02", hashMap)
        return executeAnyResponse(response)
    }

    suspend fun uploadCuisine(goodId: Long, weight: Double): Results<BaseResponse<List<UploadMenuInfo>>>{
        return safeApiCall(
            call = { requestUploadCuisine(goodId,weight) },
            errorMessage = BaseApplication.instance().resources.getString(R.string.net_connection_fail)
        )
    }

    private suspend fun requestUploadCuisine(goodId: Long, weight: Double): Results<BaseResponse<List<UploadMenuInfo>>> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("goodId",goodId)
        jsonObject.addProperty("weight",weight)
        val response = service.UpLoadList("03", jsonObject.toString())
        return executeAnyResponse(response)
    }

    override fun getUlrKey(): String = BmnBaseUrlConfig.UrlKey()

}

