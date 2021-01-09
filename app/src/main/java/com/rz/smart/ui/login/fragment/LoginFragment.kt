package com.rz.smart.ui.login.fragment

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import com.jetpack.base.mvvm.ui.fragment.BaseVMFragment
import com.rz.smart.R
import com.rz.smart.ui.login.LoginActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class LoginFragment : BaseVMFragment<LoginViewModel>() {

    private var mActivity: LoginActivity? = null
    private lateinit var userName: String
    private lateinit var passWord: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as LoginActivity
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

        btnLogin.setOnClickListener {
            if(checkInput()){
//                _viewModel.login("孙明","KD562D","孙明","KD562D")



                _viewModel.userName = userName
                _viewModel.userPwd = passWord

                mActivity?.changeToReLogin()
            }
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