package org.androidtown.newbolleh.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_history.*
import okhttp3.OkHttpClient
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.newbolleh.Datas.makeRoom.Decisionroom_response
import org.androidtown.newbolleh.Fragment.history_list.historyListAdapter
import org.androidtown.newbolleh.Fragment.history_list.items
import org.androidtown.newbolleh.R
import org.androidtown.newbolleh.RetrofitConnection.ReceivedCookiesInterceptor
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.AddCookiesInterceptor

class History : Fragment() {
    /* var itemlist = arrayListOf<items>(
         items("Chow Chow", 123, 1, 1,"20191245", "건대입구"),
         items("Chow Chow", 123, 2, 1,"20191245", "건대입구"),
         items("Chow Chow", 123, 1, 2,"20191245", "건대입구"),
         items("Chow Chow", 123, 2, 1,"20191245", "건대입구")
     )*/
    val itemList = ArrayList<items>()
    var adapter: historyListAdapter? = null
    var pin: String = ""

    companion object {
        fun newHistory(pin: String): History {
            val newH = History()
            newH.pin = pin
            return newH
        }
    }

    interface callHistoryListener {
        fun changeToWaiting(
            pin: String,
            select: Boolean,
            master: Boolean
        )
        fun changeToHistoryRoute(pin:String)
        fun changeToDecision(pin:String,list:List<Decisionroom_response.stations>)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v("History","히스토리로 넘어옴$pin!")
        //var itemAdapter = historyListAdapter(getActivity() as Context, itemlist)
        //listView.adapter = itemAdapter
        init()

    }

    fun init() {
        Log.v("History_init",pin)
        if (pin != "") {
            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
            okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

            var disposable = CompositeDisposable()
            var apiService = ApiService_Main.create(context!!)
            disposable.add(apiService.exitRoom(pin)
                .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
                .subscribeOn(Schedulers.io())
                .subscribe({ it ->
                    // 통신 성공
                    Log.v("exit", "$it")
                    serverRequest() // HistoryList 띄우기
                }){
                    Log.v("exit","$it")
                })
        } else { // 핀이 비었으면
            serverRequest()
        }
    }
    fun serverRequest(){ // 방목록 띄우기
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
        okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        disposable.add(apiService.getHistoryList()
            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                itemList.clear() // list비워줌
                for (i in 0 until it.room_list.size) {
                    val idx = it.room_list[i].duedate.indexOf(" ")
                    val date = it.room_list[i].duedate.substring(0,idx)
                    itemList.add(
                        items(
                            it.room_list[i].title,
                            it.room_list[i].pin,
                            it.room_list[i].status,
                            it.room_list[i].type,
                            date,
                            it.room_list[i].place
                        )
                    )
                }
                adapter = historyListAdapter(getActivity() as Context, itemList)
                if (adapter != null)
                    listView_history.adapter = adapter

                listView_history.setOnItemClickListener { adapterView, view, i, l ->
                    val room = itemList[i]
                    if(room.status == 1){ // 대기중
                        if (activity is callHistoryListener) {
                            val callHistory = activity as callHistoryListener
                            val pin = room.pin.toString()
                            callHistory.changeToWaiting(
                                pin,
                                false,
                                false
                            ) // 자동중점
                        }
                    }else if(room.status == 2){ // 결정된 경우
                        Log.v("History",pin)
                        if(activity is callHistoryListener) {
                            val callHistory = activity as callHistoryListener
                            callHistory.changeToHistoryRoute(room.pin.toString())
                        }
                    }


                }
                when (it.list_res) {
                    "no session" -> {
                        //파라미터 없음
                        Log.d("history", "no session")
                    }
                    "no join" -> {
                        Log.d("history", "no join")
                    }
                    "ok" -> {

                    }
                }
            }) {
                Log.v("history", "실패다 제군")
            })
    }
}
