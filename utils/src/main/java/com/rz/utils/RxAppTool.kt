package com.rz.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.util.*

/**
 * @author vondear
 * @date 2016/1/24
 */
object RxAppTool {

    /**
     * 安装App(支持7.0)
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    fun installApp(context: Context, filePath: String) {
        context.startActivity(RxIntentTool.getInstallAppIntent(context, filePath))
    }

    /**
     * 安装App（支持7.0）
     *
     * @param context 上下文
     * @param file    文件
     */
    fun installApp(context: Context, file: File) {
        if (!RxFileTool.isFileExists(file)) return
        installApp(context, file.absolutePath)
    }

    /**
     * 安装App（支持7.0）
     *
     * @param activity    activity
     * @param filePath    文件路径
     * @param requestCode 请求值
     */
    fun installApp(activity: Activity, filePath: String, requestCode: Int) {
        activity.startActivityForResult(
            RxIntentTool.getInstallAppIntent(activity, filePath),
            requestCode
        )
    }

    /**
     * 安装App(支持7.0)
     *
     * @param activity    activity
     * @param file        文件
     * @param requestCode 请求值
     */
    fun installApp(activity: Activity, file: File, requestCode: Int) {
        if (!RxFileTool.isFileExists(file)) return
        installApp(activity, file.absolutePath, requestCode)
    }

    /**
     * 静默安装App
     *
     * 非root需添加权限 `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @return `true`: 安装成功<br></br>`false`: 安装失败
     */
//    fun installAppSilent(context: Context, filePath: String): Boolean {
//        val file = RxFileTool.getFileByPath(filePath)
//        if (!RxFileTool.isFileExists(file)) return false
//        val command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install $filePath"
//        val commandResult = RxShellTool.execCmd(command, !isSystemApp(context), true)
//        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success")
//    }

    /**
     * 判断App是否是系统应用
     *
     * @param context     上下文
     * @param packageName 包名
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmOverloads
    fun isSystemApp(context: Context, packageName: String = context.packageName): Boolean {
        if (RxDataTool.isNullString(packageName)) return false
        try {
            val pm = context.packageManager
            val ai = pm.getApplicationInfo(packageName, 0)
            return ai != null && ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 卸载App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    fun uninstallApp(context: Context, packageName: String) {
        if (RxDataTool.isNullString(packageName)) return
        context.startActivity(RxIntentTool.getUninstallAppIntent(packageName))
    }

    /**
     * 卸载App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    fun uninstallApp(activity: Activity, packageName: String, requestCode: Int) {
        if (RxDataTool.isNullString(packageName)) return
        activity.startActivityForResult(
            RxIntentTool.getUninstallAppIntent(packageName),
            requestCode
        )
    }

    /**
     * 静默卸载App
     *
     * 非root需添加权限 `<uses-permission android:name="android.permission.DELETE_PACKAGES" />`
     *
     * @param context     上下文
     * @param packageName 包名
     * @param isKeepData  是否保留数据
     * @return `true`: 卸载成功<br></br>`false`: 卸载成功
     */
//    fun uninstallAppSilent(context: Context, packageName: String, isKeepData: Boolean): Boolean {
//        if (RxDataTool.isNullString(packageName)) return false
//        val command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " + (if (isKeepData) "-k " else "") + packageName
//        val commandResult = RxShellTool.execCmd(command, !isSystemApp(context), true)
//        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success")
//    }

    /**
     * 打开App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    fun launchApp(context: Context, packageName: String) {
        if (RxDataTool.isNullString(packageName)) return
        context.startActivity(RxIntentTool.getLaunchAppIntent(context, packageName))
    }

    /**
     * 打开App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    fun launchApp(activity: Activity, packageName: String, requestCode: Int) {
        if (RxDataTool.isNullString(packageName)) return
        activity.startActivityForResult(
            RxIntentTool.getLaunchAppIntent(activity, packageName),
            requestCode
        )
    }

    /**
     * 获取App包名
     *
     * @param context 上下文
     * @return App包名
     */
    fun getAppPackageName(context: Context): String {
        return context.packageName
    }

    /**
     * 获取App具体设置
     *
     * @param context     上下文
     * @param packageName 包名
     */
    @JvmOverloads
    fun getAppDetailsSettings(context: Context, packageName: String = context.packageName) {
        if (RxDataTool.isNullString(packageName)) return
        context.startActivity(RxIntentTool.getAppDetailsSettingsIntent(packageName))
    }

