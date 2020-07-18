package com.kasa.ola.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.App;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.MessageBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.MyMessageAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.AlwaysMarqueeTextView;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyMessageActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_messages)
    LoadMoreRecyclerView rvMessages;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_notice_tips)
    AlwaysMarqueeTextView tvNoticeTips;
    @BindView(R.id.tv_open)
    TextView tvOpen;
    @BindView(R.id.ll_message_notice)
    LinearLayout llMessageNotice;

    private int currentPage = 1;
    private List<MessageBean> messages = new ArrayList<>();
    private MyMessageAdapter myMessageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        initTitle();
        initView();
        loadPage(true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE * 20);
        ApiManager.get().getData(Const.GET_MESSAGE_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String contentList = jo.optString("contentList");
                    List<MessageBean> list = new Gson().fromJson(contentList, new TypeToken<List<MessageBean>>() {
                    }.getType());
                    if (!isLoadMore) {
                        messages.clear();
                        myMessageAdapter.notifyDataSetChanged();
                    }
                    if (list != null && list.size() > 0) {
                        messages.addAll(list);
                        myMessageAdapter.notifyDataSetChanged();
                        rvMessages.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            tvNoData.setVisibility(View.GONE);
                            rvMessages.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.VISIBLE);
                            rvMessages.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showShortToast(MyMessageActivity.this, msg);
            }
        }, isFirst ? loadingView : null);
    }

    private void initView() {
        tvNoticeTips.initScrollTextView(getWindowManager(), getString(R.string.open_message_permission_tips), 3);
        tvNoticeTips.starScroll();
        NotificationManagerCompat manager = NotificationManagerCompat.from(App.getApp());
        boolean isOpened = manager.areNotificationsEnabled();
        if (!isOpened) {
            llMessageNotice.setVisibility(View.VISIBLE);
        }else {
            llMessageNotice.setVisibility(View.GONE);
        }
        tvOpen.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(linearLayoutManager);
        myMessageAdapter = new MyMessageAdapter(MyMessageActivity.this, messages);
        rvMessages.setLoadingListener(this);
        rvMessages.setAdapter(myMessageAdapter);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
//                ToastUtils.showLongToast(MyMessageActivity.this, "调消息列表接口");
                loadPage(true);
            }
        });
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
    }

    private void initTitle() {
        setActionBar(getString(R.string.my_messages), "");
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_open:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", App.getApp().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                break;
        }
    }
}
