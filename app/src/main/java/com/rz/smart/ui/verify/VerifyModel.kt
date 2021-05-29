package com.rz.smart.ui.verify

import com.jetpack.base.mvvm.checkResult
import com.jetpack.base.mvvm.livedata.livedata.event.EventLiveData
import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.jetpack.base.mvvm.vm.BaseViewModel
import com.rz.smart.repository.SmartRepository
import es.dmoral.toasty.Toasty

/**
 * Created by zhou on 2020/12/31 21:21.
 */
class VerifyModel (var service: SmartRepository): BaseViewModel(BaseApplication.instance()){
    val livedata = EventLiveData<String>()
    fun getEmployeeInfo(){
       val result =  service.getEmpLoyeeInfo()
        result?.checkResult({
            livedata.value = it.toString()
        },{
            Toasty.error(app,"登录失败",Toasty.LENGTH_LONG).show()
        })
    }
    fun login(){

    }
}