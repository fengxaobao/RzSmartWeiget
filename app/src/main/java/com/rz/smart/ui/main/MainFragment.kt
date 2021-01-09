package com.rz.smart.ui.main

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.orhanobut.logger.Logger
import com.rz.smart.R
import com.rz.smart.model.MainViewModel
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.model.entity.UploadMenuInfo
import com.rz.smart.ui.adapter.GoodsAdapter
import com.rz.smart.utils.CacheDataUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {
    private var viewModel: MainViewModel = MainViewModel()
    private var dialog: GoodsPriceDialog? = null

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.openSerialPort(object : MainViewModel.SerialPortListener {
            override fun onDataReceived(data: String) {
                if (!TextUtils.isEmpty(data) && null != dialog) {
                    activity!!.runOnUiThread {
                        dialog?.let { it.setWeight(data) }
                    }
                }

            }

            override fun onDataSent(data: String) {
            }

        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getAllCuisine()
        viewModel.allCuisineInfoLiveData.observe(viewLifecycleOwner,
            Observer<List<CuisineInfo>> {
                Logger.e(it.toString())
                menuRecyclerView.setHasFixedSize(true)
                var adapter = GoodsAdapter()
                //item 点击事件
                adapter.setOnItemClickListener { adapter, view, position ->
                    val goodEntity = adapter.data[position] as CuisineInfo
                    dialog = GoodsPriceDialog.newInstance(goodEntity)
                    dialog!!.isCancelable = false
                    dialog!!.show(childFragmentManager, "GoodsPriceDialog")
                    dialog!!.setCallBackListener(object : GoodsPriceDialog.CallBackListener {

//                        override fun callbackListener(
//                            entity: CuisineInfo,
//                            entit2y: UploadMenuInfo
//                        ) {
//                            viewModel.uploadCuisine(
//                                CacheDataUtils.USERNAME1!!,
//                                entity.GoodsID,
//                                entity.SupplierID?.toInt()!!,
//                                entity.GoodsWeight,
//                                entity
//
//                            )
//                        }

                        override fun callbackListener(
                            entity: CuisineInfo,
                            entit2y: UploadMenuInfo,
                            goodsAmount: String
                        ) {
                            viewModel.uploadCuisine(
                                CacheDataUtils.USERNAME1!!,
                                entity.GoodsID,
                                entity.SupplierID?.toInt()!!,
                                entity.GoodsWeight,
                                goodsAmount.toInt(),
                                entit2y.WarehouseID,
                                "${CacheDataUtils.USERNAME1!!},${CacheDataUtils.USERNAME2!!}"
                            )
                        }
                    })
                }
                adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.ScaleIn)
                adapter.animationEnable = true
                menuRecyclerView.layoutManager = GridLayoutManager(activity!!, 4)
                menuRecyclerView.adapter = adapter
                adapter.setList(it)
            })
        viewModel.uploadMenuInfoLiveData.observe(viewLifecycleOwner,
            Observer<List<UploadMenuInfo>> {
                CacheDataUtils.WARE_HOUSE_NAME_LIST = it
                Toasty.success(activity!!, "提交数据成功成功", Toast.LENGTH_LONG).show()
            })
    }

}