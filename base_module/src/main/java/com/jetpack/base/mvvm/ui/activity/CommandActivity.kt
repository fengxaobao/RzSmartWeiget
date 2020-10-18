package com.jetpack.base.mvvm.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.jetpack.base.R
import com.rz.command.net.RxNetworkUtil


open abstract class CommandActivity : BaseActivity() {

    fun showTipDialog(content: String) {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
            .setCancelable(true)
//                .setTitle(getString(R.string.command_dialog_tip))
            .setMessage(content)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.command_dialog_confirm)) { dialog, which -> dialog.cancel() }
            .show()
    }

    protected fun showConfirmDialog(
        msn: String,
        title: String,
        confirmText: String,
        confirmListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
            .setCancelable(true)
            .setTitle(title)
            .setMessage(msn)
            .setCancelable(true)
            .setPositiveButton(confirmText, confirmListener)
            .setNegativeButton(R.string.command_dialog_cancel) { dialog, which -> dialog.cancel() }
            .show()
    }


    fun showConfirmDialog(
        msn: String,
        title: String,
        confirmText: String,
        confirmListener: DialogInterface.OnClickListener,
        cancelText: String,
        cancelListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
            .setCancelable(true)
            .setMessage(msn)
            .setCancelable(true)
            .setPositiveButton(confirmText, confirmListener)
            .setNegativeButton(cancelText, cancelListener)
            .show()
    }

    fun showConfirmDialog(msn: String) {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
            .setCancelable(true)
//                .setTitle(getString(R.string.command_dialog_tip))
            .setMessage(msn)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.command_dialog_confirm)) { dialog, _ -> dialog.cancel() }
            .setNegativeButton(R.string.command_dialog_cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

    fun networkAvailable(context: Context) {
        if (!RxNetworkUtil.isNetworkAvailable(context)) {
            showConfirmDialog("跳转网络设置页面,设置您的网络", "", "跳转",
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                })
        }
    }
}