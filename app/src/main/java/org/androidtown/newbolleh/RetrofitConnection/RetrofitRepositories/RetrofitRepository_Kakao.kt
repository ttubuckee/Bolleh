package org.androidtown.bolleh.RetrofitConnection.RetrofitRepositories

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.androidtown.bolleh.Datas.TransResult
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Kakao

class RetrofitRepository_Kakao(private var apiService: ApiService_Kakao)  {

    var disposable=CompositeDisposable()
    fun getTransCoord(auth:String,x:String,y:String,inputCoord:String,outputCoord:String):Observable<TransResult>{
        return apiService.transCoord(auth,x,y,inputCoord,outputCoord)
    }

}