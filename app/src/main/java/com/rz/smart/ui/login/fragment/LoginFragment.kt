package com.rz.smart.ui.login.fragment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.jetpack.base.mvvm.logD
import com.jetpack.base.mvvm.ui.fragment.BaseVMFragment
import com.rz.smart.R
import com.rz.smart.model.MainViewModel
import com.rz.smart.ui.login.LoginActivity
import com.rz.smart.utils.PrintUsbManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class LoginFragment : BaseVMFragment<LoginViewModel>() {
    private var viewModel: MainViewModel = MainViewModel()
    private var mActivity: LoginActivity? = null
    private lateinit var userName: String
    private lateinit var passWord: String
    private lateinit var printUsbManager: PrintUsbManager
    var weight:String = ""
    override fun onAttach(context: Context) {


        super.onAttach(context)
        mActivity = context as LoginActivity
        printUsbManager = PrintUsbManager((activity as AppCompatActivity?)!!)
    }

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun getLayoutResId(): Int = R.layout.login_fragment

    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {
        loginInput.getEditText().setText("kuguan1")
        passwordInput.editText.setText("123456")
    }

    override fun initData() {
        viewModel.openSerialPort(object : MainViewModel.SerialPortListener {
            override fun onDataReceived(data: String) {
                data.logD()
                weight= data
            }

            override fun onDataSent(data: String) {
            }

        })

        btnLogin.setOnClickListener {
            try {
                printUsbManager.printTest(weight)
            }catch (e:Exception){
                e.printStackTrace()
            }

//            if(checkInput()){
//                CacheDataUtils.USERNAME1 = userName
//                CacheDataUtils.PASSWORD1 = passWord
//                mActivity?.changeToReLogin()
//            }
        }

    }

    override fun startObserve() {

    }

    fun checkInput(): Boolean{
        userName = loginInput.text.toString().trim()
        passWord = passwordInput.text.toString().trim()
        if (userName.isEmpty()) {
            Toasty.error(activity!!,"请输入账户", Toasty.LENGTH_LONG).show()
            return false
        }
        if (passWord.isEmpty()) {
            Toasty.error(activity!!,"请输入密码", Toasty.LENGTH_LONG).show()
            return false
        }
        return true
    }

}