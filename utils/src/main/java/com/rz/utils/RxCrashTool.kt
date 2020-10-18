package com.rz.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SuppressLint("StaticFieldLeak")
/**
 * @author vondear
 * @date 2016/12/21
 */

object RxCrashTool {

    private val FILE_SEP = System.getProperty("file.separator")

    //----------------------------------------------------------------------------------------------
    private val FORMAT = SimpleDateFormat("MM-dd HH-mm-ss", Locale.getDefault())
    private val CRASH_HEAD: String
    private val DEFAULT_UNCAUGHT_EXCEPTION_HANDLER: UncaughtExceptionHandler?
    private val UNCAUGHT_EXCEPTION_HANDLER: UncaughtExceptionHandler
    private var mContext: Context? = null
    private var mCrashDirPath: String? = null
    private var dir: String? = null
    private var versionName: String? = null
    private var versionCode: Int = 0
    private var sExecutor: ExecutorService? = null

    init {


        CRASH_HEAD = "\n************* Crash Log Head ****************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +// 设备厂商

                "\nDevice Model       : " + Build.MODEL +// 设备型号

                "\nAndroid Version    : " + Build.VERSION.RELEASE +// 系统版本

                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +// SDK版本

                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n"

        DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler()

        UNCAUGHT_EXCEPTION_HANDLER = UncaughtExceptionHandler { t, e ->
            if (e == null) {
                111
                System.exit(0)
                return@UncaughtExceptionHandler
            }
            val now = Date(System.currentTimeMillis())
            val fileName = FORMAT.format(now) + ".txt"
            val fullPath = (if (dir == null) mCrashDirPath else dir) + fileName
            if (!createOrExistsFile(fullPath)) {
                return@UncaughtExceptionHandler
            }
            if (sExecutor == null) {
                sExecutor = Executors.newSingleThreadExecutor()
            }
            sExecutor!!.execute {
                var pw: PrintWriter? = null
                try {
                    pw = PrintWriter(FileWriter(fullPath, false))
                    pw.write(CRASH_HEAD)
                    e.printStackTrace(pw)
                    var cause: Throwable? = e.cause
                    while (cause != null) {
                        cause.printStackTrace(pw)
                        cause = cause.cause
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    pw?.close()
                }
            }
            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(t, e)
        }
    }

    /**
     * 初始化
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
     */
    fun init(context: Context) {
        mContext = context
        try {
            val pi = mContext!!.packageManager.getPackageInfo(mContext!!.packageName, 0)
            if (pi != null) {
                versionName = pi.versionName
                versionCode = pi.versionCode
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        init("")
    }

    /**
     * 初始化
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
     *
     * @param crashDir 崩溃文件存储目录
     */
    fun init(crashDir: File) {
        init(crashDir.absolutePath)
    }

    /**
     * 初始化
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
     *
     * @param crashDir 崩溃文件存储目录
     */
    fun init(crashDir: String) {
        if (isSpace(crashDir)) {
            dir = null
        } else {
            dir = if (crashDir.endsWith(FILE_SEP.toString())) crashDir else crashDir + FILE_SEP
        }

        try {
            val packageManager = mContext!!.packageManager
            val packageInfo = packageManager.getPackageInfo(mContext!!.packageName, 0)
            val labelRes = packageInfo.applicationInfo.labelRes
            val name = mContext!!.resources.getString(labelRes)
            mCrashDirPath =
                RxFileTool.rootPath.absolutePath + File.separator + name + File.separator + "crash" + File.separator
        } catch (e: Exception) {
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                mCrashDirPath =
                    mContext!!.externalCacheDir!!.path + File.separator + "crash" + File.separator
            } else {
                mCrashDirPath = mContext!!.cacheDir.path + File.separator + "crash" + File.separator
            }
        }


        Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER)
    }

    private fun createOrExistsFile(filePath: String): Boolean {
        val file = File(filePath)
        if (file.exists()) {
            return file.isFile
        }
        if (!createOrExistsDir(file.parentFile)) {
            return false
        }
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }

    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) {
            return true
        }
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }
}