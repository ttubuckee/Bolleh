package org.androidtown.newbolleh.Datas.history

data class History_detail_response (
    var detail_res:String,
    var pin:String,
    var master:String,
    var status:String,
    var title:String,
    var place:String,
    var type:String,
    var duedate:String,
    var makedate:String,
    var list:List<users>,
    var category:String

)
{
    data class users(var nick:String, var start:String,var path:String)
}