package com.jetpack.base.sdk.net.http

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.jetpack.base.mvvm.ui.application.BaseApplication
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File


/**
 * Created by luyao
 * on 2018/3/13 15:45
 */
abstract class RetrofitClient : BaseRetrofitClient() {

    private val cookieJar by lazy {
        PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(BaseApplication.instance())
        )
    }

    override fun handleBuilder(builder: OkHttpClient.Builder) {
        val httpCacheDirectory = File(BaseApplication.instance().cacheDir, "Mike-Rz")
        val cacheSize = 10 * 1024 * 1024L // 10 MiB
        val cache = Cache(httpCacheDirectory, cacheSize)
        builder.cache(cache)
            .cookieJar(cookieJar)
            .build()
//            .addInterceptor { chain ->
//                var request = chain.request()
//                if (!RxNetworkUtil.isNetworkAvailable(BaseApplication.instance())) {
//                    request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build()
//                }
//                val response = chain.proceed(request)
//                if (!RxNetworkUtil.isNetworkAvailable(BaseApplication.instance())) {
//                    val maxAge = 60 * 60
//                    response.newBuilder()
//                        .removeHeader("Pragma")
//                        .header("Cache-Control", "public, max-age=$maxAge")
//                        .build()
//                } else {
//                    val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
//                    response.newBuilder()
//                        .removeHeader("Pragma")
//                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
//                        .build()
//                }
//                response
//            }
    }
}