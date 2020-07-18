package com.kasa.ola.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.ConfirmOrderActivity;
import com.kasa.ola.ui.adapter.CartAdapter;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CartFragment extends BaseFragment implements View.OnClickListener {


    Unbinder unbinder;
    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.iv_back_frag)
    ImageView ivBackFrag;
    @BindView(R.id.tv_title_frag)
    TextView tvTitleFrag;
    @BindView(R.id.tv_right_text_frag)
    TextView tvRightTextFrag;
    @BindView(R.id.webProgressBar)
    ProgressBar webProgressBar;
    @BindView(R.id.view_actionbar_frag)
    LinearLayout viewActionbarFrag;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.product_list)
    RecyclerView productList;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.title_total)
    TextView titleTotal;
    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.btn_strike)
    Button btnStrike;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.sl_layout)
    SwipeRefreshLayout slLayout;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    private LayoutInflater inflater;
    //    public static String selectPayType = "";
    private boolean isFirst = true;
    private ArrayList<CartBean.ShoppingCartBean> cartProductModels;
    private double total = 0;
    private int adapterType = 0;//0为正常商城购物车列表，1为删除页面的列表
    private CartAdapter cartAdapter;
    private boolean selectAll = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }


    private void getData(boolean isFirst) {
        ApiManager.get().getData(Const.GET_SHOP_CART_LIST_TAG, new JSONObject().put("userID", LoginHandler.get().getUserId()), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    String jsonData = ((JSONObject) responseModel.data).toString();
//                    ToastUtils.showLongToast(getApplicationContext(), "请求成功");
                    CartBean cartBean = JsonUtils.jsonToObject(jsonData, CartBean.class);
                    List<CartBean.ShoppingCartBean> shoppingCartList = cartBean.getShoppingCartList();
                    List<CartBean.ShoppingCartBean> invalidProductList = cartBean.getInvalidProductList();
                    cartProductModels = new ArrayList<>();
                    if (shoppingCartList != null && shoppingCartList.size() > 0) {
                        cartProductModels.addAll(shoppingCartList);
                    }
                    if (invalidProductList != null && invalidProductList.size() > 0) {
                        for (int i = 0; i < invalidProductList.size(); i++) {
                            invalidProductList.get(i).setEffective(true);
                        }
                        cartProductModels.addAll(invalidProductList);
                    }
                    initData();
                    if (cartProductModels.size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onBusinessError(int code, String msg) {
                slLayout.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, isFirst ? loadingView : null);
    }

    private void initData() {
        double calculateTotal = getTotal(cartProductModels);
        total = CommonUtils.getTwoPoint(calculateTotal);
        totalPrice.setText("￥" + CommonUtils.getTwoZero(total));
        btnStrike.setBackgroundResource((total == 0) ? R.drawable.shape_corner_d5d5d5_20 : R.drawable.shape_confirm_corner);
        btnStrike.setEnabled(total != 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productList.setLayoutManager(layoutManager);

//        initData();
        cartAdapter = new CartAdapter(getContext(), cartProductModels, adapterType);
        cartAdapter.setTotalPriceListener(new CartAdapter.TotalPriceListener() {
            @Override
            public void addOrSub(List<CartBean.ShoppingCartBean> cartProductModels) {
//                total = getTotal(cartProductModels);
                double calculateTotal = getTotal(cartProductModels);
                total = CommonUtils.getTwoPoint(calculateTotal);
                totalPrice.setText("￥" + CommonUtils.getTwoZero(total));
                btnStrike.setBackgroundResource((total == 0) ? R.drawable.shape_corner_d5d5d5_20 : R.drawable.shape_confirm_corner);
                btnStrike.setEnabled(total != 0);

                for (int i = 0; i < cartProductModels.size(); i++) {
                    if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
                        List<CartBean.Product> productList = cartProductModels.get(i).getProductList();
                        for (int j = 0; j < productList.size(); j++) {
                            if (productList.get(j).isSelect()) {
//                                selectPayType = productList.get(j).getPayType();
                                return;
                            }
                        }
                    }
                }
//                selectPayType = "";
            }
        });
        productList.setAdapter(cartAdapter);
    }

    private double getTotal(List<CartBean.ShoppingCartBean> cartProductModels) {
        if (cartProductModels != null) {
            double total = 0;
            for (int i = 0; i < cartProductModels.size(); i++) {
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (cartProductModels.get(i).isEffective()) {//有效商品
                    if (list != null) {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).isSelect()) {
                                total += Integer.parseInt(list.get(j).getProductNum()) * Double.parseDouble(list.get(j).getPrice());
                            }
                        }
                    }
                }

            }
            return total;
        }
        return 0;
    }

    private void initView() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        viewShadow.setBackgroundColor(Color.parseColor("#ffffff"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }


        ivBackFrag.setVisibility(View.GONE);
        tvTitleFrag.setText(getString(R.string.shop_cart));
        tvRightTextFrag.setVisibility(View.VISIBLE);
        tvRightTextFrag.setTextColor(getResources().getColor(R.color.COLOR_80040404));
        tvRightTextFrag.setText(getString(R.string.edit));
        tvRightTextFrag.setOnClickListener(this);
        ivCheck.setOnClickListener(this);
        btnStrike.setOnClickListener(this);
        slLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(false);
            }
        });
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                getData(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst) {
            getData(true);
            isFirst = false;
        } else {
            getData(false);
        }
