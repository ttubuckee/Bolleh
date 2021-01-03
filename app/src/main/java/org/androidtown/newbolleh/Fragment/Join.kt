package org.androidtown.newbolleh.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_join.*
import okhttp3.OkHttpClient
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.newbolleh.R
import org.androidtown.newbolleh.RetrofitConnection.ReceivedCookiesInterceptor
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.AddCookiesInterceptor

class Join : Fragment() {
    interface callJoinListener{
        fun changeToLogin()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_join, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    fun init(){
        startBtn.setOnClickListener {
            if(name.text.toString().isEmpty()){
                Toast.makeText(activity,"name은 필수 입력사항입니다.", Toast.LENGTH_LONG).show()
            }else if(pw.text.toString().length < 4){
                Toast.makeText(activity,"비밀번호는 4자 이상 설정해주세요.", Toast.LENGTH_LONG).show()
            }else if(pw.text.toString() == pwCheck.text.toString()){
                val Name = name.text.toString()
                val Pw = pw.text.toString()
                val pref = activity!!.getPreferences(0)
                val editor = pref!!.edit()

                editor.putString("ID",Name).apply()
                editor.putString("PW",Pw).apply()
                if(activity is callJoinListener){
                    Log.v("LOG",Name)
                    Log.v("LOG",Pw)

                    val okHttpClient = OkHttpClient.Builder()
                    okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
                    okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

                    var disposable=CompositeDisposable()
                    disposable.add(ApiService_Main.create(activity!!).register(Name,Pw)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ it ->
                            Log.d("LOG","$it")

                        }){


                        })
                    val joinListener = activity as callJoinListener
                    joinListener.changeToLogin()
                }
            }else{
                Toast.makeText(activity,"비밀번호가 일치하는지 확인해주세요",Toast.LENGTH_SHORT).show()
            }
        }
        pwCheck.addTextChangedListener(object: TextWatcher { // 비밀번호 일치 검사
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = pw.text.toString()
                val confirm = pwCheck.text.toString()

                if (password == confirm && confirm.isNotEmpty()) {
                    pw_alert.visibility = INVISIBLE
                } else {
                    pw_alert.visibility = VISIBLE                }
            }
        })
    }
}