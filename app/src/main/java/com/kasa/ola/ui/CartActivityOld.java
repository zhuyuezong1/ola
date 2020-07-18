package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CartBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.CartAdapter;
import com.kasa.ola.utils.JsonUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartActivityOld extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.product_list)
    RecyclerView productList;
    @BindView(R.id.iv_check)
    ImageView ivCheck;
    @BindView(R.id.total_price)
    TextView totalPrice;
    @BindView(R.id.btn_strike)
    Button btnStrike;
    @BindView(R.id.title_total)
    TextView titleTotal;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    //    @BindView(R.id.walk_around)
//    TextView walkAround;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.sl_layout)
    SwipeRefreshLayout slLayout;
    private boolean selectAll = false;
    private double total = 0;
//    private double totalFu = 0;
//    private String totalFuStr = "";
    private CartAdapter cartAdapter;
    private ArrayList<CartBean.ShoppingCartBean> cartProductModels;
    public static String selectPayType = "";
    private int adapterType = 0;//0为正常商城购物车列表，1为删除页面的列表

    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_old);
        ButterKnife.bind(this);
        initTitle();
        initView();
    }

    private void initView() {
        slLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(false);
            }
        });
        productList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                slLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                getData(false);
            }
        });
        getData(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (isFirst) {
//            getData(true);
//            isFirst = false;
//        } else {
//            getData(false);
//        }
        selectPayType = "";
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
                    cartProductModels.addAll(shoppingCartList);
                    cartProductModels.addAll(invalidProductList);
//                    cartProductModels.addAll(invalidProductList);
//                    cartAdapter.notifyDataSetChanged();
                    initData();
                    if (cartProductModels.size() > 0) {
                        llNoData.setVisibility(View.GONE);
                        slLayout.setVisibility(View.VISIBLE);
                    } else {
                        llNoData.setVisibility(View.VISIBLE);
                        slLayout.setVisibility(View.GONE);
                    }
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onBusinessError(int code, String msg) {
                slLayout.setRefreshing(false);
                ToastUtils.showLongToast(CartActivityOld.this,msg);
            }
        }, isFirst ? loadingView : null);
    }

    private void initData() {
        getTotal(cartProductModels);
        totalPrice.setText("￥" + total);
        btnStrike.setBackgroundResource((total == 0) ? R.drawable.bg_rectangle_grey_corner_1 : R.drawable.bg_rectangle_blue_corner_1);
        btnStrike.setEnabled(total != 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productList.setLayoutManager(layoutManager);

//        initData();
        cartAdapter = new CartAdapter(this, cartProductModels, adapterType);
        cartAdapter.setTotalPriceListener(new CartAdapter.TotalPriceListener() {
            @Override
            public void addOrSub(List<CartBean.ShoppingCartBean> cartProductModels) {
//                total = getTotal(cartProductModels);
                getTotal(cartProductModels);
                totalPrice.setText("￥" + total + "");
                btnStrike.setBackgroundResource((total == 0) ? R.drawable.bg_rectangle_grey_corner_1 : R.drawable.bg_rectangle_blue_corner_1);
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
                selectPayType = "";
            }
        });
        productList.setAdapter(cartAdapter);
    }

    private void initTitle() {
        setActionBar(getString(R.string.shop_cart), getString(R.string.edit));
    }


    @OnClick({R.id.tv_right_text, R.id.iv_check, R.id.btn_strike})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_right_text://编辑
                switchCartEdit(tvRightText.getText().toString());
                break;
            case R.id.iv_check:
                if (selectAll) {
                    ivCheck.setBackgroundResource(R.mipmap.uncheck_icon);
                    selectAll = false;
                    selectAllOrNull(selectAll);
                } else {
                    ivCheck.setBackgroundResource(R.mipmap.check_icon);
                    selectAll = true;
                    selectAllOrNull(selectAll);
                }
                btnStrike.setEnabled(total!=0);
                break;
            case R.id.btn_strike:
                switchCartSettle(btnStrike.getText().toString());
//                cartAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void switchCartSettle(String s) {
        if (s.equals(getString(R.string.settle))) {
            //跳转到结算页面
            if (total != 0) {
                Intent intent = new Intent(this, ConfirmOrderActivity.class);
                ArrayList<CartBean.ShoppingCartBean> objects = new ArrayList<>();

                for (int i = 0; i < cartProductModels.size(); i++) {
                    List<CartBean.Product> list = cartProductModels.get(i).getProductList();

                    CartBean.ShoppingCartBean shoppingCartBean = new CartBean.ShoppingCartBean();
                    ArrayList<CartBean.Product> selectCartProductModels = new ArrayList<>();
                    if (list != null) {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).isSelect()) {
                                selectCartProductModels.add(list.get(j));
                            }
                        }
                        shoppingCartBean.setSuppliers(cartProductModels.get(i).getSuppliers());
                        shoppingCartBean.setSuppliersID(cartProductModels.get(i).getSuppliersID());
                        shoppingCartBean.setProductList(selectCartProductModels);
                    }
                    if (shoppingCartBean.getProductList() != null && shoppingCartBean.getProductList().size() > 0) {
                        objects.add(shoppingCartBean);
                    }
                }
                intent.putExtra(Const.PRODUCT_LIST_KEY, objects);
                intent.putExtra(Const.COMMIT_ORDER_ENTER_TYPE, 1);
                startActivity(intent);
            }
        } else if (s.equals(getString(R.string.delete))) {
           /* for (int i = 0; i < cartProductModels.size(); i++) {
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (!TextUtils.isEmpty(cartProductModels.get(i).getShopName())) {
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
            }*/
            String productIds = "";
            List<String> productIdList = new ArrayList<>();
            for (int i = 0; i < cartProductModels.size(); i++) {
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
                    if (list == null || list.size() == 0) {
                        cartProductModels.remove(i);
                    } else {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).isSelect()) {
                                productIdList.add(list.get(j).getShopProductID());
//                                if (j != list.size() - 1) {
//                                    productIds += list.get(j).getShopProductID() + ",";
//                                } else {
//                                    productIds += list.get(j).getShopProductID();
//                                }

                            }
                        }
                    }
                }
            }
            if (productIdList.size()>0){
                for (int i=0;i<productIdList.size();i++){
                    if (productIdList.size()==1){
                        productIds += productIdList.get(i);
                    }else {
                        if (i != productIdList.size() - 1){
                            productIds += productIdList.get(i) + ",";
                        }else {
                            productIds += productIdList.get(i);
                        }
                    }
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userID", LoginHandler.get().getUserId());
            jsonObject.put("shopProductID", productIds);
            ApiManager.get().getData(Const.DELETE_SHOP_CART_PRODUCT_TAG, jsonObject, new BusinessBackListener() {
                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    for (int i = 0; i < cartProductModels.size(); i++) {
                        List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                        if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
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

                    getTotal(cartProductModels);
                    totalPrice.setText("￥" + total + "");
                    btnStrike.setBackgroundResource((total == 0) ? R.drawable.bg_rectangle_grey_corner_1 : R.drawable.bg_rectangle_blue_corner_1);
                    btnStrike.setEnabled(total != 0);

                    for (int i = 0; i < cartProductModels.size(); i++) {
                        if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
                            List<CartBean.Product> productList = cartProductModels.get(i).getProductList();
                            for (int j = 0; j < productList.size(); j++) {
                                if (productList.get(j).isSelect()) {
//                                    selectPayType = productList.get(j).getPayType();
                                    return;
                                }
                            }
                        }
                    }
                    selectPayType = "";

                    if (cartProductModels.size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    ToastUtils.showLongToast(getApplicationContext(), msg);
                }
            }, null);
        }
    }

    private void switchCartEdit(String s) {
        if (s.equals(getString(R.string.edit))) {//去编辑状态
            adapterType = 1;
            tvRightText.setText(getString(R.string.complete));
            totalPrice.setVisibility(View.GONE);
            titleTotal.setVisibility(View.GONE);
            btnStrike.setText(getString(R.string.delete));
            btnStrike.setBackgroundResource((total == 0) ? R.drawable.bg_rectangle_grey_corner_1 : R.drawable.bg_rectangle_blue_corner_1);
            btnStrike.setEnabled(total != 0);
            cartAdapter.notifyDataSetChanged();
        } else if (s.equals(getString(R.string.complete))) {//去完成状态
            adapterType = 0;
            tvRightText.setText(getString(R.string.edit));
            totalPrice.setVisibility(View.VISIBLE);
//            totalPrice.setText(totalFuStr + "+￥" + total);
            titleTotal.setVisibility(View.VISIBLE);
            btnStrike.setText(getString(R.string.settle));
            btnStrike.setBackgroundResource((total == 0) ? R.drawable.bg_rectangle_grey_corner_1 : R.drawable.bg_rectangle_blue_corner_1);
            btnStrike.setEnabled(total != 0);
            cartAdapter.notifyDataSetChanged();
        }
    }

    private void selectAllOrNull(boolean selectAll) {
        if (selectAll) {
            for (int i = 0; i < cartProductModels.size(); i++) {
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
                    for (int j = 0; j < list.size(); j++) {
                        list.get(j).setSelect(true);
                    }
                }
            }
        } else {
            for (int i = 0; i < cartProductModels.size(); i++) {
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {
                    for (int j = 0; j < list.size(); j++) {
                        list.get(j).setSelect(false);
                    }
                }
            }
        }
        cartAdapter.notifyDataSetChanged();
        getTotal(cartProductModels);
        totalPrice.setText(total + "");
        btnStrike.setBackgroundResource((total == 0) && btnStrike.getText().equals(getString(R.string.settle)) ? R.drawable.bg_rectangle_grey_corner_1 : R.drawable.bg_rectangle_blue_corner_1);
    }

    private void getTotal(List<CartBean.ShoppingCartBean> cartProductModels) {
        if (cartProductModels != null) {
            total = 0;
            for (int i = 0; i < cartProductModels.size(); i++) {
                List<CartBean.Product> list = cartProductModels.get(i).getProductList();
                if (!TextUtils.isEmpty(cartProductModels.get(i).getSuppliers())) {//有效商品
                    if (list != null) {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).isSelect()) {
                                total += Integer.parseInt(list.get(j).getProductNum()) * Double.parseDouble(list.get(j).getPrice());
//                                if (!list.get(j).getPayType().equals("0")) {
//                                    totalFu += Integer.parseInt(list.get(j).getProductNum()) * Double.parseDouble(list.get(j).getFuPrice());
//                                    totalFuStr = totalFu + list.get(j).getFuPriceUnit();
//                                }
                            }
                        }
                    }
                }

            }
//            return total;
        }
//        return 0;
    }

//    @Override
//    public void addOrSub(boolean isSelect,double price) {
//        if (isSelect){
//            total += price;
//        }
//        totalPrice.setText(price+"");
//    }
}