//        selectPayType = "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_text_frag:
                switchCartEdit(tvRightTextFrag.getText().toString());
                break;
            case R.id.iv_check:
                if (selectAll) {
                    ivCheck.setBackgroundResource(R.mipmap.uncheck_icon);
                    selectAll = false;
                    selectAllOrNull(selectAll);
                } else {
                    ivCheck.setBackgroundResource(R.mipmap.icon_selected);
                    selectAll = true;
                    selectAllOrNull(selectAll);
                }
                btnStrike.setEnabled(total!=0);
                break;
            case R.id.btn_strike:
                switchCartSettle(btnStrike.getText().toString());
                break;
        }
    }
    private void selectAllOrNull(boolean selectAll) {
        if (selectAll) {
            for (int i = 0; i < cartProductModels.size(); i++) {
                cartProductModels.get(i).setSelect(true);
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
                    for (int j = 0; j < list.size(); j++) {
                        list.get(j).setSelect(true);
                    }
                }
            }
        } else {
            for (int i = 0; i < cartProductModels.size(); i++) {
                cartProductModels.get(i).setSelect(false);
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
                    for (int j = 0; j < list.size(); j++) {
                        list.get(j).setSelect(false);
                    }
                }
            }
        }
        cartAdapter.notifyDataSetChanged();
        double calculateTotal = getTotal(cartProductModels);
        total = CommonUtils.getTwoPoint(calculateTotal);
        totalPrice.setText( "￥"+CommonUtils.getTwoZero(total));
        btnStrike.setBackgroundResource((total == 0) && btnStrike.getText().equals(getString(R.string.settle)) ? R.drawable.shape_corner_d5d5d5_20 : R.drawable.shape_confirm_corner);
    }
    private void switchCartSettle(String s) {
        if (s.equals(getString(R.string.settle))) {
            //跳转到结算页面
            if (total != 0) {
                Intent intent = new Intent(getContext(), ConfirmOrderActivity.class);
                ArrayList<CartBean.ShoppingCartBean> objects = new ArrayList<>();
                for (int i = 0; i < cartProductModels.size(); i++) {
                    List<CartBean.Product> list = cartProductModels.get(i).getProductList();

                    if (cartProductModels.get(i).isEffective()) {
                        CartBean.ShoppingCartBean shoppingCartList = new CartBean.ShoppingCartBean();
                        ArrayList<CartBean.Product> selectCartProductModels = new ArrayList<>();
                        if (list != null) {
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).isSelect()) {
                                    selectCartProductModels.add(list.get(j));
                                }
                            }
                            shoppingCartList.setSuppliers(cartProductModels.get(i).getSuppliers());
                            shoppingCartList.setProductList(selectCartProductModels);
                        }
                        if (shoppingCartList.getProductList() != null && shoppingCartList.getProductList().size() > 0) {
                            objects.add(shoppingCartList);
                        }
                    }
                }
                intent.putExtra(Const.PRODUCT_LIST_KEY, objects);
                intent.putExtra(Const.COMMIT_ORDER_ENTER_TYPE, 1);
