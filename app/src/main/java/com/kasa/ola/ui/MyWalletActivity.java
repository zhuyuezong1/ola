package com.kasa.ola.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.bean.entity.DetailBean;
import com.kasa.ola.bean.entity.WithdrawBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.pop.ExchangePopup;
import com.kasa.ola.ui.adapter.WalletDetailAdapter;
import com.kasa.ola.ui.listener.OnConfirmListener;
import com.kasa.ola.ui.popwindow.CornerBtnFilterPopWindow;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyWalletActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_details)
    LoadMoreRecyclerView rvDetails;
    @BindView(R.id.rb_commission_root)
    RadioButton rbCommissionRoot;
    @BindView(R.id.rb_valance_root)
    RadioButton rbValanceRoot;
    @BindView(R.id.rg_filter_root)
    RadioGroup rgFilterRoot;
    @BindView(R.id.tv_filter_root)
    TextView tvFilterRoot;
    @BindView(R.id.rl_filter_root)
    RelativeLayout rlFilterRoot;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.bg_view)
    View bgView;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.tv_commission_value)
    TextView tvCommissionValue;
    @BindView(R.id.btn_exchange)
    Button btnExchange;
    @BindView(R.id.tv_vacancies_value)
    TextView tvVacanciesValue;
    @BindView(R.id.btn_withdraw)
    Button btnWithdraw;
    @BindView(R.id.rb_commission)
    RadioButton rbCommission;
    @BindView(R.id.rb_valance)
    RadioButton rbValance;
    @BindView(R.id.rg_filter)
    RadioGroup rgFilter;
    @BindView(R.id.tv_filter)
    TextView tvFilter;
    @BindView(R.id.rl_filter)
    RelativeLayout rlFilter;
    @BindView(R.id.ll_filter_head)
    LinearLayout llFilterHead;
    @BindView(R.id.sv_my_wallet)
    NestedScrollView svMyWallet;
    private WalletDetailAdapter walletDetailAdapter;
    //    private Button btn_exchange;
//    private Button btn_withdraw;
//    private RelativeLayout rl_filter;
//    private TextView tv_filter;
    private long startTime = 0;
    private long endTime = 0;
    private int currentPage = 1;
//    private RadioGroup rg_filter;
    private boolean firstCheck = true;
    private boolean isRefresh = true;
    private ExchangePopup exchangePopup;
    private ExchangePopup withdrawPopup;
    private ArrayList<CardBean> cardList = new ArrayList<>();
    //    private TextView tv_commission_value;
//    private TextView tv_vacancies_value;
    private int detailType = 0;//0佣金明细，1余额明细
    private List<DetailBean> detailBeans = new ArrayList<>();
    private String vacancies;
    private String commission;
    private WithdrawBean withdrawBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        getBalanceCashDetails();
        getBankList();
        //测试数据
