package com.rz.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.rz.utils.RxFileTool.Companion.getDataColumn
import com.rz.utils.RxFileTool.Companion.isDownloadsDocument
import com.rz.utils.RxFileTool.Companion.isExternalStorageDocument
import com.rz.utils.RxFileTool.Companion.isGooglePhotosUri
import com.rz.utils.RxFileTool.Companion.isMediaDocument
import java.io.File

/**
 * @author vondear
 * @date 2016/1/24
 */

object RxPhotoTool {
    val GET_IMAGE_BY_CAMERA = 5001
    val GET_IMAGE_FROM_PHONE = 5002
    val CROP_IMAGE = 5003
    var imageUriFromCamera: Uri? = null
    var cropImageUri: Uri? = null

    /**
     * 拍照
     */
    fun openCameraFile(context: Context, file: File) {
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Android7.0以上URI
        //Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            val mProviderUri =
                FileProvider.getUriForFile(context, "com.flash.rider.fileProvider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mProviderUri)
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            val uri = Uri.fromFile(file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        try {
            if (context is Activity) {
                context.startActivityForResult(intent, GET_IMAGE_BY_CAMERA)
            }
        } catch (anf: ActivityNotFoundException) {
            anf.printStackTrace()
        }

    }


    fun openLocalImage(activity: Activity) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        activity.startActivityForResult(intent, GET_IMAGE_FROM_PHONE)
    }

    fun openLocalImage(fragment: Fragment) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        fragment.startActivityForResult(intent, GET_IMAGE_FROM_PHONE)
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param context
     * @return 图片的uri
     */
    fun openCamera(context: Context, fileName: File) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var mImageCaptureUri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //临时添加一个拍照权限
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            mImageCaptureUri = FileProvider.getUriForFile(
                context,
                "com.flash.rider.fileProvider", fileName
            )
        } else {
            mImageCaptureUri = Uri.fromFile(fileName)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera)
        (context as Activity).startActivityForResult(intent, GET_IMAGE_BY_CAMERA)
    }


    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     * @author yaoxing
     * @date 2014-10-12
     */
    @TargetApi(19)
    fun getImageAbsolutePath(context: Context?, imageUri: Uri?): String? {
        if (context == null || imageUri == null) {
            return null
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                context,
                imageUri
            )
        ) {
            if (isExternalStorageDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(imageUri)) {
                val id = DocumentsContract.getDocumentId(imageUri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } // MediaStore (and general)
        else if ("content".equals(imageUri.scheme!!, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(imageUri)) {
                imageUri.lastPathSegment
            } else getDataColumn(context, imageUri, null, null)
        } else if ("file".equals(imageUri.scheme!!, ignoreCase = true)) {
            return imageUri.path
        }// File
        return null
    }


}
