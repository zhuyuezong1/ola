package com.kasa.ola.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ProductCommentBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProductCommentAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductCommentListActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_product_comments)
    LoadMoreRecyclerView rvProductComments;
    private String productID;
    private int currentPage = 1;
    private List<ProductCommentBean> productCommentBeans = new ArrayList<>();
    private ProductCommentAdapter productCommentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_comment);
        ButterKnife.bind(this);
        productID = getIntent().getStringExtra(Const.MALL_GOOD_ID_KEY);
        initTitle();
        initView();
        loadComment(true);
    }

    private void initView() {
        productCommentAdapter = new ProductCommentAdapter(ProductCommentListActivity.this, productCommentBeans);
        rvProductComments.setLoadingListener(this);
        rvProductComments.setLoadingMoreEnabled(true);
        rvProductComments.setAdapter(productCommentAdapter);
    }

    private void initTitle() {
        setActionBar(getString(R.string.product_comment_title),"");
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadComment(false,true);
    }
    private void loadComment(boolean isFirst) {
        loadComment(isFirst, false);
    }

    private void loadComment(boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productID", productID);
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE * 4);
        ApiManager.get().getData(Const.GET_PRODUCT_COMMENTS_LIST_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("commentsList");
                    if (!isLoadMore) {
                        productCommentBeans.clear();
                        productCommentAdapter.notifyDataSetChanged();
                    }
                    List<ProductCommentBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductCommentBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productCommentBeans.addAll(list);
                        productCommentAdapter.notifyDataSetChanged();
                        rvProductComments.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(ProductCommentListActivity.this, msg);
            }
        }, null);

    }
}
