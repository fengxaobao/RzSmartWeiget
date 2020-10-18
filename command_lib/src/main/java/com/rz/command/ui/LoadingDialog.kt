package com.rz.command.ui

import android.app.Dialog
import android.content.Context
import com.rz.command.R

/**
 * Created by vslimit on 16/12/24.
 */
class LoadingDialog(context: Context) : Dialog(context, R.style.CustomProgressDialog) {


    init {
        setContentView(R.layout.loading_dialog)
    }


}
