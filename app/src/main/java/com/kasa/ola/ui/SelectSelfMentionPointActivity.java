package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.kasa.ola.bean.entity.SelfMentionPointBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.SelfMentionPointAddressAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectSelfMentionPointActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.rv_self_mention_point)
    LoadMoreRecyclerView rvSelfMentionPoint;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    private int currentPage = 1;
    private List<SelfMentionPointBean> selfMentionPointBeans = new ArrayList<>();
    private SelfMentionPointAddressAdapter selfMentionPointAddressAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_self_mention_point);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        loadPage(true);
    }

    private void initEvent() {
        ivSearch.setOnClickListener(this);
    }

    private void initView() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        search();
                        break;
                }
                return false;
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SelectSelfMentionPointActivity.this);
        rvSelfMentionPoint.setLayoutManager(linearLayoutManager);
        rvSelfMentionPoint.setLoadingListener(this);
        rvSelfMentionPoint.setLoadingMoreEnabled(true);
        selfMentionPointAddressAdapter = new SelfMentionPointAddressAdapter(SelectSelfMentionPointActivity.this, selfMentionPointBeans);
        selfMentionPointAddressAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.putExtra(Const.BACK_SELF_MOTENT_POINT_KEY, selfMentionPointBeans.get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        rvSelfMentionPoint.setAdapter(selfMentionPointAddressAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
    }

    private void search() {
        currentPage = 1;
        loadPage(true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        String key = etSearch.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",key);
        jsonObject.put("pageNum",currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        ApiManager.get().getData(Const.GET_TAKE_ADDRESS_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                refreshLayout.setRefreshing(false);
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("list");
                    if (!isLoadMore) {
                        selfMentionPointBeans.clear();
                        selfMentionPointAddressAdapter.notifyDataSetChanged();
                    }
                    List<SelfMentionPointBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<SelfMentionPointBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        selfMentionPointBeans.addAll(list);
                        selfMentionPointAddressAdapter.notifyDataSetChanged();
                        rvSelfMentionPoint.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            rvSelfMentionPoint.setVisibility(View.VISIBLE);
                        } else {
                            rvSelfMentionPoint.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                refreshLayout.setRefreshing(false);
                ToastUtils.showLongToast(SelectSelfMentionPointActivity.this,msg);
            }
        },null);
    }
    private void initTitle() {
        setActionBar(getString(R.string.select_self_mention_point_title), "");
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search:
                search();
                break;
        }
    }
}
