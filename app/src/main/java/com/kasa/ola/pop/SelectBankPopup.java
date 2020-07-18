package com.kasa.ola.pop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.BankCardListActivity;
import com.kasa.ola.ui.BankCartEditActivity;
import com.kasa.ola.ui.adapter.SelectBankAdapter;
import com.kasa.ola.ui.listener.OnConfirmListener;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;


public class SelectBankPopup extends BasePopupWindow {
    private List<CardBean> cardList = new ArrayList<>();
    private final SelectBankAdapter selectBankAdapter;
    private Context context;

    public void setCardList(List<CardBean> cardList) {
        this.cardList = cardList;
        selectBankAdapter.notifyDataSetChanged();
        LogUtil.d("sssssssss","添加完成，刷新选择银行卡列表");
    }

    public SelectBankPopup(Activity context, final OnItemClickListener onItemClickListener, List<CardBean> cardList) {
        super(context);
        this.cardList = cardList;
        this.context = context;
        ImageView iv_back = findViewById(R.id.iv_back);
        RecyclerView rv_banks = findViewById(R.id.rv_banks);
        TextView tv_add_bank = findViewById(R.id.tv_add_bank);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_banks.setLayoutManager(linearLayoutManager);
        selectBankAdapter = new SelectBankAdapter(context, cardList);
        selectBankAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                dismiss();
                if (onItemClickListener!=null && cardList.size()>0){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        rv_banks.setAdapter(selectBankAdapter);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_add_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BankCartEditActivity.class);
                context.startActivityForResult(intent,Const.ADD_BANK_CARD_FOR_BACK);
            }
        });
        setPopupGravity(Gravity.BOTTOM);

    }



    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0, 350);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0, 1f, 350);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_slide_from_bottom_select_bank);
    }

//    public void refreshBankList() {
//        getBankList();
//    }
}
