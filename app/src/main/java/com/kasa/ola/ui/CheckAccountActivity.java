package com.kasa.ola.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.StoreCheckAccountInfoBean;
import com.kasa.ola.dialog.SingleBtnCommonDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.popwindow.CornerBtnFilterPopWindow;
import com.kasa.ola.utils.JsonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckAccountActivity extends BaseActivity implements View.OnClickListener {

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
    @BindView(R.id.tv_verification_value)
    TextView tvVerificationValue;
    @BindView(R.id.tv_no_verification_value)
    TextView tvNoVerificationValue;
    @BindView(R.id.tv_verification_order_num)
    TextView tvVerificationOrderNum;
    @BindView(R.id.rl_verification_orders)
    RelativeLayout rlVerificationOrders;
    @BindView(R.id.tv_no_verification_order_num)
    TextView tvNoVerificationOrderNum;
    @BindView(R.id.rl_no_verification_orders)
    RelativeLayout rlNoVerificationOrders;
    @BindView(R.id.bg_view)
    View bgView;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    private long startTime = 0;
    private long endTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_account);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        getData();
    }

    private void getData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        if (startTime != 0 && endTime != 0) {
            jsonObject.put("startTime", startTime + "");
            jsonObject.put("endTime", endTime + "");
        }
        ApiManager.get().getData(Const.GET_SUPPLIERS_MONEY_DETAIL, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    StoreCheckAccountInfoBean storeCheckAccountInfoBean = JsonUtils.jsonToObject(jo.toString(), StoreCheckAccountInfoBean.class);
                    tvIncome.setText(getString(R.string.price,storeCheckAccountInfoBean.getIncome()));
                    tvVerificationValue.setText(getString(R.string.price,storeCheckAccountInfoBean.getVerifiedMoney()));
                    tvNoVerificationValue.setText(getString(R.string.price,storeCheckAccountInfoBean.getNoVerifiedMoney()));
                    tvVerificationOrderNum.setText(getString(R.string.order_num,storeCheckAccountInfoBean.getVerifiedNum()));
                    tvNoVerificationOrderNum.setText(getString(R.string.order_num,storeCheckAccountInfoBean.getNoVerifiedNum()));
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {

            }
        }, null);
    }

    private void initEvent() {
        textViewFilter.setOnClickListener(this);
        tvNoVerificationValue.setOnClickListener(this);
        rlVerificationOrders.setOnClickListener(this);
        rlNoVerificationOrders.setOnClickListener(this);
    }

    private void initView() {

    }

    private void initTitle() {
        setActionBar(getString(R.string.store_check_account), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_filter:
                showDatePop();
                break;
            case R.id.tv_no_verification_value:
                showIntroDialog();
                break;
            case R.id.rl_verification_orders:
                Intent storeOrderIntent = new Intent(CheckAccountActivity.this, StoreOrderActivity.class);
                storeOrderIntent.putExtra(Const.STORE_ORDER_TAG, 2);
                storeOrderIntent.putExtra(Const.START_TIME, startTime);
                storeOrderIntent.putExtra(Const.END_TIME, endTime);
                startActivity(storeOrderIntent);
                break;
            case R.id.rl_no_verification_orders:
                Intent storeOrderIntent1 = new Intent(CheckAccountActivity.this, StoreOrderActivity.class);
                storeOrderIntent1.putExtra(Const.STORE_ORDER_TAG, 1);
                storeOrderIntent1.putExtra(Const.START_TIME, startTime);
                storeOrderIntent1.putExtra(Const.END_TIME, endTime);
                startActivity(storeOrderIntent1);
                break;
        }
    }

    private void showIntroDialog() {
        SingleBtnCommonDialog.Builder builder = new SingleBtnCommonDialog.Builder(CheckAccountActivity.this);
        builder.setTitle(getString(R.string.dialog_unchecked_intro_title))
                .setMessage(getString(R.string.dialog_unchecked_intro_content))
                .setRightButton(getString(R.string.confirm))
                .setDialogInterface(new SingleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void rightButtonClick(SingleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showDatePop() {
        CornerBtnFilterPopWindow filterPopWindow = new CornerBtnFilterPopWindow(CheckAccountActivity.this, startTime, endTime);
        filterPopWindow.setFilterListener(new CornerBtnFilterPopWindow.FilterListener() {
            @Override
            public void confirmClick(long startTime1, long endTime1) {
                startTime = startTime1;
                endTime = endTime1;
                getData();
            }

            @Override
            public void resetClick() {
                startTime = 0;
                endTime = 0;
                getData();
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
}
