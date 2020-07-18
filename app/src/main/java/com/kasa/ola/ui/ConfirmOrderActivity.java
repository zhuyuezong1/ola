package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.AddressBean;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.bean.entity.CommitOrderProductBean;
import com.kasa.ola.bean.entity.ConfirmOrderBean;
import com.kasa.ola.bean.entity.ConfirmOrderInfoBean;
import com.kasa.ola.bean.entity.SelfMentionPointBean;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.pop.PayPopupWindow;
import com.kasa.ola.pop.SelectSelfPointPopup;
import com.kasa.ola.ui.adapter.ConfirmOrderAdapter;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.ui.popwindow.PayPop;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import razerdp.basepopup.BasePopupWindow;

public class ConfirmOrderActivity extends BaseActivity implements View.OnClickListener/*, WechatPayManager.OnWechatPayListener*/ {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rb_address)
    RadioButton rbAddress;
    @BindView(R.id.rb_self_mention)
    RadioButton rbSelfMention;
    @BindView(R.id.rg_confirm_order)
    RadioGroup rgConfirmOrder;
    @BindView(R.id.textView_address)
    TextView textViewAddress;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_addr)
    TextView tvAddr;
    @BindView(R.id.view_address)
    LinearLayout viewAddress;
    @BindView(R.id.fl_address)
    FrameLayout flAddress;
    @BindView(R.id.textView_self_mention)
    TextView textViewSelfMention;
    @BindView(R.id.tv_self_mention_name)
    TextView tvSelfMentionName;
    @BindView(R.id.tv_self_mention_addr)
    TextView tvSelfMentionAddr;
    @BindView(R.id.view_self_mention)
    LinearLayout viewSelfMention;
    @BindView(R.id.fl_self_mention)
    FrameLayout flSelfMention;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.ll_address)
    RelativeLayout llAddress;
    @BindView(R.id.rv_product_list)
    RecyclerView rvProductList;
    @BindView(R.id.tv_notice_tips)
    TextView tvNoticeTips;
    @BindView(R.id.tv_total_num)
    TextView tvTotalNum;
    @BindView(R.id.title_total)
    TextView titleTotal;
    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.btn_strike)
    Button btnStrike;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    //    private ProductBean product;
    private AddressBean addressBean;
    private List<CartBean.ShoppingCartBean> list;
    private ConfirmOrderAdapter confirmOrderAdapter;
    private ArrayList<CommitOrderProductBean> productList;
    private double total = 0;//商品总价
    private long totalNum = 0;//商品总件数
    private int num = 0;
    private ArrayList<String> orderNoList;
    private int commitType;
//    private ArrayList<PayTypeModel> payTypeList = new ArrayList<>();
    private int payType = 1;
    private PayPop payPop;
    private String addressID = "";
    private ArrayList<ConfirmOrderBean> confirmOrderBeans = new ArrayList<>();

    private List<ConfirmOrderInfoBean.CommitInfoListBean> commitInfoListBeans = new ArrayList<>();
    private String name = "";
    private String mobile = "";
    private PasswordPopupWin pwdPop;
    private PayMsgModel payMsgModel;
    private static final int SDK_PAY_FLAG = 1;
//    private AlipayHandler alipayHandler = new AlipayHandler(this);
    private String rebates;
    private String takeID = "";
    private boolean isSelf = false;
    private boolean isCommited = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        rgConfirmOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_address:
                        isSelf = false;
                        flAddress.setVisibility(View.VISIBLE);
                        flSelfMention.setVisibility(View.GONE);
                        break;
                    case R.id.rb_self_mention:
                        isSelf = true;
                        flAddress.setVisibility(View.GONE);
                        flSelfMention.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(takeID)){
                            textViewSelfMention.setVisibility(View.VISIBLE);
                            viewSelfMention.setVisibility(View.GONE);
                        }else {
                            textViewSelfMention.setVisibility(View.GONE);
                            viewSelfMention.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
        });
    }

    private void initView() {
        setActionBar(getString(R.string.commit_order), "");
        Intent intent = getIntent();
        list = (ArrayList<CartBean.ShoppingCartBean>) intent.getSerializableExtra(Const.PRODUCT_LIST_KEY);
        commitType = intent.getIntExtra(Const.COMMIT_ORDER_ENTER_TYPE, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProductList.setLayoutManager(linearLayoutManager);
        confirmOrderAdapter = new ConfirmOrderAdapter(this, commitInfoListBeans);
        rvProductList.setAdapter(confirmOrderAdapter);
        productList = new ArrayList<>();

        confirmOrderBeans.clear();
        List<ConfirmOrderBean> confirmDataRequest = getConfirmDataRequest(list);
        confirmOrderBeans.addAll(confirmDataRequest);

        llAddress.setOnClickListener(this);
        btnStrike.setOnClickListener(this);
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
//                getDefaultAddr();
            }
        });