    /**
     * 获取App名称
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App名称
     */
    @JvmOverloads
    fun getAppName(context: Context, packageName: String = context.packageName): String? {
        if (RxDataTool.isNullString(packageName)) return null
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            return pi?.applicationInfo?.loadLabel(pm)?.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App图标
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App图标
     */
    @JvmOverloads
    fun getAppIcon(context: Context, packageName: String = context.packageName): Drawable? {
        if (RxDataTool.isNullString(packageName)) return null
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            return pi?.applicationInfo?.loadIcon(pm)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App路径
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App路径
     */
    @JvmOverloads
    fun getAppPath(context: Context, packageName: String = context.packageName): String? {
        if (RxDataTool.isNullString(packageName)) return null
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            return pi?.applicationInfo?.sourceDir
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App版本号
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本号
     */
    @JvmOverloads
    fun getAppVersionName(context: Context, packageName: String = context.packageName): String? {
        if (RxDataTool.isNullString(packageName)) return null
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            return pi?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App版本码
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App版本码
     */
    @RequiresApi(Build.VERSION_CODES.P)
    @JvmOverloads
    fun getAppVersionCode(context: Context, packageName: String = context.packageName): Long {
        if (RxDataTool.isNullString(packageName)) return -1
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            return pi?.longVersionCode!!
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return -1
        }

    }

    /**
     * 判断App是否是Debug版本
     *
     * @param context     上下文
     * @param packageName 包名
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmOverloads
    fun isAppDebug(context: Context, packageName: String = context.packageName): Boolean {
        if (RxDataTool.isNullString(packageName)) return false
        try {
            val pm = context.packageManager
            val ai = pm.getApplicationInfo(packageName, 0)
            return ai != null && ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 获取App签名
     *
     * @param context 上下文
     * @return App签名
     */
    fun getAppSignature(context: Context): Array<Signature>? {
        return getAppSignature(context, context.packageName)
    }

    /**
     * 获取App签名
     *
     * @param context     上下文
     * @param packageName 包名
     * @return App签名
     */
    @SuppressLint("PackageManagerGetSignatures")
    fun getAppSignature(context: Context, packageName: String): Array<Signature>? {
        if (RxDataTool.isNullString(packageName)) return null
        return try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            pi?.signatures
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }

    }

    /**
     * 获取应用签名的的SHA1值
     *
     * 可据此判断高德，百度地图key是否正确
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
     */
//    fun getAppSignatureSHA1(context: Context, packageName: String = context.packageName): String? {
//        val signature = getAppSignature(context, packageName) ?: return null
//        return RxEncryptTool.encryptSHA1ToString(signature[0].toByteArray()).replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
//    }

    /**
     * 判断App是否处于前台
     *
     * @param context 上下文
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isAppForeground(context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses
        if (infos == null || infos.size == 0) return false
        for (info in infos) {
            if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return info.processName == context.packageName
            }
        }
        return false
    }

    /**
     * 判断App是否处于前台
     *
     * 当不是查看当前App，且SDK大于21时，
     * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`
     *
     * @param context     上下文
     * @param packageName 包名
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isAppForeground(context: Context, packageName: String): Boolean {
        return !RxDataTool.isNullString(packageName) && packageName == RxProcessTool.getForegroundProcessName(
            context
        )
    }
    //----------------------------------------------------------------------------------------------------------------

    /**
     * 判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return `true`: 已安装<br></br>`false`: 未安装
     */
    fun isInstallApp(context: Context, packageName: String): Boolean {
        return !RxDataTool.isNullString(packageName) && RxIntentTool.getLaunchAppIntent(
            context,
            packageName
        ) != null
    }

    /**
     * 安装APK
     *
     * @param context
     * @param APK_PATH
     */
    fun InstallAPK(context: Context, APK_PATH: String) {//提示安装APK
        val i = Intent(Intent.ACTION_VIEW)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.setDataAndType(Uri.parse("file://$APK_PATH"), "application/vnd.android.package-archive")
        context.startActivity(i)
    }

    /**
     * 获取当前App信息
     *
     * AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序）
     *
     * @param context 上下文
     * @return 当前应用的AppInfo
     */
    fun getAppInfo(context: Context): AppInfo? {
        val pm = context.packageManager
        var pi: PackageInfo? = null
        try {
            pi = pm.getPackageInfo(context.applicationContext.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return if (pi != null) getBean(pm, pi) else null
    }

    /**
     * 得到AppInfo的Bean
     *
     * @param pm 包的管理
     * @param pi 包的信息
     * @return AppInfo类
     */
    private fun getBean(pm: PackageManager, pi: PackageInfo): AppInfo {
        val ai = pi.applicationInfo
        val name = ai.loadLabel(pm).toString()
        val icon = ai.loadIcon(pm)
        val packageName = pi.packageName
        val packagePath = ai.sourceDir
        val versionName = pi.versionName
        val versionCode = pi.versionCode
        val isSD = ApplicationInfo.FLAG_SYSTEM and ai.flags != ApplicationInfo.FLAG_SYSTEM
        val isUser = ApplicationInfo.FLAG_SYSTEM and ai.flags != ApplicationInfo.FLAG_SYSTEM
        return AppInfo(name, icon, packageName, packagePath, versionName, versionCode, isSD, isUser)
    }

    /**
     * 获取所有已安装App信息
     *
     * [.getBean]（名称，图标，包名，包路径，版本号，版本Code，是否安装在SD卡，是否是用户程序）
     *
     * 依赖上面的getBean方法
     *
     * @param context 上下文
     * @return 所有已安装的AppInfo列表
     */
    fun getAllAppsInfo(context: Context): List<AppInfo> {
        val list = ArrayList<AppInfo>()
        val pm = context.packageManager
        // 获取系统中安装的所有软件信息
        val installedPackages = pm.getInstalledPackages(0)
        for (pi in installedPackages) {
            if (pi != null) {
                list.add(getBean(pm, pi))
            }
        }
        return list
    }

    /**
     * 判断当前App处于前台还是后台
     *
     * 需添加权限 `<uses-permission android:name="android.permission.GET_TASKS"/>`
     *
     * 并且必须是系统应用该方法才有效
     *
     * @param context 上下文
     * @return `true`: 后台<br></br>`false`: 前台
     */
    fun isAppBackground(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        if (!tasks.isEmpty()) {
            val topActivity = tasks[0].topActivity
            return topActivity!!.packageName != context.packageName
        }
        return false
    }


    /**
     * 封装App信息的Bean类
     */
    class AppInfo
    /**
     * @param name        名称
     * @param icon        图标
     * @param packageName 包名
     * @param packagePath 包路径
     * @param versionName 版本号
     * @param versionCode 版本Code
     * @param isSD        是否安装在SD卡
     * @param isUser      是否是用户程序
     */
        (
        name: String, icon: Drawable, packageName: String, packagePath: String,
        versionName: String, versionCode: Int, isSD: Boolean, isUser: Boolean
    ) {

        var name: String? = null
        var icon: Drawable? = null
        var packageName: String? = null
        var packagePath: String? = null
        var versionName: String? = null
        var versionCode: Int = 0
        var isSD: Boolean = false
        var isUser: Boolean = false

        init {
            this.name = name
            this.icon = icon
            this.packageName = packageName
            this.packagePath = packagePath
            this.versionName = versionName
            this.versionCode = versionCode
            this.isSD = isSD
            this.isUser = isUser
        }

        //        @Override
        //        public String toString() {
        //            return getName() + "\n"
        //                    + getIcon() + "\n"
        //                    + getPackageName() + "\n"
        //                    + getPackagePath() + "\n"
        //                    + getVersionName() + "\n"
        //                    + getVersionCode() + "\n"
        //                    + isSD() + "\n"
        //                    + isUser() + "\n";
        //        }
    }

}
/**
 * 判断App是否是系统应用
 *
 * @param context 上下文
 * @return `true`: 是<br></br>`false`: 否
 */
/**
 * 获取App具体设置
 *
 * @param context 上下文
 */
/**
 * 获取App名称
 *
 * @param context 上下文
 * @return App名称
 */
/**
 * 获取App图标
 *
 * @param context 上下文
 * @return App图标
 */
/**
 * 获取App路径
 *
 * @param context 上下文
 * @return App路径
 */
/**
 * 获取App版本号
 *
 * @param context 上下文
 * @return App版本号
 */
/**
 * 获取App版本码
 *
 * @param context 上下文
 * @return App版本码
 */
/**
 * 判断App是否是Debug版本
 *
 * @param context 上下文
 * @return `true`: 是<br></br>`false`: 否
 */
/**
 * 获取应用签名的的SHA1值
 *
 * 可据此判断高德，百度地图key是否正确
 *
 * @param context 上下文
 * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
 */