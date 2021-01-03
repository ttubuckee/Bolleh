package org.androidtown.newbolleh.Fragment


import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_location_select.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.androidtown.bolleh.Datas.EnterByPin_Bangma_response
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Kakao
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_OpenApi
import org.androidtown.bolleh.RetrofitConnection.RetrofitRepositories.RetrofitRepository_OpenApi
import org.androidtown.newbolleh.R
import org.androidtown.newbolleh.Services.Transcoder

class LocationSelect : Fragment() {
    private var master = true
    var pin: String = ""
    var select = true
    var title: String = ""
    var year = 0
    var month = 0
    var day = 0
    var ACCESS_REQUEST = 100
    var posX: Double = 0.0 // 현재의 x좌표
    var posY: Double = 0.0 // 현재의 y좌표
    var transX: String = "0.0" // 변환된 x좌표
    var transY: String = "0.0" //  변환된 Y좌표
    var stationX: String = ""
    var stationY: String = ""
    var statnX: String = ""
    var statnY: String = ""
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    var decideFlag = 0 //대략적인 장소를 선택했으면 1 아니면 0이다.
    var station: String = ""
    var promiseStation: String = "" //만날위치.
    var category = ""
    lateinit var pref: SharedPreferences
    var ID = ""
    lateinit var map: MutableMap<String, String>
    lateinit var marker: MapPOIItem
    lateinit var mMapView: MapView
    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var mapListener: MapView.MapViewEventListener
    var POIlist = arrayOfNulls<MapPOIItem>(45)
    var AI_nick: String = ""

    companion object {
        fun newLocationSelect(
            pin: String,
            title: String,
            year: Int,
            month: Int,
            day: Int,
            select: Boolean,
            master: Boolean
        ): LocationSelect {
            val newRoom = LocationSelect()
            newRoom.pin = pin
            newRoom.title = title
            newRoom.year = year
            newRoom.month = month
            newRoom.day = day
            newRoom.select = select
            newRoom.master = master
            return newRoom
        }

        fun newAI(pin: String,select:Boolean, AI_nick: String): LocationSelect {
            val new_ai = LocationSelect()
            new_ai.pin = pin
            new_ai.AI_nick = AI_nick
            new_ai.select = select
            new_ai.master = false
            return new_ai
        }
    }

    interface callLocationListener {
        fun changeToWaiting(
            pin: String,
            select: Boolean,
            master: Boolean
        ) // 자동중점
        fun changeToHome()
    }

