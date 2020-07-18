package com.kasa.ola.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ShopInfoBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.ShopDetailActivity;
import com.kasa.ola.ui.adapter.ShopAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ShopFragment extends BaseFragment implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener {
    @BindView(R.id.rv_organs)
    LoadMoreRecyclerView rvOrgans;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    Unbinder unbinder;
    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.education_home_banner)
    ImageView educationHomeBanner;
    private LayoutInflater inflater;
//    private ImageView educationTrainSpecialSubjectBanner;
    private List<ShopInfoBean> shopInfoBeans = new ArrayList<>();
    private ShopAdapter shopAdapter;
    private int currentPage = 1;
    private boolean isFirst = true;
    private String province;
    private String city;
    private String area;
    private String key;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_shop, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTitle();
        initView();
        loadPage(true);
        return rootView;
    }

    private void initTitle() {
//        tvTitleFrag.setText(getString(R.string.ola_shop_title));
////        tvRightTextFrag.setText(getString(R.string.select_area));
////        tvRightTextFrag.setVisibility(View.VISIBLE);
//        tvRightTextFrag.setOnClickListener(this);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        viewShadow.setBackgroundColor(Color.parseColor("#000000"));
//        ivBackFrag.setVisibility(View.GONE);
        ivSearch.setOnClickListener(this);
    }

    private void initView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOrgans.setLayoutManager(linearLayoutManager);
        rvOrgans.setLoadingListener(this);
        shopAdapter = new ShopAdapter(getContext(), shopInfoBeans);
        shopAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), ShopDetailActivity.class);
                intent.putExtra(Const.SHOP_ID, shopInfoBeans.get(position).getSuppliersID());
                startActivity(intent);
            }
        });

        rvOrgans.setAdapter(shopAdapter);
//        View headView = LayoutInflater.from(getContext()).inflate(R.layout.view_image_head, rvOrgans, false);
//        educationTrainSpecialSubjectBanner = headView.findViewById(R.id.education_home_banner);
//
//        ViewGroup.LayoutParams layoutParams = educationTrainSpecialSubjectBanner.getLayoutParams();
//        int width = DisplayUtils.getScreenWidth(getContext()) - DisplayUtils.dip2px(getContext(), 10) * 2;
//        layoutParams.width = width;
//        layoutParams.height = width / 2;
//        educationTrainSpecialSubjectBanner.setOnClickListener(this);
//        rvOrgans.addHeaderView(headView);

        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (isFirst) {
//            isFirst = false;
//            loadPage(true);
//        } else {
//            loadPage(false);
//        }
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        key = etSearch.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        jsonObject.put("key", key);
        ApiManager.get().getData(Const.GET_SHOP_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slRefresh.setRefreshing(false);
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
//                    String educationBanner = jo.optString("suppliersBanner");
//                    ImageLoaderUtils.imageLoadRound(getContext(), educationBanner, educationTrainSpecialSubjectBanner, R.mipmap.shop_center_banner,false);
//                    ImageLoaderUtils.imageLoadRound(getContext(), educationBanner, educationHomeBanner);
                    JSONArray ja = jo.optJSONArray("suppliersList");
                    if (!isLoadMore) {
                        shopInfoBeans.clear();
                        shopAdapter.notifyDataSetChanged();
                    }
                    List<ShopInfoBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ShopInfoBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        shopInfoBeans.addAll(list);
                        shopAdapter.notifyDataSetChanged();
                        rvOrgans.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            rvOrgans.setVisibility(View.VISIBLE);
                        } else {
                            rvOrgans.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slRefresh.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_right_text_frag:
//                Intent intent2 = new Intent(getContext(), ProvinceListActivity.class);
//                intent2.putExtra(Const.SELECT_AREA_ENTER,1);
//                startActivityForResult(intent2, Const.ACTREQ_PROVINCE);
//                break;
            case R.id.iv_search:
//                Intent intent = new Intent(getContext(), ShopDetailActivity.class);
////                intent.putExtra(Const.SHOP_ID, shopInfoBeans.get(position).getBusinessID());
//                startActivity(intent);
                currentPage = 1;
                loadPage(true);
                break;
        }
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
}
