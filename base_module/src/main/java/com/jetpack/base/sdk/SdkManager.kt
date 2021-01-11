package com.jetpack.base.sdk

import EmailLogEvent
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Environment
import android.os.Looper
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatDelegate
import com.jetpack.base.BuildConfig
import com.orhanobut.logger.*
import com.rz.command.LoggerHandler
import com.rz.command.net.NetStateReceiver
import com.rz.utils.RxDeviceTool
import com.rz.utils.RxFileTool
import com.rz.utils.RxTimeTool
import com.rz.utils.RxTool
import com.rz.utils.sp.GlobalPreference
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshInitializer
import com.squareup.leakcanary.LeakCanary
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.entity.UMessage
import com.umeng.message.tag.TagManager
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import me.yokeyword.fragmentation.Fragmentation
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.concurrent.TimeUnit


class SdkManager constructor(var context: Context) {


    init {
        initSdk()
    }

    private fun initSdk() {
        initToasty()
//        initUmeng()
        initLogAdapter()
        initLeakCanary(context)
        initNetworkListener()
        GlobalPreference.init(context)
        RxTool.init(context)
        initFragmentation(context)

        initSmartRefresh(context)
    }

    private fun initSmartRefresh(context: Context) {
        //启用矢量图兼容

        //启用矢量图兼容
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        //设置全局默认配置（优先级最低，会被其他设置覆盖）
        SmartRefreshLayout.setDefaultRefreshInitializer(object : DefaultRefreshInitializer {
            override fun initialize(
                @NonNull context: Context,
                @NonNull layout: RefreshLayout
            ) {
                //全局设置（优先级最低）
                layout.setEnableAutoLoadMore(true)
                layout.setEnableOverScrollDrag(false)
                layout.setEnableOverScrollBounce(true)
                layout.setEnableLoadMoreWhenContentNotFull(true)
                layout.setEnableScrollContentWhenRefreshed(true)
                layout.setFooterMaxDragRate(4.0f)
                layout.setFooterHeight(45.toFloat())
            }
        })
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setEnableHeaderTranslationContent(true)
            ClassicsHeader(context).setTimeFormat(RxTimeTool.DEFAULT_SDF)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(
                context
            )
        }
    }

    /**
     * fragmentation 初始化
     *
     * @param context
     */
    private fun initFragmentation(context: Context) {
        Fragmentation.builder() // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(BuildConfig.DEBUG) // 实际场景建议.debug(BuildConfig.DEBUG)
            /**
             * 可以获取到[me.yokeyword.fragmentation.exception.AfterSaveStateTransactionWarning]
             * 在遇到After onSaveInstanceState时，不会抛出异常，会回调到下面的ExceptionHandler
             */
            .handleException {
                // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                // Bugtags.sendException(e);
            }
            .install()
    }

    private fun initNetworkListener() {
        //动态注册网络变化广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //实例化IntentFilter对象
            val filter = IntentFilter()
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            val netBroadcastReceiver = NetStateReceiver()
            //注册广播接收
            context.registerReceiver(netBroadcastReceiver, filter)
        }
        /*开启网络广播监听*/
        NetStateReceiver.registerNetworkStateReceiver(context)
    }

    private fun initLeakCanary(context: Context) {
        if (BuildConfig.DEBUG) {
            try {
                if (LeakCanary.isInAnalyzerProcess(context.applicationContext)) {
                    Logger.i("初始化成功")
                    return
                }
                LeakCanary.install(context as Application)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    private fun initLogAdapter() {
        val strategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(1)
            .tag("Bmn")
            .build()
        val diskPath =
            Environment.getExternalStorageDirectory().absolutePath
        val folder = diskPath + File.separatorChar + "logger" + File.separatorChar + "bmn"
        val csvFormat =
            CsvFormatStrategy.newBuilder().logStrategy(
                DiskLogStrategy(
                    LoggerHandler(
                        Looper.getMainLooper(),
                        folder,
                        (500 * 1024).toInt() // 500K averages to a 4000 lines per file
                        ,
                        RxTimeTool.milliseconds2String(
                            RxTimeTool.curTimeMills,
                            RxTimeTool.DEFAULT_YMD
                        )
                    )
                )
            ).build()
        Logger.addLogAdapter(DiskLogAdapter(csvFormat))
        Logger.addLogAdapter(AndroidLogAdapter(strategy))
        //删除3天前的日志
        try {
            Observable.timer(5, TimeUnit.MINUTES).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    var threeDay = 1000 * 3600 * 24 * 3
                    RxFileTool.listFilesInDir(folder)?.forEach {
                        val substr = it.name.toString().substring(0, 10)
                        var time = RxTimeTool.string2Date(substr, RxTimeTool.DEFAULT_YMD).time
                        if (RxTimeTool.curTimeMills - time > threeDay) {
                            RxFileTool.deleteFile(it)
                        }
                    }
                }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
     * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
     * UMConfigure.init调用中appkey和channel参数请置为null）。
     */
    private fun initUmeng() {
//        UMConfigure.init(
//            context,
//            BuildConfig.UEMNG_KEY,
//            "umeng",
//            UMConfigure.DEVICE_TYPE_PHONE,
//            BuildConfig.UEMNG_MESSAGE_SECRET
//        )
//        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
//        // 支持在子进程中统计自定义事件
//        UMConfigure.setProcessEvent(true)
//
//        //获取消息推送代理示例
//        //获取消息推送代理示例
//        val mPushAgent = PushAgent.getInstance(context)
////注册推送服务，每次调用register方法都会回调该接口
////注册推送服务，每次调用register方法都会回调该接口
//        mPushAgent.register(object : IUmengRegisterCallback {
//            override fun onSuccess(deviceToken: String) {
//                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
//                Logger.i("注册成功：deviceToken：-------->  $deviceToken")
//            }
//
//            override fun onFailure(s: String, s1: String) {
//                Logger.i("注册失败：-------->  s:$s,s1:$s1")
//            }
//        })
//        mPushAgent.setNotificaitonOnForeground(false)
//        mPushAgent.displayNotificationNumber = 1
//        val devices = GlobalPreference.get(GlobalPreference.KEY.DEVICES_APP_NAME, "")
//        val sn = RxDeviceTool.getSN()!!
//
//        var messageHandler: UmengMessageHandler = object : UmengMessageHandler() {
//            override fun dealWithCustomMessage(
//                context: Context,
//                msg: UMessage
//            ) {
//                if (msg.custom == devices) {
//                    Logger.e("上报日志")
//                    EventBus.getDefault().post(EmailLogEvent())
//                }
//            }
//        }
//        mPushAgent.messageHandler = messageHandler
//        mPushAgent.tagManager.addTags(TagManager.TCallBack { isSuccess, result ->
//            if (isSuccess) {
//                Logger.e("添加tag成功")
//            }
//        }, devices, sn)

    }

    private fun initToasty() {
        Toasty.Config.getInstance()
            .tintIcon(true) // optional (apply textColor also to the icon)
            .setTextSize(14) // optional
            .allowQueue(true) // optional (prevents several Toastys from queuing)
            .apply() // required

    }
}