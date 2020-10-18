package com.rz.smart.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kongqw.serialportlibrary.Device
import com.rz.smart.R
import java.util.*

/**
 * Created by Kongqw on 2017/11/13.
 * 串口列表适配器
 */
class DeviceAdapter(
    context: Context?,
    devices: ArrayList<Device>
) : BaseAdapter() {
    private val mInflater: LayoutInflater
    private val devices: ArrayList<Device>
    override fun getCount(): Int {
        return devices.size
    }

    override fun getItem(position: Int): Device {
        return devices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val holder: ViewHolder
        if (null == convertView) {
            holder = ViewHolder()
            convertView = mInflater.inflate(R.layout.item_device, null)
            holder.device = convertView.findViewById<View>(R.id.tv_device) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val deviceName = devices[position].name
        val driverName = devices[position].root
        val file = devices[position].file
        val canRead = file.canRead()
        val canWrite = file.canWrite()
        val canExecute = file.canExecute()
        val path = file.absolutePath
        val permission = StringBuffer()
        permission.append("\t权限[")
        permission.append(if (canRead) " 可读 " else " 不可读 ")
        permission.append(if (canWrite) " 可写 " else " 不可写 ")
        permission.append(if (canExecute) " 可执行 " else " 不可执行 ")
        permission.append("]")
        holder.device!!.text = String.format(
            "%s [%s] (%s)  %s",
            deviceName,
            driverName,
            path,
            permission
        )
        return convertView
    }

    private inner class ViewHolder {
        var device: TextView? = null
    }

    init {
        mInflater = LayoutInflater.from(context)
        this.devices = devices
    }
}