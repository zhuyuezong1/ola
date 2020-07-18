package com.kasa.ola.ui.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.PayResult;
import com.kasa.ola.bean.entity.MallOrderBean;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.dialog.CheckIsSelfMentionPointDialog;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.dialog.OrdinaryDialog;
import com.kasa.ola.dialog.QRCodeDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.manager.WechatPayManager;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.pop.PayPopup;
import com.kasa.ola.pop.PayPopupWindow;
import com.kasa.ola.ui.ConfirmOrderActivity;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.ui.ModPasswordActivity;
import com.kasa.ola.ui.MyOrderActivity;
import com.kasa.ola.ui.OrderDetailActivity;
import com.kasa.ola.ui.PaySuccessResultActivity;
import com.kasa.ola.ui.PublishProductCommentActivity;
import com.kasa.ola.ui.adapter.MallOrderAdapter;
import com.kasa.ola.ui.passwordinputwin.PasswordPopupWin;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import razerdp.basepopup.BasePopupWindow;

import static android.app.Activity.RESULT_OK;

public class OrderClassifyFragment extends BaseFragment implements LoadMoreRecyclerView.LoadingListener{

    public static final String ORDER_STATUS_KEY = "ORDER_STATUS_KEY";

    private SwipeRefreshLayout refreshLayout;
    private LoadingView loadingView;
    private LoadMoreRecyclerView orderView;
    private View viewNoResult;
    private ArrayList<MallOrderBean> mallOrderList = new ArrayList<>();
    private MallOrderAdapter mallOrderAdapter;

    private int type = 0;

