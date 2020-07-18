package com.kasa.ola.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.RecommendProductAdapter;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MallProductListActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.ll_home_title)
    LinearLayout llHomeTitle;
    @BindView(R.id.rl_price)
    RelativeLayout rlPrice;
    @BindView(R.id.rl_sales)
    RelativeLayout rlSales;
    @BindView(R.id.ll_sifting)
    LinearLayout llSifting;
    @BindView(R.id.rv_products)
    LoadMoreRecyclerView rvProducts;
    @BindView(R.id.sl_layout)
    SwipeRefreshLayout slLayout;
    @BindView(R.id.iv_my_cart)
    ImageView ivMyCart;
    @BindView(R.id.tv_price_sort)
    TextView tvPriceSort;
    @BindView(R.id.tv_sale_sort)
    TextView tvSaleSort;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    private int priceSort = 0;
    private int saleSort = 0;
    private int selectTab = 0;
    private String searchContent;
    private String classID;
    private int currentPage = 1;
    private String key = "";
    private RecommendProductAdapter recommendProductAdapter;
    private List<ProductBean> productBeans = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_product_list);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        searchContent = intent.getStringExtra(Const.SEARCH_TAG);
        classID = intent.getStringExtra(Const.CLASS_ID_TAG);
        key = searchContent;
        initView();
        loadPage(true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        jsonObject.put("key", key);
        jsonObject.put("classID", TextUtils.isEmpty(classID)?"":classID);
        jsonObject.put("priceSort", getRankValue(priceSort));
        jsonObject.put("saleSort", getRankValue(saleSort));
        ApiManager.get().getData(Const.GET_PRODUCT_LIST_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        recommendProductAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        recommendProductAdapter.notifyDataSetChanged();
                        rvProducts.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            rvProducts.setVisibility(View.VISIBLE);
                        } else {
                            rvProducts.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slLayout.setRefreshing(false);
                ToastUtils.showLongToast(MallProductListActivity.this, msg);
            }
        }, null);

    }

    private String getRankValue(int sort) {
        if (sort == 0) {
            return "";
        } else if (sort == 1) {
            return "1";
        } else if (sort == 2) {
            return "0";
        }
        return "";
    }

    private void initView() {
        ivBack.setOnClickListener(this);
        ivMyCart.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        etSearch.setText(TextUtils.isEmpty(searchContent) ? "" : searchContent);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        key = etSearch.getText().toString();
                        search();
                        break;
                }
                return false;
            }
        });
        rlPrice.setOnClickListener(this);
        rlSales.setOnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MallProductListActivity.this, 2);
        rvProducts.setLayoutManager(gridLayoutManager);
        rvProducts.setLoadingListener(this);
        rvProducts.setLoadingMoreEnabled(true);
        recommendProductAdapter = new RecommendProductAdapter(MallProductListActivity.this, productBeans);
        rvProducts.setAdapter(recommendProductAdapter);
        switchTabSelect(selectTab);
        slLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
    }

    private void search() {
        currentPage = 1;
        loadPage(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                key = etSearch.getText().toString();
                search();
                break;
            case R.id.rl_price:
                if (selectTab == 0) {
                    priceSort = 1;
                } else if (selectTab == 1) {
                    if (priceSort == 1) {
                        priceSort = 2;
                    } else if (priceSort == 2) {
                        priceSort = 1;
                    }
                } else if (selectTab == 2) {
                    priceSort = 1;
                }
                saleSort = 0;
                swithPriceImg(priceSort, saleSort);
                selectTab = 1;
                switchTabSelect(selectTab);
                currentPage = 1;
                loadPage(true);
                break;
            case R.id.rl_sales:
                if (selectTab == 0) {
                    saleSort = 1;
                } else if (selectTab == 1) {
                    saleSort = 1;
                } else if (selectTab == 2) {
                    if (saleSort == 1) {
                        saleSort = 2;
                    } else if (saleSort == 2) {
                        saleSort = 1;
                    }
                }
                priceSort = 0;
                swithPriceImg(priceSort, saleSort);
                selectTab = 2;
                switchTabSelect(selectTab);
                currentPage = 1;
                loadPage(true);
                break;
            case R.id.iv_my_cart:
                Intent cartIntent = new Intent(MallProductListActivity.this, CartActivity.class);
                startActivity(cartIntent);
                break;
        }
    }

    private void swithPriceImg(int priceSort, int saleSort) {
        if (priceSort == 0) {
            DisplayUtils.setViewDrawableRight(tvPriceSort, R.mipmap.upanddownbutton);
        } else if (priceSort == 1) {
            DisplayUtils.setViewDrawableRight(tvPriceSort, R.mipmap.upanddownbutton1);
        } else if (priceSort == 2) {
            DisplayUtils.setViewDrawableRight(tvPriceSort, R.mipmap.upanddownbutton2);
        }
        if (saleSort == 0) {
            DisplayUtils.setViewDrawableRight(tvSaleSort, R.mipmap.upanddownbutton);
        } else if (saleSort == 1) {
            DisplayUtils.setViewDrawableRight(tvSaleSort, R.mipmap.upanddownbutton1);
        } else if (saleSort == 2) {
            DisplayUtils.setViewDrawableRight(tvSaleSort, R.mipmap.upanddownbutton2);
        }
    }

    private void switchTabSelect(int selectTab) {
        if (selectTab == 0) {
            rlPrice.setBackgroundResource(R.drawable.bg_rectangle_white_stroke);
            rlSales.setBackgroundResource(R.drawable.bg_rectangle_white_stroke);
//            rlPrice.setBackgroundColor(Color.WHITE);
//            rlSales.setBackgroundColor(Color.WHITE);
//            tvPriceSort.setCompoundDrawables(null,null,getResources().getDrawable(R.mipmap.upanddownbutton),null);
//            tvSaleSort.setCompoundDrawables(null,null,getResources().getDrawable(R.mipmap.upanddownbutton),null);
        } else if (selectTab == 1) {
            rlPrice.setBackgroundResource(R.drawable.bg_rectangle_dddddd_stroke);
            rlSales.setBackgroundResource(R.drawable.bg_rectangle_white_stroke);
//            rlPrice.setBackgroundColor(getResources().getColor(R.color.COLOR_FFDDDDDD));
//            rlSales.setBackgroundColor(Color.WHITE);
//            tvSaleSort.setCompoundDrawables(null,null,getResources().getDrawable(R.mipmap.upanddownbutton),null);
//            saleSort = 0;
        } else if (selectTab == 2) {
            rlPrice.setBackgroundResource(R.drawable.bg_rectangle_white_stroke);
            rlSales.setBackgroundResource(R.drawable.bg_rectangle_dddddd_stroke);
//            rlPrice.setBackgroundColor(Color.WHITE);
//            rlSales.setBackgroundColor(getResources().getColor(R.color.COLOR_FFDDDDDD));
//            tvPriceSort.setCompoundDrawables(null,null,getResources().getDrawable(R.mipmap.upanddownbutton),null);
//            priceSort = 0;
        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

}
