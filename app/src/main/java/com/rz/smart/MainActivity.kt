package com.rz.smart

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rz.print.*
import com.rz.print.Constant.CONN_STATE_DISCONN
import com.rz.smart.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
    private  val PRINTER_COMMAND_ERROR = 0x008
    private  val CONN_MOST_DEVICES = 0x11
    private  val CONN_PRINTER = 0x12

    private var usbManager: UsbManager? = null
    var per = ArrayList<String>()
    private var counts = 0

    /**
     * ESC查询打印机实时状态指令
     */
    private val esc = byteArrayOf(0x10, 0x04, 0x02)

    /**
     * CPCL查询打印机实时状态指令
     */
    private val cpcl = byteArrayOf(0x1b, 0x68)

    /**
     * TSC查询打印机状态指令
     */
    private val tsc = byteArrayOf(0x1b, '!'.toByte(), '?'.toByte())
    private var mPermissionIntent: PendingIntent? = null

    private lateinit var threadPool: ThreadPool

    /**
     * 判断打印机所使用指令是否是ESC指令
     */
    private var id = 0
    private var printcount = 0
    private var continuityprint = false
    private var checkWifiConnThread //wifi连接线程监听
            : CheckWifiConnThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
        usbManager =
            getSystemService(Context.USB_SERVICE) as UsbManager?
        btnLabelPrint()

    }

    /**
     * 打印标签例子
     * @param view
     */
    fun btnLabelPrint() {
        threadPool = ThreadPool.getInstantiation()
        threadPool.addSerialTask(Runnable {
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) == null ||
                !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).getConnState()
            ) {
                mHandler.obtainMessage(CONN_PRINTER)
                    .sendToTarget()
                return@Runnable
            }
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.TSC
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendDataImmediately(PrintContent.getLabel(this))
            } else {
                mHandler.obtainMessage(PRINTER_COMMAND_ERROR)
                    .sendToTarget()
            }
        })
    }
    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CONN_STATE_DISCONN -> {
                    val deviceConnFactoryManager: DeviceConnFactoryManager =
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    if (deviceConnFactoryManager.getConnState()) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .closePort(id)
                        Utils.toast(this@MainActivity, getString(com.rz.print.R.string.str_disconnect_success))
                    }
                }
                PRINTER_COMMAND_ERROR -> Utils.toast(
                    this@MainActivity,
                    getString(com.rz.print.R.string.str_choice_printer_command)
                )
               CONN_PRINTER -> Utils.toast(
                    this@MainActivity,
                    getString(com.rz.print.R.string.str_cann_printer)
                )
                Constant.MESSAGE_UPDATE_PARAMETER -> {
                    val strIp = msg.data.getString("Ip")
                    val strPort = msg.data.getString("Port")
                    //初始化端口信息
                    DeviceConnFactoryManager.Build() //设置端口连接方式
                        .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI) //设置端口IP地址
                        .setIp(strIp) //设置端口ID（主要用于连接多设备）
                        .setId(id) //设置连接的热点端口号
                        .setPort(strPort!!.toInt())
                        .build(this@MainActivity)
                    threadPool = ThreadPool.getInstantiation()
                    threadPool.addSerialTask(Runnable {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .openPort()
                    })
                }
                CheckWifiConnThread.PING_SUCCESS -> Log.e(
                    "MainActivity",
                    "wifi connect success!"
                )
                CheckWifiConnThread.PING_FAIL -> {
                    Log.e(
                      "MainActivity",
                        "wifi connect fail!"
                    )
                    Utils.toast(this@MainActivity, getString(com.rz.print.R.string.disconnect))
                    checkWifiConnThread!!.cancel()
                    checkWifiConnThread = null
                    this.obtainMessage(CONN_STATE_DISCONN)
                        .sendToTarget()
                }
                Constant.tip -> {
                    val str = msg.obj as String
                    Utils.toast(this@MainActivity, str)
                }
                else -> {
                    DeviceConnFactoryManager.Build() //设置端口连接方式
                        .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.WIFI) //设置端口IP地址
                        .setIp("192.168.2.227") //设置端口ID（主要用于连接多设备）
                        .setId(id) //设置连接的热点端口号
                        .setPort(9100)
                        .build(this@MainActivity)
                    threadPool.addSerialTask(Runnable {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .openPort()
                    })
                }
            }
        }
    }

}