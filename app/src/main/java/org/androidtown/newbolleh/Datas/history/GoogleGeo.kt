package org.androidtown.newbolleh.Datas.history

data class GoogleGeo(var results:List<Components>){
    data class Components(var geometry:Geo){
        data class Geo(var location:Loc){
            data class Loc(var lat:String,var lng:String)
        }
    }
}