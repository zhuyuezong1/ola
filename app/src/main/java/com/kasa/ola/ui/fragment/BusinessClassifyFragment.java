package com.kasa.ola.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ShopInfoBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.StoreDetailsActivity;
import com.kasa.ola.ui.adapter.BusinessesAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BusinessClassifyFragment extends BaseFragment implements LoadMoreRecyclerView.LoadingListener {
    @BindView(R.id.rv_businesses)
    LoadMoreRecyclerView rvBusinesses;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    Unbinder unbinder;
    private LayoutInflater inflater;
    private String suppliersClassifyID;
    private BusinessesAdapter businessesAdapter;
    private List<ShopInfoBean> shopInfoBeans = new ArrayList<>();
    private ImageView iv_business_banner;
    private int currentPage = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_class_businesses, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        suppliersClassifyID = arguments.getString(Const.STORE_CLASSIFY_ID);
        initView();
        loadPage(true);
        return rootView;
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
//        jsonObject.put("key", key);
        jsonObject.put("suppliersClassifyID", suppliersClassifyID);
        jsonObject.put("longitude", LoginHandler.get().getLongitude());
        jsonObject.put("latitude", LoginHandler.get().getLatitude());
        ApiManager.get().getData(Const.GET_SHOP_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
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
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, isFirst?loadingView:null);
    }

    private void initView() {
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
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
        View headView = inflater.from(getContext()).inflate(R.layout.view_businesses_head, rvBusinesses, false);
        iv_business_banner = headView.findViewById(R.id.iv_business_banner);
        rvBusinesses.addHeaderView(headView);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
