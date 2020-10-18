package com.rz.utils.sp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

@SuppressLint("StaticFieldLeak")
/**
 * Created by iss on 2018/9/7.
 */

object GlobalPreference {
    val META = "Global_FlashEx"
    var mContext: Context? = null
    var sharedPreferences: SharedPreferences? = null


    //SharedPreferences Key都放到这里
    object KEY {
        val GLOBAL_LOCAL_LANGUAGE = "local_language"
        val GLOBAL_ZERO_AUTO = "global_zero_auto"//零点定额
        val GLOBAL_ZERO_CONSUME = "global_zero_consume"//零点定额消费额度
        val GLOBAL_SETTING_PASSWORD = "global_setting_password"//设置密码
        val GLOBAL_SERVICE_IP = "global_service_ip"//服务器IP
        val GLOBAL_WHITELIST_CHECK = "global_whitelist_check"//白名单
        val GLOBAL_PRINTF_CHECK = "global_printf_check"//打印
        val GLOBAL_CARD_CHECK = "global_card_check"// 1自动模式 2零点模式
        val GLOBAL_CLEAR_DATABASE = "global_clear_database"// 清除数据库时间
        val GLOBAL_LCD_MODE = "global_lcd_mode"//客屏设置 1开启 2关闭
        val GLOBAL_SCREEN_MODE = "global_screen_mode"//屏幕
        val GLOBAL_VOICE_MODE = "global_voice_mode"//声音
        val GLOBAL_INTERVAL_TIME = "global_interval_time"//读卡间隔时间
        val SAVE_LAST_NUMBER = "save_last_number"//保存最后一次刷卡的卡号
        val GLOBLE_ONLINE_MOBILE = "globle_online_mobile"//设置是否是离线状态
        val GLOBLE_OFFLINE_MODE = "globle_offline_mode"//设置离线模式
        val APP_VOLUME = "app_volume"//app声音大小
        val LCD_SHOW_TIME = "lcd_show_time"//客屏显示时间设置
        val DEVICES_APP_NAME = "devices_app_name"//机器名
        val GLOBAL_MODE_CHOSE = "global_mode_chose"//选择模式

        val GLOBAL_MENU_AUTO = "global_menu_auto"//菜单自动模式
        val GLOBAL_MENU_ZERO = "global_menu_zero"//菜单零点模式
        val GLOBAL_MENU_COMMODITY = "global_menu_commodity"//菜单商品模式
        val MODE_STATUS_UPDATE = "mode_status_update"//模式状态更改需要重启
        val UMENG_DEVICE_TOKEN = "umeng_device_token"//umeng统计token


    }


    /**
     * 初始化放到Applcation 中
     *
     * @param context
     */
    fun init(context: Context) {
        if (null == sharedPreferences || null == mContext) {
            mContext = context.applicationContext
            sharedPreferences = context.getSharedPreferences(META, 0)
        }
    }

    fun set(key: String, value: Any) {
        try {
            if (TextUtils.isEmpty(key) || value == null) {
                throw NullPointerException(
                    String.format("Key and value not be null key=%s, value=%s", key, value)
                )
            }

            val edit = sharedPreferences!!.edit()
            if (value is String) {
                edit.putString(key, value)
            } else if (value is Int) {
                edit.putInt(key, value)
            } else if (value is Long) {
                edit.putLong(key, value)
            } else if (value is Boolean) {
                edit.putBoolean(key, value)
            } else if (value is Float) {
                edit.putFloat(key, value)
            } else if (value is Set<*>) {
                edit.putStringSet(key, value as Set<String>)
            } else {
                throw IllegalArgumentException(
                    String.format("Type of value unsupported key=%s, value=%s", key, value)
                )

            }
            edit.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //封装 SharedPreferences 的各种 get 方法
    //使用泛型，调用时不用再强制转换

    fun <T> get(key: String, defValue: T): T {
        try {
            if (defValue is String) {
                return sharedPreferences!!.getString(key, defValue)!! as T
            } else if (defValue is Int) {
                return Integer.valueOf(sharedPreferences!!.getInt(key, defValue)) as T
            } else if (defValue is Boolean) {
                return java.lang.Boolean.valueOf(sharedPreferences!!.getBoolean(key, defValue)) as T
            } else if (defValue is Long) {
                return sharedPreferences!!.getLong(key, defValue) as T
            } else if (defValue is Float) {
                return sharedPreferences!!.getFloat(key, defValue) as T
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defValue
    }

    fun clearAll() {
        if (null != sharedPreferences && null != sharedPreferences!!.edit()) {
            sharedPreferences!!.edit().clear().commit()
        }
    }

    fun remove(key: String) {
        if (null != sharedPreferences && null != sharedPreferences!!.edit()) {
            sharedPreferences!!.edit().remove(key).commit()
        }
    }

}
