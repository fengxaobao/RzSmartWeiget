package com.jetpack.base.sdk.net.interceptor

import com.orhanobut.logger.Logger
import com.rz.utils.RxTool
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import java.io.IOException
import java.util.*


/**
 * 拦截处理签名加密
 */

class HandlerInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        try {
            val url = request.url
            var scheme = url.scheme//  http https
            var host = url.host//   127.0.0.1
            var path = url.encodedPath//  /test/upload/img
            Logger.e("url=$url")
//
//            if (request.method == "POST") {
////                return chain.proceed(postMethodSign(request))
//            } else {
//                var query = url.encodedQuery//  userName=xiaoming&userPassword=12345
//                Logger.d("query=$query  ")
//                return chain.proceed(getMethodSign(request))
//            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return chain.proceed(request)

    }


    fun getMethodSign(request: Request): Request {

        //拼接timestamp
        var url: String = request.url.toString()
        //获取参数列表
        val parts = url.split("?").toTypedArray()
        //TreeMap里面的数据会按照key值自动升序排列
        val param_map: TreeMap<String, String> =
            TreeMap<String, String>()
        //获取参数对
        val param_pairs = parts[1].split("&").toTypedArray()
        for (pair in param_pairs) {
            val param = pair.split("=").toTypedArray()
            if (param.size != 2) {
                //没有value的参数不进行处理
                continue
            }
            val toCharArray = param[0].toCharArray()
            var sb = sortKeys(toCharArray)
            param_map.put(sb.toString(), param[1])
        }
        val sign = StringBuilder()
        val it: Iterator<*> = param_map.keys.iterator()
        //拼接参数
        while (it.hasNext()) {
            val key = it.next().toString()
            val value: String = param_map[key].toString()
            sign.append(key).append("=").append(value).append("&")
        }

        //这里还没有处理
        sign.append("key=${""}")

        //Md5加密
        Logger.d("MD5之前的 sign=$sign.toString()")
        var sign_s = RxTool.md5(sign.toString())
        Logger.d("MD5之后的 sign=$sign")
        //重新拼接url
        val httpUrlBuilder: HttpUrl.Builder = request.url.newBuilder()
        //添加参数
        httpUrlBuilder.addQueryParameter("Sign", sign_s.toUpperCase())
        val requestBuilder: Request.Builder = request.newBuilder()
        requestBuilder.url(httpUrlBuilder.build())
        return requestBuilder.build()
    }

    private fun sortKeys(param: CharArray): StringBuilder {
        Arrays.sort(param)
        var sb = StringBuilder()
        param.forEach {
            sb.append(it)
        }
        return sb
    }

}