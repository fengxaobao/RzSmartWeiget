package com.rz.smart.ui.login

import com.jetpack.base.mvvm.ui.activity.BaseVMActivity
import com.rz.command.net.RxNetworkUtil
import com.rz.smart.R
import com.rz.smart.ui.login.fragment.LoginFragment
import com.rz.smart.ui.login.fragment.ReLoginFragment
import com.rz.smart.utils.PrintUsbManager
import com.rz.smart.widget.FixedFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class LoginActivity : BaseVMActivity<NoViewModel>() {

    override fun initVM(): NoViewModel = getViewModel()

    override fun initView() {
        val adapter = FixedFragmentPagerAdapter(supportFragmentManager)
        noScrollViewPager.adapter = adapter
        adapter.setFragmentList(LoginFragment.newInstance(),ReLoginFragment.newInstance())
    }

    override fun startObserve() {
    }

    override fun onNetworkConnected(type: RxNetworkUtil.NetType?) {
    }

    override fun initTitleBar() {
    }

    override fun getChildLayoutView(): Int = R.layout.activity_login

    /**
     * 切换到登陆fragment
     */
    fun changeToLogin() {
        if (noScrollViewPager != null) {
            noScrollViewPager.currentItem = 0
        }
    }

    /**
     * 切换到二次登陆fragment
     */
    fun changeToReLogin(){
        if(noScrollViewPager != null){
            noScrollViewPager.currentItem = 1
        }
    }

}