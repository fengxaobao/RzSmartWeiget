package com.rz.smart.ui.main

import BmnTextWatcher
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.rz.smart.R
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import com.rz.smart.utils.CacheDataUtils
import com.uc.crashsdk.export.LogType.addType
import kotlinx.android.synthetic.main.dialog_goods.*


/**
 * 作者：iss on 2020/6/12 17:32
 * 邮箱：55921173@qq.com
 * 类备注：
 */
class GoodsPriceDialog : DialogFragment() {
    lateinit var goodEntity: CuisineInfo
    private var backListener: CallBackListener? = null
    lateinit var reuslt:UploadMenuInfo

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
        goodName.text = "${goodEntity.GoodsName}"
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

        val nameList = mutableListOf<String>()
        for (i in CacheDataUtils.WARE_HOUSE_NAME_LIST){
            nameList.add(i.WarehouseName!!)
        }

        goodsAmount.addTextChangedListener(object : BmnTextWatcher(){
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                goodsConfirm.isEnabled = goodsAmount.text.toString().isNotBlank()
            }
        })

        goodsAmount.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {   // 按下完成按钮，这里和上面imeOptions对应
                handlerConfirm()
            }
            false;   //返回true，保留软键盘。false，隐藏软键盘
        }


        val adapter: ArrayAdapter<String> =
            ArrayAdapter(activity!!, R.layout.item_type, nameList)

        add_type.adapter = adapter
        add_type.setOnItemClickListener { parent, view, position, id ->
            reuslt = CacheDataUtils.WARE_HOUSE_NAME_LIST.find { it.WarehouseName.equals(nameList[position])}!!
        }

    }

    private fun handlerConfirm() {
        val price = commWeight.text.toString()
        val split = price.split(" ")
        val weight = split[0]

        var goodsAmount = goodsAmount.text.toString().trim()

        if (price.isNotBlank() && goodsAmount.isNotBlank()) {
            val weightDouble = weight.toDouble()
//            goodEntity.GoodsStock = weightDouble
            if (null != backListener) {
                backListener!!.callbackListener(goodEntity,reuslt,goodsAmount)
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
//            args.putSerializable("UploadMenuInfo",bean)
            val fragment = GoodsPriceDialog()
            fragment.arguments = args
            return fragment
        }
    }

    interface CallBackListener {
        fun callbackListener(entity: CuisineInfo,entit2y:UploadMenuInfo,goodsAmount:String)
    }
}