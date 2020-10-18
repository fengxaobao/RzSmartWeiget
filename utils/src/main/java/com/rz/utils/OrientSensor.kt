package com.rz.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

/**
 * 方向传感器
 */
class OrientSensor(private val context: Context, private val orientCallBack: OrientCallBack) :
    SensorEventListener {
    private var sensorManager: SensorManager? = null
    var accelerometerValues = FloatArray(3)
    var magneticValues = FloatArray(3)

    interface OrientCallBack {
        /**
         * 方向回调
         */
        fun Orient(orient: Int)
    }

    /**
     * 注册加速度传感器和地磁场传感器
     * @return 是否支持方向功能
     */
    fun registerOrient(): Boolean {
        var isAvailable = true
        sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 注册加速度传感器
        if (sensorManager!!.registerListener(
                this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME
            )
        ) {
            Log.i(TAG, "加速度传感器可用！")
        } else {
            Log.i(TAG, "加速度传感器不可用！")
            isAvailable = false
        }

        // 注册地磁场传感器
        if (sensorManager!!.registerListener(
                this, sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME
            )
        ) {
            Log.i(TAG, "地磁传感器可用！")
        } else {
            Log.i(TAG, "地磁传感器不可用！")
            isAvailable = false
        }
        return isAvailable
    }

    /**
     * 注销方向监听器
     */
    fun unregisterOrient() {
        sensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone()
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values.clone()
        }
        val R = FloatArray(9)
        val values = FloatArray(3)
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues)
        SensorManager.getOrientation(R, values)
        var degree = Math.toDegrees(values[0].toDouble()).toInt() //旋转角度
        if (degree < 0) {
            degree += 360
        }
        orientCallBack.Orient(degree)
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) {
    }

    companion object {
        private const val TAG = "OrientSensor"
    }

}