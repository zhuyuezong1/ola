package com.kasa.ola.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.BusinessClassifyBean;
import com.kasa.ola.bean.entity.ShopInfoBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.BusinessAndProductSearchActivity;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.ui.StoreDetailsActivity;
import com.kasa.ola.ui.adapter.BusinessesAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BusinessesFragment1 extends BaseFragment implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {

    @BindView(R.id.rv_businesses)
    LoadMoreRecyclerView rvBusinesses;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    Unbinder unbinder;
    @BindView(R.id.tv_business)
    TextView tvBusiness;
    @BindView(R.id.business_tab)
    TabLayout businessTab;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private LayoutInflater inflater;
    private BusinessesAdapter businessesAdapter;
    private int defaultColor = R.color.COLOR_FF1677FF;
    private List<ShopInfoBean> shopInfoBeans = new ArrayList<>();
    private ArrayList<String> tabList;
    private String key;
    private int currentPage = 1;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private boolean haveNoPermission;
    private int currentTab = 0;
    private List<BusinessClassifyBean> businessClassifyBeans;
    private ImageView iv_business_banner;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_businesses_1, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTitle();
        initView();
        initEvent();
//        loadPage(true,currentPage);
        return rootView;
    }

    private void initEvent() {
        llSearch.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true, currentTab);
            }
        });
    }

    private void loadPage(boolean isFirst, int position) {
        loadPage(isFirst, position, false);
    }

    private void loadPage(boolean isFirst, int position, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        jsonObject.put("key", key);
        jsonObject.put("suppliersClassifyID", businessClassifyBeans.get(position).getSuppliersClassifyID());
        jsonObject.put("longitude", LoginHandler.get().getLongitude());
        jsonObject.put("latitude", LoginHandler.get().getLatitude());
        ApiManager.get().getData(Const.GET_SHOP_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                refreshLayout.setRefreshing(false);
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String educationBanner = jo.optString("suppliersBanner");
                    ImageLoaderUtils.imageLoadRound(getContext(), educationBanner, iv_business_banner, 10);
                    JSONArray ja = jo.optJSONArray("suppliersList");
                    if (!isLoadMore) {
                        shopInfoBeans.clear();
                        businessesAdapter.notifyDataSetChanged();
                    }
                    List<ShopInfoBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ShopInfoBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        shopInfoBeans.addAll(list);
                        businessesAdapter.notifyDataSetChanged();
                        rvBusinesses.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                refreshLayout.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, isFirst?loadingView:null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.destroy(BusinessesFragment1.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        haveNoPermission = checkPermissions(getContext(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
        getPermission();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBusinesses.setLayoutManager(linearLayoutManager);
        rvBusinesses.setLoadingListener(this);
        businessesAdapter = new BusinessesAdapter(getContext(), shopInfoBeans);
        businessesAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Intent intent = new Intent(getContext(), ShopDetailActivity.class);
                Intent intent = new Intent(getContext(), StoreDetailsActivity.class);
                intent.putExtra(Const.SHOP_ID, shopInfoBeans.get(position).getSuppliersID());
                startActivity(intent);
            }
        });
        rvBusinesses.setAdapter(businessesAdapter);
        initTabListener();
        View headView = inflater.from(getContext()).inflate(R.layout.view_businesses_head, rvBusinesses, false);
        iv_business_banner = headView.findViewById(R.id.iv_business_banner);
        getClassiFy();
        rvBusinesses.addHeaderView(headView);
    }

    private void initTabListener() {
        businessTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                getBusinessList(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getBusinessList(int position) {
        loadPage(true, currentTab);
    }

    private void getClassiFy() {
        ApiManager.get().getData(Const.GET_SUPPLIERS_CLASSIFY_LIST, null, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String suppliersClassifyList = jo.optString("suppliersClassifyList");
                    businessClassifyBeans = new Gson().fromJson(suppliersClassifyList, new TypeToken<List<BusinessClassifyBean>>() {
                    }.getType());
                    businessTab.removeAllTabs();
                    initTab(businessClassifyBeans);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, null);
    }

    private void initTab(List<BusinessClassifyBean> list) {
        tabList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BusinessClassifyBean businessClassifyBean = list.get(i);
            businessTab.addTab(businessTab.newTab().setText(businessClassifyBean.getSuppliersClassifyName()));
            tabList.add(businessClassifyBean.getSuppliersClassifyName());
        }
        for (int i = 0; i < businessTab.getTabCount(); i++) {
            TabLayout.Tab tab = businessTab.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        updateTabTextView(businessTab.getTabAt(businessTab.getSelectedTabPosition()), true);

        businessTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabTextView(tab, true);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabTextView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initTitle() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            setStatusBar(defaultColor);
//        }
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        viewShadow.setBackgroundColor(getContext().getColor(R.color.COLOR_FF1677FF));
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden )
            ImmersionBar.with(getActivity()).statusBarDarkFont(false).init();
    }
    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, currentTab, true);
    }

    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item, null);
        TextView textView = (TextView) view.findViewById(R.id.tab_item_textview);
        textView.setText(tabList.get(currentPosition));
        return view;
    }

    private void updateTabTextView(TabLayout.Tab tab, boolean isSelect) {
        if (isSelect) {
            //选中加粗
            TextView tabSelect = (TextView) tab.getCustomView().findViewById(R.id.tab_item_textview);
            tabSelect.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tabSelect.setText(tab.getText());
        } else {
            TextView tabUnSelect = (TextView) tab.getCustomView().findViewById(R.id.tab_item_textview);
            tabUnSelect.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tabUnSelect.setText(tab.getText());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
                Intent intent = new Intent(getContext(), BusinessAndProductSearchActivity.class);
                intent.putExtra(Const.SEARCH_TYPE, 2);
                startActivity(intent);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermission() {
        if (haveNoPermission) {
            startLocation();
        } else {
            ((MainActivity) getActivity()).requestPermissions(getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, new BaseActivity.RequestPermissionCallBack() {
                @Override
                public void granted() {
                    if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    startLocation();
                }

                @Override
                public void denied() {
                    ToastUtils.showShortToast(getContext(), "获取权限失败，正常功能受到影响");
                }
            });
        }
    }

    private void startLocation() {

        mlocationClient = new AMapLocationClient(getContext());
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {


            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        //获取纬度
                        double latitude = amapLocation.getLatitude();
                        //获取经度
                        double longitude = amapLocation.getLongitude();
                        amapLocation.getAccuracy();//获取精度信息
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(amapLocation.getTime());
                        df.format(date);//定位时间
                        LoginHandler.get().saveLongitude(longitude + "");
                        LoginHandler.get().saveLatitude(latitude + "");
//                                ToastUtils.showLongToast(getContext(),amapLocation.getLatitude()+"---"+amapLocation.getLongitude());
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        });
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }

    public boolean checkPermissions(final Context context, final String[] permissions) {
        StringBuilder permissionNames = new StringBuilder();
        for (String s : permissions) {
            permissionNames = permissionNames.append(s + "\r\n");
        }
        //如果所有权限都已授权，则直接返回授权成功,只要有一项未授权，则发起权限请求
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }
}
