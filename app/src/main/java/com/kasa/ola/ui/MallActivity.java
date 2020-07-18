package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.MallClassBean;
import com.kasa.ola.bean.entity.MallTabClassBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ClassifyAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;
import com.kasa.ola.widget.TabLayoutView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MallActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.mall_tab)
    TabLayoutView mallTab;
//    @BindView(R.id.iv_mall_banner)
//    ImageView ivMallBanner;
    @BindView(R.id.tv_first_tab_name)
    TextView tvFirstTabName;
    @BindView(R.id.mall_recycle)
    LoadMoreRecyclerView mallRecycle;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.iv_my_cart)
    ImageView ivMyCart;
    private int firstTabSelected = 0;
    private List<MallClassBean> mallClassBeans;
    private ClassifyAdapter classifyAdapter;
    private List<MallClassBean> secondClasses = new ArrayList<>();
    private String[] titles;
    private int[] imgs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initView() {
//        ivMallBanner.setOnClickListener(this);
        ivMyCart.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MallActivity.this, 3);
        mallRecycle.setLayoutManager(gridLayoutManager);

        classifyAdapter = new ClassifyAdapter(MallActivity.this, secondClasses);
        classifyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MallActivity.this, MallProductListActivity.class);
                intent.putExtra(Const.CLASS_ID_TAG, secondClasses.get(position).getClassID());
                startActivity(intent);
            }
        });
        mallRecycle.setAdapter(classifyAdapter);

        getFirstTab("1", "");

        mallTab.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
            @Override
            public void onItemClick(int index) {

            }

            @Override
            public void onVerticalItemClick(int index, View view) {
                mallTab.setSelectStyleVertical(index);

            }
        });
    }

    private void getFirstTab(String classLv, String classID) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("classLv", classLv);
        jsonObject.put("classID", classID);
        ApiManager.get().getData(Const.GET_CLASS_LIST_TAG, jsonObject, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    String jsonData = ((JSONObject) responseModel.data).toString();
                    MallTabClassBean classBean = JsonUtils.jsonToObject(jsonData, MallTabClassBean.class);
                    mallClassBeans = classBean.getClassList();
                    if (mallClassBeans != null && mallClassBeans.size() > 0) {
                        titles = new String[mallClassBeans.size()];
                        imgs = new int[mallClassBeans.size()];
                        for (int i = 0; i < mallClassBeans.size(); i++) {
                            titles[i] = mallClassBeans.get(i).getClassName();
                            imgs[i] = R.drawable.selector_mall_tab_bg;
                        }
                        mallTab.setDataSource(titles, imgs, 0);
                        mallTab.setTextStyle(13, R.color.COLOR_FF9A9A9A, R.color.black);
                        mallTab.initDatasVertical();
                        getSecondMemu();
                        tvFirstTabName.setText(titles[0]);
                        mallTab.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
                            @Override
                            public void onItemClick(int index) {
                            }

                            @Override
                            public void onVerticalItemClick(int index, View view) {
                                mallTab.setSelectStyleVertical(index);
                                if (firstTabSelected != index) {
                                    firstTabSelected = index;
                                    tvFirstTabName.setText(mallClassBeans.get(firstTabSelected).getClassName());
                                    getSecondMemu();
                                } else {

                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MallActivity.this, msg);
            }
        }, null);
    }

    private void getSecondMemu() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("classLv", "2");
        jsonObject.put("classID", mallClassBeans.get(firstTabSelected).getClassID());
        ApiManager.get().getData(Const.GET_CLASS_LIST_TAG, jsonObject, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    String jsonData = ((JSONObject) responseModel.data).toString();
                    MallTabClassBean secongClassBean = JsonUtils.jsonToObject(jsonData, MallTabClassBean.class);
                    List<MallClassBean> mallClassBeans = secongClassBean.getClassList();
                    secondClasses.clear();
                    classifyAdapter.notifyDataSetChanged();
                    if (mallClassBeans != null && mallClassBeans.size() > 0) {
                        secondClasses.addAll(mallClassBeans);
                        classifyAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MallActivity.this, msg);
            }
        }, null);
    }

    private void initTitle() {
        setActionBar(getString(R.string.all_products), "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_mall_banner:
//                Intent mallProductListintent = new Intent(MallActivity.this, MallProductListActivity.class);
//                startActivity(mallProductListintent);
//                break;
            case R.id.iv_my_cart:
                if (CommonUtils.checkLogin(MallActivity.this)){
                    Intent cartIntent = new Intent(MallActivity.this, CartActivity.class);
                    startActivity(cartIntent);
                }
                break;
        }
    }
}
