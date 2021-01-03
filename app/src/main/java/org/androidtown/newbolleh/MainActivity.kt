package org.androidtown.newbolleh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.VISIBLE
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import org.androidtown.newbolleh.Datas.makeRoom.Decisionroom_response
import org.androidtown.newbolleh.Fragment.*

class MainActivity : AppCompatActivity(),
    Login.callLoginListener,
    Join.callJoinListener,
    Home.callHomeListener,
    makeRoom.callRoomListener,
    LocationSelect.callLocationListener,
    History.callHistoryListener,
    Waiting.callWaitingListener,
    Decision.callDecisionListener,
    HistoryRoute.callRouteListener {

    override fun changeToLoactionSelect_AI(pin: String,select:Boolean,AI_nick: String) {
        makeLocationSelect_AI(pin,select,AI_nick)
    }

    override fun changeToHome() {
        makeHome()
        nav_view.visibility = VISIBLE
    }


    override fun changeToDecision(pin: String, list: List<Decisionroom_response.stations>) {
        makeDecision(pin, list)
    }

    override fun changeToHistoryRoute(pin: String) {
        makeHistoryRoute(pin)
    }

    override fun changeToLogin() {
        makeLogin()
    }

//    override fun changeToWaiting(
//        pin: String,
//        title: String,
//        duedate: String,
//        status: Int,
//        userInfo: List<EnterByPin_Bangma_response.users>?,
//        place: String?,
//        select: Boolean,
//        master: Boolean
//    ) {
//        makeWaiting(pin, title, duedate, status, userInfo, place, select, master)
//    }

    override fun changeToWaiting(
        pin: String,
        select: Boolean,
        master: Boolean
    ) {
        makeWaiting(pin, select, master)
    }


    override fun changeToRoom() {
        makeMakeRoom()
    }

    override fun changeToLocationSelect(
        pin: String,
        title: String,
        year: Int,
        month: Int,
        day: Int,
        select: Boolean,
        master: Boolean
    ) {
        makeLocationSelect(pin, title, year, month, day, select, master)
    }

    override fun changeToHistory(pin: String) {
        makeHistory(pin)
    }

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                makeHome()

//                textMessage.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_makeroom -> {
                makeMakeRoom()
//                textMessage.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_history -> {
                makeHistory("")
//                textMessage.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 튜토리얼 페이지
        val sp = this.getSharedPreferences("Welcome", Context.MODE_PRIVATE)
        val firstviewshow = sp.getInt("First",0)
        if(firstviewshow != 1){
            val intent = Intent(this, Tutorial::class.java)
            startActivity(intent)
        }


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

//        textMessage = findViewById(R.id.message)
        val pref = this.getPreferences(0)
        var ID = pref.getString("ID", null)
        if (ID == null) {
            Log.v("실행", "Main 22번째줄")
            makeJoin()
        } else {
            Log.v("실행", "Main 25번째줄")
            makeLogin()
        }


        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    fun makeJoin() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        if (fragment == null) {
            val joinTransaction = supportFragmentManager.beginTransaction()
            val joinFragment = Join()
            joinTransaction.replace(R.id.frame, joinFragment, "join")
            joinTransaction.commit()
        }
    }

    fun makeLogin() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        if (fragment == null) { // frame이 비었을때
            val loginTransaction = supportFragmentManager.beginTransaction()
            val loginFragment = Login()
            loginTransaction.replace(R.id.frame, loginFragment, "login")
            loginTransaction.commit()
        } else { // frame이 차있지만 Login인지 아닌지 모를때
            val loginFragment = supportFragmentManager.findFragmentByTag("login")
            if (loginFragment == null) {
                val loginTransaction = supportFragmentManager.beginTransaction()
                val LoginFrag = Login()
                loginTransaction.replace(R.id.frame, LoginFrag, "login")
                loginTransaction.commit()
            } else { // loginFragment가 달려있는 상태

            }
        }
    }

    fun makeHome() {
        val frag = supportFragmentManager.findFragmentByTag("home")
        val tagStr = frag?.tag.toString()
        if (tagStr == "home") { // 현재 달린 녀석이 home인 경우

        } else { // backStack 비워야됨
            val homeTransaction = supportFragmentManager.beginTransaction()
            val homeFrag = Home()
            homeTransaction.replace(R.id.frame, homeFrag)
            val clear = supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            homeTransaction.commit()
        }
    }

    fun makeLocationSelect(
        pin: String,
        title: String,
        year: Int,
        month: Int,
        day: Int,
        select: Boolean,
        master: Boolean
    ) {
        val locationSelectFragment = supportFragmentManager.findFragmentByTag("locationSelect")
        if (locationSelectFragment == null) {
            val locationTransaction = supportFragmentManager.beginTransaction()
            val locationFrag = LocationSelect.newLocationSelect(pin, title, year, month, day, select, master)
            locationTransaction.replace(R.id.frame, locationFrag, "locationSelect")
            locationTransaction.addToBackStack(null)
            locationTransaction.commit()
        } else { // locationFragment가 달려있는 상태

        }
    }

    fun makeWaiting(
        pin: String,
        select: Boolean,
        master: Boolean
    ) {
            val waitingTransaction = supportFragmentManager.beginTransaction()
            val waitFrag = Waiting.newWaiting(pin, select, master)
            waitingTransaction.replace(R.id.frame, waitFrag, "waiting")
            waitingTransaction.addToBackStack(null)
            waitingTransaction.commit()
    }

    fun makeHistory(pin: String) {
        val historyFrag = supportFragmentManager.findFragmentByTag("histroy")
        val tagStr = historyFrag?.tag.toString()
        if (tagStr == "history") { // 현재 달린 녀석이 history인 경우

        } else { // backStack 비워야됨
            val historyTransaction = supportFragmentManager.beginTransaction()
            val historyFrag = History.newHistory(pin)
            historyTransaction.replace(R.id.frame, historyFrag, "history")
            val clear = supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            historyTransaction.commit()
        }
    }

    fun makeMakeRoom() {
        val makeRoomFragment = supportFragmentManager.findFragmentByTag("makeRoom")
        if (makeRoomFragment == null) {
            val makeRoomTransaction = supportFragmentManager.beginTransaction()
            val makeRoomFrag = makeRoom()
            makeRoomTransaction.replace(R.id.frame, makeRoomFrag, "makeRoom")
            makeRoomTransaction.addToBackStack(null)
            makeRoomTransaction.commit()
        }
    }

    fun makeDecision(pin: String, list: List<Decisionroom_response.stations>) {
        val decisionFragment = supportFragmentManager.findFragmentByTag("decision")
        if (decisionFragment == null) {
            val decisionTransaction = supportFragmentManager.beginTransaction()
            val decisionFrag = Decision.newDecision(pin, list)
            decisionTransaction.replace(R.id.frame, decisionFrag, "decision")
            decisionTransaction.addToBackStack(null)
            decisionTransaction.commit()
        }
    }

    fun makeHistoryRoute(pin: String) {
        val HistoryRouteFragment = supportFragmentManager.findFragmentByTag("historyroute")
        if (HistoryRouteFragment == null) {
            val routeTransaction = supportFragmentManager.beginTransaction()
            val routeFrag = HistoryRoute.newRoute(pin)
            routeTransaction.replace(R.id.frame, routeFrag, "historyroute")
            routeTransaction.addToBackStack(null)
            routeTransaction.commit()
        }
    }
    fun makeLocationSelect_AI(pin:String,select:Boolean,AI_nick:String){
        val locationTransaction = supportFragmentManager.beginTransaction()
        val locationFrag = LocationSelect.newAI(pin,select,AI_nick)
        locationTransaction.replace(R.id.frame, locationFrag, "locationSelect")
        locationTransaction.addToBackStack(null)
        locationTransaction.commit()
    }
}
