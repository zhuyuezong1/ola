package com.kasa.ola.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CommissionDetailsBean;
import com.kasa.ola.bean.entity.CostDetailsBean;
import com.kasa.ola.bean.entity.VacanciesDetailsBean;
import com.kasa.ola.dialog.CommissionToBalanceDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.CommissionDetailsAdapter;
import com.kasa.ola.ui.adapter.CostDetailsAdapter;
import com.kasa.ola.ui.adapter.VacanciesDetailsAdapter;
import com.kasa.ola.ui.popwindow.FilterPopWindow;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends BaseActivity implements View.OnClickListener,LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.textView_filter)
    TextView textViewFilter;
    @BindView(R.id.crowd_recycle_view)
    LoadMoreRecyclerView crowdRecycleView;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.view_no_result)
    LinearLayout viewNoResult;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.bg_view)
    View bgView;
    @BindView(R.id.crowd_refresh_layout)
    SwipeRefreshLayout crowdRefreshLayout;
    private String type="";//0是佣金明细，1是余额明细，2是消费明细
    private long startTime = 0;
    private long endTime = 0;
    private int currentPage = 1;
    private ArrayList<CommissionDetailsBean> commissionDetailsList = new ArrayList<>();
    private ArrayList<VacanciesDetailsBean> vacanciesDetailsList = new ArrayList<>();
    private ArrayList<CostDetailsBean> costDetailsList = new ArrayList<>();
    private CommissionDetailsAdapter commissionDetailsAdapter;
    private VacanciesDetailsAdapter vacanciesDetailsAdapter;
    private CostDetailsAdapter costDetailsAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        initView();
        loadPage(true,false);
    }

    private void initView() {
        type = getIntent().getStringExtra(Const.DETAIL);
        if (type.equals("0")) {
            setActionBar(getString(R.string.commission_details), "");
        } else if (type.equals("1")) {
            setActionBar(getString(R.string.vacancies_record), "");
        }else if (type.equals("2")) {
            setActionBar(getString(R.string.consume_details), "");
        }
        tvRightText.setVisibility(View.GONE);
        tvRightText.setOnClickListener(this);
        textViewFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePop();
            }
        });
        crowdRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true, false);
            }
        });
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                currentPage = 1;
                loadPage(true, false);
            }
        });
        crowdRecycleView.setLayoutManager(new LinearLayoutManager(this));
        crowdRecycleView.setLoadingListener(this);
        if (type.equals("0")){
            commissionDetailsAdapter = new CommissionDetailsAdapter(this, commissionDetailsList);
            crowdRecycleView.setAdapter(commissionDetailsAdapter);
        } else if (type.equals("1")) {
            vacanciesDetailsAdapter = new VacanciesDetailsAdapter(this, vacanciesDetailsList);
            crowdRecycleView.setAdapter(vacanciesDetailsAdapter);
        }else if (type.equals("2"))  {
            costDetailsAdapter = new CostDetailsAdapter(this, costDetailsList);
            crowdRecycleView.setAdapter(costDetailsAdapter);
        }
//        else if (type.equals("2")|| type.equals("4")) {
//            distributeDetailsAdapter = new DistributeDetailsAdapter(this, distributeDetailsList);
//            crowdRecycleView.setAdapter(distributeDetailsAdapter);
//        }else if (type.equals("3"))  {
//            costDetailsAdapter = new CostDetailsAdapter(this, costDetailsList);
//            crowdRecycleView.setAdapter(costDetailsAdapter);
//        }

    }
    private void loadPage(boolean isFirst, final boolean isLoadMore, String startTime, String endTime) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("pageNum", currentPage);
        jo.put("pageSize", Const.PAGE_SIZE * 4);
        if (null != startTime && null != endTime) {
            jo.put("startTime", startTime);
            jo.put("endTime", endTime);
        }
