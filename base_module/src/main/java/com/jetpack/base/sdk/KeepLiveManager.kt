package com.jetpack.base.sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import com.orhanobut.logger.Logger
import com.rz.keeplive.KeepLive
import com.rz.keeplive.config.ForegroundNotification
import com.rz.keeplive.config.ForegroundNotificationClickListener
import com.rz.keeplive.config.KeepLiveService

object KeepLiveManager {

    /**
     * @param context Application
     * @param iconRes 通知栏的图片Id
     */
    fun startKeepLiveSdk(context: Application, iconRes: Int, title: String, message: String) {
        KeepLive.startWork(
            context,
            KeepLive.RunMode.ENERGY,
            ForegroundNotification(title, message,
                iconRes, object : ForegroundNotificationClickListener {
                    override fun foregroundNotificationClick(context: Context, intent: Intent) {
                        //点击通知回调
                        Logger.d("点击了通知栏 foregroundNotificationClick")

                    }
                }),
            object : KeepLiveService {
                override fun onStop() {
                    //可能调用多次，跟onWorking匹配调用
                    Logger.d("服务关闭了 onStop")

                }

                override fun onWorking() {
                    Logger.d("服务开启了 onWorking")
                    //一直存活，可能调用多次
                }
            })
    }
}