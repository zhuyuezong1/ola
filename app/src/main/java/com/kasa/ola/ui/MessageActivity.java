package com.kasa.ola.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.MessageBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.MessageAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.recycler_message)
    LoadMoreRecyclerView recyclerMessage;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_no_result)
    TextView tvNoResult;
    private List<MessageBean> messages = new ArrayList<>();
    private int currentPage = 1;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.message_center), "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this);
        recyclerMessage.setLayoutManager(linearLayoutManager);
        recyclerMessage.setLoadingListener(this);
        messageAdapter = new MessageAdapter(MessageActivity.this, messages);
        recyclerMessage.setAdapter(messageAdapter);
        loadPage(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
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

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE * 20);
        ApiManager.get().getData(Const.GET_MESSAGE_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                refreshLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String contentList = jo.optString("contentList");
                    List<MessageBean> list = new Gson().fromJson(contentList, new TypeToken<List<MessageBean>>() {
                    }.getType());
                    if (!isLoadMore){
                        messages.clear();
                        messageAdapter.notifyDataSetChanged();
                    }
                    if (list != null && list.size() > 0) {
                        messages.addAll(list);
                        messageAdapter.notifyDataSetChanged();
                        recyclerMessage.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            tvNoResult.setVisibility(View.GONE);
                            recyclerMessage.setVisibility(View.VISIBLE);
                        } else {
                            tvNoResult.setVisibility(View.VISIBLE);
                            recyclerMessage.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                refreshLayout.setRefreshing(false);
                ToastUtils.showShortToast(MessageActivity.this, msg);
            }
        }, isFirst ? loadingView : null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false,true);
    }
}
