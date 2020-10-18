package com.rz.utils.sp

import android.content.Context
import android.text.TextUtils
import java.io.*

/**
 * 作者  ${mike_fxb} on 17/4/19.
 */

object RxObjectSaveUtil {

    val TAG = RxObjectSaveUtil::class.java.name
    private val FILENAME = "SAVE_OBJECT_File"
    private val KEY_COUNTRY = "countryCity"
    private val KEY_LAST_LOCATION = "last_location"

    private fun saveObject(context: Context, obj: Any, fileName: String, key: String) {
        try {
            // 保存对象
            val sharedata = context.getSharedPreferences(fileName, 0).edit()
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            val bos = ByteArrayOutputStream()
            val os = ObjectOutputStream(bos)
            //将对象序列化写入byte缓存
            os.writeObject(obj)
            //将序列化的数据转为16进制保存
            val bytesToHexString = bytesToHexString(bos.toByteArray())
            //保存该16进制数组
            sharedata.putString(key, bytesToHexString)
            sharedata.commit()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    /**
     * desc:将数组转为16进制
     *
     * @return modified:
     */
    private fun bytesToHexString(bArray: ByteArray?): String? {
        if (bArray == null) {
            return null
        }
        if (bArray.size == 0) {
            return ""
        }
        val sb = StringBuffer(bArray.size)
        var sTemp: String
        for (i in bArray.indices) {
            sTemp = Integer.toHexString(0xFF and bArray[i].toInt())
            if (sTemp.length < 2) sb.append(0)
            sb.append(sTemp.toUpperCase())
        }
        return sb.toString()
    }

    private fun readObject(context: Context, fileName: String, key: String): Any? {
        try {
            val sharedata = context.getSharedPreferences(fileName, 0)
            if (sharedata.contains(KEY_COUNTRY)) {
                val string = sharedata.getString(key, "")
                if (TextUtils.isEmpty(string)) {
                    return null
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    val stringToBytes = StringToBytes(string!!)
                    val bis = ByteArrayInputStream(stringToBytes)
                    val `is` = ObjectInputStream(bis)
                    //返回反序列化得到的对象
                    return `is`.readObject()
                }
            }
        } catch (e: StreamCorruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        //所有异常返回null
        return null
    }


    /**
     * desc:将16进制的数据转为数组
     *
     * 创建人：聂旭阳 , 2014-5-25 上午11:08:33
     *
     * @return modified:
     */
    private fun StringToBytes(data: String): ByteArray? {
        val hexString = data.toUpperCase().trim { it <= ' ' }
        if (hexString.length % 2 != 0) {
            return null
        }
        val retData = ByteArray(hexString.length / 2)
        var i = 0
        while (i < hexString.length) {
            val int_ch: Int  // 两位16进制数转化后的10进制数
            val hex_char1 = hexString[i] ////两位16进制数中的第一位(高位*16)
            val int_ch3: Int
            if (hex_char1 >= '0' && hex_char1 <= '9') {
                int_ch3 = (hex_char1.toInt() - 48) * 16   //// 0 的Ascll - 48
            } else if (hex_char1 >= 'A' && hex_char1 <= 'F') {
                int_ch3 = (hex_char1.toInt() - 55) * 16 //// A 的Ascll - 65
            } else {
                return null
            }
            i++
            val hex_char2 = hexString[i] ///两位16进制数中的第二位(低位)
            val int_ch4: Int
            if (hex_char2 >= '0' && hex_char2 <= '9') {
                int_ch4 = hex_char2.toInt() - 48 //// 0 的Ascll - 48
            } else if (hex_char2 >= 'A' && hex_char2 <= 'F') {
                int_ch4 = hex_char2.toInt() - 55 //// A 的Ascll - 65
            } else {
                return null
            }
            int_ch = int_ch3 + int_ch4
            retData[i / 2] = int_ch.toByte()//将转化后的数放入Byte里
            i++
        }
        return retData
    }

    fun clearObjectFile(context: Context) {
        try {
            val editable = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit()
            editable.putString(KEY_COUNTRY, "")
            editable.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * desc:保存对象
     *
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     * modified:
     */
    fun saveCountryCity(context: Context, obj: Any) {
        saveObject(context, obj, FILENAME, KEY_COUNTRY)
    }

    /**
     * desc:获取保存的Object对象
     *
     * @return modified:
     */
    fun readCountryCity(context: Context): Any? {
        return readObject(context, FILENAME, KEY_COUNTRY)
    }

    /**
     * desc:保存最后一次定位的位置
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     * modified:
     */
    fun saveLastLocation(context: Context, obj: Any) {
        saveObject(context, obj, FILENAME, KEY_LAST_LOCATION)
    }

    /**
     * desc:获取最后一次定位的位置
     *
     * @return modified:
     */
    fun readLastLocation(context: Context): Any? {
        return readObject(context, FILENAME, KEY_LAST_LOCATION)
    }


}
