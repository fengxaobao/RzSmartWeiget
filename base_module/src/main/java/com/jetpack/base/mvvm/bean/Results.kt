package com.jetpack.base.mvvm.bean

/**
 * Created by luyao
 * on 2019/10/12 11:08
 */
sealed class Results<out T : Any?> {

    data class Success<out T : Any>(val data: T?) : Results<T>()
    data class Error(val exception: Exception) : Results<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }

}