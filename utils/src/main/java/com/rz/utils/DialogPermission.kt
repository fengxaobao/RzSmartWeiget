package com.rz.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

/**
 * 权限提示对话框
 * Created by yf on 2016/7/25.
 */
class DialogPermission(internal var mContext: Context, internal var mNotice: String) {

    init {
        showDialog()
    }

    private fun showDialog() {
        AlertDialog.Builder(mContext).setTitle("系统提示")//设置对话框标题

            .setMessage(mNotice)//设置显示的内容

            .setPositiveButton("设置") { dialog, _ ->
                //添加确定按钮

//确定按钮的响应事件
                dialog.cancel()
                val intent = Intent(Settings.ACTION_APN_SETTINGS)
                mContext.startActivity(intent)
            }.setNegativeButton("放弃") { dialog, _ ->
                //添加返回按钮
//响应事件
                dialog.cancel()
                Log.i("DialogPermission", "Dialog关闭")
            }.show()//在按键响应事件中显示此对话框

    }

}
