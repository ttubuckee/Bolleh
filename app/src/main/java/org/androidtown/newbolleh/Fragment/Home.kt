package org.androidtown.newbolleh.Fragment


import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.OkHttpClient
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.newbolleh.EasterEgg.Easter
import org.androidtown.newbolleh.R
import org.androidtown.newbolleh.RetrofitConnection.ReceivedCookiesInterceptor
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.AddCookiesInterceptor

class Home : Fragment() {
    var ACCESS_REQUEST = 10

    interface callHomeListener {
        //        fun changeToWaiting(title: String, year: Int, month: Int, day: Int, select: Boolean, master: Boolean) // 자동중점
        fun changeToRoom()
        fun changeToLocationSelect(
            pin: String,
            title: String,
            year: Int,
            month: Int,
            day: Int,
            select: Boolean,
            master: Boolean
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("진입2", "onCreateView")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v("진입2", "onActivityCreated")

        init()
        initLocation()
    }

    fun serverRequest_checkType(PIN:String) {

        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.interceptors().add(AddCookiesInterceptor(activity!!))
        okHttpClient.interceptors().add(ReceivedCookiesInterceptor(activity!!))

        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        disposable.add(apiService.checkRoomType(PIN)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                when (it.check_res) {
                    "ok"->{
                        if(it.status == "1") { // 대기중
                            if (activity is callHomeListener) {
                                val callHome = activity as callHomeListener
                                callHome.changeToLocationSelect(PIN, "", 0, 0, 0, false, false)
                            }
                        }else if(it.status == "2"){//
                            Toast.makeText(activity,"이미 결정된 방입니다.",Toast.LENGTH_SHORT).show()
                        }

                    }
                    "no parameter"->{
                        Log.v("check_res","no param")
                    }
                    "no room"->{
                        Log.v("check_res","no room")
                    }
                    else ->{
                        Log.v("check_res","unknown")
                    }
                }
            })
            {
                Log.v("check_res", "fail2")

            })
    }

    fun init() {
        var homeListener: callHomeListener? = null

        enterBtn.setOnClickListener {
            // PIN번호로 입장
            val pin = pinNum_EditText.text.toString()
            if(pin == "지정희교수님"){
                val intent = Intent(activity, Easter::class.java)
                startActivity(intent)
            }else{
                serverRequest_checkType(pin)
            }
//            homeListener!!.changeToWaiting("",0,0,0,true,false) // waiting 들어가서 서버로부터 값 받아야됨
        }
    }

    fun initLocation() {
        if (checkAppPermission(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                    , android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        ) {
            Toast.makeText(activity, "권한승인됨", Toast.LENGTH_LONG).show()
        } else {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("위치정보 권한 사용여부")
            builder.setTitle("권한 요청")
            builder.setNegativeButton("NO") { _, _ ->
                activity!!.finish()
            }
            builder.setPositiveButton("YES") { _, _ ->
                askPermission(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), ACCESS_REQUEST
                )
                //권한체크하고 위치받아오기
            }
            val dialog = builder.create()
            dialog.show()
        }

    }

    private fun checkAppPermission(requestPermission: Array<String>): Boolean {
        val requestResult = BooleanArray(requestPermission.size)
        for (i in requestResult.indices) {
            requestResult[i] = ContextCompat.checkSelfPermission(
                activity!!.applicationContext,
                requestPermission[i]
            ) == PackageManager.PERMISSION_GRANTED
            if (!requestResult[i]) { // 허가안될경우
                return false
            }
        }
        return true
    }

    fun askPermission(requestPermission: Array<String>, REQ_PERMISSION: Int) {
        ActivityCompat.requestPermissions(
            activity!!, requestPermission, REQ_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_REQUEST -> {
                if (checkAppPermission(permissions)) {
                    Toast.makeText(activity, "ACCESS_FINE", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(activity, "ACCESS_FINE 허용안됨", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}