//        objects = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            CardBean cardBean = new CardBean();
//            cardBean.setBankName("测试银行");
//            if (1 == 0) {
//                cardBean.setIsSelected(1);
//            } else {
//                cardBean.setIsSelected(0);
//            }
//            objects.add(cardBean);
//        }
    }

    private void getBalanceCashDetails() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.BALANCE_CASH_DETAILS, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    withdrawBean = JsonUtils.jsonToObject(((JSONObject) responseModel.data).toString(), WithdrawBean.class);
                }

            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MyWalletActivity.this, msg);
            }
        }, null);
    }

    private void initData() {
        vacancies = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("balance")) ? "0.00" : CommonUtils.getTwoZero(LoginHandler.get().getMyInfo().optString("balance"));
        commission = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("commission")) ? "0.00" : CommonUtils.getTwoZero(LoginHandler.get().getMyInfo().optString("commission"));
        tvVacanciesValue.setText(getString(R.string.price, vacancies));
        tvCommissionValue.setText(getString(R.string.price, commission));
    }

    private void initEvent() {
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startTime = 0;
                endTime = 0;
                currentPage = 1;
                refreshHead();
                loadPage(true, detailType);
            }
        });
        btnExchange.setOnClickListener(this);
        btnWithdraw.setOnClickListener(this);

        rgFilterRoot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_commission_root:
                        detailType = 0;
                        if (isRefresh){
                            currentPage = 1;
                            rvDetails.setLoadingMoreEnabled(false);
                            loadPage(true, detailType);
                        }
                        if (firstCheck) {
                            firstCheck = false;
                            isRefresh = false;
                            rgFilter.check(R.id.rb_commission);
                        }else {
                            isRefresh = true;
                        }
                        break;
                    case R.id.rb_valance_root:
                        detailType = 1;
                        if (isRefresh) {
                            currentPage = 1;
                            rvDetails.setLoadingMoreEnabled(false);
                            loadPage(true, detailType);
                        }
                        if (firstCheck) {
                            firstCheck = false;
                            isRefresh = false;
                            rgFilter.check(R.id.rb_valance);
                        }else {
                            isRefresh = true;
                        }
                        break;
                }
                firstCheck = true;
                rvDetails.setLoadingMoreEnabled(true);
            }
        });
        rgFilter.check(R.id.rb_commission);
        tvFilterRoot.setOnClickListener(this);

        rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_commission:
                        detailType = 0;
                        if (isRefresh) {
                            currentPage = 1;
                            rvDetails.setLoadingMoreEnabled(false);
                            loadPage(true, detailType);
                        }
                        if (firstCheck) {
                            firstCheck = false;
                            isRefresh = false;
                            rgFilterRoot.check(R.id.rb_commission_root);
                        }else {
                            isRefresh = true;
                        }
                        break;
                    case R.id.rb_valance:
                        detailType = 1;
                        if (isRefresh) {
                            currentPage = 1;
                            rvDetails.setLoadingMoreEnabled(false);
                            loadPage(true, detailType);
                        }
                        if (firstCheck) {
                            firstCheck = false;
                            isRefresh = false;
                            rgFilterRoot.check(R.id.rb_valance_root);
                        }else {
                            isRefresh = true;
                        }
                        break;
                }
                firstCheck = true;
                rvDetails.setLoadingMoreEnabled(true);
            }
        });
        tvFilter.setOnClickListener(this);
    }

    private void initTitle() {
        setActionBar(getString(R.string.my_wallet), "");
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyWalletActivity.this);
        rvDetails.setLoadingMoreEnabled(true);
        rvDetails.setLoadingListener(this);
        rvDetails.setLayoutManager(linearLayoutManager);
        walletDetailAdapter = new WalletDetailAdapter(MyWalletActivity.this, detailBeans);
        rvDetails.setAdapter(walletDetailAdapter);
//        View headView = getLayoutInflater().from(MyWalletActivity.this).inflate(R.layout.head_my_wallet, rvDetails, false);
//        tv_commission_value = headView.findViewById(R.id.tv_commission_value);
//        tv_vacancies_value = headView.findViewById(R.id.tv_vacancies_value);
//        btn_exchange = headView.findViewById(R.id.btn_exchange);
//        btn_withdraw = headView.findViewById(R.id.btn_withdraw);
//        rg_filter = headView.findViewById(R.id.rg_filter);
//        rl_filter = headView.findViewById(R.id.rl_filter);
//        tv_filter = headView.findViewById(R.id.tv_filter);
//        rvDetails.addHeaderView(headView);


