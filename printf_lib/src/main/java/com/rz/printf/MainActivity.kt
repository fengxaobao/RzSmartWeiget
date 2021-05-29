package com.rz.printf

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.caysn.autoreplyprint.AutoReplyPrint
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import java.lang.reflect.Method
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var activity: MainActivity? = null
    private lateinit var layoutMain: LinearLayout
    private lateinit var rgPort: GRadioGroup
    private lateinit var rbBT2: RadioButton
    private lateinit var rbBT4: RadioButton
    private lateinit var rbNET: RadioButton
    private lateinit var rbUSB: RadioButton
    private lateinit var rbCOM: RadioButton
    private lateinit var cbxListBT2: ComboBox
    private lateinit var cbxListBT4: ComboBox
    private lateinit var cbxListNET: ComboBox
    private lateinit var cbxListUSB: ComboBox
    private lateinit var cbxListCOMPort: ComboBox
    private lateinit var cbxListCOMBaud: ComboBox
    private lateinit var btnEnumPort: Button
    private lateinit var btnOpenPort: Button
    private lateinit var btnClosePort: Button
    private lateinit var listViewTestFunction: ListView
    private lateinit var linearlayoutPrinterInfo: LinearLayout
    private lateinit var textViewFirmwareVersion: TextView
    private lateinit var textViewResolutionInfo: TextView
    private lateinit var textViewPrinterErrorStatus: TextView
    private lateinit var textViewPrinterInfoStatus: TextView
    private lateinit var textViewPrinterReceived: TextView
    private lateinit var textViewPrinterPrinted: TextView
    private var h: Pointer? = Pointer.NULL
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
                    textViewPrinterErrorStatus!!.text = time + error_status_string
                    textViewPrinterInfoStatus!!.text = time + info_status_string
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
                    textViewPrinterReceived!!.text =
                        "$time PrinterReceived: $printer_received_byte_count"
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
                    textViewPrinterPrinted!!.text = "$time PrinterPrinted: $printer_printed_page_id"
                }
            }
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

    private fun RemoveCallback() {
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortOpenedEvent(opened_callback)
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortOpenFailedEvent(openfailed_callback)
        AutoReplyPrint.INSTANCE.CP_Port_RemoveOnPortClosedEvent(closed_callback)
        AutoReplyPrint.INSTANCE.CP_Printer_RemoveOnPrinterStatusEvent(status_callback)
        AutoReplyPrint.INSTANCE.CP_Printer_RemoveOnPrinterReceivedEvent(received_callback)
        AutoReplyPrint.INSTANCE.CP_Printer_RemoveOnPrinterPrintedEvent(printed_callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title =
            resources.getString(R.string.app_name) + " " + AutoReplyPrint.INSTANCE.CP_Library_Version()
        activity = this
        layoutMain = findViewById<View>(R.id.layoutMain) as LinearLayout
        rbBT2 = findViewById<View>(R.id.rbBT2) as RadioButton
        rbBT4 = findViewById<View>(R.id.rbBT4) as RadioButton
        rbNET = findViewById<View>(R.id.rbNET) as RadioButton
        rbUSB = findViewById<View>(R.id.rbUSB) as RadioButton
        rbCOM = findViewById<View>(R.id.rbCOM) as RadioButton
        cbxListBT2 = findViewById<View>(R.id.cbxLisbBT2) as ComboBox
        cbxListBT4 = findViewById<View>(R.id.cbxLisbBT4) as ComboBox
        cbxListNET = findViewById<View>(R.id.cbxListNET) as ComboBox
        cbxListUSB = findViewById<View>(R.id.cbxLisbUSB) as ComboBox
        cbxListCOMPort = findViewById<View>(R.id.cbxListCOMPort) as ComboBox
        cbxListCOMBaud = findViewById<View>(R.id.cbxListCOMBaud) as ComboBox
        btnEnumPort = findViewById<View>(R.id.btnEnumPort) as Button
        btnOpenPort = findViewById<View>(R.id.btnOpenPort) as Button
        btnClosePort = findViewById<View>(R.id.btnClosePort) as Button
        listViewTestFunction = findViewById<View>(R.id.listViewTestFunction) as ListView
        linearlayoutPrinterInfo = findViewById(R.id.linearlayoutPrinterInfo)
        textViewFirmwareVersion = findViewById(R.id.textViewFirmwareVersion)
        textViewResolutionInfo = findViewById(R.id.textViewResolutionInfo)
        textViewPrinterErrorStatus = findViewById(R.id.textViewPrinterErrorStatus)
        textViewPrinterInfoStatus = findViewById(R.id.textViewPrinterInfoStatus)
        textViewPrinterReceived = findViewById(R.id.textViewPrinterReceived)
        textViewPrinterPrinted = findViewById(R.id.textViewPrinterPrinted)
        for (baud in nBaudTable) {
            cbxListCOMBaud!!.addString("" + baud)
        }
        cbxListCOMBaud!!.setText("115200")
        btnEnumPort!!.setOnClickListener(this)
        btnOpenPort!!.setOnClickListener(this)
        btnClosePort!!.setOnClickListener(this)
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            TestFunction.testFunctionOrderedList
        )
        listViewTestFunction!!.adapter = adapter
        listViewTestFunction!!.onItemClickListener = onTestFunctionClicked
        rgPort = GRadioGroup(rbBT2, rbBT4, rbNET, rbUSB, rbCOM)
        rbBT2!!.performClick()
        enableBluetooth()
        btnEnumPort!!.performClick()
        RefreshUI()
        AddCallback()
    }

    override fun onDestroy() {
        RemoveCallback()
        btnClosePort!!.performClick()
        super.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnEnumPort -> EnumPort()
            R.id.btnOpenPort -> OpenPort()
            R.id.btnClosePort -> ClosePort()
        }
    }

    private val onTestFunctionClicked =
        AdapterView.OnItemClickListener { parent, view, position, id ->
            val functionName = (view as TextView).text.toString()
            if (functionName == null || functionName.isEmpty()) return@OnItemClickListener
            DisableUI()
            Thread {
                try {
                    val `fun` = TestFunction()
                    `fun`.ctx = activity
                    val m: Method =
                        TestFunction::class.java.getDeclaredMethod(
                            functionName,
                            Pointer::class.java
                        )
                    m.invoke(`fun`, h)
                } catch (tr: Throwable) {
                    tr.printStackTrace()
                }
                activity!!.runOnUiThread { RefreshUI() }
            }.start()
        }

    private fun EnumCom() {
        cbxListCOMPort.setText("")
        cbxListCOMPort.clear()
        val devicePaths: Array<String> = AutoReplyPrint.CP_Port_EnumCom_Helper.EnumCom()
        if (devicePaths != null) {
            for (i in devicePaths.indices) {
                val name = devicePaths[i]
                cbxListCOMPort.addString(name)
                var text: String = cbxListCOMPort.getText()
                if (text.trim { it <= ' ' } == "") {
                    text = name
                    cbxListCOMPort.setText(text)
                }
            }
        }
    }

    private fun EnumUsb() {
        cbxListUSB.setText("")
        cbxListUSB.clear()
        val devicePaths: Array<String> = AutoReplyPrint.CP_Port_EnumUsb_Helper.EnumUsb()
        if (devicePaths != null) {
            for (i in devicePaths.indices) {
                val name = devicePaths[i]
                cbxListUSB.addString(name)
                var text: String = cbxListUSB.getText()
                if (text.trim { it <= ' ' } == "") {
                    text = name
                    cbxListUSB.setText(text)
                }
            }
        }
    }

    var inNetEnum = false
    private fun EnumNet() {
        if (inNetEnum) return
        inNetEnum = true
        Thread {
            val cancel = IntByReference(0)
            val callback: AutoReplyPrint.CP_OnNetPrinterDiscovered_Callback =
                object : AutoReplyPrint.CP_OnNetPrinterDiscovered_Callback {
                    override fun CP_OnNetPrinterDiscovered(
                        local_ip: String?,
                        disconvered_mac: String?,
                        disconvered_ip: String?,
                        discovered_name: String?,
                        private_data: Pointer?
                    ) {
                        activity!!.runOnUiThread {
                            if (!cbxListNET.getData()
                                    .contains(disconvered_ip)
                            ) cbxListNET.addString(disconvered_ip)
                            if (cbxListNET.getText().trim().equals("")) {
                                cbxListNET.setText(disconvered_ip)
                            }
                        }
                    }
                }
            AutoReplyPrint.INSTANCE.CP_Port_EnumNetPrinter(3000, cancel, callback, null)
            inNetEnum = false
        }.start()
    }

    var inBtEnum = false
    private fun EnumBt() {
        if (!checkBluetoothPermission()) return
        if (inBtEnum) return
        inBtEnum = true
        Thread {
            val cancel = IntByReference(0)
            val callback: Any =
                AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback { device_name, device_address, private_data ->
                    activity!!.runOnUiThread {
                        if (!cbxListBT2.getData()
                                .contains(device_address)
                        ) cbxListBT2.addString(device_address)
                        if (cbxListBT2.getText().trim().equals("")) {
                            cbxListBT2.setText(device_address)
                        }
                    }
                }
            AutoReplyPrint.INSTANCE.CP_Port_EnumBtDevice(
                12000, cancel,
                callback as AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback, null
            )
            inBtEnum = false
        }.start()
    }

    var inBleEnum = false
    private fun EnumBle() {
        if (!checkBluetoothPermission()) return
        if (inBleEnum) return
        inBleEnum = true
        Thread {
            val cancel = IntByReference(0)
            val callback: AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback =
                object : AutoReplyPrint.CP_OnBluetoothDeviceDiscovered_Callback {
                    override fun CP_OnBluetoothDeviceDiscovered(
                        device_name: String?,
                        device_address: String?,
                        private_data: Pointer?
                    ) {
                        activity!!.runOnUiThread {
                            if (!cbxListBT4.getData()
                                    .contains(device_address)
                            ) cbxListBT4.addString(device_address)
                            if (cbxListBT4.getText().trim().equals("")) {
                                cbxListBT4.setText(device_address)
                            }
                        }
                    }
                }
            AutoReplyPrint.INSTANCE.CP_Port_EnumBleDevice(20000, cancel, callback, null)
            inBleEnum = false
        }.start()
    }

    private fun enableBluetooth() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (null != adapter) {
            if (!adapter.isEnabled) {
                if (!adapter.enable()) {
                    //finish();
                    Toast.makeText(this, "Failed to enable bluetooth adapter", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    fun checkGPSEnabled(): Boolean {
        var isEnabled = false
        val lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        val ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (ok) {
            isEnabled = true
        } else {
            Toast.makeText(
                this,
                "Please enable gps else will not search ble printer",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent()
            intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            startActivityForResult(intent, 2)
        }
        return isEnabled
    }

    fun checkLocationPermission(): Boolean {
        var hasPermission = false
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                val LOCATIONGPS = arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                )
                ActivityCompat.requestPermissions(this, LOCATIONGPS, 1)
            } else {
                hasPermission = true
            }
        } else {
            hasPermission = true
        }
        return hasPermission
    }

    private fun checkBluetoothPermission(): Boolean {
        return checkGPSEnabled() && checkLocationPermission()
    }

    private fun EnumPort() {
        EnumCom()
        EnumUsb()
        EnumNet()
        EnumBt()
        EnumBle()
    }

    private fun OpenPort() {
        DisableUI()
        val rbBT2Checked = rbBT2!!.isChecked
        val rbBT4Checked = rbBT4!!.isChecked
        val rbNETChecked = rbNET!!.isChecked
        val rbUSBChecked = rbUSB!!.isChecked
        val rbCOMChecked = rbCOM!!.isChecked
        val strBT2Address: String = cbxListBT2.getText()
        val strBT4Address: String = cbxListBT4.getText()
        val strNETAddress: String = cbxListNET.getText()
        val strUSBPort: String = cbxListUSB.getText()
        val strCOMPort: String = cbxListCOMPort.getText()
        val nComBaudrate: Int = cbxListCOMBaud.getText().toInt()
        Thread {
            if (rbBT2Checked) {
                h = AutoReplyPrint.INSTANCE.CP_Port_OpenBtSpp(strBT2Address, 1)
            } else if (rbBT4Checked) {
                h = AutoReplyPrint.INSTANCE.CP_Port_OpenBtBle(strBT4Address, 1)
            } else if (rbNETChecked) {
                h = AutoReplyPrint.INSTANCE.CP_Port_OpenTcp(
                    null, strNETAddress,
                    9100.toShort(), 5000, 1
                )
            } else if (rbUSBChecked) {
                h = AutoReplyPrint.INSTANCE.CP_Port_OpenUsb(strUSBPort, 1)
            } else if (rbCOMChecked) {
                h = AutoReplyPrint.INSTANCE.CP_Port_OpenCom(
                    strCOMPort,
                    nComBaudrate,
                    AutoReplyPrint.CP_ComDataBits_8,
                    AutoReplyPrint.CP_ComParity_NoParity,
                    AutoReplyPrint.CP_ComStopBits_One,
                    AutoReplyPrint.CP_ComFlowControl_None,
                    1
                )
            }
            activity!!.runOnUiThread { RefreshUI() }
        }.start()
    }

    private fun ClosePort() {
        if (h !== Pointer.NULL) {
            AutoReplyPrint.INSTANCE.CP_Port_Close(h)
            h = Pointer.NULL
        }
        RefreshUI()
    }

    private fun DisableUI() {
        rbBT2!!.isEnabled = false
        rbBT4!!.isEnabled = false
        rbNET!!.isEnabled = false
        rbUSB!!.isEnabled = false
        rbCOM!!.isEnabled = false
        cbxListBT2.setEnabled(false)
        cbxListBT4.setEnabled(false)
        cbxListNET.setEnabled(false)
        cbxListUSB.setEnabled(false)
        cbxListCOMPort.setEnabled(false)
        cbxListCOMBaud.setEnabled(false)
        btnEnumPort!!.isEnabled = false
        btnOpenPort!!.isEnabled = false
        btnClosePort!!.isEnabled = false
        listViewTestFunction!!.isEnabled = false
    }

    private fun RefreshUI() {
        rbBT2!!.isEnabled = h === Pointer.NULL
        rbBT4!!.isEnabled = h === Pointer.NULL
        rbNET!!.isEnabled = h === Pointer.NULL
        rbUSB!!.isEnabled = h === Pointer.NULL
        rbCOM!!.isEnabled = h === Pointer.NULL
        cbxListBT2.setEnabled(h === Pointer.NULL)
        cbxListBT4.setEnabled(h === Pointer.NULL)
        cbxListNET.setEnabled(h === Pointer.NULL)
        cbxListUSB.setEnabled(h === Pointer.NULL)
        cbxListCOMPort.setEnabled(h === Pointer.NULL)
        cbxListCOMBaud.setEnabled(h === Pointer.NULL)
        btnEnumPort!!.isEnabled = h === Pointer.NULL
        btnOpenPort!!.isEnabled = h === Pointer.NULL
        btnClosePort!!.isEnabled = h !== Pointer.NULL
        listViewTestFunction!!.isEnabled = h !== Pointer.NULL
        val visibility = if (h === Pointer.NULL) View.VISIBLE else View.GONE
        if (!rbBT2!!.isChecked) {
            rbBT2!!.visibility = visibility
            cbxListBT2.setVisibility(visibility)
        }
        if (!rbBT4!!.isChecked) {
            rbBT4!!.visibility = visibility
            cbxListBT4.setVisibility(visibility)
        }
        if (!rbNET!!.isChecked) {
            rbNET!!.visibility = visibility
            cbxListNET.setVisibility(visibility)
        }
        if (!rbUSB!!.isChecked) {
            rbUSB!!.visibility = visibility
            cbxListUSB.setVisibility(visibility)
        }
        if (!rbCOM!!.isChecked) {
            rbCOM!!.visibility = visibility
            cbxListCOMPort.setVisibility(visibility)
            cbxListCOMBaud.setVisibility(visibility)
        }
        if (h !== Pointer.NULL) {
            textViewFirmwareVersion!!.text =
                "FirmwareVersion: " + AutoReplyPrint.CP_Printer_GetPrinterFirmwareVersion_Helper.GetPrinterFirmwareVersion(
                    h
                )
            val width_mm = IntByReference()
            val height_mm = IntByReference()
            val dots_per_mm = IntByReference()
            if (AutoReplyPrint.INSTANCE.CP_Printer_GetPrinterResolutionInfo(
                    h,
                    width_mm,
                    height_mm,
                    dots_per_mm
                )
            ) {
                textViewResolutionInfo!!.text =
                    "ResolutionInfo: " + "width:" + width_mm.getValue() + "mm " + "height:" + height_mm.getValue() + "mm " + "dots_per_mm:" + dots_per_mm.getValue()
            }
        }
    }

    companion object {
        private val nBaudTable = intArrayOf(
            1200,
            2400,
            4800,
            9600,
            19200,
            38400,
            57600,
            115200,
            230400,
            256000,
            500000,
            750000,
            1125000,
            1500000
        )
    }
}