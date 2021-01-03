package com.rz.smart.ui.login.fragment

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jetpack.base.mvvm.ui.fragment.BaseVMFragment
import com.rz.smart.R
import com.rz.smart.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ReLoginFragment : BaseVMFragment<ReLoginViewModel>() {

    private var mActivity: LoginActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as LoginActivity
    }

    companion object {
        fun newInstance() = ReLoginFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun getLayoutResId(): Int = R.layout.re_login_fragment

    override fun initVM(): ReLoginViewModel = getViewModel()

    override fun initView() {
    }

    override fun initData() {
    }

    override fun startObserve() {

    }

}