package com.kasa.ola.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.bean.entity.OcrSignBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.CardAdapter;
import com.kasa.ola.ui.listener.OcrListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;
import com.webank.mbank.ocr.WbCloudOcrSDK;
import com.webank.mbank.ocr.net.EXBankCardResult;
import com.webank.mbank.ocr.net.EXIDCardResult;
import com.webank.mbank.ocr.net.ResultOfDriverLicense;
import com.webank.mbank.ocr.net.VehicleLicenseResultOriginal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MineCardActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.card_recycleview)
    RecyclerView cardRecycleview;
    @BindView(R.id.btn_add_card)
    TextView btnAddCard;
    @BindView(R.id.view_no_result)
    LinearLayout viewNoResult;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private boolean isFirst = true;
    private CardAdapter cardAdapter;
    private ArrayList<CardBean> cardList = new ArrayList<>();
    private OcrSignBean ocrSignBean;
    private ProgressDialog progressDlg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_card);
        ButterKnife.bind(this);
        initView();
        getOcrSign();
        initProgress();
    }

    private void getOcrSign() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId",LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.GET_OCR_SIGN, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data!=null && responseModel.data instanceof JSONObject){
                    JSONObject jo = (JSONObject) responseModel.data;
                    ocrSignBean = JsonUtils.jsonToObject(jo.toString(), OcrSignBean.class);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(MineCardActivity.this,msg);
            }
        },null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            loadPage(true);
        } else {
            loadPage(false);
        }
    }
    private void initView() {
        setActionBar(getString(R.string.mine_card), "");
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                loadPage(true);
            }
        });
        cardRecycleview.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter = new CardAdapter(this, cardList);
        cardAdapter.setCardListener(new CardAdapter.CardListener() {
            @Override
            public void onDefaultClick(CardBean cardBean) {
                setDefault(cardBean);
            }

            @Override
            public void onDeleteClick(final CardBean cardBean) {
                CommonDialog.Builder builder = new CommonDialog.Builder(MineCardActivity.this);
                builder.setMessage(getString(R.string.delete_card_warning))
                        .setLeftButton(getString(R.string.cancel))
                        .setRightButton(getString(R.string.confirm))
                        .setDialogInterface(new CommonDialog.DialogInterface() {

                            @Override
                            public void leftButtonClick(CommonDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void rightButtonClick(CommonDialog dialog) {
                                dialog.dismiss();
                                deleteCard(cardBean.getBankID());
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onItemClick(CardBean cardBean) {
                Intent intent = new Intent();
                intent.putExtra(Const.SELECT_BANK, cardBean);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cardRecycleview.setAdapter(cardAdapter);
        btnAddCard.setOnClickListener(this);
    }
    private void loadPage(boolean isFirst) {
        ApiManager.get().getData(Const.GET_MY_BANK_CARD, new JSONObject().put("userID", LoginHandler.get().getUserId()),
                new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
                            String jsonData = ((JSONObject) responseModel.data).optString("list");
                            List<CardBean> list = new Gson().fromJson(jsonData, new TypeToken<List<CardBean>>() {
                            }.getType());

                            cardList.clear();
                            if (null != list && list.size()>0) {
                                cardList.addAll(list);
                                cardAdapter.notifyDataSetChanged();
                                cardRecycleview.setVisibility(View.VISIBLE);
                                viewNoResult.setVisibility(View.GONE);
                            } else {
                                cardRecycleview.setVisibility(View.GONE);
                                viewNoResult.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(MineCardActivity.this, msg);
                    }
                }, isFirst ? loadingView : null);
    }
    private void setDefault(final CardBean cardBean) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("bankID", cardBean.getBankID());
        ApiManager.get().getData(Const.SET_BANK_CARD_DEFAULT, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(MineCardActivity.this, responseModel.resultCodeDetail);
                loadPage(false);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MineCardActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.setting_tips)).create());
    }

    private void deleteCard(String cardId) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("bankID", cardId);
        ApiManager.get().getData(Const.DELETE_BANK_CARD, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(MineCardActivity.this, responseModel.resultCodeDetail);
                loadPage(false);
                ApiManager.get().getMyInfo(null);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MineCardActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.setting_tips)).create());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_card:
                startActivity(new Intent(MineCardActivity.this, CardActivity.class));
//                String orderNo = "ocr_orderNo" + System.currentTimeMillis();
//                progressDlg.show();
//                if (ocrSignBean!=null){
//                    CommonUtils.startOcrDemo(MineCardActivity.this,ocrSignBean.getSign(),"银行卡识别",orderNo,Const.appId,"1.0.0",ocrSignBean.getNonce(),LoginHandler.get().getUserId(),progressDlg, new OcrListener() {
//                        @Override
//                        public void bankOcrResult(EXBankCardResult exBankCardResult) {
//                            ToastUtils.showLongToast(MineCardActivity.this,exBankCardResult.bankcardNo);
//                        }
//
//                        @Override
//                        public void idResult(EXIDCardResult exidCardResult) {
//
//                        }
//
//                        @Override
//                        public void driverLicenseOcrResult(ResultOfDriverLicense resultOfDriverLicense) {
//
//                        }
//
//                        @Override
//                        public void vehicleLicenseOcrResult(VehicleLicenseResultOriginal vehicleLicenseResultOriginal) {
//
//                        }
//                    });
//                }
                break;
        }
    }
    private void initProgress() {
        if (progressDlg != null) {
            progressDlg.dismiss();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            progressDlg = new ProgressDialog(this);
        } else {
            progressDlg = new ProgressDialog(this);
            progressDlg.setInverseBackgroundForced(true);
        }
        progressDlg.setMessage("加载中...");
        progressDlg.setIndeterminate(true);
        progressDlg.setCanceledOnTouchOutside(false);
        progressDlg.setCancelable(true);
        progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDlg.setCancelable(false);
    }
}
