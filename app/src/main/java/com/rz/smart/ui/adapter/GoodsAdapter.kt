package com.rz.smart.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rz.smart.R
import com.rz.smart.model.entity.CuisineInfo


class GoodsAdapter() : BaseQuickAdapter<CuisineInfo, BaseViewHolder>(
    R.layout.adapter_menu_item
) {
    override fun convert(
        helper: BaseViewHolder,
        item: CuisineInfo
    ) {
//        helper.setText(R.id.menuName, item.GoodsName)
//        helper.setText(R.id.menuPrice, "单价:${item.CostPrice}")
//        helper.setText(R.id.menuWeight, "库存:${item.GoodsCode}")
        helper.setText(R.id.tvGoodsName,item.GoodsName)
        helper.setText(R.id.tvSupplierName,"供应商:${item.SupplierName}")
        helper.setText(R.id.tvGoodsTypeName,"分类:${item.GoodsTypeName}")

        helper.setText(R.id.tvCostPrice,"单价:${item.CostPrice}")
        helper.setText(R.id.tvGoodsStock,"库存:${item.GoodsStock}")
        helper.setText(R.id.tvGoodsCode,"编号:${item.GoodsCode}")



    }

}