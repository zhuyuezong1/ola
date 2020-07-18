package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.BankCardAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BankCardListActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.btn_add_card)
    TextView btnAddCard;
    @BindView(R.id.card_recycleview)
    RecyclerView cardRecycleview;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private BankCardAdapter bankCardAdapter;
    private SelectImagePop selectImagePop;
    private boolean isFirst = true;
    private ArrayList<CardBean> cardList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
    }

    private void initEvent() {
        btnAddCard.setOnClickListener(this);
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                loadPage(true);
            }
        });
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BankCardListActivity.this);
        cardRecycleview.setLayoutManager(linearLayoutManager);
        bankCardAdapter = new BankCardAdapter(BankCardListActivity.this, cardList);
        bankCardAdapter.setOnEditBtnClickListener(new BankCardAdapter.OnEditBtnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onEditBtnClick(int position,View view) {
                List<TextBean> bottomList = CommonUtils.getBottomList();
                selectImagePop = new SelectImagePop(BankCardListActivity.this, bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:
                                setDefault(cardList.get(position));
                                selectImagePop.dismiss();
                                break;
                            case 2:
                                deleteCard(cardList.get(position).getBankID());
                                selectImagePop.dismiss();
                                break;
                            case 3:// 取消
                                selectImagePop.dismiss();
                                break;

                            default:
                                break;
                        }
                    }
                });
                selectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                selectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                selectImagePop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                selectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        CommonUtils.backgroundAlpha(1,BankCardListActivity.this);
                    }
                });
                CommonUtils.backgroundAlpha(0.3f,BankCardListActivity.this);
            }
        });
        cardRecycleview.setAdapter(bankCardAdapter);
    }

    private void initTitle() {
        setActionBar(getString(R.string.bank_card_info),"");
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_card:
                Intent intent = new Intent(BankCardListActivity.this, BankCartEditActivity.class);
                startActivity(intent);
                break;
        }
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
                                bankCardAdapter.notifyDataSetChanged();
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(BankCardListActivity.this, msg);
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
                ToastUtils.showShortToast(BankCardListActivity.this, responseModel.resultCodeDetail);
                loadPage(false);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(BankCardListActivity.this, msg);
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
                ToastUtils.showShortToast(BankCardListActivity.this, responseModel.resultCodeDetail);
                loadPage(false);
                ApiManager.get().getMyInfo(null);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(BankCardListActivity.this, msg);
            }
        }, new LoadingDialog.Builder(this).setMessage(getString(R.string.setting_tips)).create());
    }
}
