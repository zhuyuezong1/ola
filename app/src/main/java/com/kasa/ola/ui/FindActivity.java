package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.kasa.ola.dialog.SingleBtnCommonDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.FindAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener {

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
    @BindView(R.id.iv_publish)
    ImageView ivPublish;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private int currentPage = 1;
    private List<FoundItemBean> foundItemBeans = new ArrayList<>();
    private FindAdapter findAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        SingleBtnCommonDialog.Builder builder = new SingleBtnCommonDialog.Builder(FindActivity.this);
        builder.setTitle("注意")
                .setMessage("该页面仅供测试")
                .setRightButton("确定")
                .setDialogInterface(new SingleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void rightButtonClick(SingleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }
                }).create().show();
        loadPage(true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
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
                        findAdapter.notifyDataSetChanged();
                    }
                    if (list != null && list.size() > 0) {
                        foundItemBeans.addAll(list);
                        findAdapter.notifyDataSetChanged();
                        rvFind.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }

                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(FindActivity.this, msg);
            }
        }, isFirst?loadingView:null);

    }

    private void initEvent() {
        ivPublish.setOnClickListener(this);
        //测试入口
        tvTitle.setOnClickListener(this);
    }

    private void initTitle() {
        setActionBar(getString(R.string.home_find), getString(R.string.my_publish));
        tvRightText.setOnClickListener(this);
    }

    private void initView() {
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                currentPage=1;
                loadPage(true);
            }
        });
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage=1;
                loadPage(true);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(FindActivity.this, 2);
        rvFind.setLayoutManager(gridLayoutManager);
        rvFind.setLoadingListener(this);
        rvFind.setLoadingMoreEnabled(true);
        findAdapter = new FindAdapter(FindActivity.this, foundItemBeans);
        findAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(FindActivity.this, FoundDetailActivity.class);
                startActivity(intent);
            }
        });
        rvFind.setAdapter(findAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title:
                Intent intent1 = new Intent(FindActivity.this, FoundDetailActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_right_text:
                Intent intent = new Intent(FindActivity.this, MyFindActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_publish:
                Intent publishIntent = new Intent(FindActivity.this, PublishFoundActivity.class);
                startActivity(publishIntent);
                break;
        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
}
