package com.jetpack.base.mvvm.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.jetpack.base.mvvm.vm.BaseViewModel
import com.rz.utils.RxKeyboardTool

/**
 * Created by luyao
 * on 2019/12/27 9:39
 */
abstract class BaseVMFragment<VM : BaseViewModel>(useDataBinding: Boolean = true) :
    BaseCommandFragment() {

    private val _useBinding = useDataBinding
    protected lateinit var mBinding: ViewDataBinding
    protected lateinit var _viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (_useBinding) {
            mBinding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
            onClickViewForHideKeyboard(mBinding.root)
            mBinding.root
        } else {
            val view = inflater.inflate(getLayoutResId(), container, false)
            onClickViewForHideKeyboard(view)
            view
        }

    }

    fun onClickViewForHideKeyboard(view: View) {
        view.setOnClickListener {
            RxKeyboardTool.hideKeyboard(view.context as Activity, view)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _viewModel = initVM()
        if (_useBinding) mBinding.lifecycleOwner = this
        initView()
        initData()
        startObserve()
        super.onViewCreated(view, savedInstanceState)

    }

    abstract fun getLayoutResId(): Int
    abstract fun initVM(): VM
    abstract fun initView()
    abstract fun initData()
    abstract fun startObserve()
}