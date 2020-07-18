package com.kasa.ola.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.StoreInfoBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.CheckAccountActivity;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.ui.StoreInfoEditActivity;
import com.kasa.ola.ui.StoreOrderActivity;
import com.kasa.ola.ui.StoreProductActivity;
import com.kasa.ola.ui.adapter.ImageShowAdapter;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.JsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class StoreOperationFragment extends BaseFragment implements View.OnClickListener {


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
    Unbinder unbinder;
    @BindView(R.id.tv_verification)
    TextView tvVerification;
    @BindView(R.id.tv_check_account)
    TextView tvCheckAccount;
    @BindView(R.id.tv_order)
    TextView tvOrder;
    @BindView(R.id.tv_product)
    TextView tvProduct;
    @BindView(R.id.tv_store_name)
    TextView tvStoreName;
    @BindView(R.id.tv_store_open_time)
    TextView tvStoreOpenTime;
    @BindView(R.id.tv_store_address)
    TextView tvStoreAddress;
    @BindView(R.id.tv_store_phone)
    TextView tvStorePhone;
    @BindView(R.id.tv_photo_edit)
    TextView tvPhotoEdit;
    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.rv_store_image)
    RecyclerView rvStoreImage;
    @BindView(R.id.ll_store_operation)
    LinearLayout llStoreOperation;
    @BindView(R.id.tv_checking)
    TextView tvChecking;
    private LayoutInflater inflater;
    private int defaultColor = R.color.COLOR_FF1677FF;
    private ImageShowAdapter imageShowAdapter;
    private List<String> shopImageUrls = new ArrayList<>();
    private StoreInfoBean storeInfoBean;
    private String businessLvl;
    private View rootView;
    private boolean isDark = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_store_operation, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        businessLvl = LoginHandler.get().getMyInfo().optString("businessLvl");
        initTitle(rootView);
        initView();
        initEvent();
        return rootView;
    }

    private void getData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.GET_SUPPLIERS_INFO, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    storeInfoBean = JsonUtils.jsonToObject(jo.toString(), StoreInfoBean.class);
                    showInfo(storeInfoBean);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {

            }
        }, null);
    }

    private void showInfo(StoreInfoBean storeInfoBean) {
        if (storeInfoBean == null) {
            return;
        }
        tvStoreName.setText(storeInfoBean.getShopName());
        tvStoreOpenTime.setText(storeInfoBean.getBusinessHours());
        tvStoreAddress.setText(storeInfoBean.getProvince() + storeInfoBean.getCity() + storeInfoBean.getArea() + storeInfoBean.getAddressDetail());
        tvStorePhone.setText(storeInfoBean.getMobile());
        shopImageUrls.clear();
        imageShowAdapter.notifyDataSetChanged();
        if (storeInfoBean.getShopImageUrls() != null && storeInfoBean.getShopImageUrls().size() > 0) {
            shopImageUrls.addAll(storeInfoBean.getShopImageUrls());
            imageShowAdapter.notifyDataSetChanged();
        }

    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false);
        rvStoreImage.setLayoutManager(gridLayoutManager);
        imageShowAdapter = new ImageShowAdapter(getContext(), shopImageUrls);
        rvStoreImage.setAdapter(imageShowAdapter);
        tvChecking.setText(getString(R.string.fragment_checking_modify_text));
        if (businessLvl.equals("1")) {
            llStoreOperation.setVisibility(View.VISIBLE);
            tvChecking.setVisibility(View.GONE);
            getData();
        } else if (businessLvl.equals("3")) {
            llStoreOperation.setVisibility(View.GONE);
            tvChecking.setVisibility(View.VISIBLE);
        }
    }

    private void initEvent() {
        tvVerification.setOnClickListener(this);
        tvCheckAccount.setOnClickListener(this);
        tvOrder.setOnClickListener(this);
        tvProduct.setOnClickListener(this);
        tvPhotoEdit.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    private void initTitle(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setActionBar(view, getString(R.string.store_operation));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
            lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
            if (businessLvl.equals("1")){
                isDark= false;

                viewActionbarFrag.setBackgroundColor(getContext().getColor(defaultColor));
                ivBackFrag.setVisibility(View.GONE);
                tvTitleFrag.setTextColor(getContext().getColor(R.color.white));
                viewShadow.setBackgroundColor(getContext().getColor(R.color.COLOR_FF1677FF));
            }else if (businessLvl.equals("3")){
                isDark = true;
                viewActionbarFrag.setBackgroundColor(getContext().getColor(R.color.white));
                ivBackFrag.setVisibility(View.GONE);
                tvTitleFrag.setTextColor(getContext().getColor(R.color.COLOR_FF333333));
                viewShadow.setBackgroundColor(getContext().getColor(R.color.white));
            }
            ImmersionBar.with(this)
                    .statusBarDarkFont(isDark)
                    .init();
//            setStatusBar(defaultColor);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.destroy(StoreOperationFragment.this);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden )
            ImmersionBar.with(getActivity()).statusBarDarkFont(isDark).init();
    }
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if (hidden) {   // 不在最前端显示 相当于调用了onPause();
//            ImmersionBar.with(this)
//                    .statusBarDarkFont(false)
//                    .init();
//        } else {  // 在最前端显示 相当于调用了onResume();
//            if (businessLvl.equals("1")){
//                ImmersionBar.with(this)
//                        .statusBarDarkFont(false)
//                        .init();
//            }else if (businessLvl.equals("3")){
//                ImmersionBar.with(this)
//                        .statusBarDarkFont(true)
//                        .init();
//            }
//        }
//    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_verification:
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.scanCode();
                }
                break;
            case R.id.tv_check_account:
                Intent checkAccountIntent = new Intent(getContext(), CheckAccountActivity.class);
                checkAccountIntent.putExtra(Const.STORE_ORDER_TAG, 0);
                startActivity(checkAccountIntent);
                break;
            case R.id.tv_order:
                Intent orderIntent = new Intent(getContext(), StoreOrderActivity.class);
                startActivity(orderIntent);
                break;
            case R.id.tv_product:
                Intent storeProductIntent = new Intent(getContext(), StoreProductActivity.class);
                startActivity(storeProductIntent);
                break;
            case R.id.tv_photo_edit:
                Intent storeInfoIntent = new Intent(getContext(), StoreInfoEditActivity.class);
                storeInfoIntent.putExtra(Const.STORE_INFO_EDIT, storeInfoBean);
                startActivityForResult(storeInfoIntent, Const.STORE_INFO_EDIT_SUBMIT_SUCCESS);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Const.STORE_INFO_EDIT_SUBMIT_SUCCESS) {
                ApiManager.get().getMyInfo(new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        businessLvl = LoginHandler.get().getMyInfo().optString("businessLvl");
                        initTitle(rootView);
                        initView();
                        EventBus.getDefault().post(new EventCenter.RefreshMyInfo());
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {

                    }
                });
            }
        }
    }
}