//        jo.put("type", type);
        ApiManager.get().getData(getTag(type), jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    crowdRefreshLayout.setRefreshing(false);
                    JSONObject jo = (JSONObject) responseModel.data;
                    String jsonData = jo.optString("list");
                    if (type.equals("0")){
                        if (!isLoadMore) {
                            commissionDetailsList.clear();
                            commissionDetailsAdapter.notifyDataSetChanged();
                        }
                        List<CommissionDetailsBean> list = new Gson().fromJson(jsonData, new TypeToken<List<CommissionDetailsBean>>() {
                        }.getType());
                        if (list!=null && list.size()>0) {
                            commissionDetailsList.addAll(list);
                            commissionDetailsAdapter.notifyDataSetChanged();
                            crowdRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                        }
                        if (!isLoadMore) {
                            if (list!=null&& list.size() > 0) {
                                viewNoResult.setVisibility(View.GONE);
                                crowdRecycleView.setVisibility(View.VISIBLE);
                            }else {
                                viewNoResult.setVisibility(View.VISIBLE);
                                crowdRecycleView.setVisibility(View.GONE);
                            }
                        }
                    }else if (type.equals("1")){
                        if (!isLoadMore) {
                            vacanciesDetailsList.clear();
                            vacanciesDetailsAdapter.notifyDataSetChanged();
                        }
                        List<VacanciesDetailsBean> list = new Gson().fromJson(jsonData, new TypeToken<List<VacanciesDetailsBean>>() {
                        }.getType());
                        if (list!=null && list.size()>0) {
                            vacanciesDetailsList.addAll(list);
                            vacanciesDetailsAdapter.notifyDataSetChanged();
                            crowdRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                        }
                        if (!isLoadMore) {
                            if (list!=null&& list.size() > 0) {
                                viewNoResult.setVisibility(View.GONE);
                                crowdRecycleView.setVisibility(View.VISIBLE);
                            }else {
                                viewNoResult.setVisibility(View.VISIBLE);
                                crowdRecycleView.setVisibility(View.GONE);
                            }
                        }
                    }else if ( type.equals("2")){
                        if (!isLoadMore) {
                            costDetailsList.clear();
                            costDetailsAdapter.notifyDataSetChanged();
                        }
                        List<CostDetailsBean> list = new Gson().fromJson(jsonData, new TypeToken<List<CostDetailsBean>>() {
                        }.getType());
                        if (list!=null && list.size()>0) {
                            costDetailsList.addAll(list);
                            costDetailsAdapter.notifyDataSetChanged();
                            crowdRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                        }
                        if (!isLoadMore) {
                            if (list!=null&& list.size() > 0) {
                                viewNoResult.setVisibility(View.GONE);
                                crowdRecycleView.setVisibility(View.VISIBLE);
                            }else {
                                viewNoResult.setVisibility(View.VISIBLE);
                                crowdRecycleView.setVisibility(View.GONE);
                            }
                        }
                    }
//                    else if ( type.equals("2")||type.equals("4")){
//                        if (!isLoadMore) {
//                            distributeDetailsList.clear();
//                        }
//                        List<DistributeDetailsBean> list = new Gson().fromJson(jsonData, new TypeToken<List<DistributeDetailsBean>>() {
//                        }.getType());
//                        if (list!=null && list.size()>0) {
//                            distributeDetailsList.addAll(list);
//                            distributeDetailsAdapter.notifyDataSetChanged();
//                            crowdRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
//                        }
//                        if (!isLoadMore) {
//                            if (list!=null&& list.size() > 0) {
//                                viewNoResult.setVisibility(View.GONE);
//                                crowdRecycleView.setVisibility(View.VISIBLE);
//                            }else {
//                                viewNoResult.setVisibility(View.VISIBLE);
//                                crowdRecycleView.setVisibility(View.GONE);
//                            }
//                        }
//                    }else if ( type.equals("3")){
//                        if (!isLoadMore) {
//                            costDetailsList.clear();
//                        }
//                        List<CostDetailsBean> list = new Gson().fromJson(jsonData, new TypeToken<List<CostDetailsBean>>() {
//                        }.getType());
//                        if (list!=null && list.size()>0) {
//                            costDetailsList.addAll(list);
//                            costDetailsAdapter.notifyDataSetChanged();
//                            crowdRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
//                        }
//                        if (!isLoadMore) {
//                            if (list!=null&& list.size() > 0) {
//                                viewNoResult.setVisibility(View.GONE);
//                                crowdRecycleView.setVisibility(View.VISIBLE);
//                            }else {
//                                viewNoResult.setVisibility(View.VISIBLE);
//                                crowdRecycleView.setVisibility(View.GONE);
//                            }
//                        }
//                    }



                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                crowdRefreshLayout.setRefreshing(false);
                ToastUtils.showShortToast(DetailsActivity.this, msg);
            }
        }, isFirst ? loadingView : null);
    }
    private void loadPage(boolean isFirst, boolean isLoadMore) {
        if (startTime != 0 && endTime != 0) {
            loadPage(isFirst, isLoadMore, startTime + "", endTime + "");
        } else {
            loadPage(isFirst, isLoadMore, null, null);
        }
    }
    private String getTag(String type){
        if (type.equals("0")) {
            return Const.GET_COMMISSION_DETAIL;
        } else if (type.equals("1")) {
            return Const.GET_BALANCE_DETAIL;
        }else if (type.equals("2")) {
            return Const.GET_COST_DETAIL;
        } /*else if (type.equals("2")) {
            return Const.GET_YF_DETAIL;
        }else if (type.equals("3")) {
            return Const.GET_COST_DETAIL;
        }else if (type.equals("4")) {
            return Const.GET_QIQUAN_DETAIL;
        }*/
        return "";
    }
    private void showDatePop() {
        FilterPopWindow filterPopWindow = new FilterPopWindow(DetailsActivity.this, startTime, endTime);
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
                type = "0";
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
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.tv_right_text:
//                CommissionToBalanceDialog.Builder builder = new CommissionToBalanceDialog.Builder(this);
//                builder.setLeftButton(this.getString(R.string.cancel))
//                        .setRightButton(this.getString(R.string.confirm))
//                        .setLimit(LoginHandler.get().getMyInfo().optString("commission"))
//                        .setDialogInterface(new CommissionToBalanceDialog.DialogInterface() {
//
//                            @Override
//                            public void leftButtonClick(CommissionToBalanceDialog dialog) {
//                                dialog.dismiss();
//                            }
//
//                            @Override
//                            public void rightButtonClick(CommissionToBalanceDialog dialog, double num) {
//                                dialog.dismiss();
//                                commissionToBalance(num);
//                            }
//                        })
//                        .create()
//                        .show();
//                break;
        }
    }

//    private void commissionToBalance(double num) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userID",LoginHandler.get().getUserId());
//        jsonObject.put("money",num);
//        ApiManager.get().getData(Const.COMMISSION_TO_BALANCE, jsonObject, new BusinessBackListener() {
//            @Override
//            public void onBusinessSuccess(BaseResponseModel responseModel) {
//                loadPage(true,false);
//                ApiManager.get().getMyInfo(null);
//            }
//
//            @Override
//            public void onBusinessError(int code, String msg) {
//                ToastUtils.showShortToast(DetailsActivity.this,msg);
//            }
//        },null);
//    }
}
