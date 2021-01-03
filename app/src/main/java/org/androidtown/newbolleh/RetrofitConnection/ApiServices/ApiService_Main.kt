package org.androidtown.bolleh.RetrofitConnection.ApiServices

import android.content.Context
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.androidtown.bolleh.Datas.EnterByPin_Bangma_response
import org.androidtown.bolleh.Datas.EnterByPin_response
import org.androidtown.bolleh.Datas.Login_response
import org.androidtown.bolleh.Datas.Register_response
import org.androidtown.newbolleh.Datas.ExitRoom_response
import org.androidtown.newbolleh.Datas.checkRoomType_response
import org.androidtown.newbolleh.Datas.enterRoom.EnterAI_response
import org.androidtown.newbolleh.Datas.history.History_detail_response
import org.androidtown.newbolleh.Datas.history.History_list_response
import org.androidtown.newbolleh.Datas.history.SetCategoryResult
import org.androidtown.newbolleh.Datas.makeRoom.Decision_jajung_response
import org.androidtown.newbolleh.Datas.makeRoom.Decisionroom_response
import org.androidtown.newbolleh.Datas.makeRoom.MakeBangma_response
import org.androidtown.newbolleh.Datas.makeRoom.MakeJajung_response
import org.androidtown.newbolleh.RetrofitConnection.ReceivedCookiesInterceptor
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.AddCookiesInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService_Main {
    companion object Factory {
        private fun provideOkHttpClient(
            interceptor: HttpLoggingInterceptor,
            cookiesInterceptor: AddCookiesInterceptor,
            receivedCookiesInterceptor: ReceivedCookiesInterceptor
        ): OkHttpClient {
            val b = OkHttpClient.Builder()
            b.addInterceptor(interceptor)
            b.addInterceptor(cookiesInterceptor)
            b.addInterceptor(receivedCookiesInterceptor)
            return b.build()
        }

        private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            return interceptor
        }

        fun create(context: Context): ApiService_Main { //보낼곳의 url을 받아서 ApiService를 만들어서 리턴한다.
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(
                    provideOkHttpClient(
                        provideLoggingInterceptor(),
                        AddCookiesInterceptor(context), ReceivedCookiesInterceptor
                            (context)
                    )
                )
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://49.236.137.41/")
                .build()
            return retrofit.create(ApiService_Main::class.java)
        }

    }

    @FormUrlEncoded
    @POST("user/login.php") //로그인
    fun login(
        @Field("nick") id: String,
        @Field("pw") pw: String
    ): Observable<Login_response>

    @FormUrlEncoded
    @POST("user/regist.php")
    fun register( //회원가입
        @Field("nick") nickname: String,
        @Field("pw") pw: String
    ): Observable<Register_response>

    @GET("/room/exit_room.php")
    fun exitRoom(
        @Query("pin") roomPin: String
    ): Observable<ExitRoom_response>

    @GET("enter_room_bangma.php")
    fun enterByPin( //핀으로 입장하기
        @Query("pin") roomPin: String,
        @Query("start") myStation: String
    ): Observable<EnterByPin_response>

    @GET("room/check_room_type.php")
    fun checkRoomType( // 룸타입 체크
        @Query("pin") roomPin: String
    ): Observable<checkRoomType_response>

    @FormUrlEncoded
    @POST("room/make_jajung.php")
    fun makeRoomByAuto( //핀으로 입장하기 자중
        @Field("title") title: String,
        @Field("duedate") duedate: String,
        @Field("start") station: String
    ): Observable<MakeJajung_response>

    @GET("room/enter_room_bangma.php")
    fun enterByPin_bangma( //핀으로 입장하기 방마
        @Query("pin") roomPin: String,
        @Query("start") myStation: String
    ): Observable<EnterByPin_Bangma_response>

    @FormUrlEncoded
    @POST("room/make_bangma.php")
    fun makeRoomByManual( //핀으로 입장하기 자중
        @Field("title") title: String,
        @Field("duedate") duedate: String,
        @Field("place") place: String,
        @Field("start") station: String
    ): Observable<MakeBangma_response>


    @GET("history/history_detail.php") // 히스토리 디테일
    fun getHistory( //방 하나의 히스토리 정보 자세히 가져오기
        @Query("pin") roomPin: String
    ): Observable<History_detail_response>

    @GET("history/history_list.php") // 히스토리 리스트
    fun getHistoryList(
    ): Observable<History_list_response>

    @GET("alg/decision_jajung.php")
    fun decision_jajung( // 자중 방 결정
        @Query("pin") roomPin: String,
        @Query("place") place: String // 추천리스트에서 선택한
    ): Observable<Decision_jajung_response>

    @GET("alg/room_decision.php")
    fun decision_room( // 방 결정 -> 추천 리스트 받아옴
        @Query("pin") roomPin: String
    ): Observable<Decisionroom_response>

    @GET("route.php")
    fun getRoute(  //루트 가져오기? 그냥 히스토리 가져오기로 하면 될 것 같은데.... 일단써봤다.
        @Query("a_r_pin") roomPin: String
    ): Observable<Any>

    @GET("room/select_category.php")
    fun setCategory(
        @Query("pin") pin: String,
        @Query("cate") category: String
    ): Observable<SetCategoryResult>

    @GET("room/enter_ai.php") // ai 입장
    fun enterAI(
        @Query("pin") roomPin: String,
        @Query("place") place: String
    ):Observable<EnterAI_response>
}