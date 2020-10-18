package com.rz.smart.ui.main

import BmnTextWatcher
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import com.rz.smart.R
import com.rz.smart.model.entity.CuisineInfo
import kotlinx.android.synthetic.main.dialog_goods.*

/**
 * 作者：iss on 2020/6/12 17:32
 * 邮箱：55921173@qq.com
 * 类备注：
 */
class GoodsPriceDialog : DialogFragment() {
    lateinit var goodEntity: CuisineInfo
    private var backListener: CallBackListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        if (null != arguments) {
            goodEntity =
                arguments.getSerializable("GoodEntity") as CuisineInfo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_goods, container, true)
    }

    fun setWeight(weight: String) {
        if (null != commWeight) {
            commWeight.setText("$weight kg")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        goodName.text = "${goodEntity.F_NAME}"
        dialogCloseImg.setOnClickListener {
            dismiss()
        }
        goodsConfirm.setOnClickListener {
            handlerConfirm()
        }
        commWeight.addTextChangedListener(object : BmnTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                goodsConfirm.isEnabled = commWeight.text.toString().isNotBlank()
            }
        })
        commWeight.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {   // 按下完成按钮，这里和上面imeOptions对应
                handlerConfirm()
            }
            false;   //返回true，保留软键盘。false，隐藏软键盘
        }
    }

    private fun handlerConfirm() {
        val price = commWeight.text.toString()
        val split = price.split(" ")
        val weight = split[0]
        if (price.isNotBlank()) {
            val weightDouble = weight.toDouble()
            goodEntity.F_Weight = weightDouble
            if (null != backListener) {
                backListener!!.callbackListener(goodEntity)
            }
            dismiss()
        }

    }

    fun setCallBackListener(back: CallBackListener) {
        backListener = back
    }

    companion object {
        fun newInstance(bean: CuisineInfo): GoodsPriceDialog {
            val args = Bundle()
            args.putSerializable("GoodEntity", bean)
            val fragment = GoodsPriceDialog()
            fragment.arguments = args
            return fragment
        }
    }

    interface CallBackListener {
        fun callbackListener(entity: CuisineInfo)
    }
}