//        getDefaultAddr();
        loadData();

//        showSelfMentionPointView();
    }

//    private void showSelfMentionPointView() {
//        switchIsSelfMentionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    isSelf = true;
//                    Intent selectSelfMentionPointIntent = new Intent(ConfirmOrderActivity.this, SelectSelfMentionPointActivity.class);
//                    startActivityForResult(selectSelfMentionPointIntent, Const.SELECT_SELF_MENTION_POINT);
//                } else {
//                    isSelf = false;
//                }
//            }
//        });
//        llSelfMentionPoint.setOnClickListener(this);
//    }

    private List<ConfirmOrderBean> getConfirmDataRequest(List<CartBean.ShoppingCartBean> list) {
        ArrayList<ConfirmOrderBean> confirmOrderBeans = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ConfirmOrderBean confirmOrderBean = new ConfirmOrderBean();
                CartBean.ShoppingCartBean shoppingCartBean = list.get(i);
                confirmOrderBean.setSuppliersID(shoppingCartBean.getSuppliersID());
                ArrayList<ConfirmOrderBean.ConfirmProductBean> confirmProductBeans = new ArrayList<>();
                List<CartBean.Product> productList = shoppingCartBean.getProductList();
                for (int j = 0; j < productList.size(); j++) {
                    ConfirmOrderBean.ConfirmProductBean confirmProductBean = new ConfirmOrderBean.ConfirmProductBean();
                    CartBean.Product product = productList.get(j);
                    confirmProductBean.setGroupContent(product.getGroupContent());
                    confirmProductBean.setProductID(product.getProductID());
                    confirmProductBean.setProductNum(product.getProductNum());
                    confirmProductBean.setShopProductID(product.getShopProductID());
                    confirmProductBeans.add(confirmProductBean);
                }
                confirmOrderBean.setProductList(confirmProductBeans);
                confirmOrderBeans.add(confirmOrderBean);
            }
        }
        return confirmOrderBeans;
    }

    private void loadData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("addressID", addressID);
        jsonObject.put("suppliersList", confirmOrderBeans);
        ApiManager.get().getData(Const.GET_COMMIT_INFO_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    ConfirmOrderInfoBean confirmOrderInfoBean = JsonUtils.jsonToObject(jo.toString(), ConfirmOrderInfoBean.class);
                    addressBean = confirmOrderInfoBean.getAddress();
//                    name = confirmOrderInfoBean.getName();
//                    mobile = confirmOrderInfoBean.getMobile();
                    showAddrView();
                    commitInfoListBeans.clear();
                    List<ConfirmOrderInfoBean.CommitInfoListBean> commitInfoList = confirmOrderInfoBean.getCommitInfoList();
                    commitInfoListBeans.addAll(commitInfoList);
                    confirmOrderAdapter.notifyDataSetChanged();
                    total = 0;
                    totalNum = 0;
                    if (commitInfoList != null && commitInfoList.size() > 0) {
                        for (int i = 0; i < commitInfoList.size(); i++) {
                            total += Double.parseDouble(commitInfoList.get(i).getTotalPrice());
                            totalNum += Long.parseLong(commitInfoList.get(i).getTotalNum());
                        }
                    }
                    tvNoticeTips.setText(getString(R.string.confirm_order_notice_tip,confirmOrderInfoBean.getRebatesAll()));
                    tvTotalNum.setText(getString(R.string.total_product_num_new, totalNum + ""));
                    totalPrice.setText("￥" + CommonUtils.getTwoZero(CommonUtils.getTwoPoint(total)));
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(ConfirmOrderActivity.this, msg);
            }
        }, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_address:
                if (isSelf){
                    SelectSelfPointPopup selectSelfPointPopup = new SelectSelfPointPopup(ConfirmOrderActivity.this, new SelectSelfPointPopup.OnSelfItemClickListener() {
                        @Override
                        public void onItemClick(SelfMentionPointBean selfMentionPointBean) {
                            textViewSelfMention.setVisibility(View.GONE);
                            viewSelfMention.setVisibility(View.VISIBLE);
                            tvSelfMentionName.setText(selfMentionPointBean.getName());
                            if (!TextUtils.isEmpty(selfMentionPointBean.getProvince())&&!TextUtils.isEmpty(selfMentionPointBean.getCity())&&!TextUtils.isEmpty(selfMentionPointBean.getArea())&&!TextUtils.isEmpty(selfMentionPointBean.getAddressDetail())) {
                                tvSelfMentionAddr.setText(selfMentionPointBean.getProvince()+selfMentionPointBean.getCity()+selfMentionPointBean.getArea()+selfMentionPointBean.getAddressDetail());
                                takeID = selfMentionPointBean.getTakeID();
                            }
                        }
                    });
                    selectSelfPointPopup.showPopupWindow();
                }else {
                    Intent intent = new Intent(this, MyAddressManagerActivity.class);
                    intent.putExtra(Const.ADDRESS_CHOOSE_KEY, true);
                    startActivityForResult(intent, Const.ADDRESS_CHOOSE_REQUEST_KEY);
                }
                break;
            case R.id.btn_strike:
                onSubmit();
                break;

        }
    }
    private void showPayPopup(PayMsgModel payMsgModel) {

        PayPopupWindow payPopup = new PayPopupWindow(ConfirmOrderActivity.this, new PayPopupWindow.PayPopListener() {
            @Override
            public void onPayResult(int payTypeNum, String totalMoney) {
                payType = payTypeNum;
                payResult(payTypeNum,totalMoney);
            }
        }, new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                finish();
            }
        }, totalNum, payMsgModel,rvProductList);
        payPopup.showPopupWindow();

    }
    private void payResult(int payTypeNum, String totalMoney) {
        Intent intent = new Intent(ConfirmOrderActivity.this, PaySuccessResultActivity.class);
        intent.putExtra(Const.PAY_VALUE,totalMoney);
        intent.putExtra(Const.ORDER_ENTER_TYPE,3);
        startActivity(intent);
    }

    public void onSubmit() {
//        if (CommonUtils.checkCertification(this)) {
        if ((null == addressBean || TextUtils.isEmpty(addressBean.getProvince()) || TextUtils.isEmpty(addressBean.getCity()) || TextUtils.isEmpty(addressBean.getArea())) && TextUtils.isEmpty(takeID)) {
            ToastUtils.showShortToast(ConfirmOrderActivity.this, getString(R.string.please_inputAddress_or_self_mention_point_address));
            return;
        }
        if (isCommited){
            showPayPopup(payMsgModel);
        }else {
            commitOrder();
        }
//        }
    }

    private void commitOrder() {
        List<ConfirmOrderBean> submitData = getSubmitData(commitInfoListBeans);
        confirmOrderBeans.clear();
        confirmOrderBeans.addAll(submitData);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        if (isSelf) {
            jsonObject.put("takeID", takeID);
        } else {
            jsonObject.put("addressID", addressBean == null ? "" : addressBean.getAddressID());
        }
        jsonObject.put("commitType", commitType);
        jsonObject.put("suppliersList", confirmOrderBeans);
        ApiManager.get().getData(Const.COMMIT_ORDER_INFO_TAG, jsonObject,
                new BusinessBackListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
//                        ToastUtils.showShortToast(ConfirmOrderActivity.this, responseModel.resultCodeDetail);
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
//                            String jsonData = ((JSONObject) responseModel.data).optString("orderNo");
                            JSONArray ja = ((JSONObject) responseModel.data).optJSONArray("orderNoList");
                            orderNoList = new ArrayList<>();
                            if (ja != null && ja.length() > 0) {
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject s = ja.optJSONObject(i);
                                    String orderNO = s.optString("orderNo");
                                    orderNoList.add(orderNO);
                                }
                            }
                            isCommited = true;
//                            Intent intent = new Intent(ConfirmOrderActivity.this, PayActivity.class);
                            payMsgModel = new PayMsgModel();
                            payMsgModel.totalPrice = totalPrice.getText().toString().replace("￥", "");
                            payMsgModel.orderList = orderNoList;
                            showPayPopup(payMsgModel);

//                            showPayPop(payMsgModel);

//                            intent.putExtra(Const.PAY_MSG_KEY, payMsgModel);
//                            startActivity(intent);
//                            finish();
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(ConfirmOrderActivity.this, msg);
                    }
                }, new LoadingDialog.Builder(this).setMessage(getString(R.string.submitting_tips)).create());
    }

    private List<ConfirmOrderBean> getSubmitData(List<ConfirmOrderInfoBean.CommitInfoListBean> list) {
        List<ConfirmOrderBean> objects = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ConfirmOrderBean confirmOrderBean = new ConfirmOrderBean();
                ConfirmOrderInfoBean.CommitInfoListBean commitInfoListBean = list.get(i);
                confirmOrderBean.setSuppliersID(commitInfoListBean.getSuppliersID());
                confirmOrderBean.setCouponsID(commitInfoListBean.getCoupons());
                confirmOrderBean.setRedPackID(commitInfoListBean.getRedPack());
                confirmOrderBean.setRemark(commitInfoListBean.getRemark());
                ArrayList<ConfirmOrderBean.ConfirmProductBean> productList = new ArrayList<>();
                List<ConfirmOrderInfoBean.CommitInfoListBean.ProductListBean> backProductList = commitInfoListBean.getProductList();
                for (int j = 0; j < backProductList.size(); j++) {
                    ConfirmOrderBean.ConfirmProductBean confirmProductBean = new ConfirmOrderBean.ConfirmProductBean();
                    ConfirmOrderInfoBean.CommitInfoListBean.ProductListBean productListBean = backProductList.get(j);
                    confirmProductBean.setProductNum(productListBean.getProductNum());
                    confirmProductBean.setProductID(productListBean.getProductID());
                    confirmProductBean.setGroupContent(productListBean.getGroupContent());
                    confirmProductBean.setShopProductID(productListBean.getShopProductID());
                    productList.add(confirmProductBean);
                }
                confirmOrderBean.setProductList(productList);
                objects.add(confirmOrderBean);
            }
        }
        return objects;
    }

