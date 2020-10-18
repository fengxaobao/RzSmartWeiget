package com.rz.utils

import android.os.Build
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import com.rz.utils.RxConstTool.BYTE
import com.rz.utils.RxConstTool.GB
import com.rz.utils.RxConstTool.KB
import com.rz.utils.RxConstTool.MB
import java.io.*
import java.lang.reflect.Array
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.experimental.and


/**
 * @author vondear
 * @date 2016/1/24
 * 数据处理相关
 *
 *
 * ┌───┐   ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐
 * │Esc│   │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│  ┌┐    ┌┐    ┌┐
 * └───┘   └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘  └┘    └┘    └┘
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐ ┌───┬───┬───┐ ┌───┬───┬───┬───┐
 * │~ `│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp │ │Ins│Hom│PUp│ │N L│ / │ * │ - │
 * ├───┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤ ├───┼───┼───┤ ├───┼───┼───┼───┤
 * │ Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ │ │Del│End│PDn│ │ 7 │ 8 │ 9 │   │
 * ├─────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤ └───┴───┴───┘ ├───┼───┼───┤ + │
 * │ Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │               │ 4 │ 5 │ 6 │   │
 * ├──────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤     ┌───┐     ├───┼───┼───┼───┤
 * │ Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │     │ ↑ │     │ 1 │ 2 │ 3 │   │
 * ├─────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤ ┌───┼───┼───┐ ├───┴───┼───┤ E││
 * │ Ctrl│    │Alt │         Space         │ Alt│    │    │Ctrl│ │ ← │ ↓ │ → │ │   0   │ . │←─┘│
 * └─────┴────┴────┴───────────────────────┴────┴────┴────┴────┘ └───┴───┴───┘ └───────┴───┴───┘
 */

class RxDataTool {

    /**
     * outputStream转inputStream
     *
     * @param out 输出流
     * @return inputStream子类
     */
    fun output2InputStream(out: OutputStream?): ByteArrayInputStream? {
        return if (out == null) {
            null
        } else ByteArrayInputStream((out as ByteArrayOutputStream).toByteArray())
    }

    companion object {


        private val HEX_DIGITS = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )

        /**
         * 金额 格式化
         */
        private val AMOUNT_FORMAT = DecimalFormat("###,###,###,##0.00")

        /**
         * 判断字符串是否为空 为空即true
         *
         * @param str 字符串
         * @return
         */
        fun isNullString(str: String?): Boolean {
            return str == null || str.length == 0 || "null" == str
        }

        /**
         * 判断对象是否为空
         *
         * @param obj 对象
         * @return `true`: 为空<br></br>`false`: 不为空
         */
        fun isEmpty(obj: Any?): Boolean {
            if (obj == null) {
                return true
            }
            if (obj is String && obj.toString().length == 0) {
                return true
            }
            if (obj.javaClass.isArray && Array.getLength(obj) == 0) {
                return true
            }
            if (obj is Collection<*> && obj.isEmpty()) {
                return true
            }
            if (obj is Map<*, *> && obj.isEmpty()) {
                return true
            }
            if (obj is SparseArray<*> && obj.size() == 0) {
                return true
            }
            if (obj is SparseBooleanArray && obj.size() == 0) {
                return true
            }
            if (obj is SparseIntArray && obj.size() == 0) {
                return true
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                obj is SparseLongArray && obj.size() == 0
            } else false
        }

        /**
         * 判断字符串是否是整数
         */
        fun isInteger(value: String): Boolean {
            try {
                Integer.parseInt(value)
                return true
            } catch (e: NumberFormatException) {
                return false
            }

        }

        /**
         * 判断字符串是否是双精度浮点数
         */
        fun isDouble(value: String): Boolean {
            try {
                java.lang.Double.parseDouble(value)
                return value.contains(".")
            } catch (e: NumberFormatException) {
                return false
            }

        }

        /**
         * 判断字符串是否是数字
         */
        fun isNumber(value: String): Boolean {
            return isInteger(value) || isDouble(value)
        }

