package com.rz.smart.ui.login.fragment

import android.content.Context
import com.jetpack.base.mvvm.ui.fragment.BaseVMFragment
import com.rz.smart.R
import com.rz.smart.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class LoginFragment : BaseVMFragment<LoginViewModel>() {

    private var mActivity: LoginActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as LoginActivity
    }

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun getLayoutResId(): Int = R.layout.login_fragment

    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {

    }

    override fun initData() {

    }

    override fun startObserve() {

    }

}