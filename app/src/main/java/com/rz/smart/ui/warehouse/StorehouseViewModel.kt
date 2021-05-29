package com.rz.smart.ui.warehouse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.jetpack.base.mvvm.vm.BaseViewModel

class StorehouseViewModel :BaseViewModel(BaseApplication.instance())  {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
}