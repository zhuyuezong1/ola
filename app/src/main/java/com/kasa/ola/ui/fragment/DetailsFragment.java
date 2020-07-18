package com.kasa.ola.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.kasa.ola.bean.entity.DetailBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.DetailAdapter;
import com.kasa.ola.ui.popwindow.FilterPopWindow;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailsFragment extends BaseFragment implements LoadMoreRecyclerView.LoadingListener {
    @BindView(R.id.detail_recycle_view)
    LoadMoreRecyclerView detailRecycleView;
    @BindView(R.id.view_no_result)
    LinearLayout viewNoResult;
    @BindView(R.id.detail_view)
    FrameLayout detailView;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.detail_refresh_layout)
    SwipeRefreshLayout detailRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.bg_view)
    View bgView;
    private int currentPage = 1;
    private boolean isFirst = true;
    private boolean isVisibleToUser = false;
    private boolean isInitView = false;
    private DetailAdapter detailAdapter;
    private long startTime = 0;
    private long endTime = 0;
    private TextView textViewFilter;
    private int detailType;
    private List<DetailBean> detailBeans = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_detail_layout, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        detailType = arguments.getInt(Const.DETAIL_TYPE);
        initView(inflater);
        return rootView;
    }

    private void initView(LayoutInflater inflater) {
        detailRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        detailRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        detailRecycleView.setLoadingListener(this);
        detailRecycleView.setLoadingMoreEnabled(true);
        detailAdapter = new DetailAdapter(getContext(), detailBeans);
        detailRecycleView.setAdapter(detailAdapter);

        View headView = inflater.from(getContext()).inflate(R.layout.view_account_detail_head, detailRecycleView, false);
        textViewFilter = headView.findViewById(R.id.textView_filter);
        textViewFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePop();
            }
        });
        detailRecycleView.addHeaderView(headView);
        isInitView = true;
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        ApiManager.get().getData(getDataType(detailType), jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                detailRefreshLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String jsonData = jo.optString("list");
                    if (!isLoadMore) {
                        detailBeans.clear();
                        detailAdapter.notifyDataSetChanged();
                    }
                    List<DetailBean> list = new Gson().fromJson(jsonData, new TypeToken<List<DetailBean>>() {
                    }.getType());
                    if (list!=null && list.size()>0) {
                        detailBeans.addAll(list);
                        detailAdapter.notifyDataSetChanged();
                        detailRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list!=null&& list.size() > 0) {
                            viewNoResult.setVisibility(View.GONE);
                            detailRecycleView.setVisibility(View.VISIBLE);
                        }else {
                            viewNoResult.setVisibility(View.VISIBLE);
                            detailRecycleView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                detailRefreshLayout.setRefreshing(false);
                ToastUtils.showLongToast(getContext(),msg);
            }
        },null);

    }
    private String getDataType(int detailType){
        if (detailType==0){
            return Const.GET_COMMISSION_DETAIL;
        }else if (detailType==1){
            return Const.GET_BALANCE_DETAIL;
        }
        return "";
    }

    private void showDatePop() {
        FilterPopWindow filterPopWindow = new FilterPopWindow(getActivity(), startTime, endTime);
        filterPopWindow.setFilterListener(new FilterPopWindow.FilterListener() {
            @Override
            public void confirmClick(long startTime1, long endTime1) {
                startTime = startTime1;
                endTime = endTime1;
                currentPage = 1;
                loadPage(true, false);
            }

            @Override
            public void resetClick() {
                startTime = 0;
                endTime = 0;
                currentPage = 1;
                loadPage(true, false);
            }
        });
        filterPopWindow.showAsDropDown(textViewFilter);
        bgView.setVisibility(View.VISIBLE);
        ObjectAnimator inAnimator = ObjectAnimator.ofFloat(bgView, "alpha", 0f, 1f);
        inAnimator.setDuration(300);
        inAnimator.start();
        filterPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgView.setVisibility(View.VISIBLE);
                ObjectAnimator outAnimator = ObjectAnimator.ofFloat(bgView, "alpha", bgView.getAlpha(), 0f);
                outAnimator.setDuration(300);
                outAnimator.start();
                outAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bgView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            if (isFirst) {
                isFirst = false;
                currentPage = 1;
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

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }


}
