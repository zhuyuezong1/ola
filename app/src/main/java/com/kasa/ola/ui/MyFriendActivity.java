package com.kasa.ola.ui;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import com.kasa.ola.bean.entity.MemberBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.FriendAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFriendActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_my_friends)
    LoadMoreRecyclerView rvMyFriends;
    @BindView(R.id.tv_invite_right_now)
    TextView tvInviteRightNow;
    @BindView(R.id.sl_layout)
    SwipeRefreshLayout slLayout;
    @BindView(R.id.tv_total_num)
    TextView tvTotalNum;
    @BindView(R.id.tv_customer_service)
    TextView tvCustomerService;
    private int currentPage = 1;
    private List<MemberBean> friends = new ArrayList<>();
    private FriendAdapter friendAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);
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
        jsonObject.put("memberLvl", "1");
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", 30);
        ApiManager.get().getData(Const.GET_MY_MEMBER_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    if (!isLoadMore) {
                        friends.clear();
                    }
                    JSONObject jo = (JSONObject) responseModel.data;
                    String memberNo = "";
                    memberNo = getString(R.string.one_level_member, jo.optString("totalMember"));
                    tvTotalNum.setText(memberNo);
                    String jsonData = jo.optString("list");
                    List<MemberBean> list = new Gson().fromJson(jsonData, new TypeToken<List<MemberBean>>() {
                    }.getType());
                    if (null != list) {
                        friends.addAll(list);
                        friendAdapter.notifyDataSetChanged();
                        rvMyFriends.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                        if (!isLoadMore) {
                            if (list.size() == 0) {
                                rvMyFriends.setVisibility(View.GONE);
                            } else {
                                rvMyFriends.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        rvMyFriends.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slLayout.setRefreshing(false);
                ToastUtils.showShortToast(MyFriendActivity.this, msg);
            }
        }, null);
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMyFriends.setLayoutManager(linearLayoutManager);
        rvMyFriends.setLoadingMoreEnabled(false);
        rvMyFriends.setLoadingListener(this);
        tvCustomerService.setOnClickListener(this);
        friendAdapter = new FriendAdapter(MyFriendActivity.this, friends);
        rvMyFriends.setAdapter(friendAdapter);
        tvInviteRightNow.setOnClickListener(this);
        slLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
    }

    private void initTitle() {
        setActionBar(getString(R.string.my_friends), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_invite_right_now:
                Intent intent = new Intent(MyFriendActivity.this, MyQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_customer_service:
                showTelephony(LoginHandler.get().getBaseUrlBean().getServiceMobile());
                break;
        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
    private void showTelephony(final String tel) {
        CommonDialog.Builder builder = new CommonDialog.Builder(this);
        builder.setMessage(tel)
                .setLeftButton(getString(R.string.cancel))
                .setRightButton(getString(R.string.go_telephony))
                .setDialogInterface(new CommonDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                        requestPermissions(MyFriendActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                new RequestPermissionCallBack() {
                                    @Override
                                    public void granted() {
                                        try {
                                            Uri uri = Uri.parse("tel:" + tel);
                                            Intent telIntent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(telIntent);
                                        } catch (ActivityNotFoundException a) {
                                            ToastUtils.showShortToast(MyFriendActivity.this, getString(R.string.tel_confirm));
                                        }
                                    }

                                    @Override
                                    public void denied() {
                                        ToastUtils.showShortToast(MyFriendActivity.this, "获取权限失败，正常功能受到影响");
                                    }

                                });

                    }
                })
                .create()
                .show();
    }
}
