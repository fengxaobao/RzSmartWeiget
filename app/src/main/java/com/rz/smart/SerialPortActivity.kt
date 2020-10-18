package com.rz.smart

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kongqw.serialportlibrary.Device
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import java.io.File
import java.util.*

class SerialPortActivity : AppCompatActivity(), OnOpenSerialPortListener {
    private var mSerialPortManager: SerialPortManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serial_port)
        val device =
            getIntent().getSerializableExtra(DEVICE) as Device
        Log.i(TAG, "onCreate: device = $device")
        if (null == device) {
            finish()
            return
        }
        mSerialPortManager = SerialPortManager()

        // 打开串口
        val openSerialPort = mSerialPortManager!!.setOnOpenSerialPortListener(this)
            .setOnSerialPortDataListener(object : OnSerialPortDataListener {
                override fun onDataReceived(bytes: ByteArray) {
                    Log.i(
                        TAG,
                        "onDataReceived [ byte[] ]: " + Arrays.toString(bytes)
                    )
                    Log.i(
                        TAG,
                        "onDataReceived [ String ]: " + String(bytes)
                    )
                    runOnUiThread(Runnable { showToast(String.format("接收\n%s", String(bytes))) })
                }

                override fun onDataSent(bytes: ByteArray) {
                    Log.i(
                        TAG,
                        "onDataSent [ byte[] ]: " + Arrays.toString(bytes)
                    )
                    Log.i(
                        TAG,
                        "onDataSent [ String ]: " + String(bytes)
                    )
                    runOnUiThread(Runnable { showToast(String.format("发送\n%s", String(bytes))) })
                }
            })
            .openSerialPort(device.file, 9600)
        Log.i(
            TAG,
            "onCreate: openSerialPort = $openSerialPort"
        )
    }

    override fun onDestroy() {
        if (null != mSerialPortManager) {
            mSerialPortManager!!.closeSerialPort()
            mSerialPortManager = null
        }
        super.onDestroy()
    }

    /**
     * 串口打开成功
     *
     * @param device 串口
     */
    override fun onSuccess(device: File) {
        Toast.makeText(
            getApplicationContext(),
            String.format("串口 [%s] 打开成功", device.path),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * 串口打开失败
     *
     * @param device 串口
     * @param status status
     */
    override fun onFail(
        device: File,
        status: OnOpenSerialPortListener.Status
    ) {
        when (status) {
            OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION -> showDialog(
                device.path,
                "没有读写权限"
            )
            OnOpenSerialPortListener.Status.OPEN_FAIL -> showDialog(device.path, "串口打开失败")
            else -> showDialog(device.path, "串口打开失败")
        }
    }

    /**
     * 显示提示框
     *
     * @param title   title
     * @param message message
     */
    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("退出", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
                finish()
            })
            .setCancelable(false)
            .create()
            .show()
    }

    /**
     * 发送数据
     *
     * @param view view
     */
    fun onSend(view: View?) {
        val editTextSendContent = findViewById(R.id.et_send_content) as EditText? ?: return
        val sendContent =
            editTextSendContent.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(sendContent)) {
            Log.i(TAG, "onSend: 发送内容为 null")
            return
        }
        val sendContentBytes = sendContent.toByteArray()
        val sendBytes = mSerialPortManager!!.sendBytes(sendContentBytes)
        Log.i(TAG, "onSend: sendBytes = $sendBytes")
        showToast(if (sendBytes) "发送成功" else "发送失败")
    }

    private var mToast: Toast? = null

    /**
     * Toast
     *
     * @param content content
     */
    private fun showToast(content: String) {
        if (null == mToast) {
            mToast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT)
        }
        mToast!!.setText(content)
        mToast!!.show()
    }

    companion object {
        private val TAG = SerialPortActivity::class.java.simpleName
        const val DEVICE = "device"
    }
}