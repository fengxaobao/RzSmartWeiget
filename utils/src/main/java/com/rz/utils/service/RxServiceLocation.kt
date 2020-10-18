package com.rz.utils.service

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Looper

import com.rz.utils.RxLocationTool

/**
 * @author vondear
 */
class RxServiceLocation : Service() {

    private var isSuccess: Boolean = false
    private var lastLatitude = "loading..."
    private var lastLongitude = "loading..."
    private var latitude = "loading..."
    private var longitude = "loading..."
    private var country = "loading..."
    private var locality = "loading..."
    private var street = "loading..."
    private var mOnGetLocationListener: OnGetLocationListener? = null
    private val mOnLocationChangeListener = object : RxLocationTool.OnLocationChangeListener {
        override fun getLastKnownLocation(location: Location) {
            lastLatitude = location.latitude.toString()
            lastLongitude = location.longitude.toString()
            if (mOnGetLocationListener != null) {
                mOnGetLocationListener!!.getLocation(
                    lastLatitude,
                    lastLongitude,
                    latitude,
                    longitude,
                    country,
                    locality,
                    street
                )
            }
        }

        override fun onLocationChanged(location: Location) {
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()
            if (mOnGetLocationListener != null) {
                mOnGetLocationListener!!.getLocation(
                    lastLatitude,
                    lastLongitude,
                    latitude,
                    longitude,
                    country,
                    locality,
                    street
                )
            }
            country = RxLocationTool.getCountryName(
                applicationContext,
                java.lang.Double.parseDouble(latitude),
                java.lang.Double.parseDouble(longitude)
            )
            locality = RxLocationTool.getLocality(
                applicationContext,
                java.lang.Double.parseDouble(latitude),
                java.lang.Double.parseDouble(longitude)
            )
            street = RxLocationTool.getStreet(
                applicationContext,
                java.lang.Double.parseDouble(latitude),
                java.lang.Double.parseDouble(longitude)
            )
            if (mOnGetLocationListener != null) {
                mOnGetLocationListener!!.getLocation(
                    lastLatitude,
                    lastLongitude,
                    latitude,
                    longitude,
                    country,
                    locality,
                    street
                )
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }
    }

    fun setOnGetLocationListener(onGetLocationListener: OnGetLocationListener) {
        mOnGetLocationListener = onGetLocationListener
    }

    override fun onCreate() {
        super.onCreate()
        Thread(Runnable {
            Looper.prepare()
            isSuccess =
                RxLocationTool.registerLocation(applicationContext, 0, 0, mOnLocationChangeListener)
            if (isSuccess)
            //                    RxToast.success("init success");
                Looper.loop()
        }).start()
    }

    override fun onBind(intent: Intent): IBinder? {
        return LocationBinder()
    }

    override fun onDestroy() {
        RxLocationTool.unRegisterLocation()
        // 一定要制空，否则内存泄漏
        mOnGetLocationListener = null
        super.onDestroy()
    }

    /**
     * 获取位置监听器
     */
    interface OnGetLocationListener {
        fun getLocation(
            lastLatitude: String, lastLongitude: String,
            latitude: String, longitude: String,
            country: String, locality: String, street: String
        )
    }

    inner class LocationBinder : Binder() {
        val service: RxServiceLocation
            get() = this@RxServiceLocation
    }
}