//    private void getDefaultAddr() {
//        ApiManager.get().getData(Const.GET_DEFAULT_ADDRESS, new JSONObject().put("userID", LoginHandler.get().getUserId()),
//                new BusinessBackListener() {
//                    @Override
//                    public void onBusinessSuccess(BaseResponseModel responseModel) {
//                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
//                            JSONObject jo = (JSONObject) responseModel.data;
//                            if (jo.length() != 0) {
//                                addressBean = JsonUtils.jsonToObject(jo.toString(), AddressBean.class);
//                            }
//                            showAddrView();
//                        }
//                    }
//
//                    @Override
//                    public void onBusinessError(int code, String msg) {
//                        ToastUtils.showShortToast(ConfirmOrderActivity.this, msg);
//                    }
//                }, loadingView);
//    }

    private void showAddrView() {
        if (null == addressBean) {
            textViewAddress.setVisibility(View.VISIBLE);
            viewAddress.setVisibility(View.GONE);
        } else {
            textViewAddress.setVisibility(View.GONE);
            viewAddress.setVisibility(View.VISIBLE);
            tvName.setText(TextUtils.isEmpty(addressBean.getName()) ? "" : addressBean.getName());
            tvTel.setText(TextUtils.isEmpty(addressBean.getMobile()) ? "" : addressBean.getMobile());
            tvAddr.setText(String.format("%s%s%s%s", TextUtils.isEmpty(addressBean.getProvince()) ? "" : addressBean.getProvince(), TextUtils.isEmpty(addressBean.getCity()) ? "" : addressBean.getCity(), TextUtils.isEmpty(addressBean.getArea()) ? "" : addressBean.getArea(), TextUtils.isEmpty(addressBean.getAddressDetail()) ? "" : addressBean.getAddressDetail()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.ADDRESS_CHOOSE_REQUEST_KEY) {
                addressBean = (AddressBean) data.getSerializableExtra(Const.BACK_ADDR_MODEL_KEY);
                addressID = addressBean.getAddressID();
                showAddrView();
                loadData();
            } /*else if (requestCode == Const.SELECT_SELF_MENTION_POINT) {
                SelfMentionPointBean selfMentionPointBean = (SelfMentionPointBean) data.getSerializableExtra(Const.BACK_SELF_MOTENT_POINT_KEY);
//                ToastUtils.showLongToast(ConfirmOrderActivity.this,"填入自提点地址，并给takeID赋值");
                if (selfMentionPointBean != null) {
                    tvSelfMentionPointAddress.setText(selfMentionPointBean.getProvince() + selfMentionPointBean.getCity() + selfMentionPointBean.getArea() + selfMentionPointBean.getAddressDetail());
                    takeID = selfMentionPointBean.getTakeID();
                }
            }*/
        }
    }
//
//    @Override
//    public void onWechatPayRequestError() {
//
//    }
//
//    @Override
//    public void onWechatPayRequestSuccess() {
//
//    }
//
//    @Override
//    public void onWechatPayError(int code, String msg) {
////        payResult(0, "");
//        payResult(0, payType);
//    }
//
//    @Override
//    public void onWechatPaySuccess() {
////        payResult(1, rebates);
//        payResult(1, payType);
//    }

//    private class AlipayHandler extends Handler {
//
//        private final WeakReference<ConfirmOrderActivity> mMallPayActivity;
//
//        private AlipayHandler(ConfirmOrderActivity t) {
//            mMallPayActivity = new WeakReference<>(t);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            ConfirmOrderActivity t = mMallPayActivity.get();
//            if (null != t) {
//                if (msg.what == SDK_PAY_FLAG) {
//                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//                    /**
//                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//                     */
//                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                    String resultStatus = payResult.getResultStatus();
//                    // 判断resultStatus 为9000则代表支付成功
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        ToastUtils.showShortToast(t, t.getString(R.string.pay_succeed));
////                        payResult(1, rebates);
//                        payResult(1, payType);
//                        t.finish();
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        ToastUtils.showShortToast(t, t.getString(R.string.pay_failed));
////                        payResult(0, "");
//                        payResult(0,payType);
//                    }
//                    ApiManager.get().getMyInfo(null);
//                }
//            }
//        }
//    }
}