//                intent.putExtra("payType", Integer.parseInt(selectPayType));
                startActivity(intent);
            }
        } else if (s.equals(getString(R.string.delete))) {
            String productIds = "";
            for (int i = 0; i < cartProductModels.size(); i++) {
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (cartProductModels.get(i).isEffective()) {
                    if (list == null || list.size() == 0) {
                        cartProductModels.remove(i);
                    } else {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).isSelect()) {
                                if (j != list.size() - 1) {
                                    productIds += list.get(j).getShopProductID() + ",";
                                } else {
                                    productIds += list.get(j).getShopProductID();
                                }

                            }
                        }
                    }
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userID", LoginHandler.get().getUserId());
            jsonObject.put("shopProductID", productIds);
            ApiManager.get().getData(Const.GET_PRODUCT_DELETE_LIST_TAG, jsonObject, new BusinessBackListener() {
                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    for (int i = 0; i < cartProductModels.size(); i++) {
                        List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                        if (cartProductModels.get(i).isEffective()) {
                            if (list == null || list.size() == 0) {
                                cartProductModels.remove(i);
                            } else {
                                for (int j = list.size(); j > 0; j--) {
                                    if (list.get(j - 1).isSelect()) {
                                        list.remove(j - 1);
                                    }
                                }
                                if (list == null || list.size() == 0) {
                                    cartProductModels.remove(i);
                                }
                            }
                        }
                    }
                    cartAdapter.notifyDataSetChanged();

                    double calculateTotal = getTotal(cartProductModels);
                    total = CommonUtils.getTwoPoint(calculateTotal);
                    totalPrice.setText("￥" + CommonUtils.getTwoZero(total));
                    btnStrike.setBackgroundResource((total == 0) ? R.drawable.shape_corner_d5d5d5_20 : R.drawable.shape_confirm_corner);
                    btnStrike.setEnabled(total != 0);

                    for (int i = 0; i < cartProductModels.size(); i++) {
                        if (cartProductModels.get(i).isEffective()) {
                            List<CartBean.Product> productList = cartProductModels.get(i).getProductList();
                            for (int j = 0; j < productList.size(); j++) {
                                if (productList.get(j).isSelect()) {
//                                    selectPayType = productList.get(j).getPayType();
                                    return;
                                }
                            }
                        }
                    }
//                    selectPayType = "";

                    if (cartProductModels.size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    ToastUtils.showLongToast(getContext(), msg);
                }
            }, null);
        }
    }

    private void switchCartEdit(String s) {
        if (s.equals(getString(R.string.edit))) {//去编辑状态
            adapterType = 1;
            tvRightTextFrag.setText(getString(R.string.complete));
            totalPrice.setVisibility(View.GONE);
            titleTotal.setVisibility(View.GONE);
            btnStrike.setText(getString(R.string.delete));
            btnStrike.setBackgroundResource((total == 0) ? R.drawable.shape_corner_d5d5d5_20 : R.drawable.shape_confirm_corner);
            btnStrike.setEnabled(total != 0);
            cartAdapter.notifyDataSetChanged();
        } else if (s.equals(getString(R.string.complete))) {//去完成状态
            adapterType = 0;
            tvRightTextFrag.setText(getString(R.string.edit));
            totalPrice.setVisibility(View.VISIBLE);
//            totalPrice.setText(totalFuStr + "+￥" + total);
            titleTotal.setVisibility(View.VISIBLE);
            btnStrike.setText(getString(R.string.settle));
            btnStrike.setBackgroundResource((total == 0) ? R.drawable.shape_corner_d5d5d5_20 : R.drawable.shape_confirm_corner);
            btnStrike.setEnabled(total != 0);
            cartAdapter.notifyDataSetChanged();
        }
    }
}
