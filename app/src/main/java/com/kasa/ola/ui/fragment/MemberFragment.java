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
import com.kasa.ola.bean.entity.MemberBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.MemberAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends BaseFragment implements LoadMoreRecyclerView.LoadingListener {
    private SwipeRefreshLayout refreshLayout;
    private LoadingView loadingView;
    private LoadMoreRecyclerView memberView;
    private View viewNoResult;
    private TextView tvTotalMember;
    private ArrayList<MemberBean> memberList = new ArrayList<>();
    private MemberAdapter memberAdapter;

    private int type = 1;

    private boolean isFirst = true;
    private boolean isVisibleToUser = false;
    private boolean isInitView = false;
    private int currentPage = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(Const.MEMBER_STATUS_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_my_member_layout, container, false);
        refreshLayout = rootView.findViewById(R.id.member_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        loadingView = rootView.findViewById(R.id.loading_view);
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        viewNoResult = rootView.findViewById(R.id.view_no_result);
        memberView = rootView.findViewById(R.id.member_recycle_view);
        memberView.setLayoutManager(new LinearLayoutManager(getContext()));
        memberView.setLoadingListener(this);
        memberAdapter = new MemberAdapter(getContext(), memberList);
        memberView.setAdapter(memberAdapter);

        View headView = inflater.from(getContext()).inflate(R.layout.view_my_member_head, memberView, false);
        tvTotalMember = headView.findViewById(R.id.tv_total_member);
        memberView.addHeaderView(headView);

        isInitView = true;
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            if (isFirst) {
                isFirst = false;
                currentPage=1;
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
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("pageNum", currentPage);
        jo.put("pageSize", Const.PAGE_SIZE * 2);
        jo.put("memberLvl", type);
        ApiManager.get().getData(Const.GET_MY_MEMBER_LIST, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != getActivity()) {
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        refreshLayout.setRefreshing(false);
                        if (!isLoadMore) {
                            memberList.clear();
                        }
                        JSONObject jo = (JSONObject) responseModel.data;
                        String memberNo = "";
                        if (type == 1) {
                            memberNo = getString(R.string.one_level_member, jo.optString("totalMember"));
                        } else if (type == 2) {
                            memberNo = getString(R.string.two_level_member, jo.optString("totalMember"));
                        } /*else if (type == 3) {
                            memberNo = getString(R.string.three_level_member, jo.optString("totalSize"));
                        }*/
                        tvTotalMember.setText(memberNo);
                        String jsonData = jo.optString("list");
                        List<MemberBean> list = new Gson().fromJson(jsonData, new TypeToken<List<MemberBean>>() {
                        }.getType());
                        if (null != list) {
                            memberList.addAll(list);
                            memberAdapter.notifyDataSetChanged();
                            memberView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                            if (!isLoadMore) {
                                if (list.size() == 0) {
                                    viewNoResult.setVisibility(View.VISIBLE);
                                    memberView.setVisibility(View.GONE);
                                } else {
                                    viewNoResult.setVisibility(View.GONE);
                                    memberView.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            viewNoResult.setVisibility(View.VISIBLE);
                            memberView.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                refreshLayout.setRefreshing(false);
                ToastUtils.showShortToast(getContext(), msg);
            }
        }, isFirst ? loadingView : null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

}
