package com.kasa.ola.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ShopOrderBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.StoreOrderAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StoreOrderFrament extends BaseFragment implements LoadMoreRecyclerView.LoadingListener {
    public static final String STORE_ORDER_STATUS_KEY = "STORE_ORDER_STATUS_KEY";
    @BindView(R.id.order_recycle_view)
    LoadMoreRecyclerView orderRecycleView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.order_refresh_layout)
    SwipeRefreshLayout orderRefreshLayout;
    private int type;
    private int currentPage = 1;
    private boolean isFirst = true;
    private boolean isVisibleToUser = true;
    private boolean isInitView = false;
    Unbinder unbinder;
    private long startTime;
    private long endTime;
    private StoreOrderAdapter storeOrderAdapter;
    private List<ShopOrderBean> shopOrderBeans = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        type = getArguments().getInt(STORE_ORDER_STATUS_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_store_order_classify_layout, container, false);
        unbinder = ButterKnife.bind(this, rootView);
//        ToastUtils.showLongToast(getContext(),"调接口"+type);
        isInitView = true;
        initView();
        return rootView;
    }

    private void initView() {
        orderRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true,false);
            }
        });
        orderRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderRecycleView.setLoadingMoreEnabled(true);
        orderRecycleView.setLoadingListener(this);
        storeOrderAdapter = new StoreOrderAdapter(getContext(), shopOrderBeans);
        orderRecycleView.setAdapter(storeOrderAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            if (isFirst) {
                isFirst = false;
                loadPage(true,false);
            } else {
                loadPage(false,false);
            }
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isInitView) {
            if (isFirst) {
                isFirst = false;
                loadPage(true,false);
            } else {
                loadPage(false,false);
            }
        }
    }

    private void loadPage(boolean isFirst,boolean isLoadMore) {
        if (startTime != 0 && endTime != 0) {
            loadPage(isFirst, isLoadMore, startTime + "", endTime + "");
        } else {
            loadPage(isFirst, isLoadMore, null, null);
        }
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore,String startTime,String endTime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("type", type);
        if (null != startTime && null != endTime) {
            jsonObject.put("startTime", startTime);
            jsonObject.put("endTime", endTime);
        }
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        ApiManager.get().getData(Const.GET_SUPPLIERS_ORDER_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                orderRefreshLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String jsonData = jo.optString("list");
                    String amount = jo.optString("amount");
                    if (!isLoadMore) {
                        shopOrderBeans.clear();
                        storeOrderAdapter.notifyDataSetChanged();
                    }
                    List<ShopOrderBean> list = new Gson().fromJson(jsonData, new TypeToken<List<ShopOrderBean>>() {
                    }.getType());
                    if (list!=null && list.size()>0) {
                        shopOrderBeans.addAll(list);
                        storeOrderAdapter.notifyDataSetChanged();
                        orderRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list!=null&& list.size() > 0) {
                            orderRecycleView.setVisibility(View.VISIBLE);
                        }else {
                            orderRecycleView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                orderRefreshLayout.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        },null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.TimePost timePost) {
        startTime = timePost.getStartTime();
        endTime = timePost.getEndTime();
        loadPage(true,false);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
