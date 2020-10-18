package com.rz.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.*

/**
 *
 * @author Vondear
 * @date 2017/8/18
 */

object RxPermissionsTool {

    fun with(activity: Activity): RxPermissionsTool.Builder {
        return Builder(activity)
    }

    class Builder(private val mActivity: Activity) {
        private val permissionList: MutableList<String>

        init {
            permissionList = ArrayList()
        }

        /**
         * Determine whether *you* have been granted a particular permission.
         *
         * @param permission The name of the permission being checked.
         * @return [PackageManager.PERMISSION_GRANTED] if you have the
         * permission, or [PackageManager.PERMISSION_DENIED] if not.
         * @see PackageManager.checkPermission
         */
        fun addPermission(permission: String): Builder {
            if (!permissionList.contains(permission)) {
                permissionList.add(permission)
            }
            return this
        }

        fun initPermission(): List<String> {
            val list = ArrayList<String>()
            for (permission in permissionList) {
                if (ActivityCompat.checkSelfPermission(
                        mActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    list.add(permission)
                }
            }
            if (list.size > 0) {
                ActivityCompat.requestPermissions(mActivity, list.toTypedArray(), 1)
            }
            return list
        }

    }


}
