package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProductClassifyAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
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

public class ProductClassifyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.mall_tab)
    TabLayoutView mallTab;
    @BindView(R.id.tv_first_tab_name)
    TextView tvFirstTabName;
    @BindView(R.id.mall_recycle)
    LoadMoreRecyclerView mallRecycle;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private List<MallClassBean> secondClasses = new ArrayList<>();
    private ProductClassifyAdapter productClassifyAdapter;
    private List<MallClassBean> mallClassBeans;
    private String[] titles;
    private int[] imgs;
    private int firstTabSelected = 0;
//    private String searchClassID = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_classify);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        ivBack.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ProductClassifyActivity.this, 3);
        mallRecycle.setLayoutManager(gridLayoutManager);
        productClassifyAdapter = new ProductClassifyAdapter(ProductClassifyActivity.this, secondClasses);
        productClassifyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                Intent intent = new Intent(ProductClassifyActivity.this, MallProductListActivity.class);
//                intent.putExtra(Const.CLASS_ID_TAG,secondClasses.get(position).getClassID());
//                startActivity(intent);
                Intent intent1 = new Intent(ProductClassifyActivity.this, BusinessAndProductSearchActivity.class);
                intent1.putExtra(Const.SEARCH_TYPE,3);
                intent1.putExtra(Const.CLASS_ID_TAG,secondClasses.get(position).getClassID());
                startActivity(intent1);
            }
        });
        mallRecycle.setAdapter(productClassifyAdapter);

        getFirstTab("1", "");
        mallTab.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
            @Override
            public void onItemClick(int index) {

            }

            @Override
            public void onVerticalItemClick(int index, View view) {
//                searchClassID = mallClassBeans.get(index).getClassID();
                mallTab.setSelectStyleVertical(index,R.drawable.shape_product_classify_tab_select,R.drawable.shape_product_classify_tab_unselect);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_search:
                Intent intent = new Intent(ProductClassifyActivity.this, BusinessAndProductSearchActivity.class);
                intent.putExtra(Const.SEARCH_TYPE,1);
//                intent.putExtra(Const.CLASS_ID_TAG,searchClassID);
                startActivity(intent);
                break;
        }
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
                            imgs[i] = R.drawable.selector_product_tab_bg;
                        }
//                        searchClassID = mallClassBeans.get(0).getClassID();
                        mallTab.setDataSource(titles, imgs, 0);
                        mallTab.setTextStyle(13, R.color.COLOR_FF333333, R.color.COLOR_FF1677FF);
                        mallTab.initDatasVertical();
                        mallTab.setSelectStyleVertical(0,R.drawable.shape_product_classify_tab_select,R.drawable.shape_product_classify_tab_unselect);
                        getSecondMemu();
                        mallTab.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
                            @Override
                            public void onItemClick(int index) {
                            }

                            @Override
                            public void onVerticalItemClick(int index, View view) {
//                                searchClassID = mallClassBeans.get(index).getClassID();
                                mallTab.setSelectStyleVertical(index,R.drawable.shape_product_classify_tab_select,R.drawable.shape_product_classify_tab_unselect);
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
                ToastUtils.showShortToast(ProductClassifyActivity.this, msg);
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
                    productClassifyAdapter.notifyDataSetChanged();
                    if (mallClassBeans != null && mallClassBeans.size() > 0) {
                        secondClasses.addAll(mallClassBeans);
                        productClassifyAdapter.notifyDataSetChanged();
                        mallRecycle.scrollToPosition(0);
                    }

                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(ProductClassifyActivity.this, msg);
            }
        }, null);
    }
}
