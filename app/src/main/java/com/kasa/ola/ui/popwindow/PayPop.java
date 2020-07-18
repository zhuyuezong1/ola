package com.kasa.ola.ui.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.bean.model.PayMsgModel;
import com.kasa.ola.bean.model.PayTypeModel;
import com.kasa.ola.ui.adapter.PayTypeAdapter;
import com.kasa.ola.ui.adapter.SelectImagePopAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PayPop extends PopupWindow {

    private ArrayList<PayTypeModel> list;
    private Context context;
    private final View rootView;
    private final RecyclerView rv_pay_pop;
    private Button btn_strike;
    private TextView tv_total_num;
    private TextView total_price;
    private int payType;
    private double totalMoney;
    public PayPop(Context context, ArrayList<PayTypeModel> list, PayMsgModel payMsgModel, long totalNum, final PayTypeSelectListener listener) {
        this.list = list;
        rootView = LayoutInflater.from(context).inflate(R.layout.view_pay_pop, null);
        rv_pay_pop = rootView.findViewById(R.id.rv_pay_pop);
        tv_total_num = rootView.findViewById(R.id.tv_total_num);
        total_price = rootView.findViewById(R.id.total_price);
        btn_strike = rootView.findViewById(R.id.btn_strike);

        tv_total_num.setText(context.getString(R.string.total_product_num_new,totalNum+""));
        total_price.setText("￥"+payMsgModel.totalPrice);
        btn_strike.setText(context.getString(R.string.pay_title));
//        RelativeLayout rl_bg = rootView.findViewById(R.id.rl_bg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_pay_pop.setLayoutManager(linearLayoutManager);
        PayTypeAdapter payTypeAdapter = new PayTypeAdapter(context, list);
        rv_pay_pop.setAdapter(payTypeAdapter);
        payTypeAdapter.setPayTypeListener(new PayTypeAdapter.PayTypeListener() {
            @Override
            public void onItemClick(int payTypeNum) {
                payType = payTypeNum;
                if (listener!=null){
                    listener.onSelectPayType(payTypeNum);
                }
            }
        });
        btn_strike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onPay(payType,totalMoney,btn_strike);
                }
            }
        });
        setBackgroundDrawable(new BitmapDrawable());
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.BottomInPopWin);
        setContentView(rootView);
    }
    public interface PayTypeSelectListener{
        void onSelectPayType(int payTypeNum);
        void onPay(int payTypeNum,double totalMoney,View v);
    }
}
