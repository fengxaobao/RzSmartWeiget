package com.jetpack.base.mvvm.repository

import com.jetpack.base.mvvm.bean.BaseResponse
import com.jetpack.base.mvvm.bean.Results
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import java.util.*

/**
 * mike.feng
 */
open class BaseRepository {

    suspend fun <T : Any> apiCall(call: suspend () -> BaseResponse<T>): BaseResponse<T> {
        return call.invoke()
    }

    suspend fun <T : Any> safeApiCall(
        call: suspend () -> Results<T>,
        errorMessage: String
    ): Results<T> {
        return try {
            call()
        } catch (e: Exception) {
            Logger.e(e.message + "")
            Results.Error(IOException(errorMessage, e))
        }
    }

    suspend fun <T : Any> executeResponse(
        response: BaseResponse<T>, successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): Results<T> {
        return coroutineScope {
            Logger.e(response.toString())
            if (response.Status != 0) {
                errorBlock?.let { it() }
                Results.Error(IOException(response.Message))
            } else {
                successBlock?.let { it() }
                //传入的返回类型可以为null
                Results.Success(response.Data)
            }
        }
    }


    suspend fun <T : Any> executeAnyResponse(
        response: T, successBlock: (suspend CoroutineScope.() -> Unit)? = null,
        errorBlock: (suspend CoroutineScope.() -> Unit)? = null
    ): Results<T> {
        return coroutineScope {
            Logger.e(response.toString())
            if (response == null) {
                errorBlock?.let { it() }
                Results.Error(IOException("数据返回失败"))
            } else {
                successBlock?.let { it() }
                Results.Success(response)
            }
        }
    }


    protected fun getMutableMaps(): TreeMap<String, Any> {
        return TreeMap(Comparator<String?> { o1, o2 -> o1!!.compareTo(o2!!) })
    }
}