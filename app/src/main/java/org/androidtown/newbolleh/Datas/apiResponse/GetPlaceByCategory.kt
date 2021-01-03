package org.androidtown.newbolleh.Datas.apiResponse

data class GetPlaceByCategory(var meta: PlaceMeta, var documents:List<PlaceDocuments>){
    data class PlaceDocuments(var place_name:String, var place_url:String,var category_name:String,var address_name:String,var road_address_name:String,var id:String,var phone:String,
                              var x:String, var y:String)
    data class PlaceMeta(var same_name:String,var pageable_count:String, var total_count:String, var is_end:String)
}