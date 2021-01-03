package org.androidtown.newbolleh.Services

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Kakao

class Transcoder {
    val apiService :ApiService_Kakao= ApiService_Kakao.create()

    fun Wgs84toWtmTranscoder(auth:String,posX:String,posY:String): MutableList<String> {
        var result= mutableListOf<String>()
        var disposable:CompositeDisposable= CompositeDisposable()
        disposable.add(apiService.transCoord(" KakaoAK $auth",posX,posY,"WGS84","WTM")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                Log.d("location","$it")

                Log.d("location", "$it")
            }){

                Log.d("location","${it.message}!")
            })
        return result

    }
}