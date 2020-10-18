package com.jetpack.base.mvvm.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import okhttp3.internal.wait

open abstract class BaseViewModel(var app: Application) : AndroidViewModel(app) {


    open class UiState<T>(
        val isLoading: Boolean = false,
        val data: T? = null,
        val isError: String? = null
    )


    open class RefreshUiResultModel<T>(
        var showLoading: Boolean = false,
        var showErrorView: Boolean = false,
        var showEmptyView: Boolean = false,
        var isRefresh: Boolean = false,//是否需要重新刷新
        var isRefreshFinish: Boolean = false,//是否完成刷新
        var data: T? = null
    )

    val mException: MutableLiveData<Throwable> = MutableLiveData()


    /**
     * 创建携程
     */
    fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    /**
     * 在指定的当前协程运行
     */
    suspend fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        withContext(Dispatchers.IO) {
            block
        }

    }

    /**
     * 在指定的当前协程运行
     */
    suspend fun <T> launchOnAsync(block: suspend CoroutineScope.() -> T) {
        GlobalScope.async { block.wait() }
    }

}