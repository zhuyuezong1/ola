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
import com.kasa.ola.bean.entity.CommentBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.CommentManagerAdapter;
import com.kasa.ola.ui.adapter.MyCommentsAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCommentNewActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.comment_recycle_view)
    LoadMoreRecyclerView commentRecycleView;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.comment_refresh_layout)
    SwipeRefreshLayout commentRefreshLayout;
    private int currentPage = 1;
    private List<CommentBean> commentBeans = new ArrayList<>();
//    private MyCommentsAdapter myCommentsAdapter;
    private int type = 0;
    private CommentManagerAdapter commentManagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comments);
        ButterKnife.bind(this);
        initTitle();
        initView();
        loadPage(true);
    }

    private void initTitle() {
        setActionBar(getString(R.string.my_comments), "");

    }
    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyCommentNewActivity.this);
        commentRecycleView.setLayoutManager(linearLayoutManager);
        commentRecycleView.setLoadingListener(this);

        commentManagerAdapter = new CommentManagerAdapter(MyCommentNewActivity.this, commentBeans);

        commentRecycleView.setAdapter(commentManagerAdapter);
        commentRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });

    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        if (LoginHandler.get().checkLogined()) {
            JSONObject jo = new JSONObject();
            jo.put("userID", LoginHandler.get().getUserId());
            jo.put("pageNum", currentPage);
            jo.put("pageSize", Const.PAGE_SIZE);
            jo.put("type", type);
            ApiManager.get().getData(Const.GET_MY_COMMENTS_TAG, jo, new BusinessBackListener() {
                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    commentRefreshLayout.setRefreshing(false);
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        JSONObject jo = (JSONObject) responseModel.data;
                        String contentList = jo.optString("commentsList");
                        List<CommentBean> list = new Gson().fromJson(contentList, new TypeToken<List<CommentBean>>() {
                        }.getType());
                        if (!isLoadMore) {
                            commentBeans.clear();
                            commentManagerAdapter.notifyDataSetChanged();
                        }
                        if (list != null && list.size() > 0) {
                            commentBeans.addAll(list);
                            commentManagerAdapter.notifyDataSetChanged();
                            commentRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                        }
                        if (!isLoadMore) {
                            if (list != null && list.size() > 0) {
                                tvNoData.setVisibility(View.GONE);
                                commentRecycleView.setVisibility(View.VISIBLE);
                            } else {
                                tvNoData.setVisibility(View.VISIBLE);
                                commentRecycleView.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    commentRefreshLayout.setRefreshing(false);
                    ToastUtils.showShortToast(MyCommentNewActivity.this, msg);
                }
            }, isFirst ? loadingView : null);
        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

}