//        tv_filter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePop(rg_filter);
//            }
//        });
        svMyWallet.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int totalDy = 0;
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                totalDy += scrollY - oldScrollY;
                if (totalDy < 0) {
                    totalDy = 0;
                }
                if (totalDy < DisplayUtils.dip2px(MyWalletActivity.this, 124)) {
                    llFilter.setVisibility(View.GONE);
                } else {
                    llFilter.setVisibility(View.VISIBLE);
                }
            }
        });
        initData();
        loadPage(true,detailType);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        isRefresh = true;
        loadPage(false,detailType,true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exchange:
                exchangePopup = new ExchangePopup(MyWalletActivity.this, new OnConfirmListener() {

                    @Override
                    public void confirm(String text,String bankID) {
                        commissionToBalance(Double.parseDouble(TextUtils.isEmpty(text.trim()) ? "0" : text.trim()));
                    }
                }, Const.COMMISSION_POP,null);
                exchangePopup.showPopupWindow();
                break;
            case R.id.btn_withdraw:
                String isCertification = LoginHandler.get().getMyInfo().optString("isCertification");
                if (isCertification.equals("0")){
                    Intent intent = new Intent(MyWalletActivity.this, AuthManagerActivity.class);
                    startActivity(intent);
                }else if(isCertification.equals("1")){
                    withdrawPopup = new ExchangePopup(MyWalletActivity.this, new OnConfirmListener() {

                        @Override
                        public void confirm(String text,String bankID) {
                            submit(text,bankID);
                        }
                    }, Const.WITHDRAW_POP, cardList);
                    withdrawPopup.setWithdrawBean(withdrawBean);
                    withdrawPopup.showPopupWindow();
                }
                break;
            case R.id.tv_filter:
                showDatePop(rlFilter);
                break;
            case R.id.tv_filter_root:
                showDatePop(rlFilterRoot);
                break;
        }
    }

    private void showDatePop(View view) {
        CornerBtnFilterPopWindow filterPopWindow = new CornerBtnFilterPopWindow(MyWalletActivity.this, startTime, endTime);
        filterPopWindow.setFilterListener(new CornerBtnFilterPopWindow.FilterListener() {
            @Override
            public void confirmClick(long startTime1, long endTime1) {
                startTime = startTime1;
                endTime = endTime1;
                currentPage = 1;
                loadPage(true, detailType, false);
            }

            @Override
            public void resetClick() {
                startTime = 0;
                endTime = 0;
                currentPage = 1;
                loadPage(true, detailType, false);
            }
        });
        filterPopWindow.showAsDropDown(view);
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

    private void loadPage(boolean isFirst, int detailType) {
        loadPage(isFirst, detailType, false);
    }

    private void loadPage(boolean isFirst, int detailType, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        ApiManager.get().getData(getDataType(detailType), jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String jsonData = jo.optString("list");
                    if (!isLoadMore) {
                        detailBeans.clear();
                        walletDetailAdapter.notifyDataSetChanged();
                    }
                    List<DetailBean> list = new Gson().fromJson(jsonData, new TypeToken<List<DetailBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        detailBeans.addAll(list);
                        walletDetailAdapter.notifyDataSetChanged();
                        rvDetails.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(MyWalletActivity.this, msg);
            }
        }, null);

    }

    private String getDataType(int detailType) {
        if (detailType == 0) {
            return Const.GET_COMMISSION_DETAIL;
        } else if (detailType == 1) {
            return Const.GET_BALANCE_DETAIL;
        }
        return "";
    }
    private void getBankList() {
        ApiManager.get().getData(Const.GET_MY_BANK_CARD, new JSONObject().put("userID", LoginHandler.get().getUserId()),
                new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            String jsonData = ((JSONObject) responseModel.data).optString("list");
                            List<CardBean> list = new Gson().fromJson(jsonData, new TypeToken<List<CardBean>>() {
                            }.getType());
                            cardList.clear();
                            if (null != list && list.size()>0) {
                                cardList.addAll(list);
                                if (withdrawPopup!=null){
                                    withdrawPopup.setCardBeans(cardList);
                                }
                            }
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(MyWalletActivity.this, msg);
                    }
                }, null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Const.ADD_BANK_CARD_FOR_BACK) {
//            objects.clear();
//            for (int i = 0; i < 10; i++) {
//                CardBean cardBean = new CardBean();
//                cardBean.setBankName("测试银行222");
//                if (1 == 0) {
//                    cardBean.setIsSelected(1);
//                } else {
//                    cardBean.setIsSelected(0);
//                }
//                objects.add(cardBean);
//            }
            LogUtil.d("sssssssss","添加完成，刷新页面银行卡列表");
            getBankList();
//            withdrawPopup.refreshBankList();
        }
    }
    private void commissionToBalance(double num) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("money", num);
        ApiManager.get().getData(Const.COMMISSION_TO_BALANCE, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                refreshHead();
                loadPage(true, detailType);
                exchangePopup.dismiss();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MyWalletActivity.this, msg);
            }
        }, null);
    }

    private void refreshHead() {
        ApiManager.get().getMyInfo(new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    LoginHandler.get().saveMyInfo(jo);
                    initData();
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {

            }
        });
    }

    private void submit(String amount,String bankID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("amount", amount);
        jsonObject.put("bankID", bankID);
        ApiManager.get().getData(Const.BALANCE_CASH, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showLongToast(MyWalletActivity.this, getString(R.string.withdraw_apply_success_tips));
                refreshHead();
                loadPage(true, detailType);
                withdrawPopup.dismiss();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(MyWalletActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.loading_tips)).create());
    }
}
