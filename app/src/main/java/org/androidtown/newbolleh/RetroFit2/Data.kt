package org.androidtown.newbolleh.RetroFit2

data class Data( var meta:Meta, var documents: ArrayList<Document>) {
    data class Meta(
        var total_count:Int
    )
    data class Document(
        var x:String,
        var y:String
    )
}