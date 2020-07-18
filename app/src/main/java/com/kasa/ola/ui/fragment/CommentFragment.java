package com.kasa.ola.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CommentBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.MyCommentsAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CommentFragment extends BaseFragment implements LoadMoreRecyclerView.LoadingListener {

    public static final String COMMENT_TYPE_KEY = "ORDER_STATUS_KEY";
    @BindView(R.id.comment_recycle_view)
    LoadMoreRecyclerView commentRecycleView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.comment_refresh_layout)
    SwipeRefreshLayout commentRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    private int type = 0;
    private boolean isFirst = true;
    private boolean isVisibleToUser = false;
    private boolean isInitView = false;
    private int currentPage = 1;
    private List<CommentBean> commentBeans = new ArrayList<>();
    private MyCommentsAdapter myCommentsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(COMMENT_TYPE_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_comment_layout, container, false);
        isInitView = true;
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        commentRecycleView.setLayoutManager(linearLayoutManager);
        commentRecycleView.setLoadingListener(this);
        myCommentsAdapter = new MyCommentsAdapter(getActivity(), commentBeans,type);
        commentRecycleView.setAdapter(myCommentsAdapter);
        commentRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            if (isFirst) {
                isFirst = false;
                loadPage(true);
            } else {
                loadPage(false);
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
                loadPage(true);
            } else {
                loadPage(false);
            }
        }
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
                    if (null != getActivity()) {
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
                            String contentList = jo.optString("commentsList");
                            List<CommentBean> list = new Gson().fromJson(contentList, new TypeToken<List<CommentBean>>() {
                            }.getType());
                            if (!isLoadMore) {
                                commentBeans.clear();
                                myCommentsAdapter.notifyDataSetChanged();
                            }
                            if (list != null && list.size() > 0) {
                                commentBeans.addAll(list);
                                myCommentsAdapter.notifyDataSetChanged();
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
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    commentRefreshLayout.setRefreshing(false);
                    ToastUtils.showShortToast(getContext(), msg);
                }
            }, isFirst ? loadingView : null);
        }
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