    private boolean isFirst = true;
    private boolean isVisibleToUser = false;
    private boolean isInitView = false;
    private int currentPage = 1;
//    private String orderType;
    private int payType = 1;
    private int totalNum = 0;
    private PayMsgModel payMsgModel;
    private static final int SDK_PAY_FLAG = 1;
    private PasswordPopupWin pwdPop;
    private boolean isFirstOpen = false;
    private PayPopup payPopup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(ORDER_STATUS_KEY);
//        orderType = getArguments().getString(Const.ORDER_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_order_classify_layout, container, false);
        refreshLayout = rootView.findViewById(R.id.order_refresh_layout);
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
        orderView = rootView.findViewById(R.id.order_recycle_view);
        orderView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderView.setLoadingListener(this);
        mallOrderAdapter = new MallOrderAdapter(getContext(), mallOrderList);
        mallOrderAdapter.setMallOrderListener(new MallOrderAdapter.MallOrderListener() {
            @Override
            public void goPay(MallOrderBean mallOrderbean) {
//                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
//                intent.putExtra(Const.ACTION,"0");
//                intent.putExtra(Const.ORDER_ID_KEY,mallOrderbean.getOrderNo());
//                startActivityForResult(intent,Const.OEDER_DTEAIL_TAG);
                totalNum = mallOrderbean.getTotalNum();
                ArrayList orderNoList = new ArrayList<>();
                orderNoList.add(mallOrderbean.getOrderNo());
                payMsgModel = new PayMsgModel();
                payMsgModel.totalPrice = mallOrderbean.getTotalPrice().toString().replace("￥", "");
                payMsgModel.orderList = orderNoList;
                showPayPopup(payMsgModel);
            }

            @Override
            public void confirm(final String orderID) {
                CommonDialog.Builder builder = new CommonDialog.Builder(getActivity());
                builder.setMessage(getContext().getString(R.string.confirm_accept_good_tips))
                        .setLeftButton(getString(R.string.cancel))
                        .setRightButton(getString(R.string.confirm))
                        .setDialogInterface(new CommonDialog.DialogInterface() {

                            @Override
                            public void leftButtonClick(CommonDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void rightButtonClick(CommonDialog dialog) {
                                updateMallOrderState(orderID,1);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void cancel(String orderID) {
                showTelephony(LoginHandler.get().getBaseUrlBean().getServiceMobile());
            }

            @Override
            public void goComment(MallOrderBean mallOrderBean) {
                Intent intent = new Intent(getContext(), PublishProductCommentActivity.class);
                intent.putExtra(Const.ORDER_ID_KEY,mallOrderBean.getOrderNo());
                intent.putExtra(Const.PUBLISH_PRODUCT_COMMENT, (Serializable) mallOrderBean.getProductList());
                startActivityForResult(intent,Const.PUBLISH_COMMENT_SUCCESS);
            }

            @Override
            public void goBuyAgain(String productID) {

            }

            @Override
            public void orderDetails(String orderID) {
//                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
//                intent.putExtra(Const.ORDER_ID_KEY,orderID);
//                startActivityForResult(intent,Const.OEDER_DTEAIL_TAG);
//                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
//                intent.putExtra(Const.ORDER_ID_KEY,orderID);
//                startActivityForResult(intent,Const.OEDER_DTEAIL_TAG);
            }

            @Override
            public void returnGood(final String orderID) {
                OrdinaryDialog.Builder builder = new OrdinaryDialog.Builder(getActivity());
                builder.setMessage(getContext().getString(R.string.return_good_tips))
                        .setTitle(getContext().getString(R.string.return_good_tips_title))
                        .setLeftButton(getString(R.string.cancel))
                        .setRightButton(getString(R.string.confirm))
                        .setDialogInterface(new OrdinaryDialog.DialogInterface() {
                            @Override
                            public void disagree(OrdinaryDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void agree(OrdinaryDialog dialog) {
                                updateMallOrderState(orderID,2);
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void showQRCode(MallOrderBean mallOrderBean) {
                QRCodeDialog.Builder builder = new QRCodeDialog.Builder(getContext());
                builder.setMessage(getString(R.string.show_qr_code_intro))
                        .setCodeImageUrl(mallOrderBean.getCodeImageUrl())
                        .setDialogInterface(new QRCodeDialog.DialogInterface() {
                            @Override
                            public void close(QRCodeDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void checkIsSelfMentionPoint(MallOrderBean mallOrderBean) {
                CheckIsSelfMentionPointDialog.Builder builder = new CheckIsSelfMentionPointDialog.Builder(getContext());
                builder.setMessage(getString(R.string.address_title)+mallOrderBean.getTakeAddress().getProvince()+mallOrderBean.getTakeAddress().getCity()+mallOrderBean.getTakeAddress().getArea()+mallOrderBean.getTakeAddress().getAddressDetail())
                .setPhoneNumber(getString(R.string.connect_phone_number_title)+mallOrderBean.getTakeAddress().getMobile())
                .setLeftButton(getString(R.string.one_key_dial))
                .setRightButton(getString(R.string.back))
                .setDialogInterface(new CheckIsSelfMentionPointDialog.DialogInterface() {
                    @Override
                    public void leftButtonClick(String phone) {
                        showTelephony(phone);
                    }

                    @Override
                    public void rightButtonClick(CheckIsSelfMentionPointDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
            }

            @Override
            public void checkLogistics(MallOrderBean mallOrderBean) {
                ToastUtils.showLongToast(getContext(),"查看物流");
            }
        });
        orderView.setAdapter(mallOrderAdapter);

        rootView.findViewById(R.id.view_go_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.finishTopActivity(MainActivity.class);
                EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
            }
        });

        isInitView = true;
        return rootView;
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

    private void showPayPopup(PayMsgModel payMsgModel) {
        PayPopupWindow payPopup = new PayPopupWindow(getActivity(), new PayPopupWindow.PayPopListener() {
            @Override
            public void onPayResult(int payTypeNum, String totalMoney) {
                payType = payTypeNum;
                payResult(payTypeNum,totalMoney);
            }
        }, new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        }, totalNum, payMsgModel,orderView);
        payPopup.showPopupWindow();

    }

    private void payResult(int payTypeNum, String totalMoney) {
        Intent intent = new Intent(getContext(), PaySuccessResultActivity.class);
        intent.putExtra(Const.PAY_VALUE,totalMoney);
        intent.putExtra(Const.ORDER_ENTER_TYPE,2);
        startActivity(intent);
    }

    private boolean checkPayPwd() {
        if (LoginHandler.get().getMyInfo().optInt("hasPayPwd") == 0) {
            CommonDialog.Builder builder = new CommonDialog.Builder(getContext());
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
                            Intent intent = new Intent(getContext(), ModPasswordActivity.class);
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
            jo.put("pageSize", 1000);
            jo.put("orderType", type);
//            jo.put("type", orderType);
            ApiManager.get().getData(Const.GET_ORDER_LIST_TAG, jo, new BusinessBackListener() {
                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    if (null != getActivity()) {
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            refreshLayout.setRefreshing(false);
                            if (!isLoadMore) {
                                mallOrderList.clear();
                            }
                            JSONObject jo = (JSONObject) responseModel.data;
                            String jsonData = jo.optJSONArray("orderList").toString();
                            List<MallOrderBean> list = new Gson().fromJson(jsonData, new TypeToken<List<MallOrderBean>>() {
                            }.getType());
                            if (list!=null && list.size()>0) {
                                mallOrderList.addAll(list);
                                mallOrderAdapter.notifyDataSetChanged();
                                orderView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                            }

                            if (!isLoadMore) {
                                if (list!=null&& list.size() > 0) {
                                    viewNoResult.setVisibility(View.GONE);
                                    orderView.setVisibility(View.VISIBLE);
                                }else {
                                    viewNoResult.setVisibility(View.VISIBLE);
                                    orderView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    refreshLayout.setRefreshing(false);
                    ToastUtils.showShortToast(getContext(),msg);
                }
            }, isFirst ? loadingView : null);
        }
    }

    private void updateMallOrderState(String orderID,int type) {
        ApiManager.get().updateMallOrderState(orderID, type,new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(getContext(), responseModel.resultCodeDetail);
                ApiManager.get().getMyInfo(null);
                loadPage(false);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(getContext(), msg);
            }
        }, getContext());
    }

    private void showTelephony(final String tel) {
        CommonDialog.Builder builder = new CommonDialog.Builder(getContext());
        builder.setMessage(tel)
                .setLeftButton(getContext().getString(R.string.cancel))
                .setRightButton(getContext().getString(R.string.go_telephony))
                .setDialogInterface(new CommonDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(CommonDialog dialog) {
                        dialog.dismiss();
                        ((BaseActivity) getActivity()).requestPermissions(getContext(), new String[]{Manifest.permission.CALL_PHONE},
                                new BaseActivity.RequestPermissionCallBack() {
                                    @Override
                                    public void granted() {
                                        try {
                                            Uri uri = Uri.parse("tel:" + tel);
                                            Intent telIntent = new Intent(Intent.ACTION_DIAL, uri);
                                            startActivity(telIntent);
                                        } catch (ActivityNotFoundException a) {
                                            ToastUtils.showShortToast(getContext(), getString(R.string.tel_confirm));
                                        }
                                    }

                                    @Override
                                    public void denied() {
                                        ToastUtils.showShortToast(getContext(), "获取权限失败，正常功能受到影响");
                                    }

                                });

                    }
                })
                .create()
                .show();
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            loadPage(true);
        }
    }
}

