package com.rz.smart.ui.login.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.jetpack.base.mvvm.logD
import com.jetpack.base.mvvm.ui.fragment.BaseVMFragment
import com.rz.smart.MainActivity
import com.rz.smart.R
import com.rz.smart.ui.login.LoginActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.login_fragment.loginInput
import kotlinx.android.synthetic.main.login_fragment.passwordInput
import kotlinx.android.synthetic.main.re_login_fragment.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ReLoginFragment : BaseVMFragment<LoginViewModel>() {

    private var mActivity: LoginActivity? = null

    private lateinit var reUserName: String
    private lateinit var rePassWord: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as LoginActivity
    }

    companion object {
        fun newInstance() = ReLoginFragment()
    }

    override fun getLayoutResId(): Int = R.layout.re_login_fragment

    override fun initVM(): LoginViewModel = getViewModel()

    override fun initView() {

        reLoginInput.editText.setText("kuguan2")
        rePasswordInput.editText.setText("123456")
    }

    override fun initData() {
        reUserName = reLoginInput.text.toString().trim()
        rePassWord = rePasswordInput.text.toString().trim()
        reBtnLogin.setOnClickListener {
            if(checkInput()){
                //跳转main
                "从loginfragment里面获取的账户密码：${_viewModel.userName},${_viewModel.userPwd}".logD()
                _viewModel.login(_viewModel.userName,_viewModel.userPwd,reUserName,rePassWord)
            }
        }
    }

    override fun startObserve() {
        _viewModel.userLoginSuccess.observe(viewLifecycleOwner, Observer {
            if(it){
                val intent = Intent()
                intent.setClass(requireActivity(),MainActivity::class.java)
                startActivity(intent)
                mActivity?.finish()
//                startActivity(Intent(this@))
            }
        })
    }


    fun checkInput(): Boolean{
        reUserName = reLoginInput.text.toString().trim()
        reUserName = rePasswordInput.text.toString().trim()
        if (reUserName.isEmpty()) {
            Toasty.error(activity!!,"请输入账户", Toasty.LENGTH_LONG).show()
            return false
        }
        if (reUserName.isEmpty()) {
            Toasty.error(activity!!,"请输入密码", Toasty.LENGTH_LONG).show()
            return false
        }
        return true
    }

}