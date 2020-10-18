package com.rz.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationUtils private constructor(private val context: Context) :
    ContextWrapper(context) {
    private var manager: NotificationManager? = null
    private val id: String
    private val name: String
    private var channel: NotificationChannel? = null

    init {
        id = context.packageName
        name = context.packageName
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        if (channel == null) {
            channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            channel!!.enableVibration(false)
            channel!!.enableLights(false)
            channel!!.enableVibration(false)
            channel!!.vibrationPattern = longArrayOf(0, 1000, 1000, 1000)
            channel!!.setSound(null, null)
            getManager()!!.createNotificationChannel(channel!!)
        }
    }

    private fun getManager(): NotificationManager? {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getChannelNotification(
        title: String,
        content: String,
        icon: Int,
        intent: Intent
    ): Notification.Builder {
        //PendingIntent.FLAG_UPDATE_CURRENT 这个类型才能传值
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return Notification.Builder(context, channel!!.id)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    fun getNotification_25(
        title: String,
        content: String,
        icon: Int,
        intent: Intent
    ): NotificationCompat.Builder {
        //PendingIntent.FLAG_UPDATE_CURRENT 这个类型才能传值
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(context, id)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content).setBigContentTitle(title))
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 1000, 1000, 1000))
            .setLights(Color.GREEN, 1000, 1000)//设置提示灯
            .setContentIntent(pendingIntent)
    }

    companion object {

        fun sendNotification(
            context: Context,
            title: String,
            content: String,
            icon: Int,
            intent: Intent
        ) {
            val notificationUtils = NotificationUtils(context)
            var notification: Notification? = null
            if (Build.VERSION.SDK_INT >= 26) {
                notificationUtils.createNotificationChannel()
//                notification = notificationUtils.getChannelNotification(title, content, icon, intent).build()
                notification =
                    notificationUtils.getNotification_25(title, content, icon, intent).build()
            } else {
                notification =
                    notificationUtils.getNotification_25(title, content, icon, intent).build()
            }
            notificationUtils.getManager()!!.notify(java.util.Random().nextInt(10000), notification)
        }

        fun createNotification(
            context: Context,
            title: String,
            content: String,
            icon: Int,
            intent: Intent
        ): Notification? {
            val notificationUtils = NotificationUtils(context)
            var notification: Notification? = null
            if (Build.VERSION.SDK_INT >= 26) {
                notificationUtils.createNotificationChannel()
                notification =
                    notificationUtils.getChannelNotification(title, content, icon, intent).build()
            } else {
                notification =
                    notificationUtils.getNotification_25(title, content, icon, intent).build()
            }
            return notification
        }
    }
}
