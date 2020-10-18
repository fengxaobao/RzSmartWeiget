package com.rz.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import java.util.*

/**
 * 传感器工具类
 */
class SensorUtil private constructor() {
    private var sensorManager: SensorManager? = null
    private var initialOrient = -1 // 初始方向
    private var endOrient = -1 // 转动停止方向
    private var isRotating = false // 是否正在转动
    private var lastDOrient = 0 // 上次方向与初始方向差值
    private val dOrientStack = Stack<Int>() // 历史方向与初始方向的差值栈

    /**
     * 获取传感器管理类的实例
     */
    fun getSensorManager(context: Context): SensorManager? {
        if (sensorManager == null) {
            sensorManager =
                context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        return sensorManager
    }

    /**
     * 打印所有可用传感器
     */
    fun printAllSensor(context: Context) {
        val mSensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList =
            mSensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in sensorList) {
            Log.d(TAG, "所有可用传感器----: " + sensor.name)
        }
    }

    /**
     * 获取手机转动停止的方向
     * @param orient 手机实时方向
     */
    fun getRotateEndOrient(orient: Int): Int {
        if (initialOrient == -1) {
            // 初始化转动
            initialOrient = orient
            endOrient = initialOrient
            Log.i(
                TAG,
                "getRotateEndOrient: 初始化，方向：$initialOrient"
            )
        }
        val currentDOrient = Math.abs(orient - initialOrient) // 当前方向与初始方向差值
        if (!isRotating) {
            // 检测是否开始转动
            lastDOrient = currentDOrient
            if (lastDOrient >= SENSE) {
                // 开始转动
                isRotating = true
            }
        } else {
            // 检测是否停止转动
            if (currentDOrient <= lastDOrient) {
                // 至少累计STOP_COUNT次出现当前方向差小于上次方向差
                val size = dOrientStack.size
                if (size >= STOP_COUNT) {
                    // 只有以前SENSE次方向差距与当前差距的差值都小于等于SENSE，才判断为停止
                    for (i in 0 until size) {
                        if (Math.abs(currentDOrient - dOrientStack.pop()) >= SENSE) {
                            isRotating = true
                            break
                        }
                        isRotating = false
                    }
                }
                if (!isRotating) {
                    // 停止转动
                    dOrientStack.clear()
                    initialOrient = -1
                    endOrient = orient
                    Log.i(
                        TAG,
                        "getRotateEndOrient: ------停止转动，方向：$endOrient"
                    )
                } else {
                    // 正在转动，把当前方向与初始方向差值入栈
                    dOrientStack.push(currentDOrient)
                    Log.i(
                        TAG,
                        "getRotateEndOrient: 正在转动，方向：$orient"
                    )
                }
            } else {
                lastDOrient = currentDOrient
            }
        }
        return endOrient
    }

    companion object {
        private const val TAG = "SensorUtil"

        /**
         * 单例获取
         */
        val instance = SensorUtil() // 单例常量
        private const val SENSE = 10 // 方向差值灵敏度
        private const val STOP_COUNT = 6 // 停止次数

    }
}