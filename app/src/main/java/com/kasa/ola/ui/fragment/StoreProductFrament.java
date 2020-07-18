package com.kasa.ola.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ShopProductBean;
import com.kasa.ola.dialog.DoubleBtnCommonDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.StoreAddProductActivity;
import com.kasa.ola.ui.adapter.StoreProductVerticalAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StoreProductFrament extends BaseFragment implements LoadMoreRecyclerView.LoadingListener {
    public static final String STORE_PRODUCT_STATUS_KEY = "STORE_PRODUCT_STATUS_KEY";
    @BindView(R.id.product_recycle_view)
    LoadMoreRecyclerView productRecycleView;
   /* @BindView(R.id.loading_view)
    LoadingView loadingView;*/
    @BindView(R.id.product_refresh_layout)
    SwipeRefreshLayout productRefreshLayout;

    private int type;
    private int currentPage = 1;
    private boolean isFirst = true;
    private boolean isVisibleToUser = true;
    private boolean isInitView = false;
    Unbinder unbinder;
    private StoreProductVerticalAdapter storeProductVerticalAdapter;
    private List<ShopProductBean> productBeans = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(STORE_PRODUCT_STATUS_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_store_product_classify_layout, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        isInitView = true;
        initView();
        return rootView;
    }

    private void initView() {
        productRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
        productRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        productRecycleView.setLoadingListener(this);
        storeProductVerticalAdapter = new StoreProductVerticalAdapter(getContext(), productBeans);
        storeProductVerticalAdapter.setOnStoreProductListListener(new StoreProductVerticalAdapter.OnStoreProductListListener() {
            @Override
            public void onItemClick(int position) {
                ShopProductBean shopProductBean = productBeans.get(position);
                Intent intent = new Intent(getContext(), StoreAddProductActivity.class);
                intent.putExtra(Const.STORE_PRODUCT_DETAILS,shopProductBean);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {
                String productStatus = productBeans.get(position).getProductStatus();
                if (productStatus.equals("3")||productStatus.equals("1")||productStatus.equals("0")){
                    showDeleteDialog(position,2,getContext().getString(R.string.store_delete_product_title),getContext().getString(R.string.store_delete_product_message),
                            getContext().getString(R.string.cancel),getContext().getString(R.string.delete));
                }else {
                    ToastUtils.showLongToast(getContext(),getString(R.string.delete_store_product_tips));
                }
            }

            @Override
            public void downProduct(int position) {
                showDeleteDialog(position,0,getContext().getString(R.string.store_undercarriage_product_title),getContext().getString(R.string.store_undercarriage_product_message),
                        getContext().getString(R.string.cancel),getContext().getString(R.string.undercarriage_action));
            }

            @Override
            public void upProduct(int position) {
                showDeleteDialog(position,1,getContext().getString(R.string.store_put_on_product_title),getContext().getString(R.string.store_put_on_product_message),
                        getContext().getString(R.string.cancel),getContext().getString(R.string.put_on_action));
            }
        });
        productRecycleView.setAdapter(storeProductVerticalAdapter);
    }

    private void showDeleteDialog(int position,int changeType,String title,String content,String leftText,String rightText) {
        DoubleBtnCommonDialog.Builder builder = new DoubleBtnCommonDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(content)
                .setLeftButton(leftText)
                .setRightButton(rightText)
                .setDialogInterface(new DoubleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void leftButtonClick(DoubleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(DoubleBtnCommonDialog dialog) {
                        dialog.dismiss();
                        changeProductStatus(productBeans.get(position).getProductID(),changeType);
                    }
                }).create().show();
    }

    private void changeProductStatus(String productID, int i) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID",LoginHandler.get().getUserId());
        jsonObject.put("productID",productID);
        jsonObject.put("type",i+"");
        ApiManager.get().getData(Const.SUPPLIERS_PRODUCT_ON_OR_OFF, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showLongToast(getContext(),"成功");
                loadPage(true);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(getContext(),msg);
            }
        },null);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser) {
            if (isFirst) {
                isFirst = false;
                loadPage(true);
            } else {
                loadPage(false);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser && isInitView) {
            if (isFirst) {
                isFirst = false;
                loadPage(true);
            } else {
                loadPage(false);
            }
        }
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE / 2);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        jsonObject.put("type", type);
        ApiManager.get().getData(Const.GET_SHOP_PRODUCT_LIST, jsonObject, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                productRefreshLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        storeProductVerticalAdapter.notifyDataSetChanged();
                    }
                    List<ShopProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ShopProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        storeProductVerticalAdapter.notifyDataSetChanged();
                        productRecycleView.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
//                    if (!isLoadMore) {
//                        if (list != null && list.size() > 0) {
//                            rvHotSale.setVisibility(View.VISIBLE);
//                        } else {
//                            rvHotSale.setVisibility(View.GONE);
//                        }
//                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                productRefreshLayout.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, /*isFirst ? loadingView : */null);
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
}
