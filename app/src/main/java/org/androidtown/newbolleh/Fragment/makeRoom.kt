package org.androidtown.newbolleh.Fragment


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_make_room.*
import org.androidtown.newbolleh.R
import java.util.*

class makeRoom : Fragment() {
    lateinit var RoomTitle:String
    lateinit var calendar: Calendar
    var year = 0
    var month = 0
    var day = 0
    interface callRoomListener{
        fun changeToLocationSelect(pin:String,title:String,year:Int,month:Int,day:Int,select: Boolean,master: Boolean) // 방장마음대로
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_room, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v("진입2","onActivityCreated")
        init()
    }
    fun init(){
        var roomListener: makeRoom.callRoomListener? = null

        makeRoomBtn.setOnClickListener {
            // makeRoom 버튼 눌렀을때
            RoomTitle = roomTitle.text.toString() // 방제 설정
            val checkedId = radioGroup.checkedRadioButtonId // 체크란 확인
            if (activity is callRoomListener) {
                roomListener = activity as callRoomListener
            }
            if(RoomTitle == "" || year == 0 || month == 0 || day == 0) {
                Toast.makeText(activity,"방제와 날짜를 입력했는지 확인해주세요",Toast.LENGTH_SHORT).show()
            }else{
                when (checkedId) {
                    R.id.radioBtn1 -> { // 방장마음대로일때

                        roomListener!!.changeToLocationSelect("", RoomTitle, year, month, day, true, true)
                    }
                    R.id.radioBtn2 -> { // 자동중점
                        roomListener!!.changeToLocationSelect(
                            "",
                            RoomTitle,
                            year,
                            month,
                            day,
                            false,
                            true
                        ) // 방장으로 자동중점 실행
                    }
                }
            }
        }

        date_textView.setOnClickListener {
            calendar = Calendar.getInstance()
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)

            var date_listener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year_: Int, month_: Int, dayOfMonth_: Int) {
                    year = year_
                    month = month_+ 1
                    day = dayOfMonth_
                    date_textView.text = "${year}년 ${month}월 ${day}일"
                }
            }
            var builder = DatePickerDialog(activity!!, date_listener, year, month, day)
            builder.show()
        }
    }
}
