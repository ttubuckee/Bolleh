package org.androidtown.newbolleh.Datas

data class EnterByPin_Jajung_response (
    var enter_res:String, //방 입장 여부
    var title:String, //방 제목
    var pin:String, //방 핀
    var status:Int, // 방 상태 -> 1(대기), 2(결정)
    var type:Int, // 자중/방마status -> 1(방마), 2(자중)
    var place : String, //약속 위치
    var user_info:List<String>, // 방에 있는 사람들의 목록
    var r_duedate:String//약속 날짜
)