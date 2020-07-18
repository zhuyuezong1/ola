package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.MallProductBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProductAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {

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
    @BindView(R.id.tv_no_result)
    TextView tvNoResult;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    private List<MallProductBean> mallProductBeans = new ArrayList<>();
    private int productType;
    private int currentPage = 1;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        productType = getIntent().getIntExtra(Const.PRODUCT_TYPE_KEY, 0);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        switch (productType) {
            case 1:
                setActionBar(getString(R.string.vip_product), "");
                break;
            case 2:
                setActionBar(getString(R.string.promote_sales), "");
                break;
            case 3:
                setActionBar(getString(R.string.hot_products), "");
                break;
            case 4:
                setActionBar(getString(R.string.everyday_new_products), "");
                break;
            case 5:
                setActionBar(getString(R.string.must_buy_quality_goods), "");
                break;
            case 6:
                setActionBar(getString(R.string.surprise_brand), "");
                break;
            case 7:
                setActionBar(getString(R.string.good_product), "");
                break;
            case 8:
                setActionBar(getString(R.string.today_product), "");
                break;
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                currentPage = 1;
                loadPage(true);
            }
        });

        productAdapter = new ProductAdapter(ProductListActivity.this, mallProductBeans);
        productAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MallProductBean mallProductBean = mallProductBeans.get(position);
                String productID = mallProductBean.getProductID();
                Intent intent = new Intent(ProductListActivity.this, ProductInfoActivity.class);
                intent.putExtra(Const.MALL_GOOD_ID_KEY, productID);
                startActivity(intent);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ProductListActivity.this, 2);
        rvProducts.setLayoutManager(gridLayoutManager);
        rvProducts.setLoadingListener(this);
        rvProducts.setLoadingMoreEnabled(true);
        rvProducts.setAdapter(productAdapter);
        loadPage(true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jo = new JSONObject();
        jo.put("pageNum", currentPage);
        jo.put("pageSize", Const.PAGE_SIZE * 4);
        jo.put("homeClassType", productType + "");
        ApiManager.get().getData(Const.GET_HOME_TYPE_PRODUCT_LIST, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                refreshLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    if (!isLoadMore) {
                        mallProductBeans.clear();
                    }
                    JSONArray ja = jo.optJSONArray("mallProductList");
                    List<MallProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<MallProductBean>>() {
                    }.getType());

                    if (list != null && list.size() > 0) {
                        mallProductBeans.addAll(list);
                        productAdapter.notifyDataSetChanged();
                        rvProducts.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            tvNoResult.setVisibility(View.GONE);
                            rvProducts.setVisibility(View.VISIBLE);
                        } else {
                            tvNoResult.setVisibility(View.VISIBLE);
                            rvProducts.setVisibility(View.GONE);
                        }
                    }
                }else {
                    rvProducts.setLoadingMoreEnabled(false);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                refreshLayout.setRefreshing(false);
                ToastUtils.showShortToast(ProductListActivity.this, msg);
            }
        }, isFirst ? loadingView : null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
}
