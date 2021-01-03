package org.androidtown.newbolleh.RetrofitConnection.SharedPreference

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*

/**
 * Created by 10107PSH on 2017-09-23.
 * Request마다 Preference에 저장되어있는 쿠키값을 함께 Header에 넣어주는 클래스.
 */

class AddCookiesInterceptor(context: Context) : Interceptor {

    internal var mSharedPreferenceBase: SharedPreferenceBase

    init {
        mSharedPreferenceBase = SharedPreferenceBase.getInstanceOf(context)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        //Preference에서 cookies를 가져오는 작업을 수행
        val cookies = mSharedPreferenceBase.getSharedPreferences(
            SharedPreferenceBase.SHARED_PREFERENCE_NAME_COOKIE, HashSet<String>()
        ) as HashSet<String>

        for (cookie in cookies) {
            builder.addHeader("Cookie", cookie)
        }

        //Web, Android, ios 구분을 위해 User-Agent세팅
        builder.removeHeader("User-Agent").addHeader("User-Agent", "Android")

        return chain.proceed(builder.build())
    }

}