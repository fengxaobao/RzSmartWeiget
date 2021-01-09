package com.rz.smart.repository

import com.jetpack.base.mvvm.bean.BaseResponse
import com.jetpack.base.mvvm.bean.Results

class LoginRepository(val smart:SmartRepository) {

    suspend fun login(operator1Name: String, password1: String, operator2Name: String, password2: String): Results<BaseResponse<Any>> {
       return  smart.SysUserLogin(operator1Name,password1,operator2Name,password2)
    }

}