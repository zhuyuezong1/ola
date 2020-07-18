package com.kasa.ola.ui;

import android.content.Intent;
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
import com.kasa.ola.bean.entity.AppointmentBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.AppointmentAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentDetailsActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_appointment)
    LoadMoreRecyclerView rvAppointment;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    private int currentPage = 1;
    private List<AppointmentBean> appointmentBeans = new ArrayList<>();
    private AppointmentAdapter appointmentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);
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
        jsonObject.put("pageSize", Const.PAGE_SIZE * 4);
        ApiManager.get().getData(Const.GET_RESERVATION_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("reservationList");
                    if (!isLoadMore) {
                        appointmentBeans.clear();
                        appointmentAdapter.notifyDataSetChanged();
                    }
                    List<AppointmentBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<AppointmentBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        appointmentBeans.addAll(list);
                        appointmentAdapter.notifyDataSetChanged();
                        rvAppointment.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            rvAppointment.setVisibility(View.VISIBLE);
                        } else {
                            rvAppointment.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(AppointmentDetailsActivity.this,msg);
            }
        }, null);
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AppointmentDetailsActivity.this);
        rvAppointment.setLayoutManager(linearLayoutManager);
        rvAppointment.setLoadingListener(this);
        rvAppointment.setLoadingMoreEnabled(true);
        appointmentAdapter = new AppointmentAdapter(AppointmentDetailsActivity.this, appointmentBeans);
        appointmentAdapter.setOnAppointmentListListener(new AppointmentAdapter.OnAppointmentListListener() {
            @Override
            public void onCancel(final int position) {
                final String classID = appointmentBeans.get(position).getClassID();
                CommonDialog.Builder builder = new CommonDialog.Builder(AppointmentDetailsActivity.this);
                builder.setMessage(AppointmentDetailsActivity.this.getString(R.string.cancel_appointment_dialog_content))
                        .setLeftButton(getString(R.string.cancel))
                        .setRightButton(getString(R.string.confirm))
                        .setDialogInterface(new CommonDialog.DialogInterface() {

                            @Override
                            public void leftButtonClick(CommonDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void rightButtonClick(CommonDialog dialog) {
                                cancel(classID,appointmentBeans.get(position).getSchoolID());
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

            }

            @Override
            public void onComment(int position) {
                Intent intent = new Intent(AppointmentDetailsActivity.this, PublishActivity.class);
//                intent.putExtra(Const.COMMENT_TYPE,1);
                intent.putExtra(Const.PUBLISH_BEAN_TAG, appointmentBeans.get(position));
                startActivityForResult(intent,Const.CLASS_COMMENT_BACK);
            }
        });
        rvAppointment.setAdapter(appointmentAdapter);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
    }

    private void cancel(String classID, String schoolID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("classID", classID);
        jsonObject.put("schoolID", schoolID);
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("type", "0");
        ApiManager.get().getData(Const.UPDATE_RESERVATION_STATE_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showLongToast(AppointmentDetailsActivity.this, responseModel.resultCodeDetail);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(AppointmentDetailsActivity.this, msg);
            }
        }, null);
    }

    private void initTitle() {
        setActionBar(getString(R.string.appointment_details_title), "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            currentPage = 1;
            loadPage(true);
        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false,true);
    }
}
