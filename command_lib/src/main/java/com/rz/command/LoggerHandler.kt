package com.rz.command

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.NonNull
import java.io.File
import java.io.FileWriter
import java.io.IOException


/**
 * 作者：iss on 2020/6/26 22:36
 * 邮箱：55921173@qq.com
 * 类备注：
 */
class LoggerHandler(
    @NonNull looper: Looper,
    @NonNull folder: String,
    maxFileSize: Int,
    var time: String
) : Handler(looper) {
    @NonNull
    private val folder: String
    private val maxFileSize: Int
    override fun handleMessage(@NonNull msg: Message) {
        val content = msg.obj as String
        var fileWriter: FileWriter? = null
        val logFile = getLogFile(folder, "logs")
        try {
            fileWriter = FileWriter(logFile, true)
            writeLog(fileWriter, content)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: IOException) {
            if (fileWriter != null) {
                try {
                    fileWriter.flush()
                    fileWriter.close()
                } catch (e1: IOException) { /* fail silently */
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun writeLog(
        @NonNull fileWriter: FileWriter,
        @NonNull content: String
    ) {
        fileWriter
        content
        fileWriter.append(content)
    }

    private fun getLogFile(
        @NonNull folderName: String,
        @NonNull fileName: String
    ): File {
        folderName
        fileName
        val folder = File(folderName)
        if (!folder.exists()) {
            //TODO: What if folder is not created, what happens then?
            folder.mkdirs()
        }
        var newFileCount = 0
        var newFile: File
        var existingFile: File? = null
        newFile =
            File(folder, String.format("%s_%s.csv", "$time", newFileCount))
        while (newFile.exists()) {
            existingFile = newFile
            newFileCount++
            newFile =
                File(folder, String.format("%s_%s.csv", "$time", newFileCount))
        }
        return if (existingFile != null) {
            if (existingFile.length() >= maxFileSize) {
                newFile
            } else existingFile
        } else newFile
    }

    init {
        this.folder = folder
        this.maxFileSize = maxFileSize
    }
}