    inner class CustomOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.d("LOG", "${p0!!.getItemAtPosition(p2).toString()}")
            category = map[p0.getItemAtPosition(p2).toString()]!!

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_location_select, container, false)
    }

    fun gotoWaiting() { // 권한의 상태에 따라 다른 결과

        if (master) { // 방장의 권한으로 옴
            if (select) { // 방마
                if (decideFlag == 0) {
                    decideFlag = 1

                    spinnerLinear.visibility = View.VISIBLE
                    categoryLinear.visibility = View.GONE
//                    button.text="핀 주변으로 검색하기."
                    setSpinner()
                    Toast.makeText(activity!!.applicationContext, "Choice The Location to Meet", Toast.LENGTH_LONG)
                        .show()
                } else { //이미 내 위치는 정했고, 이번에 넘어가는 거는 만날 위치까지해서 넘어간다.
                    val duedate = "$year-$month-$day 00:00:00" // 서버로 보낼 duedate
                    var disposable = CompositeDisposable()
                    var apiService = ApiService_Main.create(context!!)
                    disposable.add(apiService.makeRoomByManual(
                        title, duedate, promise.text.toString(), station
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ it ->
                            Log.d("hsh", "${it.create_res}")
                            when (it.create_res) {
                                "true" -> {
                                    Log.v("create_res", "succcess")
                                    if (activity is callLocationListener) {
                                        pref = activity!!.getPreferences(0)
                                        ID = pref.getString("ID", null)
                                        Log.v("create_res", "$ID")
                                        val user = ArrayList<EnterByPin_Bangma_response.users>()
                                        user.add(EnterByPin_Bangma_response.users(ID, station))
                                        Log.v("create_res", "진입1")
                                        val callLocation = activity as callLocationListener
                                        callLocation!!.changeToWaiting(it.pin, true, true)
                                        Log.v("create_res", "진입2")
                                    }
                                }
                                else -> {
                                    Log.v("create_res", "fail")
                                    gotoHome()
                                }
                            }
                        })
                        {
                            Log.v("hsh", "${it.message}방마, 방만들기실패")
                            gotoHome()
                        })
                }
            } else { // 자중
                // 자기 위치 근처 지하철역만 찾고 waiting으로
                serverRequest_Jajung_Make()
            }
        } else { // 게스트권한으로 옴 (PIN)
            // 자기 위치 근처 지하철역만 찾고 waiting으로
            serverRequest_PIN(pin)
        }
    }

    fun serverRequest_Jajung_Make() {
        /*   var okHttpClient = OkHttpClient.Builder()
           okHttpClient.interceptors().add(AddCookiesInterceptor(context!!))
           okHttpClient.interceptors().add(ReceivedCookiesInterceptor(context!!))
   */
        val duedate = "$year-$month-$day 00:00:00" // 서버로 보낼 duedate
        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        disposable.add(apiService.makeRoomByAuto(
            title, duedate, station
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                when (it.create_res) {
                    "true" -> {
                        Log.v("create_res", "succcess")
                        if (activity is callLocationListener) {
                            pref = activity!!.getPreferences(0)
                            ID = pref.getString("ID", null)
                            val user = ArrayList<EnterByPin_Bangma_response.users>()
                            user.add(EnterByPin_Bangma_response.users(ID, station))
                            Log.v("create_res", "진입1")
                            val callLocation = activity as callLocationListener
                            callLocation!!.changeToWaiting(it.pin, false, true)
                            Log.v("create_res", "진입2")
                        }
                    }
                    else -> {
                        Log.v("create_res", "fail")
                        gotoHome()
                    }
                }
            })
            {
                Log.v("enter_res", "fail2")
                gotoHome()
            })
    }

    fun serverRequest_PIN(PIN: String) { // 핀번호랑 역정보 담아서 서버로 전송
        if (AI_nick == "AI") {
            var disposable = CompositeDisposable()
            var apiService = ApiService_Main.create(context!!)
            disposable.add(apiService.enterAI(pin, station)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.v("AI","success")
                    Log.v("AI","$it")
                    if(activity is callLocationListener){
                        val callLocation = activity as callLocationListener
//                        callLocation.changeToHome()
                        callLocation.changeToWaiting(pin,select,true)
                    }
                })
                {
                    Log.v("AI","fail")
                    Log.v("AI","${it.printStackTrace()}")
                })
        } else {
            var disposable = CompositeDisposable()
            var apiService = ApiService_Main.create(context!!)
            disposable.add(apiService.enterByPin_bangma(
                PIN, station
            )
                .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
                .subscribeOn(Schedulers.io())
                .subscribe({ it ->
                    // 통신 성공
                    val duedate = "$year-$month-$day 00:00:00"
                    when (it.enter_res) {
                        "ok" -> { // 참여 성공
                            Log.v("enter_room", "success")
                            if (activity is callLocationListener) {
                                val callLocation = activity as callLocationListener
                                Log.v("enter_room", "--$it")
                                Log.d("enter_room", "$it")
                                callLocation.changeToWaiting(pin, false, true)
                            }
                        }
                        "no room" -> { // 방이없을때
                            Log.v("enter_room", "no room")
                            gotoHome()
                        }
                        "already enter" -> { // 이미 참여 중
                            Log.v("enter_room", "alr enter")
                            Toast.makeText(context, "이미 참여중인 방입니다", Toast.LENGTH_SHORT).show()
                            if (activity is callLocationListener) {
                                val callLocation = activity as callLocationListener
                                callLocation.changeToWaiting(pin, false, false)
                            }
                        }
                        "already decide" -> { // 방이 결정된 상태
                            Log.v("enter_room", "alr decide")
                            if (activity is callLocationListener) {
                                val callLocationListener = activity as callLocationListener
                                callLocationListener.changeToWaiting(PIN, false, false)
                            }
                        }
                        "join no" -> { // 참여 실패
                            Log.v("enter_room", "join no")
                            gotoHome()
                        }
                    }

                }) {
                    Log.v("enter_room", "실패했다 제군")
                    Log.v("enter_room", "${it.printStackTrace()}")
                    gotoHome()
                })
        }
    }

    fun gotoHome() { // 실패시 home fragment로 돌아감
        if (activity is callLocationListener) {
            val callLocation = activity as callLocationListener
            callLocation.changeToHome()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("hsh", "셀렉트는 $select 이다.")
        button2.setOnClickListener {
            // 서버로 정보 보내기
            if (station != "") {
                gotoWaiting()
            } else {
                Log.v("enter_room", "이것도 실패다 제군")
            }
        }

        initLocation()
        var mLocationManager: LocationManager
        lateinit var myLocation: Location
        mLocationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var providers = mLocationManager.getProviders(true)


        var a_provider: String = mLocationManager.getProviders(true)[0]
        for (provider in providers) {
            if (checkAppPermission(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                        , android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            ) {
                if (mLocationManager.getLastKnownLocation(provider) != null) {
                    if (mLocationManager.getLastKnownLocation(provider).accuracy >= mLocationManager.getLastKnownLocation(
                            a_provider
                        ).accuracy
                    )
                        latitude = mLocationManager.getLastKnownLocation(provider).latitude
                    longitude = mLocationManager.getLastKnownLocation(provider).longitude
                }
            }

        }

        setMap()
        posX = longitude
        posY = latitude
        Log.d("location", "$posX = posX , $posY = posY")
        var openService = ApiService_OpenApi.create()
        var openRepository = RetrofitRepository_OpenApi(openService)
        var disposable = CompositeDisposable()
        var transcoder = Transcoder()
        button.setOnClickListener {
            var apiService = ApiService_Kakao.create()
            if (decideFlag == 0) {
                disposable.add(apiService.transCoord(
                    " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                    posX.toString(),
                    posY.toString(),
                    "WGS84",
                    "WTM"
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ it ->
                        transX = it.documents[0].x.toString()
                        transY = it.documents[0].y.toString()
                        disposable.add(openService.nearStation(
                            "4d66724f78746f6d3732426c574e6e",
                            transX.toString(),
                            transY.toString()
                        )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ it ->
                                Log.d("please", "wpqkf ${it.stationList}")
                                stationX = it.stationList[0].subwayXcnts //결정을 누르면 현재 가장 가까운 역으로 트랜스X,Y가 바뀌며,
                                stationY = it.stationList[0].subwayYcnts //여기다가 마커를 찍어? 아니면 주변 역들에 마커를 찍고,
                                Log.d("please", "wpqkf ${it.stationList}")
                                station = it.stationList[0].statnNm

                                Log.d("hsh", "트랜즈액스, 트랜즈와이 스테이션, $transX, $transY,$station")
                                disposable.add(apiService.transCoord(
                                    " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                                    stationX, stationY, "WTM", "WGS84"
                                )
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ it ->
                                        statnX = it.documents[0].x.toString()
                                        statnY = it.documents[0].y.toString()
                                        Log.d("hsh", "$statnX, ${statnX.toDouble()}")
                                        mMapView.setMapCenterPoint(
                                            MapPoint.mapPointWithGeoCoord(
                                                statnY.toDouble(),
                                                statnX.toDouble()
                                            ), true
                                        )
                                        mMapView.setZoomLevel(2, true)
                                        // mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true)
                                    }) {
                                        Log.d("LOG", "${it.message}!")
                                    })
                            }) {
                                Log.d("LOG", "${it.message}!")
                            })
                    }) {
                        Log.d("location", "${it.message}!")
                    })
            } else { //디사이드는 1이면,

                mMapView.removeAllPOIItems()
                for (i in 0 until POIlist.size - 1) POIlist[i] = null //초기화
                disposable.add(apiService.transCoord(
                    " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                    posX.toString(), //현재위치를 가지고, wtm으로 변환.
                    posY.toString(),
                    "WGS84",
                    "WTM"
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ it ->
                        transX = it.documents[0].x.toString()
                        transY = it.documents[0].y.toString()
                        disposable.add(openService.nearStation(
                            "4d66724f78746f6d3732426c574e6e",
                            transX.toString(),
                            transY.toString()
                        )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ it ->
                                Log.d("please", "wpqkf ${it.stationList}")
                                stationX = it.stationList[0].subwayXcnts //결정을 누르면 현재 가장 가까운 역으로 트랜스X,Y가 바뀌며,
                                stationY = it.stationList[0].subwayYcnts //여기다가 마커를 찍어? 아니면 주변 역들에 마커를 찍고,
                                Log.d("please", "wpqkf ${it.stationList}")
                                promiseStation = it.stationList[0].statnNm
                                promise.text = promiseStation
                                Log.d("hsh", "현재 만날장소는 $promiseStation")
                                Log.d("hsh", "트랜즈액스, 트랜즈와이 스테이션, $transX, $transY,$station")
                                disposable.add(apiService.transCoord(
                                    " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                                    stationX, stationY, "WTM", "WGS84"
                                )
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ it ->
                                        statnX = it.documents[0].x.toString()
                                        statnY = it.documents[0].y.toString()
                                        Log.d("hsh", "$statnX, ${statnX.toDouble()}")
                                        mMapView.setMapCenterPoint(
                                            MapPoint.mapPointWithGeoCoord(
                                                statnY.toDouble(),
                                                statnX.toDouble()
                                            ), true
                                        )
                                        mMapView.setZoomLevel(2, true)
                                        // mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true)
                                        mMapView.removePOIItem(marker) //이제 역으로이동까지됨
                                        Log.d(
                                            "hsh",
                                            " Tlqkf ${statnX.toDouble() - 0.0170454},${statnY.toDouble() - 0.013705}, ${statnX.toDouble() + 0.0170454},${statnY.toDouble() + 0.013705}"
                                        ) //127, 37 동경 127, 북위 32도 X는 경도 127 Y는 위도 32
                                        disposable.add(apiService.getPlaceByCategory(
                                            " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                                            category,
                                            "${(statnX.toDouble() - 0.0020437)},${(statnY.toDouble() - 0.00421385)},${(statnX.toDouble() + 0.0020437)},${(statnY.toDouble() + 0.00421385)} "
                                        )
                                            /*disposable.add(apiService.getPlaceByCategory(
                                                " KakaoAK 1efba18e6b14c729866a09cc83b41ed8","FD6","127.0561466,37.5058277,127.0602340,37.5142554"
                                            )*/
                                            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
                                            .subscribeOn(Schedulers.io())
                                            .subscribe({ it ->
                                                var order: Int = 0
                                                for (i in it.documents) {

                                                    Log.d("hsh", "${i.place_name}")
                                                    var markers = MapPOIItem()
                                                    markers.itemName = i.place_name
                                                    markers.tag = i.id.toInt()
                                                    markers.mapPoint = MapPoint.mapPointWithGeoCoord(
                                                        i.y.toDouble(),
                                                        i.x.toDouble()
                                                    )
                                                    Log.d("hsh", " ${i.y}, ${i.x}")
                                                    markers.markerType = MapPOIItem.MarkerType.BluePin
                                                    markers.selectedMarkerType = MapPOIItem.MarkerType.YellowPin
                                                    POIlist[order] = markers
                                                    mMapView.addPOIItem(markers)
                                                    Log.d(
                                                        "hsh",
                                                        "${POIlist[order]!!.tag}, ${markers.mapPoint.mapPointGeoCoord.latitude},${markers.itemName}"
                                                    )
                                                    order++
                                                }
                                                //mMapView.addPOIItems(POIlist)
                                                Log.d(
                                                    "hsh",
                                                    "{${mMapView.poiItems[0].itemName}poiItems[0].itemName}"
                                                )

                                                //placeList=it.documents
                                            }) {
                                                Log.d("LOG", "${it.message}!")
                                            })
                                    }) {
                                        Log.d("LOG", "${it.message}!")
                                    })
                            }) {
                                Log.d("LOG", "${it.message}!")
                            })
                    }) {
                        Log.d("location", "${it.message}!")
                    })
            }
        }
        //    transcoder.Wgs84toWtmTranscoder("1efba18e6b14c729866a09cc83b41ed8",posX.toString(),posY.toString())
        AddressButton.setOnClickListener {
            //주소검색으로 지역 설정

            var apiService = ApiService_Kakao.create()
            var addressText = AddressText.text.toString()
            Log.d("hsh", "????")
            disposable.add(apiService.getPlaceByAddress(
                " KakaoAK 1efba18e6b14c729866a09cc83b41ed8", addressText
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    posX = it.documents[0].x.toDouble()
                    posY = it.documents[0].y.toDouble()
                    disposable.add(apiService.transCoord(
                        " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                        posX.toString(),
                        posY.toString(),
                        "WGS84",
                        "WTM"
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ it ->
                            transX = it.documents[0].x.toString()
                            transY = it.documents[0].y.toString()
                            disposable.add(openService.nearStation(
                                "4d66724f78746f6d3732426c574e6e",
                                transX.toString(),
                                transY.toString()
                            )
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ it ->
                                    Log.d("please", "wpqkf ${it.stationList}")
                                    stationX = it.stationList[0].subwayXcnts //결정을 누르면 현재 가장 가까운 역으로 트랜스X,Y가 바뀌며,
                                    stationY = it.stationList[0].subwayYcnts //여기다가 마커를 찍어? 아니면 주변 역들에 마커를 찍고,
                                    Log.d("please", "wpqkf ${it.stationList}")
                                    station = it.stationList[0].statnNm
                                    Log.d("hsh", "트랜즈액스, 트랜즈와이 스테이션, $transX, $transY,$station")
                                    disposable.add(apiService.transCoord(
                                        " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                                        stationX, stationY, "WTM", "WGS84"
                                    )
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe({ it ->
                                            statnX = it.documents[0].x.toString()
                                            statnY = it.documents[0].y.toString()
                                            Log.d("hsh", "$statnX, ${statnX.toDouble()}")
                                            mMapView.setMapCenterPoint(
                                                MapPoint.mapPointWithGeoCoord(
                                                    statnY.toDouble(),
                                                    statnX.toDouble()
                                                ), true
                                            )
                                            // mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true)
                                        }) {
                                            Log.d("LOG", "${it.message}!")
                                        })
                                }) {
                                    Log.d("LOG", "${it.message}!")
                                })
                        }) {
                            Log.d("location", "${it.message}!")
                        })
                })
        }
        promiseSearch.setOnClickListener {
            for (i in 0 until POIlist.size - 1) POIlist[i] = null //초기화
            mMapView.removeAllPOIItems()
            var apiService = ApiService_Kakao.create()
            var addressText = AddressText.text.toString()
            Log.d("hsh", "????")
            disposable.add(apiService.getPlaceByAddress(
                " KakaoAK 1efba18e6b14c729866a09cc83b41ed8", promiseText.text.toString()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    posX = it.documents[0].x.toDouble()
                    posY = it.documents[0].y.toDouble()
                    disposable.add(apiService.transCoord(
                        " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                        posX.toString(),
                        posY.toString(),
                        "WGS84",
                        "WTM"
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ it ->
                            transX = it.documents[0].x.toString()
                            transY = it.documents[0].y.toString()
                            disposable.add(openService.nearStation(
                                "4d66724f78746f6d3732426c574e6e",
                                transX.toString(),
                                transY.toString()
                            )
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ it ->
                                    Log.d("please", "wpqkf ${it.stationList}")
                                    stationX = it.stationList[0].subwayXcnts //결정을 누르면 현재 가장 가까운 역으로 트랜스X,Y가 바뀌며,
                                    stationY = it.stationList[0].subwayYcnts //여기다가 마커를 찍어? 아니면 주변 역들에 마커를 찍고,
                                    Log.d("please", "wpqkf ${it.stationList}")
                                    promiseStation = it.stationList[0].statnNm
                                    promise.text = promiseStation
                                    Log.d("hsh", "트랜즈액스, 트랜즈와이 스테이션, $transX, $transY,$promiseStation")
                                    disposable.add(apiService.transCoord(
                                        " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                                        stationX, stationY, "WTM", "WGS84"
                                    )
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe({ it ->
                                            statnX = it.documents[0].x.toString()
                                            statnY = it.documents[0].y.toString()
                                            Log.d("hsh", "$statnX, ${statnX.toDouble()}")
                                            mMapView.setMapCenterPoint(
                                                MapPoint.mapPointWithGeoCoord(
                                                    statnY.toDouble(),
                                                    statnX.toDouble()
                                                ), true
                                            )
                                            disposable.add(apiService.getPlaceByCategory(
                                                " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                                                category,
                                                "${(statnX.toDouble() - 0.0020437)},${(statnY.toDouble() - 0.00421385)},${(statnX.toDouble() + 0.0020437)},${(statnY.toDouble() + 0.00421385)} "
                                            )
                                                /*disposable.add(apiService.getPlaceByCategory(
                                                    " KakaoAK 1efba18e6b14c729866a09cc83b41ed8","FD6","127.0561466,37.5058277,127.0602340,37.5142554"
                                                )*/
                                                .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
                                                .subscribeOn(Schedulers.io())
                                                .subscribe({ it ->
                                                    var order: Int = 0
                                                    for (i in it.documents) {

                                                        Log.d("hsh", "${i.place_name}")
                                                        var markers = MapPOIItem()
                                                        markers.itemName = i.place_name
                                                        markers.tag = i.id.toInt()
                                                        markers.mapPoint = MapPoint.mapPointWithGeoCoord(
                                                            i.y.toDouble(),
                                                            i.x.toDouble()
                                                        )
                                                        Log.d("hsh", " ${i.y}, ${i.x}")
                                                        markers.markerType = MapPOIItem.MarkerType.BluePin
                                                        markers.selectedMarkerType = MapPOIItem.MarkerType.YellowPin
                                                        POIlist[order] = markers
                                                        mMapView.addPOIItem(markers)
                                                        Log.d(
                                                            "hsh",
                                                            "${POIlist[order]!!.tag}, ${markers.mapPoint.mapPointGeoCoord.latitude},${markers.itemName}"
                                                        )
                                                        order++
                                                    }
                                                    //mMapView.addPOIItems(POIlist)
                                                    Log.d(
                                                        "hsh",
                                                        "{${mMapView.poiItems[0].itemName}poiItems[0].itemName}"
                                                    )

                                                    //placeList=it.documents
                                                }) {
                                                    Log.d("LOG", "${it.message}!")
                                                })
                                            // mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true)
                                        }) {
                                            Log.d("LOG", "${it.message}!")
                                        })
                                }) {
                                    Log.d("LOG", "${it.message}!")
                                })
                        }) {
                            Log.d("location", "${it.message}!")
                        })
                })
        }
    }

    fun setMap() {
        mapListener = object : MapView.MapViewEventListener {
            override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onMapViewInitialized(p0: MapView?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d("LOG", "이니셜라이즈")
            }

            override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
/*                var center=p0!!.mapCenterPoint
                Log.d("location",",\n ${center.mapPointGeoCoord.latitude}, \n ${center.mapPointGeoCoord.longitude}")
                posX=center.mapPointGeoCoord.longitude
                posY=center.mapPointGeoCoord.latitude*/
            }

            override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d("LOG", "싱글탭")

                if (marker != null) mMapView.removePOIItem(marker)

                marker = MapPOIItem()
                marker.setItemName("myChoice")
                marker.tag = 0
                marker.markerType = MapPOIItem.MarkerType.BluePin
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                marker.mapPoint = p1
                mMapView.addPOIItem(marker)

                posX = p1!!.mapPointGeoCoord.longitude
                posY = p1.mapPointGeoCoord.latitude //찍은대로 마커생성하고 그곳 위치 현재 위치로 저장.


            }

            override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        mMapView = net.daum.mf.map.api.MapView(activity!!)
        mMapView.setDaumMapApiKey("1efba18e6b14c729866a09cc83b41ed8")
        val mapViewContainer = mapView
        mapViewContainer.addView(mMapView)
        var locationManager: LocationManager
        val REQUEST_CODE_LOCATION = 2
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)
        Log.d("location", "아 씨발 $longitude $latitude")
        mMapView.setMapViewEventListener(mapListener)
        marker = MapPOIItem()
        marker.setItemName("myChoice")
        marker.tag = 0
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mMapView.addPOIItem(marker)

    }

    fun initLocation() {
        if (checkAppPermission(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                    , android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        ) {
            Toast.makeText(activity, "권한승인됨", Toast.LENGTH_LONG).show()
        } else {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("위치정보 권한 사용여부")
            builder.setTitle("권한 요청")
            builder.setNegativeButton("NO") { _, _ ->
                activity!!.finish()
            }
            builder.setPositiveButton("YES") { _, _ ->
                askPermission(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), ACCESS_REQUEST
                )
                //권한체크하고 위치받아오기
            }
            val dialog = builder.create()
            dialog.show()
        }
        Log.d("location", "$longitude, $latitude 132")
    }

    private fun checkAppPermission(requestPermission: Array<String>): Boolean {
        val requestResult = BooleanArray(requestPermission.size)
        for (i in requestResult.indices) {
            requestResult[i] = ContextCompat.checkSelfPermission(
                activity!!.applicationContext,
                requestPermission[i]
            ) == PackageManager.PERMISSION_GRANTED
            if (!requestResult[i]) { // 허가안될경우
                return false
            }
        }
        return true
    }


    fun askPermission(requestPermission: Array<String>, REQ_PERMISSION: Int) {
        ActivityCompat.requestPermissions(
            activity!!, requestPermission, REQ_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_REQUEST -> {
                if (checkAppPermission(permissions)) {
                    Toast.makeText(activity, "ACCESS_FINE", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(activity, "ACCESS_FINE 허용안됨", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setSpinner() {
        map = mutableMapOf<String, String>("음식점" to "FD6")
        map.put("편의점", "CS2")
        map.put("대형마트", "MT1")
        map.put("유치원", "PS3")
        map.put("학교", "SC4")
        map.put("학원", "AC5")
        map.put("주차장", "PK6")
        map.put("주유소", "OL7")
        map.put("지하철역", "SW8")
        map.put("은행", "BK9")
        map.put("문화시설", "CT1")
        map.put("중개업소", "AG2")
        map.put("관광명소", "AT4")
        map.put("숙박", "AD5")
        map.put("카페", "CE7")
        map.put("병원", "HP8")
        map.put("약국", "PM9")
        spinnerAdapter = ArrayAdapter<String>(
            activity!!.applicationContext,
            android.R.layout.simple_dropdown_item_1line,
            map.keys.toList()
        )
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = CustomOnItemSelectedListener()
    }
}