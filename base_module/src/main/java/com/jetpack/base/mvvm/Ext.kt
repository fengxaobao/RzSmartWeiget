package com.jetpack.base.mvvm

import com.jetpack.base.mvvm.bean.Results

/**
 * Created by luyao
 * on 2020/3/30 16:19
 */
inline fun <T : Any> Results<T>.checkResult(success: (T?) -> Unit, error: (String?) -> Unit) {
    if (this is Results.Success) {
        success(data)
    } else if (this is Results.Error) {
        error(exception.message)
    }
}

inline fun <T : Any> Results<T>.checkSuccess(success: (T?) -> Unit) {
    if (this is Results.Success) {
        success(data)
    }
}