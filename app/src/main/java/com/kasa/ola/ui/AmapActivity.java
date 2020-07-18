package com.kasa.ola.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.ui.adapter.MapAdapter;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmapActivity extends BaseActivity implements AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener, LocationSource {

    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.vCenter)
    View vCenter;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.mapLayout)
    RelativeLayout mapLayout;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.rv)
    LoadMoreRecyclerView rv;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private List<PoiItem> poiItems = new ArrayList<>();
    private TextView tvLocationHeader;
    private MapAdapter mapAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        initView();
        initEvent();
        initMap();
    }

    private void initEvent() {
        tvLocation.setOnClickListener(this);
    }

    private void initView() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setLoadingMoreEnabled(false);
        mapAdapter = new MapAdapter(AmapActivity.this, poiItems);
        rv.setAdapter(mapAdapter);
        View rvHeadView = LayoutInflater.from(AmapActivity.this).inflate(R.layout.item_maplocation_head,rv,false);
        tvLocationHeader = rvHeadView.findViewById(R.id.tvTitle);
        rv.addHeaderView(rvHeadView);
    }

    private void initMap() {
        if (aMap == null) {
            aMap = this.mapView.getMap();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色    不显示范围圆圈
//        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色 不显示范围圆圈
        aMap.setLocationSource(this);//设置了定位的监听,这里要实现LocationSource接口
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // 控件交互 缩放按钮、指南针、定位按钮、比例尺等
        UiSettings mUiSettings;//定义一个UiSettings对象
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//

        goLocation();
        //地图移动监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
//                setMarker(cameraPosition.target);
                animTranslate();
                getGeocodeSearch(cameraPosition.target);
            }
        });
    }
    private void goLocation() {
        //获取位置信息
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(AmapActivity.this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(-1000);
        mLocationOption.setOnceLocation(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
            setMapCenter(aMapLocation);
        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            LogUtil.d("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
        }
        mlocationClient.stopLocation();
        mlocationClient.onDestroy();
    }

    private void setMapCenter(AMapLocation amapLocation) {
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())
                        , 15, 0, 0)), 300, null); //设置地图中心点
    }

    private AnimatorSet animatorSet;

    public void animTranslate() {
        if (animatorSet == null) {
            animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(ivLocation, "scaleX", 1, 0.5f, 1).setDuration(300)
                    , ObjectAnimator.ofFloat(ivLocation, "scaleY", 1, 0.5f, 1).setDuration(300));
        }
        animatorSet.start();
    }

    private GeocodeSearch geocoderSearch;

    //逆地理编码获取当前位置信息
    private void getGeocodeSearch(LatLng targe) {
        if (geocoderSearch == null) geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(targe.latitude, targe.longitude), 1000, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i != 1000) return;
        poiItems.clear();
        poiItems.addAll(regeocodeResult.getRegeocodeAddress().getPois());
        mapAdapter.notifyDataSetChanged();
        tvLocationHeader.setText(regeocodeResult.getRegeocodeAddress().getProvince()
                + (TextUtils.equals(regeocodeResult.getRegeocodeAddress().getCity(), regeocodeResult.getRegeocodeAddress().getProvince()) ? "" : regeocodeResult.getRegeocodeAddress().getCity())
                + regeocodeResult.getRegeocodeAddress().getDistrict());
        tvLocation.setText("当前位置：" + regeocodeResult.getRegeocodeAddress().getFormatAddress());
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvLocation:
                break;
        }
    }
    private Marker centerMarker;
    private void setMarker(LatLng target) {
//        if (centerMarker != null) centerMarker.remove();
//        centerMarker = aMap.addMarker(new MarkerOptions().position(target).title("").snippet(""));
//        Animation animation = new RotateAnimation(centerMarker.getRotateAngle() - 180, centerMarker.getRotateAngle(), 0, 0, 0);
////        Animation animation = new TranslateAnimation(target);
//        animation.setDuration(360L);
//        animation.setInterpolator(new LinearInterpolator());
//        centerMarker.setAnimation(animation);
//        centerMarker.startAnimation();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        goLocation();
    }

    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;

    }
}
