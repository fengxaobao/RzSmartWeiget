package com.rz.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast

import java.io.File

/**
 * @author vondear
 * @date 2016/1/24
 */
object RxIntentTool {

    /**
     * 获取安装App(支持7.0)的意图
     *
     * @param context
     * @param filePath
     * @return
     */
    fun getInstallAppIntent(context: Context, filePath: String): Intent? {
        //apk文件的本地路径
        val apkfile = File(filePath)
        if (!apkfile.exists()) {
            return null
        }
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri = RxFileTool.getUriForFile(context, apkfile)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        return intent
    }

    /**
     * 获取卸载App的意图
     *
     * @param packageName 包名
     * @return 意图
     */
    fun getUninstallAppIntent(packageName: String): Intent {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取打开App的意图
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 意图
     */
    fun getLaunchAppIntent(context: Context, packageName: String): Intent? {
        return getIntentByPackageName(context, packageName)
    }

    /**
     * 根据包名获取意图
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 意图
     */
    private fun getIntentByPackageName(context: Context, packageName: String): Intent? {
        return context.packageManager.getLaunchIntentForPackage(packageName)
    }

    /**
     * 获取App信息的意图
     *
     * @param packageName 包名
     * @return 意图
     */
    fun getAppInfoIntent(packageName: String): Intent {
        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
        return intent.setData(Uri.parse("package:$packageName"))
    }

    /**
     * 获取App信息分享的意图
     *
     * @param info 分享信息
     * @return 意图
     */
    fun getShareInfoIntent(info: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        return intent.putExtra(Intent.EXTRA_TEXT, info)
    }

    /**
     * 获取其他应用的Intent
     *
     * @param packageName 包名
     * @param className   全类名
     * @return 意图
     */
    @JvmOverloads
    fun getComponentNameIntent(
        packageName: String,
        className: String,
        bundle: Bundle? = null
    ): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        if (bundle != null) intent.putExtras(bundle)
        val cn = ComponentName(packageName, className)
        intent.component = cn
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 获取应用详情页面具体设置 intent
     *
     * @return
     */
    fun getAppDetailsSettingsIntent(mContext: Context): Intent {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", mContext.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName(
                "com.android.settings",
                "com.android.settings.InstalledAppDetails"
            )
            localIntent.putExtra("com.android.settings.ApplicationPkgName", mContext.packageName)
        }
        return localIntent
    }

    /**
     * 获取应用详情页面具体设置 intent
     *
     * @param packageName 包名
     * @return intent
     */
    fun getAppDetailsSettingsIntent(packageName: String): Intent {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName(
                "com.android.settings",
                "com.android.settings.InstalledAppDetails"
            )
            localIntent.putExtra("com.android.settings.ApplicationPkgName", packageName)
        }
        return localIntent
    }

    fun launchAppDetail(context: Context, appPkg: String) {    //appPkg 是应用的包名
        val GOOGLE_PLAY = "com.android.vending"//这里对应的是谷歌商店，跳转别的商店改成对应的即可
        try {
            if (TextUtils.isEmpty(appPkg))
                return
            val uri = Uri.parse("market://details?id=$appPkg")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage(GOOGLE_PLAY)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Jump failed", Toast.LENGTH_LONG).show()    //跳转失败的处理
        }

    }
}
/**
 * 获取其他应用的Intent
 *
 * @param packageName 包名
 * @param className   全类名
 * @return 意图
 */
