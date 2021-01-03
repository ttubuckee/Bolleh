package org.androidtown.newbolleh.Fragment


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_waiting.*
import okhttp3.OkHttpClient
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.newbolleh.Datas.history.History_detail_response
import org.androidtown.newbolleh.Datas.makeRoom.Decisionroom_response
import org.androidtown.newbolleh.RetrofitConnection.ReceivedCookiesInterceptor
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.AddCookiesInterceptor
import org.androidtown.newbolleh.myAdapter
import java.util.*


class Waiting : Fragment() {

    var pin: String = ""
    var select = false  // LocationSelect에서 온건지 자동중점에서 온건지 판단, false면 자동중점
    var master = true // true이면 방장, false이면 게스트
    lateinit var adapter: myAdapter

    companion object {
        fun newWaiting(
            pin: String,
            select: Boolean,
            master: Boolean
        ): Waiting {
            val newWait = Waiting()
            newWait.pin = pin
            newWait.select = select
            newWait.master = master
            return newWait
        }
    }

    interface callWaitingListener{
        fun changeToHistory(pin:String)
        fun changeToHistoryRoute(pin:String)
        fun changeToDecision(pin:String,list:List<Decisionroom_response.stations>)
        fun changeToLoactionSelect_AI(pin:String,select:Boolean,AI_nick:String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("waiting", "진입0")
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.v("waiting", "시작2")
        return inflater.inflate(org.androidtown.newbolleh.R.layout.fragment_waiting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textView_decision.setOnClickListener {
            if(adapter.list.size >= 2)
                requestDecision()
        }
        textView_exit.setOnClickListener {
            if(activity != null){
                val callWaiting = activity as Waiting.callWaitingListener
                callWaiting.changeToHistory(pin)
            }
        }
        textView_refresh.setOnClickListener {
            init()
        }
        ImageView_plus.setOnClickListener {
            Log.v("AI","클릭!")
            if(activity is callWaitingListener){
                val callWaiting =activity as callWaitingListener
                callWaiting.changeToLoactionSelect_AI(pin,select,"AI")
            }
        }
        Log.v("waiting", "시작1")
    }
    fun requestDecision(){ // 방 결정
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
        okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        disposable.add(apiService.decision_room(pin)
            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                when(it.decision_res){
                    "no parameter" ->{
                        Log.v("decision","no param")
                    }
                    "no master" ->{
                        Log.v("decision","no master")
                    }
                    "fail"->{
                        Log.v("decision","fail")
                    }
                    "bangma_ok"->{
                        Log.v("decision","bangma_ok")
                        if(activity is callWaitingListener){
                            val callWaiting = activity as callWaitingListener
                            Log.d("hsh","루트히스토리로")
                            callWaiting.changeToHistoryRoute(pin)
                            Log.d("hsh","루트히스토리안가짐")
                        }
                        //HistoryRoute 로 넘겨야 됨
                    }
                    "jajung_ok"->{
                        // Decision으로 넘기기
                        Log.v("decision","jajung_ok")
                        Log.v("decision","${it.list}")
                        if(activity is callWaitingListener){
                            val callWaiting = activity as callWaitingListener
                            callWaiting.changeToDecision(pin,it.list)
                        }


                    }
                }
            })
            {

            })
    }
    fun init() {
        Log.v("waiting", "$pin")
//        if(pin != ""){
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
        okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        disposable.add(apiService.getHistory(pin)
            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                // 통신 성공
                Log.d("waiting", "$it")
                // ai 추가 버튼 방장만 보이게
                if(it.master == "0"){
                    ImageView_plus.visibility = ImageView.INVISIBLE
                }
                //
                val userList = ArrayList<History_detail_response.users>()
                for (i in 0 until it.list.size) {
                    userList.add(History_detail_response.users(it.list[i].nick, it.list[i].start,""))
                }
                adapter = myAdapter(activity!!, org.androidtown.newbolleh.R.layout.userlist, userList)
                if (adapter != null)
                    listView.adapter = adapter
                listView.setOnItemClickListener { parent, view, position, id ->

                }
                when (it.detail_res) {
                    "no parameter" -> {
                        //파라미터 없음
                        Log.d("waiting", "no param")
                    }
                    "no room" -> {
                        Log.d("waiting", "no room")
                    }
                    "ok" -> {
                        Log.d("waiting", "success")
                        Log.v("detail","$it")
                        if (it.master == "0") { // 게스트
                            textView_decision.visibility = TextView.INVISIBLE
                        } else if (it.master == "1") { // 방장
                            textView_decision.visibility = TextView.VISIBLE
                        }
                        if (it.status == "1") {
                            // 대기 상태
                            when (it.type) {
                                "1" -> {
                                    //방마
                                    textView_title.text = it.title
                                    textView_status.text = "대기중"
                                    textView_duedate.text = it.duedate
                                    textView_pin.text = it.pin
                                }
                                "2" -> {
                                    //자중
                                    textView_title.text = it.title
                                    textView_status.text = "대기중"
                                    textView_duedate.text = it.duedate
                                    textView_pin.text = it.pin
                                }
                            }
                            textView_status.setTextColor(Color.GREEN)
                        } else if (it.status == "2") {
                            // 결정 상태
                            when (it.type) {
                                "1" -> {
                                    //방마
                                    textView_title.text = it.title
                                    textView_status.text = "결정"
                                    textView_duedate.text = it.duedate
                                    textView_pin.text = it.pin
                                }
                                "2" -> {
                                    //자중
                                    textView_title.text = it.title
                                    textView_status.text = "결정"
                                    textView_duedate.text = it.duedate
                                    textView_pin.text = it.pin
                                }
                            }
                            textView_status.setTextColor(Color.RED)
                            if(activity is callWaitingListener){
                                val callWaiting = activity as callWaitingListener
                                callWaiting.changeToHistoryRoute(pin)
                            }
                        } else {
                            Log.d("waiting", "알수없는 오류")
                        }
                    }
                }
            }) {
                Log.v("waiting", "실패다 제군 여긴가?")
            })
    }
}