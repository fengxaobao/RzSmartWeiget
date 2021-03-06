package com.rz.utils

import android.content.Context
import android.content.pm.PackageInfo
import java.util.*

/**
 * @author vondear
 * @date 2018/5/2 15:00:00
 */
object RxPackageManagerTool {

    fun initPackageManager(mContext: Context): List<PackageInfo>? {
        val mPackageManager = mContext.packageManager
        return mPackageManager.getInstalledPackages(0)
    }

    /**
     * 包名是否存在
     *
     * @param mContext    实体
     * @param packageName 待检测 包名
     * @return 结果
     */
    fun haveExistPackageName(mContext: Context, packageName: String): Boolean {
        val packageInfos = initPackageManager(mContext)
        val mPackageNames = ArrayList<String>()
        if (packageInfos != null) {
            for (i in packageInfos.indices) {
                mPackageNames.add(packageInfos[i].packageName)
            }
        }
        return mPackageNames.contains(packageName)
    }
}