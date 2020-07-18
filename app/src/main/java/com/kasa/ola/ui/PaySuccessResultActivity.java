package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProductVerticalAdapter;
import com.kasa.ola.utils.ActivityUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaySuccessResultActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_products)
    LoadMoreRecyclerView rvProducts;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private ProductVerticalAdapter productVerticalAdapter;
    private List<ProductBean> productBeans = new ArrayList<>();
    private int currentPage = 1;
    private String payValue;
    private int enterType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);
        payValue = getIntent().getStringExtra(Const.PAY_VALUE);
        enterType = getIntent().getIntExtra(Const.ORDER_ENTER_TYPE,0);
        initTitle();
        initView();
        initEvent();
        loadPage(true);
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PaySuccessResultActivity.this);
        rvProducts.setLayoutManager(linearLayoutManager);
        rvProducts.setLoadingListener(this);
        rvProducts.setLoadingMoreEnabled(true);
        productVerticalAdapter = new ProductVerticalAdapter(PaySuccessResultActivity.this, productBeans);
        rvProducts.setAdapter(productVerticalAdapter);
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        View headView = LayoutInflater.from(PaySuccessResultActivity.this).inflate(R.layout.view_pay_success_head, rvProducts, false);
        TextView tv_pay_value = headView.findViewById(R.id.tv_pay_value);
        TextView tv_back_home = headView.findViewById(R.id.tv_back_home);
        TextView tv_check_order = headView.findViewById(R.id.tv_check_order);
        tv_pay_value.setText(getString(R.string.pay_value,TextUtils.isDigitsOnly(payValue)?"":payValue));

        tv_back_home.setOnClickListener(this);
        tv_check_order.setOnClickListener(this);

        rvProducts.addHeaderView(headView);
    }

    private void initTitle() {
        setActionBar(getString(R.string.pay_result), "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBar(R.color.COLOR_FF1677FF);
        }
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        ivBack.setImageResource(R.mipmap.back_icon);
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.COLOR_FF1677FF));
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE / 2);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        jsonObject.put("otherType", "6");
        ApiManager.get().getData(Const.GET_PRODUCT_LIST_TAG, jsonObject, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        productVerticalAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        productVerticalAdapter.notifyDataSetChanged();
                        rvProducts.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(PaySuccessResultActivity.this, msg);
            }
        }, isFirst ? loadingView : null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
        ActivityUtils.finishTopActivity(MainActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
            case R.id.tv_back_home:
                EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
                ActivityUtils.finishTopActivity(MainActivity.class);
                break;
            case R.id.tv_check_order:
                Intent orderIntent = new Intent(PaySuccessResultActivity.this,MyOrderActivity.class);
                orderIntent.putExtra(Const.ORDER_TAG, 0);
                orderIntent.putExtra(Const.ORDER_ENTER_TYPE,enterType);
                startActivity(orderIntent);
                break;
        }
    }
}
