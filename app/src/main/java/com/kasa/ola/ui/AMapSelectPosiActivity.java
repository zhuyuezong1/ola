package com.kasa.ola.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.adapter.MapAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AMapSelectPosiActivity extends BaseActivity implements LocationSource, GeocodeSearch.OnGeocodeSearchListener, AMapLocationListener, PoiSearch.OnPoiSearchListener, View.OnClickListener {

    //    @BindView(R.id.keyWord)
//    AutoCompleteTextView searchText;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.rv_positions)
    RecyclerView rvPositions;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    //    @BindView(R.id.radio0)
//    RadioButton radio0;
//    @BindView(R.id.radio1)
//    RadioButton radio1;
//    @BindView(R.id.radio2)
//    RadioButton radio2;
//    @BindView(R.id.radio3)
//    RadioButton radio3;
//    @BindView(R.id.segmented_group)
//    RadioGroup segmentedGroup;
    private AMap aMap;
    private GeocodeSearch geocoderSearch;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems = new ArrayList<>();// poi数据
    private String searchKey = "";
    private LatLonPoint searchLatlonPoint;
    private PoiItem firstItem;
    private ProgressDialog progDialog = null;
    private boolean isInputKeySearch;
    private MapAdapter mapAdapter;
    private boolean isItemClickAction;
    private Marker locationMarker;
    private List<Tip> autoTips;
    private boolean isfirstinput = true;
    private String inputSearchKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap_select_posi);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        setActionBar(getString(R.string.select_store_address_title),"");
        init();
        initView();
    }

    private void initView() {
        tvSearch.setOnClickListener(this);
        rvPositions.setLayoutManager(new LinearLayoutManager(this));
        mapAdapter = new MapAdapter(AMapSelectPosiActivity.this, poiItems);
        mapAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PoiItem poiItem = poiItems.get(position);
                Intent intent = new Intent();
                intent.putExtra(Const.POSITION_INFO, poiItem);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        rvPositions.setAdapter(mapAdapter);
//
//        searchText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String newText = s.toString().trim();
//                if (newText.length() > 0) {
//                    InputtipsQuery inputquery = new InputtipsQuery(newText, "北京");
//                    Inputtips inputTips = new Inputtips(AMapSelectPosiActivity.this, inputquery);
//                    inputquery.setCityLimit(true);
//                    inputTips.setInputtipsListener(inputtipsListener);
//                    inputTips.requestInputtipsAsyn();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.i("MY", "setOnItemClickListener");
//                if (autoTips != null && autoTips.size() > position) {
//                    Tip tip = autoTips.get(position);
//                    searchPoi(tip);
//                }
//            }
//        });

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
    }

    //    Inputtips.InputtipsListener inputtipsListener = new Inputtips.InputtipsListener() {
//        @Override
//        public void onGetInputtips(List<Tip> list, int rCode) {
//            if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
//                autoTips = list;
//                List<String> listString = new ArrayList<String>();
//                for (int i = 0; i < list.size(); i++) {
//                    listString.add(list.get(i).getName());
//                }
//                ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
//                        getApplicationContext(),
//                        R.layout.route_inputs, listString);
//                searchText.setAdapter(aAdapter);
//                aAdapter.notifyDataSetChanged();
//                if (isfirstinput) {
//                    isfirstinput = false;
//                    searchText.showDropDown();
//                }
//            } else {
//                Toast.makeText(AMapSelectPosiActivity.this, "erroCode " + rCode , Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
//                if (!isItemClickAction && !isInputKeySearch) {
//                geoAddress();
//                startJumpAnimation();
//                }
                searchLatlonPoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                geoAddress();
                startJumpAnimation();
                isInputKeySearch = false;
                isItemClickAction = false;
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                addMarkerInScreenCenter(null);
            }
        });
    }

    private void searchPoi(Tip result) {
        isInputKeySearch = true;
        inputSearchKey = result.getName();//getAddress(); // + result.getRegeocodeAddress().getCity() + result.getRegeocodeAddress().getDistrict() + result.getRegeocodeAddress().getTownship();
        searchLatlonPoint = result.getPoint();
        firstItem = new PoiItem("tip", searchLatlonPoint, inputSearchKey, result.getAddress());
        firstItem.setCityName(result.getDistrict());
        firstItem.setAdName("");

        poiItems.clear();
        rvPositions.scrollToPosition(0);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(searchLatlonPoint.getLatitude(), searchLatlonPoint.getLongitude()), 16f));

