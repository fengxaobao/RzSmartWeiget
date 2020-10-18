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
        helper.setText(R.id.menuName, item.F_NAME)
        helper.setText(R.id.menuPrice, "单价:${item.F_Money}")
        helper.setText(R.id.menuWeight, "库存:${item.F_Weight}")
    }

}