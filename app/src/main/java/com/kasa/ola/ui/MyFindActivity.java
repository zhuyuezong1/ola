package com.kasa.ola.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.FoundItemBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.MyFindAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFindActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_find)
    LoadMoreRecyclerView rvFind;
    @BindView(R.id.slRefresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private int currentPage = 1;
    private List<FoundItemBean> foundItemBeans = new ArrayList<>();
    private MyFindAdapter myFindAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_find);
        ButterKnife.bind(this);
        initTitle();
        initView();
        loadPage(true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        jsonObject.put("userID", LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.GET_FOUND_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String foundList = jo.optString("foundList");
                    List<FoundItemBean> list = new Gson().fromJson(foundList, new TypeToken<List<FoundItemBean>>() {
                    }.getType());
                    if (!isLoadMore) {
                        foundItemBeans.clear();
                        myFindAdapter.notifyDataSetChanged();
                    }
                    if (list != null && list.size() > 0) {
                        foundItemBeans.addAll(list);
                        myFindAdapter.notifyDataSetChanged();
                        rvFind.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }

                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(MyFindActivity.this, msg);
            }
        }, isFirst ? loadingView : null);

    }

    private void initTitle() {
        setActionBar(getString(R.string.home_find), "");
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MyFindActivity.this, 2);
        rvFind.setLayoutManager(gridLayoutManager);
        rvFind.setLoadingListener(this);
        rvFind.setLoadingMoreEnabled(true);
        myFindAdapter = new MyFindAdapter(MyFindActivity.this, foundItemBeans);
        rvFind.setAdapter(myFindAdapter);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false,true);
    }
}
