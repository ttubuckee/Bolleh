package org.androidtown.newbolleh.Datas.Map

data class GetPlaceByAddress(var meta:AddressMeta, var documents:List<AddressList>){
    data class AddressMeta(var total_count :Int, var is_end:Boolean)
    data class AddressList(var address_name :String, var x:String,var y:String)
}