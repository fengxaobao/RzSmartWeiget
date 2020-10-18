package com.rz.utils

import java.text.DecimalFormat
import java.util.*

/**
 * @author vondear
 * @date 2017/1/13
 */

object RxConstants {

    val FAST_CLICK_TIME = 100

    val VIBRATE_TIME = 100

    //----------------------------------------------------常用链接- start ------------------------------------------------------------

    /**
     * RxTool的Github地址
     */
    val URL_VONTOOLS = "https://github.com/vondear/RxTool"

    /**
     * 百度文字搜索
     */
    val URL_BAIDU_SEARCH = "http://www.baidu.com/s?wd="

    /**
     * ACFUN
     */
    val URL_ACFUN = "http://www.acfun.tv/"

    val URL_JPG_TO_FONT = "http://ku.cndesign.com/pic/"

    //===================================================常用链接== end ==============================================================
    val URL_BORING_PICTURE = "http://jandan.net/?oxwlxojflwblxbsapi=jandan.get_pic_comments&page="
    val URL_PERI_PICTURE = "http://jandan.net/?oxwlxojflwblxbsapi=jandan.get_ooxx_comments&page="
    val URL_JOKE_MUSIC =
        "http://route.showapi.com/255-1?type=31&showapi_appid=20569&showapi_sign=0707a6bfb3e842fb8c8aa450012d9756&page="
    val SP_MADE_CODE = "MADE_CODE"

    //==========================================煎蛋 API end=========================================
    val SP_SCAN_CODE = "SCAN_CODE"

    //微信统一下单接口
    val WX_TOTAL_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder"

    //------------------------------------------煎蛋 API start--------------------------------------
    var URL_JOKE = "http://ic.snssdk.com/neihan/stream/mix/v1/?" +
            "mpic=1&essence=1" +
            "&content_type=-102" +
            "&message_cursor=-1" +
            "&bd_Stringitude=113.369569" +
            "&bd_latitude=23.149678" +
            "&bd_city=%E5%B9%BF%E5%B7%9E%E5%B8%82" +
            "&am_Stringitude=113.367846" +
            "&am_latitude=23.149878" +
            "&am_city=%E5%B9%BF%E5%B7%9E%E5%B8%82" +
            "&am_loc_time=1465213692154&count=30" +
            "&min_time=1465213700&screen_width=720&iid=4512422578" +
            "&device_id=17215021497" +
            "&ac=wifi" +
            "&channel=NHSQH5AN" +
            "&aid=7" +
            "&app_name=joke_essay" +
            "&version_code=431" +
            "&device_platform=android" +
            "&ssmix=a" +
            "&device_type=6s+Plus" +
            "&os_api=19" +
            "&os_version=4.4.2" +
            "&uuid=864394108025091" +
            "&openudid=80FA5B208E050000" +
            "&manifest_version_code=431"


    //高德地图APP 包名
    val GAODE_PACKAGE_NAME = "com.autonavi.minimap"

    //百度地图APP 包名
    val BAIDU_PACKAGE_NAME = "com.baidu.BaiduMap"

    /**
     * 速度格式化
     */
    val FORMAT_ONE = DecimalFormat("#.#")

    /**
     * 距离格式化
     */
    val FORMAT_TWO = DecimalFormat("#.##")

    /**
     * 速度格式化
     */
    val FORMAT_THREE = DecimalFormat("#.###")

    //图片名称
    val pictureName: String
        get() = RxTimeTool.getCurrentDateTime(DATE_FORMAT_LINK) + "_" + Random().nextInt(1000) + ".jpg"

    //Date格式
    val DATE_FORMAT_LINK = "yyyyMMddHHmmssSSS"

    //Date格式 常用
    val DATE_FORMAT_DETACH = "yyyy-MM-dd HH:mm:ss"
    val DATE_FORMAT_TIME = "yyyy-MM-dd"

    //Date格式 带毫秒
    val DATE_FORMAT_DETACH_SSS = "yyyy-MM-dd HH:mm:ss SSS"

    //时间格式 分钟：秒钟 一般用于视频时间显示
    val DATE_FORMAT_MM_SS = "mm:ss"
}
