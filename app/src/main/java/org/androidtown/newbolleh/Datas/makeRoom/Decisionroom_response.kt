package org.androidtown.newbolleh.Datas.makeRoom

data class Decisionroom_response(
    var decision_res:String,
    var list:List<stations>
){
    data class stations(
        var rank:String,
        var place:String,
        var avg:String
    )
}