package com.jetpack.base.mvvm.ui.activity

import android.os.Bundle
import android.view.WindowManager
import com.rz.command.net.NetChangeObserver
import com.rz.command.net.NetStateReceiver
import com.rz.command.net.RxNetworkUtil
import com.rz.utils.RxBarTool
import com.rz.utils.RxPermissionUtil
import me.yokeyword.fragmentation.SupportActivity

open abstract class BaseActivity : SupportActivity() {
    protected var isConnectNetWork = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewMode()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVMContextView()
        initTitleBar()
        initNetworkStatusListener()
        if (RxPermissionUtil.hasAllPermission(this)) {
            scheduleLogic()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        RxBarTool.setStickFullScreen(window.decorView)
        RxBarTool.hideBottomMenu(this)
    }

    private fun initNetworkStatusListener() {
        val mNetChangeObserver = object : NetChangeObserver {
            override fun onNetConnected(type: RxNetworkUtil.NetType?) {
                isConnectNetWork = true
                onNetworkConnected(type)
            }

            override fun onNetDisConnect() {
                isConnectNetWork = false
                onNetworkDisConnected()
            }

        }

        //开启广播去监听 网络 改变事件
        NetStateReceiver.registerObserver(mNetChangeObserver)
    }

    /**
     * 网络连接状态
     *
     * @param type 网络状态
     */
    abstract fun onNetworkConnected(type: RxNetworkUtil.NetType?)

    /**
     * 网络断开的时候调用
     */
    open fun onNetworkDisConnected() {

    }

    abstract fun setVMContextView()

    abstract fun initTitleBar()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        scheduleLogic()
    }

    /**
     * 处理业务量逻辑
     */
    abstract fun scheduleLogic()

    abstract fun getChildLayoutView(): Int

    abstract fun bindViewMode()

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}