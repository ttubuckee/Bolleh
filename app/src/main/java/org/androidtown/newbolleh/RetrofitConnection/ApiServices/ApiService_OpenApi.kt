package org.androidtown.bolleh.RetrofitConnection.ApiServices

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.androidtown.newbolleh.Datas.apiResponse.NearStation
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService_OpenApi {
    companion object Factory{
        private fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
            val b= OkHttpClient.Builder()
            b.addInterceptor(interceptor)
            return b.build()
        }
        private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            return interceptor
        }
        fun create(): ApiService_OpenApi { //보낼곳의 url을 받아서 ApiService를 만들어서 리턴한다.
            val retrofit= Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideOkHttpClient(provideLoggingInterceptor()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://swopenAPI.seoul.go.kr/api/")
                .build()
            return retrofit.create(ApiService_OpenApi::class.java)
        }

    }
    @GET("v2/local/geo/transcoord.json")
    fun transCoord(
        @Header("Authorization") auth:String,
        @Query("x") x:String,
        @Query("y") y:String,
        @Query("input_coord") input_coord:String,
        @Query("output_coord") output_coord:String
    ): Observable<org.androidtown.bolleh.Datas.TransResult>


    @GET("subway/{key}/json/nearBy/0/1/{transX}/{transY}") // 근처 지하철역 받아오기
    fun nearStation(
        @Path("key")key:String,
        @Path("transX")transX:String,
        @Path("transY")transY:String
        ):Observable<NearStation>
}


