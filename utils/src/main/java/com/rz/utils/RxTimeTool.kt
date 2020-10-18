package com.rz.utils

import android.annotation.SuppressLint
import android.util.Log
import com.rz.utils.RxConstTool.DAY
import com.rz.utils.RxConstTool.HOUR
import com.rz.utils.RxConstTool.MIN
import com.rz.utils.RxConstTool.MSEC
import com.rz.utils.RxConstTool.SEC
import com.rz.utils.RxConstTool.TimeUnit
import com.rz.utils.RxDataTool.Companion.isNullString
import com.rz.utils.RxDataTool.Companion.stringToInt
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author vondear
 * @date 2016/1/24
 * 时间相关工具类
 */
object RxTimeTool {

    val DEFAULT_SDF = SimpleDateFormat(RxConstants.DATE_FORMAT_DETACH, Locale.getDefault())
    val DEFAULT_MD = SimpleDateFormat("dd/MM", Locale.getDefault())
    val DEFAULT_DMY = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val DEFAULT_YMD = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * 获取当前时间
     *
     * @return 毫秒时间戳
     */
    val curTimeMills: Long
        get() = System.currentTimeMillis()

    /**
     * 获取当前时间
     *
     * 格式为yyyy-MM-dd HH:mm:ss
     *
     * @return 时间字符串
     */
    val curTimeString: String
        get() = date2String(Date())

    /**
     * 获取当前时间
     *
     * Date类型
     *
     * @return Date类型时间
     */
    val curTimeDate: Date
        get() = Date()

    /**
     * 将时间戳转为时间字符串
     *
     * 格式为用户自定义
     *
     * @param milliseconds 毫秒时间戳
     * @param format       时间格式
     * @return 时间字符串
     */
    @JvmOverloads
    fun milliseconds2String(milliseconds: Long, format: SimpleDateFormat = DEFAULT_SDF): String {
        return format.format(Date(milliseconds))
    }

