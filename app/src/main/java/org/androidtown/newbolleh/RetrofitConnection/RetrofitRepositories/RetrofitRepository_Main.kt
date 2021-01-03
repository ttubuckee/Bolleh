package org.androidtown.bolleh.RetrofitConnection.RetrofitRepositories

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main

class RetrofitRepository_Main( var apiService: ApiService_Main) {
     fun sendQuery(
        apiFunction: () -> Observable<Any>,
        disposable: CompositeDisposable

    ) {

    }
}