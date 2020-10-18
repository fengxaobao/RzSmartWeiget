package com.rz.print

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gprinter.command.CpclCommand
import com.gprinter.command.EscCommand
import com.gprinter.command.FactoryCommand
import com.gprinter.command.LabelCommand
import com.rz.print.Constant.ACTION_USB_PERMISSION
import com.rz.print.Constant.MESSAGE_UPDATE_PARAMETER
import com.rz.print.DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE
import com.rz.print.DeviceConnFactoryManager.CONN_STATE_FAILED
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator
 *
 * @author 猿史森林
 * Date: 2017/8/2
 * Class description:
 */
class MainActivity : AppCompatActivity() {
    var per = ArrayList<String>()
    private var usbManager: UsbManager? = null
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
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH
    )
    private var usbName: String? = null
    private var tvConnState: TextView? = null
    private lateinit var threadPool: ThreadPool

    /**
     * 判断打印机所使用指令是否是ESC指令
     */
    private var id = 0
    private var etPrintCounts: EditText? = null
    private var mode_sp: Spinner? = null
    private var printcount = 0
    private var continuityprint = false
    private var checkWifiConnThread //wifi连接线程监听
            : CheckWifiConnThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate()")
        setContentView(R.layout.activity_main)
        usbManager =
            getSystemService(Context.USB_SERVICE) as UsbManager?
        checkPermission()
        requestPermission()
        tvConnState = findViewById(R.id.tv_connState) as TextView?
        etPrintCounts = findViewById(R.id.et_print_counts) as EditText?
        initsp()
        initBroadcast()
    }

    /**
     * 注册广播
     * Registration broadcast
     */
    private fun initBroadcast() {
        val filter =
            IntentFilter(ACTION_USB_PERMISSION) //USB访问权限广播
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED) //USB线拔出
        filter.addAction(ACTION_QUERY_PRINTER_STATE) //查询打印机缓冲区状态广播，用于一票一控
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE) //与打印机连接状态
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED) //USB线插入
        registerReceiver(receiver, filter)
    }

    private fun initsp() {
        val list: MutableList<String> =
            ArrayList()
        list.add(getString(R.string.str_cpclmode))
        list.add(getString(R.string.str_tscmode))
        list.add(getString(R.string.str_escmode))
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                R.layout.simple_spinner_item, list
            )
        adapter.setDropDownViewResource(R.layout.simple_list_item_single_choice)
        mode_sp = findViewById(R.id.mode_sp) as Spinner?
        mode_sp!!.adapter = adapter
    }

    private fun checkPermission() {
        for (permission in permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    this,
                    permission
                )
            ) {
                per.add(permission)
            }
        }
    }

    private fun requestPermission() {
        if (per.size > 0) {
            val p = arrayOfNulls<String>(per.size)
            ActivityCompat.requestPermissions(
                this,
                per.toArray(p),
                REQUEST_CODE
            )
        }
    }

    /**
     * 蓝牙连接
     * 安卓系统6.0+，搜索蓝牙需要用户授予访问定位权限，否则将搜索不到蓝牙
     * Bluetooth connection
     * Android 6.0+, search for Bluetooth requires users to grant access to the location, otherwise Bluetooth will not be searched
     */
    fun btnBluetoothConn(view: View?) {
        startActivityForResult(
            Intent(this, BluetoothDeviceList::class.java),
            Constant.BLUETOOTH_REQUEST_CODE
        )
    }

    /**
     * USB连接
     * 访问USB设备需要客户授予访问权限
     * USB connection
     * Access to USB devices requires client access
     * @param view
     */
    fun btnUsbConn(view: View?) {
        startActivityForResult(
            Intent(this, UsbDeviceList::class.java),
            Constant.USB_REQUEST_CODE
        )
    }

    /**
     * 连接多设备
     * Connect multiple devices
     * @param view
     */
    fun btnMoreDevices(view: View?) {
        unregisterReceiver(receiver) //进入多设备取消广播监听
        startActivityForResult(
            Intent(this, ConnMoreDevicesActivity::class.java),
            CONN_MOST_DEVICES
        )
    }

    /**
     * 获取打印机ip信息说明：
     * 打印打印机自检页，查看当前打印机网络信息，步骤如下
     * 1.打印机关机
     * 2.按住FEED键（不松手），此时开启打印机
     * 3..红灯灭后，松手feed键，此过程打印机3-5秒，正常则打印一张自检页，可查看ip
     * 若打印机打印出Hexadecimal Dump字样，则进去16进制模式，需要重试上述步骤
     *
     * 安卓设备需要跟打印机处于局域网内，实现通讯
     * 如需要设置打印机ip，请联系客服索要修改工具即可
     *
     * WIFI连接：
     * 连接打印机wifi，打印打印机自检页或接口，查看当前ip
     *
     * 网口：
     * 1.安卓机器与打印机用网线连接
     * 2.进入安卓系统设置以太网，打印机与安卓ip段处于局域网内（不能一样）
     *
     * Get the printer ip information description:
     * Print the printer self-test page to view the current printer network information, the steps are as follows
     * 1. Printer shutdown
     * 2. Press and hold the FEED button (do not let go), then turn on the printer
     * 3. After the red light is off, release the feed button, the process printer 3-5 seconds, normal print a self-test page, you can view ip
     * If the printer prints the Hexadecimal Dump, enter the hexadecimal mode and you need to retry the above steps.
     *
     * Android devices need to be in the LAN with the printer to achieve communication
     * If you need to set the printer ip, please contact customer service to request the modification tool.
     *
     * WIFI connection:
     * Connect printer wifi, print printer self-test page or interface, view current ip
     *
     * Network port:
     * 1. Android machine and printer are connected by network cable
     * 2. Enter the Android system to set the Ethernet, the printer and the Android ip segment are in the LAN (not the same)
     * @param view
     */
    fun btnWifiConn(view: View?) {
        val wifiParameterConfigDialog =
            WifiParameterConfigDialog(this, mHandler)
        wifiParameterConfigDialog.show()
    }

    /**
     * 串口连接
     *
     * @param view
     */
    fun btnSerialPortConn(view: View?) {
        startActivityForResult(
            Intent(this, SerialPortList::class.java),
            Constant.SERIALPORT_REQUEST_CODE
        )
    }

    /**
     * 断开连接
     * @param view
     */
    fun btnDisConn(view: View?) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                .get(id) == null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                .getConnState()
        ) {
            Utils.toast(this, getString(R.string.str_cann_printer))
            return
        }
        mHandler.obtainMessage(CONN_STATE_DISCONN)
            .sendToTarget()
    }

    /**
     * 打印票据例子
     *
     * @param view
     */
    fun btnReceiptPrint(view: View?) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                .get(id) == null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                .getConnState()
        ) {
            Utils.toast(this, getString(R.string.str_cann_printer))
            return
        }
        threadPool = ThreadPool.getInstantiation()
        threadPool.addSerialTask(Runnable {
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.ESC
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendDataImmediately(PrintContent.getReceipt(this))
            } else {
                mHandler.obtainMessage(PRINTER_COMMAND_ERROR)
                    .sendToTarget()
            }
        })
    }

    /**
     * 打印标签例子
     * @param view
     */
    fun btnLabelPrint(view: View?) {
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

    fun btnLabelMatrix(view: View?) {
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
                    .sendDataImmediately(PrintContent.getNewCommandToPrintQrcode())
            } else {
                mHandler.obtainMessage(PRINTER_COMMAND_ERROR)
                    .sendToTarget()
            }
        })
    }

    /**
     * 打印标签长图
     * 若不支持该指令则打印空白
     * @param view
     */
    fun btnlabelPhoto(view: View?) {
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
                val bitmap =
                    BitmapFactory.decodeResource(
                        getResources(),
                        R.drawable.test
                    )
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendDataImmediately(PrintContent.printViewPhoto(bitmap))
            } else {
                mHandler.obtainMessage(PRINTER_COMMAND_ERROR)
                    .sendToTarget()
            }
        })
    }

    /**
     * 打印面单例子
     * @param view
     */
    fun btnCpclPrint(view: View?) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) == null ||
            !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).getConnState()
        ) {
            Utils.toast(this, getString(R.string.str_cann_printer))
            return
        }
        threadPool = ThreadPool.getInstantiation()
        threadPool.addSerialTask(Runnable {
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.CPCL
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendDataImmediately(PrintContent.getCPCL(this))
            } else {
                mHandler.obtainMessage(PRINTER_COMMAND_ERROR)
                    .sendToTarget()
            }
        })
    }

    /**
     * 打印自检页
     * @param view
     */
    fun btnPrintSelftest(view: View?) {
        threadPool = ThreadPool.getInstantiation()
        threadPool.addSerialTask(Runnable {
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                    .get(id) == null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                    .get(id).getConnState()
            ) {
                mHandler.obtainMessage(CONN_PRINTER)
                    .sendToTarget()
                return@Runnable
            }
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.TSC
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendByteDataImmediately(FactoryCommand.printSelfTest(FactoryCommand.printerMode.TSC))
            } else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.ESC
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendByteDataImmediately(FactoryCommand.printSelfTest(FactoryCommand.printerMode.ESC))
            } else {
                mHandler.obtainMessage(PRINTER_COMMAND_ERROR)
                    .sendToTarget()
            }
        })
    }

    /**
     * 打印XML布局文件
     *
     * @param view
     */
    fun btnPrintXml(view: View?) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) == null ||
            !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).getConnState()
        ) {
            mHandler.obtainMessage(CONN_PRINTER)
                .sendToTarget()
            return
        }
        threadPool = ThreadPool.getInstantiation()
        threadPool.addSerialTask(Runnable {
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.CPCL
            ) {
                val cpcl = CpclCommand()
                cpcl.addInitializePrinter(1500, 1)
                // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
                // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576 例子为80mmd打印机
                cpcl.addCGraphics(0, 0, 576, PrintContent.getBitmap(this@MainActivity))
                cpcl.addPrint()
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendDataImmediately(cpcl.command)
            } else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.TSC
            ) {
                val labelCommand = LabelCommand()
                labelCommand.addSize(80, 180)
                labelCommand.addCls()
                // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
                // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576 例子为80mmd打印机
                labelCommand.addBitmap(0, 0, 576, PrintContent.getBitmap(this@MainActivity))
                labelCommand.addPrint(1)
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendDataImmediately(labelCommand.command)
            } else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.ESC
            ) {
                val esc = EscCommand()
                esc.addInitializePrinter()
                // 打印图片  光栅位图  384代表打印图片像素  0代表打印模式
                // 58mm打印机 可打印区域最大点数为 384 ，80mm 打印机 可打印区域最大点数为 576 例子为80mmd打印机
                esc.addRastBitImage(PrintContent.getBitmap(this@MainActivity), 576, 0)
                esc.addPrintAndLineFeed()
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendDataImmediately(esc.command)
            }
        })
    }

    /**
     * 打印机状态查询，部分打印机没有返回值，则无法收到返回，
     *
     * @param view
     */
    fun btnPrinterState(view: View?) {
        //打印机状态查询
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) == null ||
            !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).getConnState()
        ) {
            Utils.toast(this, getString(R.string.str_cann_printer))
            return
        }
        ThreadPool.getInstantiation().addSerialTask(Runnable {
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.ESC
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendByteDataImmediately(esc)
            } else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.TSC
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendByteDataImmediately(tsc)
            } else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.CPCL
            ) {
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .sendByteDataImmediately(cpcl)
            }
        })
    }

    /**
     * 切换打印模式
     *
     * @param view
     */
    fun btnModeChange(view: View?) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) == null ||
            !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).getConnState()
        ) {
            Utils.toast(this, getString(R.string.str_cann_printer))
            return
        }
        val sp_no = mode_sp!!.selectedItemPosition
        var bytes: ByteArray? = null
        when (sp_no) {
            0 -> bytes = if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.CPCL
            ) {
                tip(
                    java.lang.String.format(
                        getString(R.string.str_mode_tip),
                        getString(R.string.str_cpclmode)
                    )
                )
                return
            } else {
                FactoryCommand.changPrinterMode(FactoryCommand.printerMode.CPCL)
            }
            1 -> bytes = if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.TSC
            ) {
                tip(
                    java.lang.String.format(
                        getString(R.string.str_mode_tip),
                        getString(R.string.str_tscmode)
                    )
                )
                return
            } else {
                FactoryCommand.changPrinterMode(FactoryCommand.printerMode.TSC)
            }
            2 -> bytes = if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getCurrentPrinterCommand() === PrinterCommand.ESC
            ) {
                tip(
                    java.lang.String.format(
                        getString(R.string.str_mode_tip),
                        getString(R.string.str_escmode)
                    )
                )
                return
            } else {
                FactoryCommand.changPrinterMode(FactoryCommand.printerMode.ESC)
            }
        }
        threadPool = ThreadPool.getInstantiation()
        val finalBytes = bytes
        threadPool.addSerialTask(Runnable { //发送切换打印机模式后会断开连接，如果切换模式成功，打印机蜂鸣器会响一声，打印机关机，需手动开启
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                .sendByteDataImmediately(finalBytes)
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).closePort(id)
        })
    }

    /**
     * 提示
     * @param msg
     */
    private fun tip(msg: String) {
        val message = Message()
        message.obj = msg
        message.what = Constant.tip
        mHandler.sendMessage(message)
    }

    /**
     * 连续打印
     *
     * @param view
     */
    fun btnReceiptAndLabelContinuityPrint(view: View?) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                .get(id) == null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                .getConnState()
        ) {
            Utils.toast(this, getString(R.string.str_cann_printer))
            return
        }
        if (etPrintCounts!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            Utils.toast(this, getString(R.string.str_continuity_count))
            return
        }
        counts = etPrintCounts!!.text.toString().trim { it <= ' ' }.toInt()
        printcount = 0
        continuityprint = true
        sendContinuityPrint()
    }

    private fun sendContinuityPrint() {
        ThreadPool.getInstantiation().addSerialTask(Runnable {
            if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) != null
                && DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    .getConnState()
            ) {
                val threadFactoryBuilder =
                    ThreadFactoryBuilder("MainActivity_sendContinuity_Timer")
                val scheduledExecutorService: ScheduledExecutorService =
                    ScheduledThreadPoolExecutor(1, threadFactoryBuilder)
                scheduledExecutorService.schedule(threadFactoryBuilder.newThread(Runnable {
                    counts--
                    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .getCurrentPrinterCommand() === PrinterCommand.ESC
                    ) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .sendDataImmediately(PrintContent.getReceipt(this))
                    } else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                            .get(id).getCurrentPrinterCommand() === PrinterCommand.TSC
                    ) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .sendDataImmediately(PrintContent.getLabel(this))
                    } else {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .sendDataImmediately(PrintContent.getCPCL(this))
                    }
                }), 1000, TimeUnit.MILLISECONDS)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constant.BLUETOOTH_REQUEST_CODE -> {
                    closeport()
                    /*获取蓝牙mac地址*/
                    val macAddress =
                        data!!.getStringExtra(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS)
                    //初始化话DeviceConnFactoryManager
                    DeviceConnFactoryManager.Build()
                        .setId(id) //设置连接方式
                        .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH) //设置连接的蓝牙mac地址
                        .setMacAddress(macAddress)
                        .build(this)
                    //打开端口
                    threadPool = ThreadPool.getInstantiation()
                    threadPool.addSerialTask(Runnable {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .openPort()
                    })
                }
                Constant.USB_REQUEST_CODE -> {
                    closeport()
                    //获取USB设备名
                    usbName = data!!.getStringExtra(UsbDeviceList.USB_NAME)
                    //通过USB设备名找到USB设备
                    val usbDevice: UsbDevice =
                        Utils.getUsbDeviceFromName(this@MainActivity, usbName)
                    //判断USB设备是否有权限
                    if (usbManager!!.hasPermission(usbDevice)) {
                        usbConn(usbDevice)
                    } else { //请求权限
                        mPermissionIntent = PendingIntent.getBroadcast(
                            this,
                            0,
                            Intent(ACTION_USB_PERMISSION),
                            0
                        )
                        usbManager!!.requestPermission(usbDevice, mPermissionIntent)
                    }
                }
                Constant.SERIALPORT_REQUEST_CODE -> {
                    closeport()
                    //获取波特率
                    val baudrate = data!!.getIntExtra(Constant.SERIALPORTBAUDRATE, 0)
                    //获取串口号
                    val path = data!!.getStringExtra(Constant.SERIALPORTPATH)
                    if (baudrate != 0 && !TextUtils.isEmpty(path)) {
                        //初始化DeviceConnFactoryManager
                        DeviceConnFactoryManager.Build() //设置连接方式
                            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.SERIAL_PORT)
                            .setId(id) //设置波特率
                            .setBaudrate(baudrate) //设置串口号
                            .setSerialPort(path)
                            .build(this)
                        //打开端口
                        //打开端口
                        threadPool = ThreadPool.getInstantiation()
                        threadPool.addSerialTask(Runnable {
                            DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                                .openPort()
                        })
                    }
                }
                CONN_MOST_DEVICES -> {
                    initBroadcast() //注册广播监听
                    id = data!!.getIntExtra("id", -1)
                    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) != null &&
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .getConnState()
                    ) {
                        tvConnState!!.text =
                            getString(R.string.str_conn_state_connected).toString() + "\n" + connDeviceInfo
                    } else {
                        tvConnState!!.text = getString(R.string.str_conn_state_disconnect)
                    }
                }
                else -> {
                }
            }
        }
    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private fun closeport() {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                .get(id) != null && DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                .get(id).mPort != null
        ) {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).reader.cancel()
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).mPort.closePort()
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).mPort = null
        }
    }

    /**
     * usb连接
     *
     * @param usbDevice
     */
    private fun usbConn(usbDevice: UsbDevice) {
        DeviceConnFactoryManager.Build()
            .setId(id)
            .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.USB)
            .setUsbDevice(usbDevice)
            .setContext(this)
            .build(this)
        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).openPort()
    }

    /**
     * 停止连续打印
     */
    fun btnStopContinuityPrint(v: View?) {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id) == null ||
            !DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id).getConnState()
        ) {
            Utils.toast(this, getString(R.string.str_cann_printer))
            return
        }
        if (counts != 0) {
            counts = 0
            Utils.toast(this, getString(R.string.str_stop_continuityprint_success))
        }
    }

    private val receiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                val action = intent.action
                when (action) {
                    ACTION_USB_PERMISSION -> synchronized(this) {
                        val device =
                            intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.let { usbConn(it) }
                        } else { //用户点击不授权,则无权限访问USB
                            Log.e(
                                TAG,
                                "No access to USB"
                            )
                        }
                    }
                    UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                        val usbDevice =
                            intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                        if (usbDevice == DeviceConnFactoryManager.getDeviceConnFactoryManagers()
                                .get(id).usbDevice()
                        ) {
                            mHandler.obtainMessage(CONN_STATE_DISCONN)
                                .sendToTarget()
                        }
                    }
                    DeviceConnFactoryManager.ACTION_CONN_STATE -> {
                        val state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1)
                        val deviceId =
                            intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1)
                        when (state) {
                            DeviceConnFactoryManager.CONN_STATE_DISCONNECT -> if (id == deviceId) {
                                Log.e(
                                    TAG,
                                    "connection is lost"
                                )
                                tvConnState!!.setText(getString(R.string.str_conn_state_disconnect))
                            }
                            DeviceConnFactoryManager.CONN_STATE_CONNECTING -> tvConnState!!.setText(
                                getString(R.string.str_conn_state_connecting)
                            )
                            DeviceConnFactoryManager.CONN_STATE_CONNECTED -> tvConnState!!.setText(
                                getString(R.string.str_conn_state_connected).toString() + "\n" + connDeviceInfo
                            )
                            CONN_STATE_FAILED -> {
                                Utils.toast(this@MainActivity, getString(R.string.str_conn_fail))
                                //wificonn=false;
                                tvConnState!!.setText(getString(R.string.str_conn_state_disconnect))
                            }
                            else -> {
                            }
                        }
                    }
                    ACTION_QUERY_PRINTER_STATE -> if (counts >= 0) {
                        if (continuityprint) {
                            printcount++
                            Utils.toast(
                                this@MainActivity,
                                getString(R.string.str_continuityprinter).toString() + " " + printcount
                            )
                        }
                        if (counts != 0) {
                            sendContinuityPrint()
                        } else {
                            continuityprint = false
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CONN_STATE_DISCONN -> {
                    val deviceConnFactoryManager: DeviceConnFactoryManager =
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                    if (deviceConnFactoryManager != null && deviceConnFactoryManager.getConnState()) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
                            .closePort(id)
                        Utils.toast(this@MainActivity, getString(R.string.str_disconnect_success))
                    }
                }
                PRINTER_COMMAND_ERROR -> Utils.toast(
                    this@MainActivity,
                    getString(R.string.str_choice_printer_command)
                )
                CONN_PRINTER -> Utils.toast(
                    this@MainActivity,
                    getString(R.string.str_cann_printer)
                )
                MESSAGE_UPDATE_PARAMETER -> {
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
                    TAG,
                    "wifi connect success!"
                )
                CheckWifiConnThread.PING_FAIL -> {
                    Log.e(
                        TAG,
                        "wifi connect fail!"
                    )
                    Utils.toast(this@MainActivity, getString(R.string.disconnect))
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

    override fun onStart() {
        super.onStart()
        //获取连接对象是否连接
        val deviceConnFactoryManagers: Array<DeviceConnFactoryManager?>
        deviceConnFactoryManagers = DeviceConnFactoryManager.getDeviceConnFactoryManagers()
        for (i in 0..3) {
            if (deviceConnFactoryManagers[i] != null && deviceConnFactoryManagers[i]!!
                    .getConnState()
            ) {
                tvConnState!!.setText(getString(R.string.str_conn_state_connected).toString() + "\n" + connDeviceInfo)
                break
            } else {
                tvConnState!!.setText(getString(R.string.str_conn_state_disconnect))
            }
        }
    }//开启监听WiFi线程

    /**
     * 获取当前连接设备信息
     * @return
     */
    private val connDeviceInfo: String
        private get() {
            var str = ""
            val deviceConnFactoryManager: DeviceConnFactoryManager =
                DeviceConnFactoryManager.getDeviceConnFactoryManagers().get(id)
            if (deviceConnFactoryManager != null
                && deviceConnFactoryManager.getConnState()
            ) {
                if ("USB" == deviceConnFactoryManager.getConnMethod().toString()) {
                    str += "USB\n"
                    str += "USB Name: " + deviceConnFactoryManager.usbDevice().getDeviceName()
                } else if ("WIFI" == deviceConnFactoryManager.getConnMethod().toString()) {
                    str += "WIFI\n"
                    str += "IP: " + deviceConnFactoryManager.getIp().toString() + "\t"
                    str += "Port: " + deviceConnFactoryManager.getPort()
                    checkWifiConnThread =
                        CheckWifiConnThread(deviceConnFactoryManager.getIp(), mHandler) //开启监听WiFi线程
                    checkWifiConnThread!!.start()
                } else if ("BLUETOOTH" == deviceConnFactoryManager.getConnMethod().toString()) {
                    str += "BLUETOOTH\n"
                    str += "MacAddress: " + deviceConnFactoryManager.getMacAddress()
                } else if ("SERIAL_PORT" == deviceConnFactoryManager.getConnMethod().toString()) {
                    str += "SERIAL_PORT\n"
                    str += "Path: " + deviceConnFactoryManager.getSerialPortPath().toString() + "\t"
                    str += "Baudrate: " + deviceConnFactoryManager.getBaudrate()
                }
            }
            return str
        }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy()")
        unregisterReceiver(receiver)
        if (usbManager != null) {
            usbManager = null
        }
        DeviceConnFactoryManager.closeAllPort()
        if (threadPool != null) {
            threadPool.stopThreadPool()
        }
    }

    companion object {
        private val TAG =
            MainActivity::class.java.simpleName
        private const val REQUEST_CODE = 0x004

        /**
         * 连接状态断开
         */
        private const val CONN_STATE_DISCONN = 0x007

        /**
         * 使用打印机指令错误
         */
        private const val PRINTER_COMMAND_ERROR = 0x008
        private const val CONN_MOST_DEVICES = 0x11
        private const val CONN_PRINTER = 0x12
    }
}