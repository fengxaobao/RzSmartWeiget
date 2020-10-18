package com.jetpack.base.mvvm.ui.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.jetpack.base.R
import com.rz.command.net.RxNetworkUtil


open abstract class BaseCommandFragment : BaseFragment() {

    fun showTipDialog(content: String) {
        AlertDialog.Builder(activity!!, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
            .setCancelable(true)
            .setTitle(getString(R.string.command_dialog_tip))
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
        AlertDialog.Builder(activity!!, R.style.Theme_MaterialComponents_Light_Dialog)
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
        AlertDialog.Builder(activity!!, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
            .setCancelable(true)
            .setMessage(msn)
            .setCancelable(true)
            .setPositiveButton(confirmText, confirmListener)
            .setNegativeButton(cancelText, cancelListener)
            .show()
    }

    fun showConfirmDialog(msn: String) {
        AlertDialog.Builder(activity!!, R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
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
            showConfirmDialog("网络连接失败，请链接网络", "", "设置",
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                })
        }
    }

    fun replaceEmptyView(group: ViewGroup) {
        group.removeAllViews()
        var layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        //设置中间位置   
        layoutParams.gravity = Gravity.CENTER
        val emptyView = layoutInflater.inflate(R.layout.empty_view, null, false)
        group.addView(emptyView, layoutParams)
        emptyView.setOnClickListener {
            handlerEmpty()
        }
    }

    fun replaceErrorView(group: ViewGroup) {
        group.removeAllViews()
        var layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        //设置中间位置   
        layoutParams.gravity = Gravity.CENTER
        val errorView = layoutInflater.inflate(R.layout.err_view, null, false)
        group.addView(errorView, layoutParams)
        errorView.setOnClickListener {
            handlerError()
        }
    }

    /**
     * 处理错误页面
     */
    protected fun handlerError() {

    }

    /**
     * 处理空页面
     */
    protected fun handlerEmpty() {

    }
}