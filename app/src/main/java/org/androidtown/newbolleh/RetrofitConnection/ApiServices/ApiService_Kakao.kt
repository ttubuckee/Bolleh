package org.androidtown.bolleh.RetrofitConnection.ApiServices

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.androidtown.newbolleh.Datas.Map.GetPlaceByAddress
import org.androidtown.newbolleh.Datas.apiResponse.GetPlaceByCategory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService_Kakao {
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
        fun create(): ApiService_Kakao { //보낼곳의 url을 받아서 ApiService를 만들어서 리턴한다.
            val retrofit= Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideOkHttpClient(provideLoggingInterceptor()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://dapi.kakao.com/")
                .build()
            return retrofit.create(ApiService_Kakao::class.java)
        }

    }
    @GET("v2/local/geo/transcoord.json") // 좌표계변환
    fun transCoord(
        @Header("Authorization") auth:String,
        @Query("x") x:String,
        @Query("y") y:String,
        @Query("input_coord") input_coord:String,
        @Query("output_coord") output_coord:String
    ): Observable<org.androidtown.bolleh.Datas.TransResult>


    @GET("v2/local/search/category.json") // 카테고리 받아오기
    fun getPlaceByCategory(
        @Header("Authorization")auth:String,
        @Query("category_group_code")category:String,
        @Query("rect")timeStream:String
    ):Observable<GetPlaceByCategory>

    @GET("v2/local/search/address.json")
    fun getPlaceByAddress(
        @Header("Authorization")auth:String,
        @Query("query")address:String
    ):Observable<GetPlaceByAddress>
}