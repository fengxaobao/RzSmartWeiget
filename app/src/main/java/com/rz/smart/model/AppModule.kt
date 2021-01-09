package com.rz.smart.model

import com.jetpack.base.mvvm.CoroutinesDispatcherProvider
import com.rz.bmn.service.SmartService
import com.rz.smart.repository.LoginRepository
import com.rz.smart.repository.SmartRepository
import com.rz.smart.service.BMNRetrofitClient
import com.rz.smart.service.BmnBaseUrlConfig
import com.rz.smart.ui.login.NoViewModel
import com.rz.smart.ui.login.fragment.LoginViewModel
import com.rz.smart.ui.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by luyao
 * on 2019/11/15 15:44
 */

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { SplashViewModel() }
    viewModel { NoViewModel() }
    viewModel { LoginViewModel(get()) }
}

val repositoryModule = module {
    single { BMNRetrofitClient.getService(SmartService::class.java, BmnBaseUrlConfig.URL_SERVER) }
    single { SmartRepository() }
    single { LoginRepository(get()) }
    single { CoroutinesDispatcherProvider() }
}

val appModule = listOf(
    viewModelModule,
    repositoryModule
)