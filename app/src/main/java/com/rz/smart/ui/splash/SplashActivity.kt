package com.rz.smart.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.jetpack.base.mvvm.ui.activity.BaseVMActivity
import com.orhanobut.logger.Logger
import com.rz.command.net.RxNetworkUtil
import com.rz.smart.MainActivity
import com.rz.smart.R
import com.rz.smart.ui.login.LoginActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.util.concurrent.TimeUnit

class SplashActivity : BaseVMActivity<SplashViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.e("SplashActivity onCreate")
    }
    override fun initVM(): SplashViewModel = getViewModel()

    @SuppressLint("CheckResult")
    override fun initView() {

    }

    override fun startObserve() {
       Logger.e("startObserve")
        //暂无逻辑处理 直接跳转登录
        Observable.timer(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread()).subscribe {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                overridePendingTransition(R.anim.zoom_small_in, R.anim.zoom_small_out)
            }
    }

    override fun onNetworkConnected(type: RxNetworkUtil.NetType?) {

    }

    override fun initTitleBar() {

    }

    override fun getChildLayoutView(): Int  = R.layout.activity_splash
}