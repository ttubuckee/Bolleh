package org.androidtown.bolleh.RetrofitConnection.RetrofitRepositories

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_OpenApi
import org.androidtown.newbolleh.Datas.apiResponse.NearStation

class RetrofitRepository_OpenApi( var apiService: ApiService_OpenApi) {

    var disposable: CompositeDisposable= CompositeDisposable()
    fun getNearStation(auth:String,posX:String,posY:String): Observable<NearStation> {

        return apiService.nearStation(auth,posX,posY)
    }

}