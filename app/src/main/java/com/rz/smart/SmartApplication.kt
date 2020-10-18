package com.rz.smart

import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.orhanobut.logger.Logger
import com.rz.smart.model.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by kqw on 2016/10/26.
 * InitApplication
 */
class SmartApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        Logger.d("BusApplication onCreate")
        startKoin {
            androidContext(this@SmartApplication)
            androidLogger()
            modules(appModule)
        }
    }
}