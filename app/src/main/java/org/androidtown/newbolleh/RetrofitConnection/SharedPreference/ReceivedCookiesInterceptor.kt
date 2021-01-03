package org.androidtown.newbolleh.RetrofitConnection

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import org.androidtown.newbolleh.RetrofitConnection.SharedPreference.SharedPreferenceBase
import java.io.IOException


class ReceivedCookiesInterceptor(context: Context) : Interceptor {

    var mSharedPreferenceBase: SharedPreferenceBase?=null

    init {

        mSharedPreferenceBase = SharedPreferenceBase.getInstanceOf(context)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = HashSet<String>()

            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }

            mSharedPreferenceBase!!.putSharedPreference(SharedPreferenceBase.SHARED_PREFERENCE_NAME_COOKIE, cookies)
        }
        return originalResponse
    }
}