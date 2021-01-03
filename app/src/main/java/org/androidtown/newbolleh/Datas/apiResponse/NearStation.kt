package org.androidtown.newbolleh.Datas.apiResponse

class NearStation (var stationList:List<StationList>){
    data class StationList(
        var statnId:String,
        var statnNm:String,
        var subywayNm:String,
        var subwayId:String,
        var subwayXcnts:String,
        var subwayYcnts:String
    )
}