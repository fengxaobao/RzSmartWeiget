package com.rz.smart.ui.verify

import androidx.lifecycle.Observer
import com.jetpack.base.mvvm.ui.activity.BaseVMActivity
import com.rz.command.net.RxNetworkUtil
import com.rz.smart.R
import kotlinx.android.synthetic.main.activity_verify.*
import org.koin.androidx.viewmodel.ext.android.getViewModel


class VerifyActivity : BaseVMActivity<VerifyModel>() {
    override fun initVM(): VerifyModel = getViewModel()

    override fun initView() {
        _viewModel.livedata.observe(this, observer = Observer<Any> {

        })
        loginBtn.setOnClickListener {
            _viewModel.login()
        }

    }

    override fun startObserve() {

    }

    override fun onNetworkConnected(type: RxNetworkUtil.NetType?) {
    }

    override fun initTitleBar() {
    }

    override fun getChildLayoutView(): Int = R.layout.activity_verify


}