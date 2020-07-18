package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alipay.sdk.app.PayTask;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.PayResult;
import com.kasa.ola.bean.entity.AddressBean;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.bean.entity.CommitOrderProductBean;
import com.kasa.ola.bean.entity.ConfirmOrderBean;
import com.kasa.ola.bean.entity.ConfirmOrderInfoBean;
import com.kasa.ola.bean.entity.SelfMentionPointBean;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.PaySuccessDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.manager.WechatPayManager;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ConfirmOrderAdapter;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.ui.popwindow.PayPop;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmOrderActivityOld extends BaseActivity implements View.OnClickListener, WechatPayManager.OnWechatPayListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
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
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.ll_address)
    RelativeLayout llAddress;
    @BindView(R.id.btn_strike)
    Button btnStrike;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.rv_product_list)
    RecyclerView rvProductList;
    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.tv_total_num)
    TextView tvTotalNum;
    @BindView(R.id.tv_self_mention_point_address)
    TextView tvSelfMentionPointAddress;
    @BindView(R.id.switch_is_self_mention_point)
    Switch switchIsSelfMentionPoint;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.ll_self_mention_point)
    LinearLayout llSelfMentionPoint;

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
    private ArrayList<PayTypeModel> payTypeList = new ArrayList<>();
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
    private AlipayHandler alipayHandler = new AlipayHandler(this);
    private String rebates;
    private String takeID = "";
    private boolean isSelf = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order_old);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setActionBar(getString(R.string.detail), "");
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

        showSelfMentionPointView();
    }

    private void showSelfMentionPointView() {
        switchIsSelfMentionPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSelf = true;
                    Intent selectSelfMentionPointIntent = new Intent(ConfirmOrderActivityOld.this, SelectSelfMentionPointActivity.class);
                    startActivityForResult(selectSelfMentionPointIntent,Const.SELECT_SELF_MENTION_POINT);
                } else {
                    isSelf = false;
                }
            }
        });
        llSelfMentionPoint.setOnClickListener(this);
    }

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
                    tvTotalNum.setText(getString(R.string.total_product_num_new, totalNum + ""));
                    totalPrice.setText("￥" + CommonUtils.getTwoZero(CommonUtils.getTwoPoint(total)));
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(ConfirmOrderActivityOld.this, msg);
            }
        }, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_address:
                Intent intent = new Intent(this, AddressManagerActivity.class);
                intent.putExtra(Const.ADDRESS_CHOOSE_KEY, true);
                startActivityForResult(intent, Const.ADDRESS_CHOOSE_REQUEST_KEY);
                break;
            case R.id.btn_strike:
                onSubmit();
                break;
            case R.id.ll_self_mention_point:
                if (isSelf){
                    Intent selectSelfMentionPointIntent = new Intent(ConfirmOrderActivityOld.this, SelectSelfMentionPointActivity.class);
                    startActivityForResult(selectSelfMentionPointIntent,Const.SELECT_SELF_MENTION_POINT);
                }else {

                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showPayPop(PayMsgModel payMsgModel) {
        initPayTypeList();
        payPop = new PayPop(ConfirmOrderActivityOld.this, payTypeList, payMsgModel, totalNum, new PayPop.PayTypeSelectListener() {
            @Override
            public void onSelectPayType(int payTypeNum) {
                payType = payTypeNum;
            }

            @Override
            public void onPay(int payTypeNum, double totalMoney, View v) {
                onGoPay(v);
            }
        });
        payPop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        payPop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        payPop.showAtLocation(rlBottom, Gravity.BOTTOM, 0, 0);
        payPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1);
            }
        });
        backgroundAlpha(0.3f);
    }

    private void payResult(int resultType, String rebates) {
        if (pwdPop!=null){
            pwdPop.dismiss();
        }
       if (payPop!=null){
           payPop.dismiss();
       }
        PaySuccessDialog.Builder builder = new PaySuccessDialog.Builder(ConfirmOrderActivityOld.this);
        builder.setResult(resultType, rebates)
                .setDialogInterface(new PaySuccessDialog.DialogInterface() {
                    @Override
                    public void walkAround(int resultType, PaySuccessDialog dialog) {
                        if (resultType == 1) {
                            dialog.dismiss();
                                EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
                                ActivityUtils.finishTopActivity(MainActivity.class);
//
                        } else if (resultType == 0) {
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }

    private void onGoPay(View v) {
        if (payType == 3) { //余额支付
            if (!checkPayPwd()) {
                return;
            }
            showPayPasswordPop(btnStrike);
        } else {//其他支付
            payOrder();
        }
    }

    public void showPayPasswordPop(View v) {
        if (null == pwdPop) {
            pwdPop = new PasswordPopupWin(ConfirmOrderActivityOld.this);
        }
        pwdPop.setPasswordPopListener(new PasswordPopupWin.PasswordPopListener() {
            @Override
            public void onPasswordVerify(String pwd) {
                //此处为密码输到第六位的监听，替换成付款逻辑即可
                verifyPayPwd(pwd);
            }
        });
        pwdPop.showAtBottom(v.getRootView(), v);
    }

    private void verifyPayPwd(String payPwd) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("payPwd", payPwd);
        ApiManager.get().getData(Const.VERIFY_PAY_PWD, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                payOrder();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(ConfirmOrderActivityOld.this, msg);
                if (null != pwdPop) {
                    pwdPop.clearPwd();
                }
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.checking_tips)).create());
    }

    private void payOrder() {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("payment", payType);
        JSONArray ja = new JSONArray();
        for (int i = 0; i < payMsgModel.orderList.size(); i++) {
            ja.put(new JSONObject().put("orderNo", payMsgModel.orderList.get(i)));
        }
        jo.put("orderNoList", ja);
//        jo.put("orderNo", orderId);
        ApiManager.get().getData(Const.ORDER_PAY, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {

                if (payType == 3) {
//                    ToastUtils.showShortToast(ConfirmOrderActivity.this, responseModel.resultCodeDetail);
                    ApiManager.get().getMyInfo(null);
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        JSONObject jo = (JSONObject) responseModel.data;
                        rebates = jo.optString("rebates");
                    }
                    payResult(1, rebates);
//                    finish();
                } else if (payType == 1 || payType == 2) {
                    //下一步进入支付宝
                    if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                        JSONObject jo = (JSONObject) responseModel.data;
                        rebates = jo.optString("rebates");
                        if (payType == 1) {
                            //下一步进入支付宝
                            alipay(jo.optString("orderString"));
                        } else {
                            //微信支付
                            WechatPayManager.get().pay(ConfirmOrderActivityOld.this, jo, ConfirmOrderActivityOld.this);
                        }

                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(ConfirmOrderActivityOld.this, msg);
                payResult(0, "");
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.paying_tips)).create());
    }

    private void alipay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ConfirmOrderActivityOld.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                alipayHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private boolean checkPayPwd() {
        if (LoginHandler.get().getMyInfo().optInt("hasPayPwd") == 0) {
            CommonDialog.Builder builder = new CommonDialog.Builder(this);
            builder.setMessage(this.getString(R.string.set_pwd_tips))
                    .setLeftButton(this.getString(R.string.cancel_pay))
                    .setRightButton(this.getString(R.string.confirm_pay))
                    .setDialogInterface(new CommonDialog.DialogInterface() {

                        @Override
                        public void leftButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                        }

                        @Override
                        public void rightButtonClick(CommonDialog dialog) {
                            dialog.dismiss();
                            Intent intent = new Intent(ConfirmOrderActivityOld.this, ModPasswordActivity.class);
                            intent.putExtra(Const.PWD_TYPE_KEY, 2);
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();
            return false;
        }
        return true;
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        this.getWindow().setAttributes(lp);
    }

    private void initPayTypeList() {
        payTypeList.clear();
        PayTypeModel alipayPayModel = new PayTypeModel();
        alipayPayModel.iconId = R.mipmap.alipay;
        alipayPayModel.payType = 1;
        alipayPayModel.payTypeDetails = getString(R.string.ali_pay);
        alipayPayModel.explain = getString(R.string.ali_explain);
        alipayPayModel.status = 0;

        PayTypeModel wechatPayPayModel = new PayTypeModel();
        wechatPayPayModel.iconId = R.mipmap.wechat_pay;
        wechatPayPayModel.payType = 2;
        wechatPayPayModel.payTypeDetails = getString(R.string.wechat);
        wechatPayPayModel.explain = getString(R.string.wechat_explain);
        wechatPayPayModel.status = 0;

        PayTypeModel quotaPayPayModel = new PayTypeModel();
        quotaPayPayModel.iconId = R.mipmap.yue;
        quotaPayPayModel.payType = 3;
        quotaPayPayModel.payTypeDetails = getString(R.string.quota_pay);
        quotaPayPayModel.explain = getString(R.string.quota_explain);
        quotaPayPayModel.status = 0;

        alipayPayModel.status = 1;
        payTypeList.add(alipayPayModel);
        payTypeList.add(wechatPayPayModel);
        payTypeList.add(quotaPayPayModel);
    }

    public void onSubmit() {
//        if (CommonUtils.checkCertification(this)) {
        if ((null == addressBean || TextUtils.isEmpty(addressBean.getProvince()) || TextUtils.isEmpty(addressBean.getCity()) || TextUtils.isEmpty(addressBean.getArea())) &&TextUtils.isEmpty(takeID)) {
            ToastUtils.showShortToast(ConfirmOrderActivityOld.this, getString(R.string.please_inputAddress_or_self_mention_point_address));
            return;
        }
        commitOrder();
//        }
    }

    private void commitOrder() {
        List<ConfirmOrderBean> submitData = getSubmitData(commitInfoListBeans);
        confirmOrderBeans.clear();
        confirmOrderBeans.addAll(submitData);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        if (isSelf){
            jsonObject.put("takeID", takeID);
        }else {
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
//                            Intent intent = new Intent(ConfirmOrderActivity.this, PayActivity.class);
                            payMsgModel = new PayMsgModel();
                            payMsgModel.totalPrice = totalPrice.getText().toString().replace("￥", "");
                            payMsgModel.orderList = orderNoList;
                            showPayPop(payMsgModel);
//                            intent.putExtra(Const.PAY_MSG_KEY, payMsgModel);
//                            startActivity(intent);
//                            finish();
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(ConfirmOrderActivityOld.this, msg);
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
            } else if (requestCode == Const.SELECT_SELF_MENTION_POINT) {
                SelfMentionPointBean selfMentionPointBean = (SelfMentionPointBean) data.getSerializableExtra(Const.BACK_SELF_MOTENT_POINT_KEY);
//                ToastUtils.showLongToast(ConfirmOrderActivity.this,"填入自提点地址，并给takeID赋值");
                if (selfMentionPointBean != null) {
                    tvSelfMentionPointAddress.setText(selfMentionPointBean.getProvince() + selfMentionPointBean.getCity() + selfMentionPointBean.getArea() + selfMentionPointBean.getAddressDetail());
                    takeID = selfMentionPointBean.getTakeID();
                }
            }
        }
    }

    @Override
    public void onWechatPayRequestError() {

    }

    @Override
    public void onWechatPayRequestSuccess() {

    }

    @Override
    public void onWechatPayError(int code, String msg) {
        payResult(0, "");
    }

    @Override
    public void onWechatPaySuccess() {
        payResult(1, rebates);
    }

    private class AlipayHandler extends Handler {

        private final WeakReference<ConfirmOrderActivityOld> mMallPayActivity;

        private AlipayHandler(ConfirmOrderActivityOld t) {
            mMallPayActivity = new WeakReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ConfirmOrderActivityOld t = mMallPayActivity.get();
            if (null != t) {
                if (msg.what == SDK_PAY_FLAG) {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast(t, t.getString(R.string.pay_succeed));
                        payResult(1, rebates);
                        t.finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast(t, t.getString(R.string.pay_failed));
                        payResult(0, "");
                    }
                    ApiManager.get().getMyInfo(null);
                }
            }
        }
    }
}
