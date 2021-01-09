package com.rz.smart.ui.login.fragment

import com.jetpack.base.mvvm.checkResult
import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.jetpack.base.mvvm.vm.BaseViewModel
import com.rz.smart.repository.LoginRepository
import com.rz.smart.utils.CacheDataUtils
import es.dmoral.toasty.Toasty
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData

class LoginViewModel(var loginRepository: LoginRepository) : BaseViewModel(BaseApplication.instance()) {

    var userLoginSuccess = BooleanLiveData()

    var userName : String = ""
    var userPwd : String = ""

    /**
     * Operator1Name	String	*	操作人1
    Password1	String	*	操作人1密码
    Operator2Name	String	*	操作人2
    Password2	String	*	操作人2密码
     */
    fun login(Operator1Name:String,Password1:String,Operator2Name:String,Password2:String){
       launchOnUI {
          val result=  loginRepository.login(Operator1Name,Password1,Operator2Name,Password2)
           result.checkResult({
               if (it?.Status== 0) {
                   CacheDataUtils.TOKEN = it?.Token
                   Toasty.success(app,"登录成功",Toasty.LENGTH_LONG).show()
                   userLoginSuccess.postValue(true)
               }else{
                   Toasty.error(app,"登录失败",Toasty.LENGTH_LONG).show()
                   userLoginSuccess.postValue(false)
               }
           },{
               Toasty.error(app,"登录失败",Toasty.LENGTH_LONG).show()
           })
       }
    }
}