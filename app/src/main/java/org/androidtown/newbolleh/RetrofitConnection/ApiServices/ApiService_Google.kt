package org.androidtown.bolleh.RetrofitConnection.ApiServices

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.androidtown.newbolleh.Datas.history.GoogleGeo
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService_Google {
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
        fun create(): ApiService_Google { //보낼곳의 url을 받아서 ApiService를 만들어서 리턴한다.
            val retrofit= Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideOkHttpClient(provideLoggingInterceptor()))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .build()
            return retrofit.create(ApiService_Google::class.java)
        }

    }
    @GET("geocode/json") // 좌표계변환
    fun findGeoByStation(
        @Query("address") station:String,
        @Query("key") googleKey:String
    ):Observable<GoogleGeo>

}