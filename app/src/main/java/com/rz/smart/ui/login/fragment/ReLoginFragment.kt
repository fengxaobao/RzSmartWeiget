package com.rz.smart.ui.login.fragment

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.jetpack.base.mvvm.logD
import com.jetpack.base.mvvm.ui.fragment.BaseVMFragment
import com.rz.smart.MainActivity
import com.rz.smart.R
import com.rz.smart.event.LoginEvent
import com.rz.smart.ui.login.LoginActivity
import com.rz.smart.utils.CacheDataUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.re_login_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ReLoginFragment : BaseVMFragment<LoginViewModel>() {

    private var mActivity: LoginActivity? = null

    private lateinit var reUserName: String
    private lateinit var rePassWord: String
    var userName : String = ""
    var userPwd : String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as LoginActivity
    }

    companion object {
        fun newInstance() = ReLoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
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
            if (checkInput()) {
                //跳转main
                "从loginfragment里面获取的账户密码：${CacheDataUtils.USERNAME1},${CacheDataUtils.PASSWORD1}".logD()
                _viewModel.login(
                    CacheDataUtils.USERNAME1!!,
                    CacheDataUtils.PASSWORD1!!,
                    reUserName,
                    rePassWord
                )
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