package com.rz.smart.model.request

/**
 * Created by zhou on 2021/1/9 15:11.
 */
class GoodsWeightRequest(var Code: String, var EquipmentID: String, var OperatorName: String, var GoodsID: String,
                         var SupplierID: String, var GoodsWeight: Double, var GoodsAmount: Int, var WarehouseID: Int,
                         var Remark:String,  var Token: String,var CostPrice:String
//                        , var Sign: String
)