package com.rz.smart.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.caysn.autoreplyprint.AutoReplyPrint
import com.rz.printf.TestFunction
import com.sun.jna.Pointer
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class PrintUsbManager(val activity: AppCompatActivity) {
    init {
        openUsb()
        AddCallback()
    }
    private var h: Pointer? = Pointer.NULL

    private fun getEnumUsb() : String {
       var name ="VID:0x0FE6,PID:0x811E"
//        val devicePaths: Array<String> = AutoReplyPrint.CP_Port_EnumUsb_Helper.EnumUsb()
//        if (devicePaths != null) {
//            name = devicePaths[1]
//            name = name.trim()
//        }
       return name
    }

    fun openUsb() {
        Thread {
            h = AutoReplyPrint.INSTANCE.CP_Port_OpenUsb(getEnumUsb(), 1)
        }.start()
    }


    @SuppressLint("CheckResult")
    fun printTest(weight: String) {
        Observable.timer(500,TimeUnit.MILLISECONDS).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
            .subscribe{
                val testFunction = TestFunction()
                testFunction.ctx = activity
//                val m: Method =
//                    TestFunction::class.java.getDeclaredMethod(
//                        "Test_Label_SampleTicket_58MM_1",
//                        Pointer::class.java
//                    )
//                m.isAccessible = true
//                m.invoke(testFunction, h)
                testFunction.Test_Label_SampleTicket_58MM_1(h,weight)
            }
    }

    fun onDestroy(){
        removeCallback()
    }

    private fun removeCallback() {
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortOpenedEvent(opened_callback)
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortOpenFailedEvent(openfailed_callback)
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortClosedEvent(closed_callback)
        AutoReplyPrint.INSTANCE.CP_Printer_RemoveOnPrinterStatusEvent(status_callback)
        AutoReplyPrint.INSTANCE.CP_Printer_RemoveOnPrinterReceivedEvent(received_callback)
        AutoReplyPrint.INSTANCE.CP_Printer_RemoveOnPrinterPrintedEvent(printed_callback)
    }


    private fun AddCallback() {
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortOpenedEvent(opened_callback, Pointer.NULL)
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortOpenFailedEvent(openfailed_callback, Pointer.NULL)
        AutoReplyPrint.INSTANCE.CP_Port_AddOnPortClosedEvent(closed_callback, Pointer.NULL)
        AutoReplyPrint.INSTANCE.CP_Printer_AddOnPrinterStatusEvent(status_callback, Pointer.NULL)
        AutoReplyPrint.INSTANCE.CP_Printer_AddOnPrinterReceivedEvent(
            received_callback,
            Pointer.NULL
        )
        AutoReplyPrint.INSTANCE.CP_Printer_AddOnPrinterPrintedEvent(printed_callback, Pointer.NULL)
    }

    private fun checkBluetoothPermission(): Boolean {
        return checkGPSEnabled() && checkLocationPermission()
    }
    fun checkGPSEnabled(): Boolean {
        var isEnabled = false
        val lm = activity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (ok) {
            isEnabled = true
        } else {
            Toast.makeText(
                activity,
                "Please enable gps else will not search ble printer",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent()
            intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            activity. startActivityForResult(intent, 2)
        }
        return isEnabled
    }

    fun checkLocationPermission(): Boolean {
        var hasPermission = false
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                val LOCATIONGPS = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                )
                ActivityCompat.requestPermissions(activity, LOCATIONGPS, 1)
            } else {
                hasPermission = true
            }
        } else {
            hasPermission = true
        }
        return hasPermission
    }

    var opened_callback: AutoReplyPrint.CP_OnPortOpenedEvent_Callback =
        object : AutoReplyPrint.CP_OnPortOpenedEvent_Callback {
            override fun CP_OnPortOpenedEvent(
                handle: Pointer?,
                name: String?,
                private_data: Pointer?
            ) {
                activity!!.runOnUiThread {
                    Toast.makeText(
                        activity,
                        "Open Success",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    var openfailed_callback: AutoReplyPrint.CP_OnPortOpenFailedEvent_Callback =
        object : AutoReplyPrint.CP_OnPortOpenFailedEvent_Callback {
            override fun CP_OnPortOpenFailedEvent(
                handle: Pointer?,
                name: String?,
                private_data: Pointer?
            ) {
                activity!!.runOnUiThread {
                    Toast.makeText(
                        activity,
                        "Open Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    var closed_callback: AutoReplyPrint.CP_OnPortClosedEvent_Callback =
        object : AutoReplyPrint.CP_OnPortClosedEvent_Callback {
            override fun CP_OnPortClosedEvent(h: Pointer?, private_data: Pointer?) {
                activity!!.runOnUiThread { ClosePort() }
            }
        }

    private fun ClosePort() {
        if (h !== com.sun.jna.Pointer.NULL) {
            AutoReplyPrint.INSTANCE.CP_Port_Close(h)
            h = Pointer.NULL
        }
//        RefreshUI()
    }

    var status_callback: AutoReplyPrint.CP_OnPrinterStatusEvent_Callback =
        object : AutoReplyPrint.CP_OnPrinterStatusEvent_Callback {
            override fun CP_OnPrinterStatusEvent(
                h: Pointer?,
                printer_error_status: Long,
                printer_info_status: Long,
                private_data: Pointer?
            ) {
                activity!!.runOnUiThread {
                    val calendar = Calendar.getInstance()
                    val calendarDate = calendar.time
                    val time =
                        DateFormat.format("yyyy-MM-dd kk:mm:ss", calendarDate)
                            .toString()
                    val status: AutoReplyPrint.CP_PrinterStatus =
                        AutoReplyPrint.CP_PrinterStatus(printer_error_status, printer_info_status)
                    var error_status_string = String.format(
                        " Printer Error Status: 0x%04X",
                        printer_error_status and 0xffff
                    )
                    if (status.ERROR_OCCURED()) {
                        if (status.ERROR_CUTTER()) error_status_string += "[ERROR_CUTTER]"
                        if (status.ERROR_FLASH()) error_status_string += "[ERROR_FLASH]"
                        if (status.ERROR_NOPAPER()) error_status_string += "[ERROR_NOPAPER]"
                        if (status.ERROR_VOLTAGE()) error_status_string += "[ERROR_VOLTAGE]"
                        if (status.ERROR_MARKER()) error_status_string += "[ERROR_MARKER]"
                        if (status.ERROR_ENGINE()) error_status_string += "[ERROR_MOVEMENT]"
                        if (status.ERROR_OVERHEAT()) error_status_string += "[ERROR_OVERHEAT]"
                        if (status.ERROR_COVERUP()) error_status_string += "[ERROR_COVERUP]"
                        if (status.ERROR_MOTOR()) error_status_string += "[ERROR_MOTOR]"
                    }
                    var info_status_string = String.format(
                        " Printer Info Status: 0x%04X",
                        printer_info_status and 0xffff
                    )
                    if (status.INFO_LABELMODE()) info_status_string += "[Label Mode]"
                    if (status.INFO_LABELPAPER()) info_status_string += "[Label Paper]"
                    if (status.INFO_PAPERNOFETCH()) info_status_string += "[Paper Not Fetch]"
//                    textViewPrinterErrorStatus!!.text = time + error_status_string
//                    textViewPrinterInfoStatus!!.text = time + info_status_string
                }
            }
        }
    var received_callback: AutoReplyPrint.CP_OnPrinterReceivedEvent_Callback =
        object : AutoReplyPrint.CP_OnPrinterReceivedEvent_Callback {
            override fun CP_OnPrinterReceivedEvent(
                h: Pointer?,
                printer_received_byte_count: Int,
                private_data: Pointer?
            ) {
                activity!!.runOnUiThread {
                    val calendar = Calendar.getInstance()
                    val calendarDate = calendar.time
                    val time =
                        DateFormat.format("yyyy-MM-dd kk:mm:ss", calendarDate)
                            .toString()
//                    textViewPrinterReceived!!.text =
//                        "$time PrinterReceived: $printer_received_byte_count"
                }
            }
        }
    var printed_callback: AutoReplyPrint.CP_OnPrinterPrintedEvent_Callback =
        object : AutoReplyPrint.CP_OnPrinterPrintedEvent_Callback {
            override fun CP_OnPrinterPrintedEvent(
                h: Pointer?,
                printer_printed_page_id: Int,
                private_data: Pointer?
            ) {
                activity!!.runOnUiThread {
                    val calendar = Calendar.getInstance()
                    val calendarDate = calendar.time
                    val time =
                        DateFormat.format("yyyy-MM-dd kk:mm:ss", calendarDate)
                            .toString()
//                    textViewPrinterPrinted!!.text = "$time PrinterPrinted: $printer_printed_page_id"
                }
            }
        }
}