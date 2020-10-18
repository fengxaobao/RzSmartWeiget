package com.jetpack.base.mvvm.ui.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.jetpack.base.mvvm.vm.BaseViewModel

/**
 * Created by luyao
 * on 2019/12/18 14:46∂
 */
abstract class BaseVMActivity<VM : BaseViewModel>(useDataBinding: Boolean = true) :
    CommandActivity() {

    private val _useBinding = useDataBinding
    protected lateinit var _binding: ViewDataBinding
    lateinit var _viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        startObserve()
    }

    override fun setVMContextView() {
        if (_useBinding) {
            _binding = DataBindingUtil.setContentView(this, getChildLayoutView())
            _binding.lifecycleOwner = this
        } else setContentView(getChildLayoutView())
    }

    override fun bindViewMode() {
        _viewModel = initVM()
    }

    abstract fun initVM(): VM
    abstract fun initView()
    abstract fun startObserve()

}