//        hideSoftKey(searchText);
        doSearchQuery();
    }

    private void addMarkerInScreenCenter(LatLng locationLatLng) {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_location)));
        //设置Marker在屏幕上,不跟随地图移动
        locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        locationMarker.setZIndex(1);

    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {

//        if (locationMarker != null ) {
//            //根据屏幕距离计算需要移动的目标点
//            final LatLng latLng = locationMarker.getPosition();
//            Point point =  aMap.getProjection().toScreenLocation(latLng);
//            point.y -= DisplayUtils.dip2px(this,125);
//            LatLng target = aMap.getProjection()
//                    .fromScreenLocation(point);
//            //使用TranslateAnimation,填写一个需要移动的目标点
//            Animation animation = new TranslateAnimation(target);
//            animation.setInterpolator(new Interpolator() {
//                @Override
//                public float getInterpolation(float input) {
//                    // 模拟重加速度的interpolator
//                    if(input <= 0.5) {
//                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
//                    } else {
//                        return (float) (0.5f - Math.sqrt((input - 0.5f)*(1.5f - input)));
//                    }
//                }
//            });
//            //整个移动所需要的时间
//            animation.setDuration(600);
//            //设置动画
//            locationMarker.setAnimation(animation);
//            //开始动画
//            locationMarker.startAnimation();
//
//        } else {
//            Log.e("ama","screenMarker is null");
//        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
//        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.MAP_TYPE_NORMAL);
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17.5f));
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setOnceLocation(true);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
//        Log.i("MY", "doSearchQuery");
        searchKey = etSearch.getText().toString();
        currentPage = 0;
        query = new PoiSearch.Query(searchKey, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setCityLimit(true);
        query.setPageSize(20);
        query.setPageNum(currentPage);

        if (searchLatlonPoint != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(searchLatlonPoint, 1000, true));//
            poiSearch.searchPOIAsyn();
        }
    }

    /**
     * 响应逆地理编码
     */
    public void geoAddress() {
//        Log.i("MY", "geoAddress"+ searchLatlonPoint.toString());
        showDialog();
//        searchText.setText("");
        if (searchLatlonPoint != null) {
            RegeocodeQuery query = new RegeocodeQuery(searchLatlonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            geocoderSearch.getFromLocationAsyn(query);
        }
    }

    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在加载...");
        progDialog.show();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String address = result.getRegeocodeAddress().getProvince() + result.getRegeocodeAddress().getCity() + result.getRegeocodeAddress().getDistrict() + result.getRegeocodeAddress().getTownship();
                firstItem = new PoiItem("regeo", searchLatlonPoint, address, address);
                doSearchQuery();
            }
        } else {
            Toast.makeText(AMapSelectPosiActivity.this, "error code is " + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);

                LatLng curLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                searchLatlonPoint = new LatLonPoint(curLatlng.latitude, curLatlng.longitude);

                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16f));

                isInputKeySearch = false;

//                searchText.setText("");

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {
                if (poiResult.getQuery().equals(query)) {
                    poiItems.clear();
                    poiItems.addAll(poiResult.getPois());
                    if (poiItems != null && poiItems.size() > 0) {
                        mapAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AMapSelectPosiActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(AMapSelectPosiActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private void hideSoftKey(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                doSearchQuery();
                hideSoftKey(v);
                break;
        }
    }
}
