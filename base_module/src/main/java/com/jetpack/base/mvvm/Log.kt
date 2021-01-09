/**
 * 作者：iss on 2020/11/11 01:15
 * 邮箱：55921173@qq.com
 * 类备注：
 */
package com.jetpack.base.mvvm

import com.orhanobut.logger.Logger


fun String.logD() {
    Logger.d(this)
}

fun Any.logD() {
    Logger.d(this.toString())
}

fun Any.logE() {
    Logger.e(this.toString())
}

fun Any.logI() {
    Logger.i(this.toString())
}

fun Any.log2Json() {
    Logger.json(this.toString())
}

fun Any.logV() {
    Logger.v(this.toString())
}

fun Any.logW() {
    Logger.w(this.toString())
}