        /**
         * 根据日期判断星座
         *
         * @param month
         * @param day
         * @return
         */
        fun getAstro(month: Int, day: Int): String {
            val starArr = arrayOf(
                "魔羯座",
                "水瓶座",
                "双鱼座",
                "牡羊座",
                "金牛座",
                "双子座",
                "巨蟹座",
                "狮子座",
                "处女座",
                "天秤座",
                "天蝎座",
                "射手座"
            )
            val DayArr = intArrayOf(22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22)  // 两个星座分割日

            if (month <= 0 || day <= 0) {
                return "猴年马月座"
            } else if (month > 12 || day > 31) {
                return "猴年马月座"
            }

            var index = month
            // 所查询日期在分割日之前，索引-1，否则不变
            if (day < DayArr[month - 1]) {
                index = index - 1
            }
            // 返回索引指向的星座string
            return starArr[index]
        }

        /**
         * 隐藏手机中间4位号码
         * 130****0000
         *
         * @param mobile_phone 手机号码
         * @return 130****0000
         */
        fun hideMobilePhone4(mobile_phone: String): String {
            return if (mobile_phone.length != 11) {
                "手机号码不正确"
            } else mobile_phone.substring(0, 3) + "****" + mobile_phone.substring(7, 11)
        }

        /**
         * 格式化银行卡 加*
         * 3749 **** **** 330
         *
         * @param cardNo 银行卡
         * @return 3749 **** **** 330
         */
        fun formatCard(cardNo: String): String {
            if (cardNo.length < 8) {
                return "银行卡号有误"
            }
            var card: String
            card = cardNo.substring(0, 4) + " **** **** "
            card += cardNo.substring(cardNo.length - 4)
            return card
        }

        /**
         * 银行卡后四位
         *
         * @param cardNo
         * @return
         */
        fun formatCardEnd4(cardNo: String): String {
            if (cardNo.length < 8) {
                return "银行卡号有误"
            }
            var card = ""
            card += cardNo.substring(cardNo.length - 4)
            return card
        }

        /**
         * 字符串转换成整数 ,转换失败将会 return 0;
         *
         * @param str 字符串
         * @return
         */
        fun stringToInt(str: String): Int {
            return if (isNullString(str)) {
                0
            } else {
                try {
                    Integer.parseInt(str)
                } catch (e: NumberFormatException) {
                    0
                }

            }
        }

        /**
         * 字符串转换成整型数组
         *
         * @param s
         * @return
         */
        fun stringToInts(s: String): IntArray {
            val n = IntArray(s.length)
            if (RxDataTool.isNullString(s)) {

            } else {
                for (i in 0 until s.length) {
                    n[i] = Integer.parseInt(s.substring(i, i + 1))
                }
            }
            return n
        }

        /**
         * 整型数组求和
         *
         * @param ints
         * @return
         */
        fun intsGetSum(ints: IntArray): Int {
            var sum = 0

            var i = 0
            val len = ints.size
            while (i < len) {
                sum += ints[i]
                i++
            }

            return sum
        }

        /**
         * 字符串转换成long ,转换失败将会 return 0;
         *
         * @param str 字符串
         * @return
         */
        fun stringToLong(str: String): Long {
            return str.toLong()
        }

        /**
         * 字符串转换成double ,转换失败将会 return 0;
         *
         * @param str 字符串
         * @return
         */
        fun stringToDouble(str: String): Double {
            return if (isNullString(str)) {
                0.0
            } else {
                try {
                    java.lang.Double.parseDouble(str)
                } catch (e: NumberFormatException) {
                    0.0
                }

            }
        }

        /**
         * 字符串转换成浮点型 Float
         *
         * @param str 待转换的字符串
         * @return 转换后的 float
         */
        fun stringToFloat(str: String): Float {
            return if (isNullString(str)) {
                0f
            } else {
                try {
                    java.lang.Float.parseFloat(str)
                } catch (e: NumberFormatException) {
                    0f
                }

            }
        }

        /**
         * 将字符串格式化为带两位小数的字符串
         *
         * @param str 字符串
         * @return
         */
        fun format2Decimals(str: String): String {
            val df = DecimalFormat("#.00")
            return if (df.format(stringToDouble(str)).startsWith(".")) {
                "0" + df.format(stringToDouble(str))
            } else {
                df.format(stringToDouble(str))
            }
        }

        /**
         * 字符串转InputStream
         *
         * @param str
         * @return
         */
        fun StringToInputStream(str: String): InputStream {
//InputStream   in_withcode   =   new ByteArrayInputStream(str.getBytes("UTF-8"));
            return ByteArrayInputStream(str.toByteArray())
        }