    /**
     * 将时间字符串转为时间戳
     *
     * 格式为用户自定义
     *
     * @param time   时间字符串
     * @param format 时间格式
     * @return 毫秒时间戳
     */
    @JvmOverloads
    fun string2Milliseconds(time: String, format: SimpleDateFormat = DEFAULT_SDF): Long {
        try {
            return format.parse(time).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return -1
    }

    /**
     * 将时间字符串转为Date类型
     *
     * 格式为用户自定义
     *
     * @param time   时间字符串
     * @param format 时间格式
     * @return Date类型
     */
    @JvmOverloads
    fun string2Date(time: String, format: SimpleDateFormat = DEFAULT_SDF): Date {
        return Date(string2Milliseconds(time, format))
    }

    /**
     * 将Date类型转为时间字符串
     *
     * 格式为用户自定义
     *
     * @param time   Date类型时间
     * @param format 时间格式
     * @return 时间字符串
     */
    @JvmOverloads
    fun date2String(time: Date, format: SimpleDateFormat = DEFAULT_SDF): String {
        return format.format(time)
    }

    @JvmOverloads
    fun date2StringMd(time: Date, format: SimpleDateFormat = DEFAULT_MD): String {
        return format.format(time)
    }

    /**
     * 将Date类型转为时间戳
     *
     * @param time Date类型时间
     * @return 毫秒时间戳
     */
    fun date2Milliseconds(time: Date): Long {
        return time.time
    }

    /**
     * 将时间戳转为Date类型
     *
     * @param milliseconds 毫秒时间戳
     * @return Date类型时间
     */
    fun milliseconds2Date(milliseconds: Long): Date {
        return Date(milliseconds)
    }

    /**
     * 毫秒时间戳单位转换（单位：unit）
     *
     * @param milliseconds 毫秒时间戳
     * @param unit
     *  * [TimeUnit.MSEC]: 毫秒
     *  * [TimeUnit.SEC]: 秒
     *  * [TimeUnit.MIN]: 分
     *  * [TimeUnit.HOUR]: 小时
     *  * [TimeUnit.DAY]: 天
     *
     * @return unit时间戳
     */
    private fun milliseconds2Unit(milliseconds: Long, unit: TimeUnit): Long {
        when (unit) {
            RxConstTool.TimeUnit.MSEC -> return milliseconds / MSEC
            RxConstTool.TimeUnit.SEC -> return milliseconds / SEC
            RxConstTool.TimeUnit.MIN -> return milliseconds / MIN
            RxConstTool.TimeUnit.HOUR -> return milliseconds / HOUR
            RxConstTool.TimeUnit.DAY -> return milliseconds / DAY
        }
        return -1
    }

    /**
     * 获取两个时间差（单位：unit）
     *
     * time1和time2格式都为format
     *
     * @param time0  时间字符串1
     * @param time1  时间字符串2
     * @param unit
     *  * [TimeUnit.MSEC]: 毫秒
     *  * [TimeUnit.SEC]: 秒
     *  * [TimeUnit.MIN]: 分
     *  * [TimeUnit.HOUR]: 小时
     *  * [TimeUnit.DAY]: 天
     *
     * @param format 时间格式
     * @return unit时间戳
     */
    @JvmOverloads
    fun getIntervalTime(
        time0: String,
        time1: String,
        unit: TimeUnit,
        format: SimpleDateFormat = DEFAULT_SDF
    ): Long {
        return Math.abs(
            milliseconds2Unit(
                string2Milliseconds(time0, format) - string2Milliseconds(
                    time1,
                    format
                ), unit
            )
        )
    }

    /**
     * 获取两个时间差（单位：unit）
     *
     * time1和time2都为Date类型
     *
     * @param time1 Date类型时间1
     * @param time2 Date类型时间2
     * @param unit
     *  * [TimeUnit.MSEC]: 毫秒
     *  * [TimeUnit.SEC]: 秒
     *  * [TimeUnit.MIN]: 分
     *  * [TimeUnit.HOUR]: 小时
     *  * [TimeUnit.DAY]: 天
     *
     * @return unit时间戳
     */
    fun getIntervalTime(time1: Date, time2: Date, unit: TimeUnit): Long {
        return Math.abs(
            milliseconds2Unit(
                date2Milliseconds(time2) - date2Milliseconds(time1),
                unit
            )
        )
    }

    /**
     * 获取当前时间
     *
     * 格式为用户自定义
     *
     * @param format 时间格式
     * @return 时间字符串
     */
    fun getCurTimeString(format: SimpleDateFormat): String {
        return date2String(Date(), format)
    }

    /**
     * 获取与当前时间的差（单位：unit）
     *
     * time格式为format
     *
     * @param time   时间字符串
     * @param unit
     *  * [TimeUnit.MSEC]: 毫秒
     *  * [TimeUnit.SEC]: 秒
     *  * [TimeUnit.MIN]: 分
     *  * [TimeUnit.HOUR]: 小时
     *  * [TimeUnit.DAY]: 天
     *
     * @param format 时间格式
     * @return unit时间戳
     */
    @JvmOverloads
    fun getIntervalByNow(
        time: String,
        unit: TimeUnit,
        format: SimpleDateFormat = DEFAULT_SDF
    ): Long {
        return getIntervalTime(curTimeString, time, unit, format)
    }

    /**
     * 获取与当前时间的差（单位：unit）
     *
     * time为Date类型
     *
     * @param time Date类型时间
     * @param unit
     *  * [TimeUnit.MSEC]: 毫秒
     *  * [TimeUnit.SEC]: 秒
     *  * [TimeUnit.MIN]: 分
     *  * [TimeUnit.HOUR]: 小时
     *  * [TimeUnit.DAY]: 天
     *
     * @return unit时间戳
     */
    fun getIntervalByNow(time: Date, unit: TimeUnit): Long {
        return getIntervalTime(curTimeDate, time, unit)
    }

    /**
     * 判断闰年
     *
     * @param year 年份
     * @return `true`: 闰年<br></br>
     * `false`: 平年
     */
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    /**
     * 将date转换成format格式的日期
     *
     * @param format 格式
     * @param date   日期
     * @return
     */
    fun simpleDateFormat(format: String, date: Date): String {
        var format = format
        if (isNullString(format)) {
            format = RxConstants.DATE_FORMAT_DETACH_SSS
        }
        return SimpleDateFormat(format).format(date)
    }

    //--------------------------------------------字符串转换成时间戳-----------------------------------

    /**
     * 将指定格式的日期转换成时间戳
     *
     * @param mDate
     * @return
     */
    fun Date2Timestamp(mDate: Date): String {
        return mDate.time.toString().substring(0, 10)
    }

    /**
     * 将日期字符串 按照 指定的格式 转换成 DATE
     * 转换失败时 return null;
     *
     * @param format
     * @param datess
     * @return
     */
    fun string2Date(format: String, datess: String): Date? {
        val sdr = SimpleDateFormat(format)
        var date: Date? = null
        try {
            date = sdr.parse(datess)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }


    /**
     * 获取当前日期时间 / 得到今天的日期
     * str yyyyMMddhhMMss 之类的
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateTime(format: String): String {
        return simpleDateFormat(format, Date())
    }

    /**
     * 时间戳  转换成 指定格式的日期
     * 如果format为空，则默认格式为
     *
     * @param times  时间戳
     * @param format 日期格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun getDate(times: String, format: String): String {
        return simpleDateFormat(format, Date(stringToInt(times) * 1000L))
    }

    /**
     * 得到昨天的日期
     *
     * @param format 日期格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    fun getYestoryDate(format: String): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        return simpleDateFormat(format, calendar.time)
    }

    /**
     *x 转换成 "yyyy-MM-dd HH:mm:ss"
     *
     * @param milliseconds
     * @return
     */
    fun formatTime(milliseconds: Long): String {
        val format = RxConstants.DATE_FORMAT_DETACH
        val sdf = SimpleDateFormat(format)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        return sdf.format(milliseconds)
    }

    /**
     *历史订单时间显示
     * @param milliseconds
     * @return
     */
    fun formatTimeForOrderMDY(milliseconds: Long): String {
        val format = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(format)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        return sdf.format(milliseconds)
    }

    /**
     *历史订单时间显示
     * @param milliseconds
     * @return
     */
    fun formatTimeForOrderHm(milliseconds: Long): String {
        val format = "hh:mm a"
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")

        return sdf.format(milliseconds)
    }

    /**
     *历史订单时间显示
     * @param milliseconds
     * @return
     */
    fun formatTimeForTransacions(milliseconds: Long): String {
        val format = "yyyy-MM-dd hh:mm a"
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        return sdf.format(milliseconds)
    }


    fun isTodayTime(inputJudgeDate: Long): Boolean {
        var flag = false
        //获取当前系统时间
        val longDate = System.currentTimeMillis()
        val nowDate = Date(longDate)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val format = dateFormat.format(nowDate)
        val subDate = format.substring(0, 10)
        //定义每天的24h时间范围
        val beginTime = "$subDate 00:00:00"
        val endTime = "$subDate 23:59:59"
        var paseBeginTime: Date? = null
        var paseEndTime: Date? = null
        try {
            paseBeginTime = dateFormat.parse(beginTime)
            paseEndTime = dateFormat.parse(endTime)

        } catch (e: ParseException) {
        }
        var day = milliseconds2Date(inputJudgeDate)

        if (day.after(paseBeginTime) && day.before(paseEndTime)) {
            flag = true
        }
        return flag
    }


    /**
     * 视频时间 转换成 "YYYY-MM-DD"
     *
     * @param milliseconds
     * @return
     */
    fun formatTimeYear(milliseconds: Long): String {
        val format = RxConstants.DATE_FORMAT_TIME
        val sdf = SimpleDateFormat(format)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        return sdf.format(milliseconds)
    }

    /**
     * 视频时间 转换成 "YYYY/MM"
     *
     * @param milliseconds
     * @return
     */
    fun formatTimeYearAndMonth(milliseconds: Long): String {
        val sdf = SimpleDateFormat("yyyy/MM")
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        return sdf.format(milliseconds)
    }


    /**
     * "mm:ss" 转换成 视频时间
     *
     * @param time
     * @return
     */
    fun formatSeconds(time: String): Long {
        val format = RxConstants.DATE_FORMAT_MM_SS
        val sdf = SimpleDateFormat(format)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        val date: Date
        var times: Long = 0
        try {
            date = sdf.parse(time)
            val l = date.time
            times = l
            Log.d("时间戳", times.toString() + "")
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return times
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    fun getDaysByYearMonth(year: Int, month: Int): Int {
        val a = Calendar.getInstance()
        a.set(Calendar.YEAR, year)
        a.set(Calendar.MONTH, month - 1)
        a.set(Calendar.DATE, 1)
        a.roll(Calendar.DATE, -1)
        return a.get(Calendar.DATE)
    }

    /**
     * 判断当前日期是星期几
     *
     * @param strDate 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常<br></br>
     */
    @Throws(Exception::class)
    fun dateForWeek(data: Date): Int {
        val c = Calendar.getInstance()
        c.time = data
        return if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            7
        } else {
            c.get(Calendar.DAY_OF_WEEK) - 1
        }
    }

    /**
     * 判断当前日期是星期几
     *
     * @param strDate 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常<br></br>
     */
    @Throws(Exception::class)
    fun stringForWeek(strDate: String): Int {
        val format = SimpleDateFormat("dd-MM")
        val c = Calendar.getInstance()
        c.time = format.parse(strDate)
        return if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            7
        } else {
            c.get(Calendar.DAY_OF_WEEK) - 1
        }
    }

    /**
     * 判断当前日期是星期几
     *
     * @param strDate 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常<br></br>
     */
    @Throws(Exception::class)
    fun stringForWeek(strDate: String, simpleDateFormat: SimpleDateFormat): Int {
        val c = Calendar.getInstance()
        c.time = simpleDateFormat.parse(strDate)
        return if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            7
        } else {
            c.get(Calendar.DAY_OF_WEEK) - 1
        }
    }
}