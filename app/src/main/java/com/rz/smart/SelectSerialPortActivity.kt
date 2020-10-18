package com.rz.smart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.kongqw.serialportlibrary.Device
import com.kongqw.serialportlibrary.SerialPortFinder
import com.rz.smart.ui.adapter.DeviceAdapter

class SelectSerialPortActivity : AppCompatActivity(), OnItemClickListener {
    private var mDeviceAdapter: DeviceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_serial_port)
        val listView =
            findViewById(R.id.lv_devices) as ListView?
        val serialPortFinder = SerialPortFinder()
        val devices =
            serialPortFinder.devices
        if (listView != null) {
            listView.emptyView = findViewById(R.id.tv_empty)
            mDeviceAdapter = DeviceAdapter(getApplicationContext(), devices)
            listView.adapter = mDeviceAdapter
            listView.onItemClickListener = this
        }
    }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
        val device: Device = mDeviceAdapter!!.getItem(position)
        val intent = Intent(this, SerialPortActivity::class.java)
        intent.putExtra(SerialPortActivity.Companion.DEVICE, device)
        startActivity(intent)
    }

    companion object {
        private val TAG = SelectSerialPortActivity::class.java.simpleName
    }
}