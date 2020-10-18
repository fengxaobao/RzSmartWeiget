package com.rz.command.net

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.rz.command.net.RxNetworkUtil.NetType
import java.util.*

/**
 * 使用广播去监听网络
 * Created by 邓鉴恒 on 16/9/13.
 */
class NetStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        mBroadcastReceiver = this@NetStateReceiver
        if (intent.action.equals(
                ANDROID_NET_CHANGE_ACTION,
                ignoreCase = true
            ) || intent.action.equals(
                CUSTOM_ANDROID_NET_CHANGE_ACTION,
                ignoreCase = true
            )
        ) {
            if (!RxNetworkUtil.isNetworkAvailable(context)) {
                Log.e(this.javaClass.name, "<--- network disconnected --->")
                isNetworkAvailable = false
            } else {
                Log.e(this.javaClass.name, "<--- network connected --->")
                isNetworkAvailable = true
                aPNType = RxNetworkUtil.getAPNType(context)
            }
            notifyObserver()
        }
    }

    private fun notifyObserver() {
        if (!mNetChangeObservers!!.isEmpty()) {
            val size = mNetChangeObservers!!.size
            for (i in 0 until size) {
                val observer =
                    mNetChangeObservers!![i]
                if (observer != null) {
                    if (isNetworkAvailable) {
                        observer.onNetConnected(aPNType)
                    } else {
                        observer.onNetDisConnect()
                    }
                }
            }
        }
    }

    companion object {
        const val CUSTOM_ANDROID_NET_CHANGE_ACTION =
            "com.zhanyun.api.netstatus.CONNECTIVITY_CHANGE"
        private const val ANDROID_NET_CHANGE_ACTION =
            "android.net.conn.CONNECTIVITY_CHANGE"
        private val TAG = NetStateReceiver::class.java.simpleName
        var isNetworkAvailable = false
            private set
        var aPNType: NetType? = null
            private set
        private var mNetChangeObservers: ArrayList<NetChangeObserver>? =
            ArrayList()
        private var mBroadcastReceiver: BroadcastReceiver? = null
        private val receiver: BroadcastReceiver?
            private get() {
                if (null == mBroadcastReceiver) {
                    synchronized(NetStateReceiver::class.java) {
                        if (null == mBroadcastReceiver) {
                            mBroadcastReceiver = NetStateReceiver()
                        }
                    }
                }
                return mBroadcastReceiver
            }

        /**
         * 注册
         *
         * @param mContext
         */
        fun registerNetworkStateReceiver(mContext: Context) {
            val filter = IntentFilter()
            filter.addAction(CUSTOM_ANDROID_NET_CHANGE_ACTION)
            filter.addAction(ANDROID_NET_CHANGE_ACTION)
            mContext.applicationContext
                .registerReceiver(receiver, filter)
        }

        /**
         * 清除
         *
         * @param mContext
         */
        fun checkNetworkState(mContext: Context) {
            val intent = Intent()
            intent.action = CUSTOM_ANDROID_NET_CHANGE_ACTION
            mContext.sendBroadcast(intent)
        }

        /**
         * 反注册
         *
         * @param mContext
         */
        fun unRegisterNetworkStateReceiver(mContext: Context) {
            if (mBroadcastReceiver != null) {
                try {
                    mContext.applicationContext
                        .unregisterReceiver(mBroadcastReceiver)
                } catch (e: Exception) {
                }
            }
        }

        /**
         * 添加网络监听
         *
         * @param observer
         */
        fun registerObserver(observer: NetChangeObserver) {
            if (mNetChangeObservers == null) {
                mNetChangeObservers =
                    ArrayList()
            }
            mNetChangeObservers!!.add(observer)
        }

        /**
         * 移除网络监听
         *
         * @param observer
         */
        fun removeRegisterObserver(observer: NetChangeObserver) {
            if (mNetChangeObservers != null) {
                if (mNetChangeObservers!!.contains(observer)) {
                    mNetChangeObservers!!.remove(observer)
                }
            }
        }
    }
}