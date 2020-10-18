package com.rz.utils.file

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File

object FileUtils {

    val localSavePath: String
        get() = Environment.getExternalStorageDirectory().absolutePath


    val localFileCachePath: String
        get() = "$localSavePath/shansong/SdcardImage/"

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.applicationContext
            .contentResolver
            .query(contentUri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }

    /**
     * 删除目录下地文件
     */
    fun deleteFilePath(file: File) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile) { // 判断是否是文件
                file.delete() // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory) { // 否则如果它是一个目录
                val files = file.listFiles() // 声明目录下所有的文件 files[];
                for (i in files.indices) { // 遍历目录下所有的文件
                    deleteFilePath(files[i]) // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete()
        } else {
            Log.d("fxb", "文件不存在！" + "\n")
        }
    }
}
