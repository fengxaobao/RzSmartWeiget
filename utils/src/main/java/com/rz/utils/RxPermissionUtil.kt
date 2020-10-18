package com.rz.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.collection.SimpleArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * Created by yf on 2016/7/22 0022.
 */
object RxPermissionUtil {
    val REQUEST_SHOWCAMERA = 0
    val REQUEST_READ_EXTERNAL_STORAGE = 1
    val REQUEST_RECORD_AUDIO = 2
    val REQUEST_CONTACTS = 3
    val REQUEST_LOCATION = 4


    private val MIN_SDK_PERMISSIONS: SimpleArrayMap<String, Int>

    init {
        MIN_SDK_PERMISSIONS = SimpleArrayMap(8)
        MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14)
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20)
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16)
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16)
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9)
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16)
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23)
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23)
    }

    private fun permissionExists(permission: String): Boolean {
        val minVersion = MIN_SDK_PERMISSIONS.get(permission)
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion
    }

    fun hasAllPermission(activity: Activity): Boolean {
        var permissionList = arrayListOf<String>()
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionList.add(Manifest.permission.CAMERA)
        permissionList.add(Manifest.permission.CALL_PHONE)
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionList.add(Manifest.permission.READ_PHONE_STATE)
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var needPermission = arrayListOf<String>()
        for (permission in permissionList) {
            val hasPermission = ContextCompat.checkSelfPermission(
                activity,
                permission
            )
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                needPermission.add(permission)
            }
        }
        if (needPermission.size > 0) {
            ActivityCompat.requestPermissions(
                activity,
                needPermission.toTypedArray(),
                RxPermissionUtil.REQUEST_SHOWCAMERA
            )
            return false
        }
        return true
    }

    fun hasCameraPermission(activity: Activity): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        )
        if (!permissionExists(Manifest.permission.CAMERA)) {
            Log.e(
                "permission",
                "your system does not suppport" + Manifest.permission.CAMERA + " permission"
            )
            return false
        }
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                RxPermissionUtil.REQUEST_SHOWCAMERA
            )
            return false
        }
        return true
    }

    fun hasReadExternalStoragePermission(activity: Activity): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (!permissionExists(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Log.e(
                "permission",
                "your system does not suppport " + Manifest.permission.READ_EXTERNAL_STORAGE + " permission"
            )
            return false
        }
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                RxPermissionUtil.REQUEST_READ_EXTERNAL_STORAGE
            )
            return false
        }
        return true
    }

    fun hasRecordAudioPermission(activity: Activity): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        )
        if (!permissionExists(Manifest.permission.RECORD_AUDIO)) {
            Log.e(
                "permission",
                "your system does not suppport" + Manifest.permission.RECORD_AUDIO + " permission"
            )
            return false
        }
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RxPermissionUtil.REQUEST_RECORD_AUDIO
            )
            return false
        }
        return true
    }

    fun hasContactsPermission(activity: Activity): Boolean {
        val hasWPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_CONTACTS
        )
        val hasRPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_CONTACTS
        )

        if (hasRPermission == PackageManager.PERMISSION_GRANTED && hasWPermission == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS),
            RxPermissionUtil.REQUEST_CONTACTS
        )
        return false
    }

    fun hasLocationPermission(activity: Activity): Boolean {
        val hasFPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (hasFPermission == PackageManager.PERMISSION_GRANTED && hasCPermission == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            RxPermissionUtil.REQUEST_LOCATION
        )
        return false
    }

}
