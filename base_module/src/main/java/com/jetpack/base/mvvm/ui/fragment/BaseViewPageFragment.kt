package com.jetpack.base.mvvm.ui.fragment

import androidx.fragment.app.Fragment


/**
 * mike.feng
 * 2019/1/31.
 */
abstract class BaseViewPageFragment : BaseFragment() {
    /**
     * 页面的title
     *
     * @return
     */
    protected abstract fun fragmentTitle(): List<String>

    /**
     * @return
     */
    protected abstract fun listFragment(): List<Fragment>

    /**
     * viewpage limit
     *
     * @return
     */
    protected abstract fun viewLimit(): Int


}
