package org.androidtown.newbolleh.Fragment


import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.OkHttpClient
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.newbolleh.R
import org.androidtown.newbolleh.RetrofitConnection.ReceivedCookiesInterceptor
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.AddCookiesInterceptor

class Login : Fragment() {
    lateinit var pref: SharedPreferences
    var ID: String = ""
    var PW: String = ""

    interface callLoginListener {
        fun changeToHome()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("진입", "onCreate")
        pref = activity!!.getPreferences(0)
        ID = pref.getString("ID", null)
        PW = pref.getString("PW", null)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("진입", "onCreateView")
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v("진입", "onActivityCreated")
        init()
    }

    fun init() {
//        val nav = activity!!.findViewById<LinearLayout>(R.id.navigationBar)
//        nav.visibility = LinearLayout.GONE
        ID_textView.text = ID // 왜 activity로 findViewByID 안해도 되지?
        logInBtn.setOnClickListener {
            val pw = activity!!.findViewById<EditText>(R.id.login_pw)

            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
            okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

            var disposable = CompositeDisposable()
            disposable.add(
                ApiService_Main.create(activity!!).login(ID, pw.text.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ it ->
                        Log.d("LOG", "$it")
                        if(it.login_res == "login_ok") {
                            Log.v("LOGIN","$ID / $PW")
                            val homeListener = activity as callLoginListener
//                            nav.visibility = LinearLayout.VISIBLE
                            homeListener.changeToHome()
                        }else{
                            Log.v("LOGIN","fail")
                        }

                    }) {
                        Log.d("LOGIN", "실패 ${it.message}!")
                        Toast.makeText(activity,"비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                    })
        }
    }
}
