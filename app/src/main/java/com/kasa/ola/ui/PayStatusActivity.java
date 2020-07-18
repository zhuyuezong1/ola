package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.manager.Const;
import com.kasa.ola.utils.ActivityUtils;

import org.greenrobot.eventbus.EventBus;

public class PayStatusActivity extends BaseActivity implements View.OnClickListener {

    private int mnPayStatus = 0;   // 1:成功; 0:失败
//    private String orderId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paystatus);

        mnPayStatus = getIntent().getIntExtra(Const.PAY_STATUS_KEY, 0);
//        orderId = getIntent().getStringExtra(Const.ORDER_ID_KEY);

        initView();
    }

    private void initView() {
        View view = findViewById(R.id.include_titlebar);
        TextView tvTitle = view.findViewById(R.id.textview_title);
        LinearLayout layout = view.findViewById(R.id.layout_back);
        layout.setVisibility(View.VISIBLE);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView imgvIcon = findViewById(R.id.imageView_icon);
        TextView tvPayStatus = findViewById(R.id.textView_payStatus);
        TextView tvDescribe = findViewById(R.id.textView_describe);
//        Button btnCheckOrder = findViewById(R.id.button_checkOrder);
        Button btnLeft = findViewById(R.id.button_left);

//        btnCheckOrder.setOnClickListener(this);
        btnLeft.setOnClickListener(this);

        if (1 == mnPayStatus) {
            tvTitle.setText(getString(R.string.pay_succeed));
            imgvIcon.setImageResource(R.mipmap.pay_succeed);
            tvPayStatus.setText(getString(R.string.pay_succeed));
            tvDescribe.setText("");
            btnLeft.setText(getString(R.string.back));
        } else if (0 == mnPayStatus) {
            tvTitle.setText(getString(R.string.pay_failed));
            imgvIcon.setImageResource(R.mipmap.pay_failed);
            tvPayStatus.setText(getString(R.string.pay_failed));
            tvDescribe.setText(getString(R.string.pay_fail_tips));
            btnLeft.setText(getString(R.string.pay_again));
        }
//        btnCheckOrder.setText(getString(R.string.check_order));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.button_checkOrder:
//                if (0 == mnPayStatus) {
//                    ActivityUtils.finishActivity(PayActivity.class);
//                }
//                Intent intent = new Intent(PayStatusActivity.this, MallOrderDetailActivity.class);
//                intent.putExtra(Const.ORDER_ID_KEY, orderId);
//                startActivity(intent);
//                finish();
//                break;

            case R.id.button_left:
                if (1 == mnPayStatus) {
                    finish();
                } else if (0 == mnPayStatus) {
                    finish();
                    EventBus.getDefault().post(new EventCenter.PwdClear());
                }
                break;
        }
    }
}
