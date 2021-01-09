package com.rz.smart.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jetpack.base.mvvm.checkResult
import com.jetpack.base.mvvm.ui.application.BaseApplication
import com.jetpack.base.mvvm.vm.BaseViewModel
import com.kongqw.serialportlibrary.Device
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import com.rz.smart.repository.SmartRepository
import com.rz.smart.utils.RxStringUtils
import es.dmoral.toasty.Toasty
import java.io.File

class MainViewModel : BaseViewModel(BaseApplication.instance()), OnOpenSerialPortListener {
    var roomRepository: SmartRepository = SmartRepository()
    private val allCuisineInfo = MutableLiveData<List<CuisineInfo>>()
    val allCuisineInfoLiveData: LiveData<List<CuisineInfo>>
        get() = allCuisineInfo

    private val uploadMenuInfo = MutableLiveData<List<UploadMenuInfo>>()
    val uploadMenuInfoLiveData: LiveData<List<UploadMenuInfo>>
        get() = uploadMenuInfo

    fun getAllCuisine() {
        launchOnUI {
            val result = roomRepository.getGoodsData("123")
            result.checkResult({
                allCuisineInfo.postValue(it?.Data)
            }, {
                it?.let { it1 ->
                    Toasty.error(BaseApplication.instance(), it1).show()
                }
            })
        }
    }

    fun uploadCuisine(GoodId: String?, Weight: Double) {
        launchOnUI {
            val result = roomRepository.setGoodsWeight("","","","","","","")
            result.checkResult({
//                val upLoadList = it!!.UpLoadList
//                if (null != upLoadList) {
//                    val info = upLoadList[0]
//                    val liveData = allCuisineInfo.value
//                    val find = liveData?.find { it.F_ID == info.F_GoodId }
//                    find?.let { find ->
//                        find.F_Money = info.F_Money
//                        find.F_Weight = info.F_Weight
//                    }
//                    allCuisineInfo.postValue(liveData)
//                }
//                uploadMenuInfo.postValue(it?.UpLoadList)
            }, {
                it?.let { it1 ->
                    Toasty.error(BaseApplication.instance(), it1).show()
                }
            })
        }
    }

    fun openSerialPort(listener: SerialPortListener) {
        val device = Device("ttyS1", "serial", File("/dev/ttyS1"))
        val mSerialPortManager = SerialPortManager()
        // 打开串口
        val openSerialPort: Boolean = mSerialPortManager.setOnOpenSerialPortListener(this)
            .setOnSerialPortDataListener(object : OnSerialPortDataListener {
                override fun onDataReceived(bytes: ByteArray) {
//                    Log.i(
//                        "MainViewModel",
//                        "onDataReceived [ byte[] ]: " + Arrays.toString(bytes)
//                    )
//                    Log.i(
//                        "MainViewModel",
//                        "onDataReceived [ String ]: " + String(bytes)
//                    )
                    try {
                        val stringData = String(bytes)
                        val replace = RxStringUtils.replaceAll(stringData)
                        val substring = RxStringUtils.substring(replace)
                        val split = substring.split("kg")
                        val weight = split[0]
                        listener?.let {
                            it.onDataReceived(weight)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onDataSent(bytes: ByteArray) {

                    listener?.let {
//                        val weight = String(bytes)
//                        val split = weight.split("")
//                        it.onDataSent(weight)
                    }
                }
            })
            .openSerialPort(device.file, 9600)

        Log.i("MainViewModel", "onCreate: openSerialPort = $openSerialPort")

    }

    interface SerialPortListener {
        fun onDataReceived(data: String)
        fun onDataSent(data: String)
    }

    override fun onSuccess(device: File?) {
        Toasty.success(
            app,
            String.format("串口 [%s] 打开成功", device!!.path),
            Toast.LENGTH_SHORT
        ).show()

    }

    override fun onFail(device: File?, status: OnOpenSerialPortListener.Status?) {

        Toasty.error(
            app,
            "串口打开失败，Status=${status}",
            Toast.LENGTH_SHORT
        ).show()
    }
}