package com.jetpack.base.mvvm.ui.fragment

import com.rz.command.ui.LoadingDialog
import com.rz.utils.RxKeyboardTool
import me.yokeyword.fragmentation.SupportFragment

open abstract class BaseFragment : SupportFragment() {
    private var progressDialog: LoadingDialog? = null

    override fun onStop() {
        super.onStop()
        RxKeyboardTool.hideSoftInput(activity!!)
    }

    fun showLoading() {
        getProgressDialog()
        progressDialog!!.show()
    }

    fun hindLoading() {
        dismissProgressDialog()
    }

    fun dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    private fun getProgressDialog(): LoadingDialog {
        if (progressDialog == null) {
            progressDialog = LoadingDialog(activity!!)
        }
        return progressDialog!!
    }

}