package com.rz.utils.file

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider

import java.io.File

/**
 * 作者  ${mike_fxb} on 17/11/23.
 */

object RxFileProvider7 {
    /**
     * @param context
     * @param file
     * @return
     */

    fun getUriForFile(context: Context, file: File): Uri? {
        var fileUri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = getUriForFile24(context, file)
        } else {
            fileUri = Uri.fromFile(file)
        }
        return fileUri
    }

    private fun getUriForFile24(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider", file
        )
    }

    /**
     * "
     * FLAG_GRANT_READ_URI_PERMISSION：表示读取权限；
     * FLAG_GRANT_WRITE_URI_PERMISSION：表示写入权限。
     */

    fun grantPermissions(
        context: Context, intent: Intent, uri: Uri,
        writeAble: Boolean
    ) {

        var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (writeAble) {
            flag = flag or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        intent.addFlags(flag)
        val resInfoList = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, flag)
        }
    }

    fun setIntentData(context: Context, intent: Intent, file: File, writeAble: Boolean) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.data = getUriForFile(context, file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.data = Uri.fromFile(file)
        }
    }

    fun setIntentDataAndType(
        context: Context, intent: Intent, type: String, file: File,
        writeAble: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file), type)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }
    }
}
