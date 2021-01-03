package org.androidtown.newbolleh.RetrofitConnection.SharedPreference

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity

import java.util.*

/**
 * Created by 10107PSH on 2017-09-21.
 * SharedPreferences 일종의 DB.
 */

class SharedPreferenceBase (val context: Context) : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences

    init {
        val SHARED_PREFERENCE_NAME_COOKIE = context.packageName
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME_COOKIE, Activity.MODE_PRIVATE)
    }

    /**
     * @param key
     * @param hashSet
     */

    //SharedPreferences에 데이터 저장하기
    fun putSharedPreference(key: String, hashSet: HashSet<String>) {
        val editor = sharedPreferences.edit()
        editor.putStringSet(key, hashSet) //key라는 key값으로 hashSet 데이터를 저장
        editor.commit() //완료
    }

    /**
     * @param key
     * @param cookie
     * @return
     */

    fun getSharedPreferences(key: String, cookie: HashSet<String>): HashSet<String> {
        try {
            return sharedPreferences.getStringSet(key, cookie) as HashSet<String>
        } catch (e: Exception) {
            return cookie
        }
    }

    companion object {
        val SHARED_PREFERENCE_NAME_COOKIE = "firealarm.cookie"
        private var mSharedPreferenceBase: SharedPreferenceBase?=null

        fun getInstanceOf(c: Context): SharedPreferenceBase {
            if (mSharedPreferenceBase == null) {
                mSharedPreferenceBase = SharedPreferenceBase(c)
            }
            return mSharedPreferenceBase!!
        }
    }
}
