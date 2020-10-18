package com.rz.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import java.util.*


/**
 * @author vondear
 * @date 2016/1/24
 *
 *
 * 封装Activity相关工具类
 */
object RxActivityTool {

    var activityStack: Stack<Activity>? = null
        private set

    /**
     * 添加Activity 到栈
     *
     * @param activity
     */
    fun addActivity(activity: Activity) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }

    /**
     * 获取当前的Activity（堆栈中最后一个压入的)
     */
    fun currentActivity(): Activity {
        return activityStack!!.lastElement()
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    fun finishActivity() {
        activityStack!!.lastElement()

    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null) {
            activityStack!!.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        for (activity in activityStack!!) {
            if (activity.javaClass == cls) {
                finishActivity(activity)
            }
        }
    }

    /**
     * 结束所有的Activity
     */
    fun finishAllActivity() {
        val size = activityStack!!.size
        for (i in 0 until size) {
            if (null != activityStack!![i]) {
                activityStack!![i].finish()
            }
        }
        activityStack!!.clear()
    }

    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.restartPackage(context.packageName)
            System.exit(0)
        } catch (e: Exception) {

        }

    }

    /**
     * 判断是否存在指定Activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   activity全路径类名
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isExistActivity(context: Context, packageName: String, className: String): Boolean {
        val intent = Intent()
        intent.setClassName(packageName, className)
        return !(context.packageManager.resolveActivity(intent, 0) == null ||
                intent.resolveActivity(context.packageManager) == null ||
                context.packageManager.queryIntentActivities(intent, 0).size == 0)
    }

    /**
     * 打开指定的Activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   全类名
     * @param bundle      bundle
     */
    @JvmOverloads
    fun launchActivity(
        context: Context,
        packageName: String,
        className: String,
        bundle: Bundle? = null
    ) {
        context.startActivity(RxIntentTool.getComponentNameIntent(packageName, className, bundle))
    }

    /**
     * 要求最低API为11
     * Activity 跳转
     * 跳转后Finish之前所有的Activity
     *
     * @param context
     * @param goal
     */
    fun skipActivityAndFinishAll(context: Context, goal: Class<*>, bundle: Bundle) {
        val intent = Intent(context, goal)
        intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        (context as Activity).finish()
    }

    /**
     * 要求最低API为11
     * Activity 跳转
     * 跳转后Finish之前所有的Activity
     *
     * @param context
     * @param goal
     */
    fun skipActivityAndFinishAll(context: Context, goal: Class<*>) {
        val intent = Intent(context, goal)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        (context as Activity).finish()
    }


    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    fun skipActivityAndFinish(context: Context, goal: Class<*>, bundle: Bundle) {
        val intent = Intent(context, goal)
        intent.putExtras(bundle)
        context.startActivity(intent)
        (context as Activity).finish()
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    fun skipActivityAndFinish(context: Context, goal: Class<*>) {
        val intent = Intent(context, goal)
        context.startActivity(intent)
        (context as Activity).finish()
    }


    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    fun skipActivity(context: Context, goal: Class<*>) {
        val intent = Intent(context, goal)
        context.startActivity(intent)
    }

    /**
     * Activity 跳转
     *
     * @param context
     * @param goal
     */
    fun skipActivity(context: Context, goal: Class<*>, bundle: Bundle) {
        val intent = Intent(context, goal)
        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    fun skipActivityForResult(context: Activity, goal: Class<*>, requestCode: Int) {
        val intent = Intent(context, goal)
        context.startActivityForResult(intent, requestCode)
    }

    fun skipActivityForResult(context: Activity, goal: Class<*>, bundle: Bundle, requestCode: Int) {
        val intent = Intent(context, goal)
        intent.putExtras(bundle)
        context.startActivityForResult(intent, requestCode)
    }

    /**
     * 获取launcher activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @return launcher activity
     */
    fun getLauncherActivity(context: Context, packageName: String): String {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = context.packageManager
        val infos = pm.queryIntentActivities(intent, 0)
        for (info in infos) {
            if (info.activityInfo.packageName == packageName) {
                return info.activityInfo.name
            }
        }
        return "no $packageName"
    }

    /**
     * Activity 启动自己
     *
     * @param context
     */
    fun reStartActivity(context: Activity) {
        val intent = context.intent
        context.finish()
        context.startActivity(intent)
    }
}

