package com.rz.smart.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jetpack.base.mvvm.checkResult
import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.jetpack.base.mvvm.vm.BaseViewModel
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import com.rz.smart.repository.SmartRepository
import es.dmoral.toasty.Toasty

class MainViewModel : BaseViewModel(BaseApplication.instance()) {
    var roomRepository: SmartRepository = SmartRepository()
    private val allCuisineInfo = MutableLiveData<List<CuisineInfo>>()
    val allCuisineInfoLiveData: LiveData<List<CuisineInfo>>
        get() = allCuisineInfo

    private val uploadMenuInfo = MutableLiveData<UploadMenuInfo>()
    val uploadMenuInfoLiveData: LiveData<UploadMenuInfo>
        get() = uploadMenuInfo

    fun getAllCuisine() {
        launchOnUI {
            val result = roomRepository.getAllCuisine()
            result.checkResult({
                allCuisineInfo.postValue(it?.MenuList)
            }, {
                it?.let { it1 ->
                    Toasty.error(BaseApplication.instance(), it1).show()
                }
            })
        }
    }

    fun uploadCuisine(GoodId: Long, Weight: Double) {
        launchOnUI {
            val result = roomRepository.uploadCuisine(GoodId, Weight)
            result.checkResult({
                uploadMenuInfo.postValue(it?.UpLoadList)
            }, {
                it?.let { it1 ->
                    Toasty.error(BaseApplication.instance(), it1).show()
                }
            })
        }
    }
}