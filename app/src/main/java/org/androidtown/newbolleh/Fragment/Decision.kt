package org.androidtown.newbolleh.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.decision_list.view.*
import kotlinx.android.synthetic.main.fragment_decision.*
import okhttp3.OkHttpClient
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.newbolleh.Datas.makeRoom.Decisionroom_response
import org.androidtown.newbolleh.DecisionAdapter
import org.androidtown.newbolleh.R
import org.androidtown.newbolleh.RetrofitConnection.ReceivedCookiesInterceptor
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.AddCookiesInterceptor

class Decision : Fragment() {
    lateinit var list:List<Decisionroom_response.stations>
    lateinit var adapter:DecisionAdapter
    var pin:String = ""
    companion object {
        fun newDecision(pin:String,list:List<Decisionroom_response.stations>):Decision{
            val newD = Decision()
            newD.pin = pin
            newD.list = list
            return newD
        }
    }
    interface callDecisionListener{
        fun changeToHistoryRoute(pin:String)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_decision, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }
    fun init(){
        val layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL,false)
        recycle.layoutManager = layoutManager

        val listener = object: DecisionAdapter.decisionListener {
            override fun onclick(view:View) {
//                val place = view.findViewById<TextView>(R.id.textView_station).text.toString()
                val place = view.textView_station.text.toString()
                serverRequest_Jajung(place)

                if(activity is callDecisionListener){
                    val callDecision = activity as callDecisionListener
                    callDecision.changeToHistoryRoute(pin)
                }

            }
        }
        Log.v("Decision","패스!$list!")
        val adapter = DecisionAdapter(activity as Context,listener,list)
        Log.v("decision","여기인가1")
        recycle.adapter = adapter
        Log.v("decision","여기인가2")
    }
    fun serverRequest_Jajung(place:String){
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
        okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        disposable.add(apiService.decision_jajung(pin,place)
            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                Log.v("decision_jajung","success")
                Log.v("decision_jajung","$it")
            }){
                Log.v("decision_jajung","실패다 제군")
                Log.v("decision_jajung","${it.printStackTrace()}")
            })
    }
}
