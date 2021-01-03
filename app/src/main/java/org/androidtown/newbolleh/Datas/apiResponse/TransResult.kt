package org.androidtown.bolleh.Datas

import com.google.gson.annotations.SerializedName

data class TransResult(var meta :ee, var documents:List<Document>){ //좌표계변환할때 카카오에 보내는친구

    data class ee(@SerializedName("total_count")var total_count:Integer)
    data class Document(var x:Double, var y:Double)


}