        /**
         * 首字母大写
         *
         * @param s 待转字符串
         * @return 首字母大写字符串
         */
        fun upperFirstLetter(s: String): String? {
            return if (isNullString(s) || !Character.isLowerCase(s[0])) {
                s
            } else (s[0].toInt() - 32).toChar().toString() + s.substring(1)
        }

        /**
         * 首字母小写
         *
         * @param s 待转字符串
         * @return 首字母小写字符串
         */
        fun lowerFirstLetter(s: String): String? {
            return if (isNullString(s) || !Character.isUpperCase(s[0])) {
                s
            } else (s[0].toInt() + 32).toChar().toString() + s.substring(1)
        }

        /**
         * 反转字符串
         *
         * @param s 待反转字符串
         * @return 反转字符串
         */
        fun reverse(s: String): String {
            val len = s.length
            if (len <= 1) {
                return s
            }
            val mid = len shr 1
            val chars = s.toCharArray()
            var c: Char
            for (i in 0 until mid) {
                c = chars[i]
                chars[i] = chars[len - i - 1]
                chars[len - i - 1] = c
            }
            return String(chars)
        }

        /**
         * 转化为半角字符
         *
         * @param s 待转字符串
         * @return 半角字符串
         */
        fun toDBC(s: String): String? {
            if (isNullString(s)) {
                return s
            }
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                if (chars[i].toInt() == 12288) {
                    chars[i] = ' '
                } else if (65281 <= chars[i].toInt() && chars[i].toInt() <= 65374) {
                    chars[i] = (chars[i].toInt() - 65248).toChar()
                } else {
                    chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }

        /**
         * 转化为全角字符
         *
         * @param s 待转字符串
         * @return 全角字符串
         */
        fun toSBC(s: String): String? {
            if (isNullString(s)) {
                return s
            }
            val chars = s.toCharArray()
            var i = 0
            val len = chars.size
            while (i < len) {
                if (chars[i] == ' ') {
                    chars[i] = 12288.toChar()
                } else if (33 <= chars[i].toInt() && chars[i].toInt() <= 126) {
                    chars[i] = (chars[i].toInt() + 65248).toChar()
                } else {
                    chars[i] = chars[i]
                }
                i++
            }
            return String(chars)
        }

        /**
         * 单个汉字转成ASCII码
         *
         * @param s 单个汉字字符串
         * @return 如果字符串长度是1返回的是对应的ascii码，否则返回-1
         */
        fun oneCn2ASCII(s: String): Int {
            if (s.length != 1) {
                return -1
            }
            var ascii = 0
            try {
                val bytes = s.toByteArray(charset("GB2312"))
                if (bytes.size == 1) {
                    ascii = bytes[0].toInt()
                } else if (bytes.size == 2) {
                    val highByte = 256 + bytes[0]
                    val lowByte = 256 + bytes[1]
                    ascii = 256 * highByte + lowByte - 256 * 256
                } else {
                    throw IllegalArgumentException("Illegal resource string")
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            return ascii
        }

        /**
         * 单个汉字转成拼音
         *
         * @param s 单个汉字字符串
         * @return 如果字符串长度是1返回的是对应的拼音，否则返回`null`
         */
        fun oneCn2PY(s: String): String? {
            val ascii = oneCn2ASCII(s)
            if (ascii == -1) {
                return null
            }
            var ret: String? = null
            if (0 <= ascii && ascii <= 127) {
                ret = ascii.toChar().toString()
            } else {
                for (i in pyValue.indices.reversed()) {
                    if (pyValue[i] <= ascii) {
                        ret = pyStr[i]
                        break
                    }
                }
            }
            return ret
        }

        /**
         * 获得第一个汉字首字母
         *
         * @param s 单个汉字字符串
         * @return 拼音
         */
        fun getPYFirstLetter(s: String): String? {
            if (isNullString(s)) {
                return ""
            }
            val first: String
            val py: String?
            first = s.substring(0, 1)
            py = oneCn2PY(first)
            return py?.substring(0, 1)
        }

        /**
         * 中文转拼音
         *
         * @param s 汉字字符串
         * @return 拼音
         */
        fun cn2PY(s: String): String {
            var hz: String
            var py: String?
            val sb = StringBuilder()
            for (i in 0 until s.length) {
                hz = s.substring(i, i + 1)
                py = oneCn2PY(hz)
                if (py == null) {
                    py = "?"
                }
                sb.append(py)
            }
            return sb.toString()
        }

        /**
         * byteArr转hexString
         *
         * 例如：
         * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
         *
         * @param bytes byte数组
         * @return 16进制大写字符串
         */
//        fun bytes2HexString(bytes: ByteArray): String {
//            val ret = CharArray(bytes.size shl 1)
//            var i = 0
//            var j = 0
//            while (i < bytes.size) {
//                ret[j++] = HEX_DIGITS[bytes[i].ushr(4) and 0x0f]
//                ret[j++] = HEX_DIGITS[(bytes[i] and 0x0f).toInt()]
//                i++
//            }
//            return String(ret)
//        }

        /**
         * hexString转byteArr
         *
         * 例如：
         * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
         *
         * @param hexString 十六进制字符串
         * @return 字节数组
         */
        fun hexString2Bytes(hexString: String): ByteArray {
            val len = hexString.length
            if (len % 2 != 0) {
                throw IllegalArgumentException("长度不是偶数")
            }
            val hexBytes = hexString.toUpperCase().toCharArray()
            val ret = ByteArray(len.ushr(1))
            var i = 0
            while (i < len) {
                ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
                i += 2
            }
            return ret
        }

        /**
         * hexChar转int
         *
         * @param hexChar hex单个字节
         * @return 0..15
         */
        private fun hex2Dec(hexChar: Char): Int {
            return if (hexChar >= '0' && hexChar <= '9') {
                hexChar - '0'
            } else if (hexChar >= 'A' && hexChar <= 'F') {
                hexChar - 'A' + 10
            } else {
                throw IllegalArgumentException()
            }
        }

        /**
         * charArr转byteArr
         *
         * @param chars 字符数组
         * @return 字节数组
         */
        fun chars2Bytes(chars: CharArray): ByteArray {
            val len = chars.size
            val bytes = ByteArray(len)
            for (i in 0 until len) {
                bytes[i] = chars[i].toByte()
            }
            return bytes
        }

        /**
         * byteArr转charArr
         *
         * @param bytes 字节数组
         * @return 字符数组
         */
        fun bytes2Chars(bytes: ByteArray): CharArray {
            val len = bytes.size
            val chars = CharArray(len)
            for (i in 0 until len) {
                chars[i] = (bytes[i] and 0xff.toByte()).toChar()
            }
            return chars
        }

        /**
         * 字节数转以unit为单位的size
         *
         * @param byteNum 字节数
         * @param unit
         *  * [RxConstTool.MemoryUnit.BYTE]: 字节
         *  * [RxConstTool.MemoryUnit.KB]  : 千字节
         *  * [RxConstTool.MemoryUnit.MB]  : 兆
         *  * [RxConstTool.MemoryUnit.GB]  : GB
         *
         * @return 以unit为单位的size
         */
        fun byte2Size(byteNum: Long, unit: RxConstTool.MemoryUnit): Double {
            if (byteNum < 0) {
                return -1.0
            }
            when (unit) {
                RxConstTool.MemoryUnit.BYTE -> return byteNum.toDouble() / BYTE
                RxConstTool.MemoryUnit.KB -> return byteNum.toDouble() / KB
                RxConstTool.MemoryUnit.MB -> return byteNum.toDouble() / MB
                RxConstTool.MemoryUnit.GB -> return byteNum.toDouble() / GB
                else -> return byteNum.toDouble() / BYTE
            }
        }

        /**
         * 以unit为单位的size转字节数
         *
         * @param size 大小
         * @param unit
         *  * [RxConstTool.MemoryUnit.BYTE]: 字节
         *  * [RxConstTool.MemoryUnit.KB]  : 千字节
         *  * [RxConstTool.MemoryUnit.MB]  : 兆
         *  * [RxConstTool.MemoryUnit.GB]  : GB
         *
         * @return 字节数
         */
        fun size2Byte(size: Long, unit: RxConstTool.MemoryUnit): Long {
            if (size < 0) {
                return -1
            }
            when (unit) {
                RxConstTool.MemoryUnit.BYTE -> return size * BYTE
                RxConstTool.MemoryUnit.KB -> return size * KB
                RxConstTool.MemoryUnit.MB -> return size * MB
                RxConstTool.MemoryUnit.GB -> return size * GB
                else -> return size * BYTE
            }
        }

        /**
         * 字节数转合适大小
         *
         * 保留3位小数
         *
         * @param byteNum 字节数
         * @return 1...1024 unit
         */
        fun byte2FitSize(byteNum: Long): String {
            return if (byteNum < 0) {
                "shouldn't be less than zero!"
            } else if (byteNum < KB) {
                String.format(Locale.getDefault(), "%.3fB", byteNum.toDouble())
            } else if (byteNum < MB) {
                String.format(Locale.getDefault(), "%.3fKB", byteNum.toDouble() / KB)
            } else if (byteNum < GB) {
                String.format(Locale.getDefault(), "%.3fMB", byteNum.toDouble() / MB)
            } else {
                String.format(Locale.getDefault(), "%.3fGB", byteNum.toDouble() / GB)
            }
        }


        /**
         * inputStream转byteArr
         *
         * @param is 输入流
         * @return 字节数组
         */
        fun inputStream2Bytes(`is`: InputStream): ByteArray? {
//            return input2OutputStream(`is`)!!.toByteArray()
            return null
        }

        /**
         * byteArr转inputStream
         *
         * @param bytes 字节数组
         * @return 输入流
         */
        fun bytes2InputStream(bytes: ByteArray): InputStream {
            return ByteArrayInputStream(bytes)
        }

        /**
         * outputStream转byteArr
         *
         * @param out 输出流
         * @return 字节数组
         */
        fun outputStream2Bytes(out: OutputStream?): ByteArray? {
            return if (out == null) {
                null
            } else (out as ByteArrayOutputStream).toByteArray()
        }

        /**
         * outputStream转byteArr
         *
         * @param bytes 字节数组
         * @return 字节数组
         */
        fun bytes2OutputStream(bytes: ByteArray): OutputStream? {
            var os: ByteArrayOutputStream? = null
            try {
                os = ByteArrayOutputStream()
                os.write(bytes)
                return os
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                RxFileTool.closeIO(os!!)
            }
        }


        /**
         * string转inputStream按编码
         *
         * @param string      字符串
         * @param charsetName 编码格式
         * @return 输入流
         */
        fun string2InputStream(string: String?, charsetName: String): InputStream? {
            if (string == null || isNullString(charsetName)) {
                return null
            }
            try {
                return ByteArrayInputStream(string.toByteArray(charset(charsetName)))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            }

        }


        /**
         * string转outputStream按编码
         *
         * @param string      字符串
         * @param charsetName 编码格式
         * @return 输入流
         */
        fun string2OutputStream(string: String?, charsetName: String): OutputStream? {
            if (string == null || isNullString(charsetName)) {
                return null
            }
            try {
                return bytes2OutputStream(string.toByteArray(charset(charsetName)))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 金额格式化
         *
         * @param value 数值
         * @return
         */
        fun getAmountValue(value: Double): String {
            return AMOUNT_FORMAT.format(value)
        }

        /**
         * 金额格式化
         *
         * @param value 数值
         * @return
         */
        fun getAmountValue(value: String): String {
            return if (isNullString(value)) {
                "0"
            } else AMOUNT_FORMAT.format(java.lang.Double.parseDouble(value))
        }

        /**
         * 四舍五入
         *
         * @param value 数值
         * @param digit 保留小数位
         * @return
         */
        fun getRoundUp(value: BigDecimal, digit: Int): String {
            return value.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
        }

        /**
         * 四舍五入
         *
         * @param value 数值
         * @param digit 保留小数位
         * @return
         */
        fun getRoundUp(value: Double, digit: Int): String {
            val result = BigDecimal(value)
            return result.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
        }

        /**
         * 四舍五入
         *
         * @param value 数值
         * @param digit 保留小数位
         * @return
         */
        fun getRoundUp(value: String, digit: Int): String {
            if (isNullString(value)) {
                return "0"
            }
            val result = BigDecimal(java.lang.Double.parseDouble(value))
            return result.setScale(digit, BigDecimal.ROUND_HALF_UP).toString()
        }

        /**
         * 获取百分比（乘100）
         *
         * @param value 数值
         * @param digit 保留小数位
         * @return
         */
        fun getPercentValue(value: BigDecimal, digit: Int): String {
            val result = value.multiply(BigDecimal(100))
            return getRoundUp(result, digit)
        }

        /**
         * 获取百分比（乘100）
         *
         * @param value 数值
         * @param digit 保留小数位
         * @return
         */
        fun getPercentValue(value: Double, digit: Int): String {
            val result = BigDecimal(value)
            return getPercentValue(result, digit)
        }

        /**
         * 获取百分比（乘100,保留两位小数）
         *
         * @param value 数值
         * @return
         */
        fun getPercentValue(value: Double): String {
            val result = BigDecimal(value)
            return getPercentValue(result, 2)
        }

        fun changeDistance(length: Double, displayMeter: Boolean): String {
            return if (length < 1000) {
                RxConstants.FORMAT_TWO.format(length) + if (displayMeter) "米" else ""
            } else {
                RxConstants.FORMAT_TWO.format(length / 1000) + if (displayMeter) "千米" else ""
            }
        }

        private val pyValue = intArrayOf(
            -20319,
            -20317,
            -20304,
            -20295,
            -20292,
            -20283,
            -20265,
            -20257,
            -20242,
            -20230,
            -20051,
            -20036,
            -20032,
            -20026,
            -20002,
            -19990,
            -19986,
            -19982,
            -19976,
            -19805,
            -19784,
            -19775,
            -19774,
            -19763,
            -19756,
            -19751,
            -19746,
            -19741,
            -19739,
            -19728,
            -19725,
            -19715,
            -19540,
            -19531,
            -19525,
            -19515,
            -19500,
            -19484,
            -19479,
            -19467,
            -19289,
            -19288,
            -19281,
            -19275,
            -19270,
            -19263,
            -19261,
            -19249,
            -19243,
            -19242,
            -19238,
            -19235,
            -19227,
            -19224,
            -19218,
            -19212,
            -19038,
            -19023,
            -19018,
            -19006,
            -19003,
            -18996,
            -18977,
            -18961,
            -18952,
            -18783,
            -18774,
            -18773,
            -18763,
            -18756,
            -18741,
            -18735,
            -18731,
            -18722,
            -18710,
            -18697,
            -18696,
            -18526,
            -18518,
            -18501,
            -18490,
            -18478,
            -18463,
            -18448,
            -18447,
            -18446,
            -18239,
            -18237,
            -18231,
            -18220,
            -18211,
            -18201,
            -18184,
            -18183,
            -18181,
            -18012,
            -17997,
            -17988,
            -17970,
            -17964,
            -17961,
            -17950,
            -17947,
            -17931,
            -17928,
            -17922,
            -17759,
            -17752,
            -17733,
            -17730,
            -17721,
            -17703,
            -17701,
            -17697,
            -17692,
            -17683,
            -17676,
            -17496,
            -17487,
            -17482,
            -17468,
            -17454,
            -17433,
            -17427,
            -17417,
            -17202,
            -17185,
            -16983,
            -16970,
            -16942,
            -16915,
            -16733,
            -16708,
            -16706,
            -16689,
            -16664,
            -16657,
            -16647,
            -16474,
            -16470,
            -16465,
            -16459,
            -16452,
            -16448,
            -16433,
            -16429,
            -16427,
            -16423,
            -16419,
            -16412,
            -16407,
            -16403,
            -16401,
            -16393,
            -16220,
            -16216,
            -16212,
            -16205,
            -16202,
            -16187,
            -16180,
            -16171,
            -16169,
            -16158,
            -16155,
            -15959,
            -15958,
            -15944,
            -15933,
            -15920,
            -15915,
            -15903,
            -15889,
            -15878,
            -15707,
            -15701,
            -15681,
            -15667,
            -15661,
            -15659,
            -15652,
            -15640,
            -15631,
            -15625,
            -15454,
            -15448,
            -15436,
            -15435,
            -15419,
            -15416,
            -15408,
            -15394,
            -15385,
            -15377,
            -15375,
            -15369,
            -15363,
            -15362,
            -15183,
            -15180,
            -15165,
            -15158,
            -15153,
            -15150,
            -15149,
            -15144,
            -15143,
            -15141,
            -15140,
            -15139,
            -15128,
            -15121,
            -15119,
            -15117,
            -15110,
            -15109,
            -14941,
            -14937,
            -14933,
            -14930,
            -14929,
            -14928,
            -14926,
            -14922,
            -14921,
            -14914,
            -14908,
            -14902,
            -14894,
            -14889,
            -14882,
            -14873,
            -14871,
            -14857,
            -14678,
            -14674,
            -14670,
            -14668,
            -14663,
            -14654,
            -14645,
            -14630,
            -14594,
            -14429,
            -14407,
            -14399,
            -14384,
            -14379,
            -14368,
            -14355,
            -14353,
            -14345,
            -14170,
            -14159,
            -14151,
            -14149,
            -14145,
            -14140,
            -14137,
            -14135,
            -14125,
            -14123,
            -14122,
            -14112,
            -14109,
            -14099,
            -14097,
            -14094,
            -14092,
            -14090,
            -14087,
            -14083,
            -13917,
            -13914,
            -13910,
            -13907,
            -13906,
            -13905,
            -13896,
            -13894,
            -13878,
            -13870,
            -13859,
            -13847,
            -13831,
            -13658,
            -13611,
            -13601,
            -13406,
            -13404,
            -13400,
            -13398,
            -13395,
            -13391,
            -13387,
            -13383,
            -13367,
            -13359,
            -13356,
            -13343,
            -13340,
            -13329,
            -13326,
            -13318,
            -13147,
            -13138,
            -13120,
            -13107,
            -13096,
            -13095,
            -13091,
            -13076,
            -13068,
            -13063,
            -13060,
            -12888,
            -12875,
            -12871,
            -12860,
            -12858,
            -12852,
            -12849,
            -12838,
            -12831,
            -12829,
            -12812,
            -12802,
            -12607,
            -12597,
            -12594,
            -12585,
            -12556,
            -12359,
            -12346,
            -12320,
            -12300,
            -12120,
            -12099,
            -12089,
            -12074,
            -12067,
            -12058,
            -12039,
            -11867,
            -11861,
            -11847,
            -11831,
            -11798,
            -11781,
            -11604,
            -11589,
            -11536,
            -11358,
            -11340,
            -11339,
            -11324,
            -11303,
            -11097,
            -11077,
            -11067,
            -11055,
            -11052,
            -11045,
            -11041,
            -11038,
            -11024,
            -11020,
            -11019,
            -11018,
            -11014,
            -10838,
            -10832,
            -10815,
            -10800,
            -10790,
            -10780,
            -10764,
            -10587,
            -10544,
            -10533,
            -10519,
            -10331,
            -10329,
            -10328,
            -10322,
            -10315,
            -10309,
            -10307,
            -10296,
            -10281,
            -10274,
            -10270,
            -10262,
            -10260,
            -10256,
            -10254
        )
        private val pyStr = arrayOf(
            /*A*/
            "a",
            "ai",
            "an",
            "ang",
            "ao",
            "ba",
            "bai",
            "ban",
            "bang",
            "bao",

            /*B*/
            "bei",
            "ben",
            "beng",
            "bi",
            "bian",
            "biao",
            "bie",
            "bin",
            "bing",
            "bo",
            "bu",

            /*C*/
            "ca",
            "cai",
            "can",
            "cang",
            "cao",
            "ce",
            "ceng",
            "cha",
            "chai",
            "chan",
            "chang",
            "chao",
            "che",
            "chen",
            "cheng",
            "chi",
            "chong",
            "chou",
            "chu",
            "chuai",
            "chuan",
            "chuang",
            "chui",
            "chun",
            "chuo",
            "ci",
            "cong",
            "cou",
            "cu",
            "cuan",
            "cui",
            "cun",
            "cuo",

            /*D*/
            "da",
            "dai",
            "dan",
            "dang",
            "dao",
            "de",
            "deng",
            "di",
            "dian",
            "diao",
            "die",
            "ding",
            "diu",
            "dong",
            "dou",
            "du",
            "duan",
            "dui",
            "dun",
            "duo",

            /*E*/
            "e",
            "en",
            "er",

            /*F*/
            "fa",
            "fan",
            "fang",
            "fei",
            "fen",
            "feng",
            "fo",
            "fou",
            "fu",

            /*G*/
            "ga",
            "gai",
            "gan",
            "gang",
            "gao",
            "ge",
            "gei",
            "gen",
            "geng",
            "gong",
            "gou",
            "gu",
            "gua",
            "guai",
            "guan",
            "guang",
            "gui",
            "gun",
            "guo",

            /*H*/
            "ha",
            "hai",
            "han",
            "hang",
            "hao",
            "he",
            "hei",
            "hen",
            "heng",
            "hong",
            "hou",
            "hu",
            "hua",
            "huai",
            "huan",
            "huang",
            "hui",
            "hun",
            "huo",

            /*J*/
            "ji",
            "jia",
            "jian",
            "jiang",
            "jiao",
            "jie",
            "jin",
            "jing",
            "jiong",
            "jiu",
            "ju",
            "juan",
            "jue",
            "jun",

            /*K*/
            "ka",
            "kai",
            "kan",
            "kang",
            "kao",
            "ke",
            "ken",
            "keng",
            "kong",
            "kou",
            "ku",
            "kua",
            "kuai",
            "kuan",
            "kuang",
            "kui",
            "kun",
            "kuo",

            /*L*/
            "la",
            "lai",
            "lan",
            "lang",
            "lao",
            "le",
            "lei",
            "leng",
            "li",
            "lia",
            "lian",
            "liang",
            "liao",
            "lie",
            "lin",
            "ling",
            "liu",
            "long",
            "lou",
            "lu",
            "lv",
            "luan",
            "lue",
            "lun",
            "luo",

            /*M*/
            "ma",
            "mai",
            "man",
            "mang",
            "mao",
            "me",
            "mei",
            "men",
            "meng",
            "mi",
            "mian",
            "miao",
            "mie",
            "min",
            "ming",
            "miu",
            "mo",
            "mou",
            "mu",

            /*N*/
            "na",
            "nai",
            "nan",
            "nang",
            "nao",
            "ne",
            "nei",
            "nen",
            "neng",
            "ni",
            "nian",
            "niang",
            "niao",
            "nie",
            "nin",
            "ning",
            "niu",
            "nong",
            "nu",
            "nv",
            "nuan",
            "nue",
            "nuo",

            /*O*/
            "o",
            "ou",

            /*P*/
            "pa",
            "pai",
            "pan",
            "pang",
            "pao",
            "pei",
            "pen",
            "peng",
            "pi",
            "pian",
            "piao",
            "pie",
            "pin",
            "ping",
            "po",
            "pu",

            /*Q*/
            "qi",
            "qia",
            "qian",
            "qiang",
            "qiao",
            "qie",
            "qin",
            "qing",
            "qiong",
            "qiu",
            "qu",
            "quan",
            "que",
            "qun",

            /*R*/
            "ran",
            "rang",
            "rao",
            "re",
            "ren",
            "reng",
            "ri",
            "rong",
            "rou",
            "ru",
            "ruan",
            "rui",
            "run",
            "ruo",

            /*S*/
            "sa",
            "sai",
            "san",
            "sang",
            "sao",
            "se",
            "sen",
            "seng",
            "sha",
            "shai",
            "shan",
            "shang",
            "shao",
            "she",
            "shen",
            "sheng",
            "shi",
            "shou",
            "shu",
            "shua",
            "shuai",
            "shuan",
            "shuang",
            "shui",
            "shun",
            "shuo",
            "si",
            "song",
            "sou",
            "su",
            "suan",
            "sui",
            "sun",
            "suo",

            /*T*/
            "ta",
            "tai",
            "tan",
            "tang",
            "tao",
            "te",
            "teng",
            "ti",
            "tian",
            "tiao",
            "tie",
            "ting",
            "tong",
            "tou",
            "tu",
            "tuan",
            "tui",
            "tun",
            "tuo",

            /*W*/
            "wa",
            "wai",
            "wan",
            "wang",
            "wei",
            "wen",
            "weng",
            "wo",
            "wu",

            /*X*/
            "xi",
            "xia",
            "xian",
            "xiang",
            "xiao",
            "xie",
            "xin",
            "xing",
            "xiong",
            "xiu",
            "xu",
            "xuan",
            "xue",
            "xun",

            /*Y*/
            "ya",
            "yan",
            "yang",
            "yao",
            "ye",
            "yi",
            "yin",
            "ying",
            "yo",
            "yong",
            "you",
            "yu",
            "yuan",
            "yue",
            "yun",

            /*Z*/
            "za",
            "zai",
            "zan",
            "zang",
            "zao",
            "ze",
            "zei",
            "zen",
            "zeng",
            "zha",
            "zhai",
            "zhan",
            "zhang",
            "zhao",
            "zhe",
            "zhen",
            "zheng",
            "zhi",
            "zhong",
            "zhou",
            "zhu",
            "zhua",
            "zhuai",
            "zhuan",
            "zhuang",
            "zhui",
            "zhun",
            "zhuo",
            "zi",
            "zong",
            "zou",
            "zu",
            "zuan",
            "zui",
            "zun",
            "zuo"
        )
    }

}
