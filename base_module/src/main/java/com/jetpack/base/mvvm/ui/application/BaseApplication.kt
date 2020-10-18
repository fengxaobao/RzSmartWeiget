package com.jetpack.base.mvvm.ui.application

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.jetpack.base.sdk.SdkManager
import com.rz.command.net.NetStateReceiver
import kotlin.properties.Delegates

open abstract class BaseApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //初始化sdk
        SdkManager(instance.applicationContext)

    }

    override fun onLowMemory() {
        super.onLowMemory()
        NetStateReceiver.unRegisterNetworkStateReceiver(this)
        System.gc()
    }


    //单例化的第二种方式：利用系统自带的Delegates生成委托属性
    companion object {
        private var instance: BaseApplication by Delegates.notNull()
        fun instance() = instance
    }
}