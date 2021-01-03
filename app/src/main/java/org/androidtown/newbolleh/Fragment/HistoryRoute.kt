package org.androidtown.newbolleh.Fragment


import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_history_route.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Google
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Kakao
import org.androidtown.bolleh.RetrofitConnection.ApiServices.ApiService_Main
import org.androidtown.newbolleh.Datas.history.routeBaseAdapter
import org.androidtown.newbolleh.R

class HistoryRoute : Fragment() {

    lateinit var map: MutableMap<String, String>
    lateinit var marker: MapPOIItem
    lateinit var mMapView: MapView
    var mPin=""
    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var mapListener: MapView.MapViewEventListener
    var category = ""
    var POIlist = arrayOfNulls<MapPOIItem>(45)
    var posX:Double=0.0
    var posY:Double=0.0
    var stationX:String=""
    var stationY:String=""
    var station=""
    var flag=0
    var userMap:MutableMap<String,String> = mutableMapOf<String,String>()
    var keySet:MutableList<String> = mutableListOf()
    lateinit var listAdapter:routeBaseAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("hsh","루트히스토리들어옴")
        return inflater.inflate(R.layout.fragment_history_route, container, false)
    }

    companion object {
        fun newRoute(
            pin:String
        ): HistoryRoute {
            val newRoute=HistoryRoute()
            newRoute.mPin=pin
            return newRoute
        }
    }
    interface callRouteListener{
        fun changeToHistory(pin:String)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        exitText.setOnClickListener {
            if(activity != null){
                val callWaiting = activity as Waiting.callWaitingListener
                callWaiting.changeToHistory(mPin)
            }
        }
        setMap()
        Log.v("HistoryRoute",mPin)
        getGeoByStation(station)
        refreshMap()
        setSpinner()
//        categoryButton.visibility=View.GONE
//        categoryButton.setOnClickListener {
//            var disposable = CompositeDisposable()
//            var apiService = ApiService_Main.create(context!!)
//            disposable.add(apiService.setCategory(mPin,category)
//                .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
//                .subscribeOn(Schedulers.io())
//                .subscribe({ it ->
//                    // 통신 성공
//                    Log.d("hsh","${it.toString()}")
//                }) {
//                    Log.v("detail", "실패다 제군")
//                })
//        }
    }

    fun setMap(){
        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        /*disposable.add(apiService.getHistory(pin)
            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                // 통신 성공
            }) {
                Log.v("detail", "실패다 제군")
            })*/

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
        val mapViewContainer = routeMapView
        mapViewContainer.addView(mMapView)
        var locationManager: LocationManager
        val REQUEST_CODE_LOCATION = 2
        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

//        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)
//        Log.d("location", "$longitude $latitude")
        //위에는 히스토리 정보로 가져와서 해야한다.
        mMapView.setMapViewEventListener(mapListener)
        /*     marker = MapPOIItem()
             marker.setItemName("myChoice")
             marker.tag = 0
             marker.markerType = MapPOIItem.MarkerType.BluePin
             marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
             mMapView.addPOIItem(marker)*/

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
        routeSpinner.adapter = spinnerAdapter
        routeSpinner.onItemSelectedListener = CustomOnItemSelectedListener()
    }

    inner class CustomOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.d("LOG", "${p0!!.getItemAtPosition(p2).toString()}")
            category = map[p0.getItemAtPosition(p2).toString()]!!
            Log.d("hsh","온아이템셀렉티드 카테고리$category")
            var apiServiceDaum=ApiService_Kakao.create()
            var disposable=CompositeDisposable()
            if(flag==1) {
                disposable.add(apiServiceDaum.getPlaceByCategory(

                    " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",
                    category,
                    "${(stationX.toDouble() - 0.0020437)},${(stationY.toDouble() - 0.00421385)},${(stationX.toDouble() + 0.0020437)},${(stationY.toDouble() + 0.00421385)} "
                )
                    /*disposable.add(apiService.getPlaceByCategory(
                    " KakaoAK 1efba18e6b14c729866a09cc83b41ed8","FD6","127.0561466,37.5058277,127.0602340,37.5142554"
                )*/
                    .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
                    .subscribeOn(Schedulers.io())
                    .subscribe({ it ->
                        mMapView.removeAllPOIItems()
                        var order: Int = 0
                        for (i in it.documents) {
                            Log.d("hsh", "${i.place_name}")
                            var markers = MapPOIItem()
                            markers.itemName = i.place_name
                            markers.tag = i.id.toInt()
                            markers.mapPoint = MapPoint.mapPointWithGeoCoord(i.y.toDouble(), i.x.toDouble())
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
                        mMapView.setZoomLevel(2,true)
                        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(stationY.toDouble(),stationX.toDouble()),true)
                        //mMapView.addPOIItems(POIlist)
                        Log.d("hsh", "{${mMapView.poiItems[0].itemName}poiItems[0].itemName}")

                        //placeList=it.documents
                    }) {
                        Log.d("LOG", "${it.message}!")
                    })
            }


        }

    }
    fun getGeoByStation(station:String){
        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        var apiServiceGoogle=ApiService_Google.create()
        var apiServiceDaum=ApiService_Kakao.create()
        disposable.add(apiService.getHistory(mPin)
            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                this.station=it.place.toString()
                val pref = activity!!.getPreferences(0)
                var ID = pref.getString("ID", null)
                Log.d("hsh","${it.list[0].nick}, ${it.category}")
                if(it.list[0].nick==ID&&it.category=="no"){ //여기 수정.
//                    categoryButton.visibility=View.VISIBLE
                }
                for(i in 0..it.list.size-1){
                    keySet.add(it.list[i].nick)
                    userMap.put(it.list[i].nick,it.list[i].path)

                }
                listAdapter= routeBaseAdapter(activity!!.applicationContext,userMap,keySet)
                routeList.adapter=listAdapter

                disposable.add(apiServiceGoogle.findGeoByStation(this.station+"역","AIzaSyCA5CpNdjgVCYrIW9dqWtJSMmSXTJrN2OY")
                    .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
                    .subscribeOn(Schedulers.io())
                    .subscribe({ it ->
                        Log.d("hsh","히스토리루트, ${it} // ${it.results} // ${it.results[0].geometry}//${it.results[0].geometry.location.lat}//")
                        Log.d("hsh","${this.station} 스테이션이어디여")
                        // 통신 성공
                        stationX=it.results[0].geometry.location.lng
                        stationY=it.results[0].geometry.location.lat
                        mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(stationY.toDouble(),stationX.toDouble()),true)
                        mMapView.setZoomLevel(2,true)
                        // mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude),true)
                        Toast.makeText(activity,"약속 장소는 ${this.station} 역입니다.",Toast.LENGTH_SHORT).show()
                        disposable.add(apiServiceDaum.getPlaceByCategory(

                            " KakaoAK 1efba18e6b14c729866a09cc83b41ed8",category,"${(stationX.toDouble()-0.0020437)},${(stationY.toDouble()-0.00421385)},${(stationX.toDouble()+0.0020437)},${(stationY.toDouble()+0.00421385)} "
                        )
                            .observeOn(AndroidSchedulers.mainThread()) //이제 변환된 주소를 기반으로 검색 실시
                            .subscribeOn(Schedulers.io())
                            .subscribe({ it ->
                                var order:Int=0
                                for(i in it.documents) {
                                    Log.d("hsh", "${i.place_name}")
                                    var markers=MapPOIItem()
                                    markers.itemName=i.place_name
                                    markers.tag=i.id.toInt()
                                    markers.mapPoint= MapPoint.mapPointWithGeoCoord(i.y.toDouble(),i.x.toDouble())
                                    Log.d("hsh"," ${i.y}, ${i.x}")
                                    markers.markerType=MapPOIItem.MarkerType.BluePin
                                    markers.selectedMarkerType=MapPOIItem.MarkerType.YellowPin
                                    POIlist[order]=markers
                                    mMapView.addPOIItem(markers)
                                    Log.d("hsh", "${POIlist[order]!!.tag}, ${markers.mapPoint.mapPointGeoCoord.latitude},${markers.itemName}")
                                    order++

                                }
                                flag=1
                                //mMapView.addPOIItems(POIlist)
                                Log.d("hsh","{${mMapView.poiItems[0].itemName}poiItems[0].itemName}")

                                //placeList=it.documents
                            }) {
                                Log.d("LOG", "${it.message}!")
                            })
                    }) {
                        Log.v("detail", "실패다 제군")
                    })
            }) {
                Log.v("detail", "실패다 제군")
            })

    }
    fun refreshMap(){
        var disposable = CompositeDisposable()
        var apiService = ApiService_Main.create(context!!)
        var apiServiceGoogle=ApiService_Google.create()

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}