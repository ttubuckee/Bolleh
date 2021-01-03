package org.androidtown.bolleh.Datas

data class EnterByPin_Bangma_response(

    var enter_res:String, //방 입장 여부
    var pin:String, //방 핀
    var status:String, // 방 상태 -> 1(대기), 2(결정)
    var title:String, //방 제목
    var place : String?, //약속 위치
    var type:String, // 자중/방마status -> 1(방마), 2(자중)
    var list:List<users> // 방에 있는 사람들의 목록
)
{
    data class users(var nick:String, var start:String)
}