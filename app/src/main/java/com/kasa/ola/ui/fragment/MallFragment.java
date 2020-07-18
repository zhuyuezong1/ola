package com.kasa.ola.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.MallClassBean;
import com.kasa.ola.bean.entity.MallTabClassBean;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.CartActivity;
import com.kasa.ola.ui.MallProductListActivity;
import com.kasa.ola.ui.adapter.ClassifyAdapter;
import com.kasa.ola.ui.adapter.VipProductAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
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
import butterknife.Unbinder;

public class MallFragment extends BaseFragment implements View.OnClickListener {

    Unbinder unbinder;
    @BindView(R.id.mall_tab)
    TabLayoutView mallTab;
    @BindView(R.id.mall_recycle)
    LoadMoreRecyclerView mallRecycle;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
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
    @BindView(R.id.iv_mall_banner)
    ImageView ivMallBanner;
    @BindView(R.id.iv_my_cart)
    ImageView ivMyCart;
    @BindView(R.id.tv_first_tab_name)
    TextView tvFirstTabName;
    private LayoutInflater inflater;
    //    private List<ProductBean> vipProducts = new ArrayList<>();
    private List<ProductBean> vipProducts = new ArrayList<>();
    private int currentPage = 1;
    private VipProductAdapter vipProductAdapter;
    private String[] titles = {"矿泉水", "酒水", "矿泉水", "酒水", "矿泉水", "酒水", "矿泉水", "酒水", "矿泉水", "酒水", "矿泉水", "酒水", "矿泉水", "酒水", "酒水", "矿泉水", "酒水"};
    private int[] imgs;
    private int firstTabSelected = 0;
    private List<MallClassBean> mallClassBeans;
    private ClassifyAdapter classifyAdapter;
    private List<MallClassBean> secondClasses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_mall, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTitle();
        initView();
        return rootView;
    }

    private void initView() {
        ivMallBanner.setOnClickListener(this);
        ivMyCart.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mallRecycle.setLayoutManager(gridLayoutManager);

        classifyAdapter = new ClassifyAdapter(getContext(), secondClasses);
        classifyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), MallProductListActivity.class);
                intent.putExtra(Const.CLASS_ID_TAG,secondClasses.get(position).getClassID());
                startActivity(intent);
            }
        });
        mallRecycle.setAdapter(classifyAdapter);

//        imgs = new int[titles.length];
//        for (int i = 0;i<titles.length;i++){
//            imgs[i] = R.drawable.selector_mall_tab_bg;
//        }
//        mallTab.setDataSource(titles, imgs, 0);
//        mallTab.setTextStyle(13, R.color.COLOR_FF9A9A9A, R.color.black);
//        mallTab.initDatasVertical();
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
                ToastUtils.showShortToast(getContext(), msg);
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
                    ImageLoaderUtils.imageLoad(getContext(), secongClassBean.getAdsImg(), ivMallBanner);
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
                ToastUtils.showShortToast(getContext(), msg);
            }
        }, null);
    }

    private void initTitle() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        viewShadow.setBackgroundColor(Color.parseColor("#000000"));
        ivBackFrag.setVisibility(View.GONE);
        tvTitleFrag.setText(getString(R.string.all_products));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

//    private void loadPage(boolean isFirst) {
//        loadPage(isFirst, false);
//    }

//    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
//        JSONObject jo = new JSONObject();
//        jo.put("pageNum", currentPage);
//        jo.put("pageSize", Const.PAGE_SIZE * 4);
//        jo.put("homeClassType", 1 + "");
//        ApiManager.get().getData(Const.GET_HOME_TYPE_PRODUCT_LIST, jo, new BusinessBackListener() {
//            @Override
//            public void onBusinessSuccess(BaseResponseModel responseModel) {
//                refreshLayout.setRefreshing(false);
//                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
//                    JSONObject jo = (JSONObject) responseModel.data;
//                    JSONArray ja = jo.optJSONArray("mallProductList");
//                    if (!isLoadMore) {
//                        vipProducts.clear();
//                        vipProductAdapter.notifyDataSetChanged();
//                    }
//                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
//                    }.getType());
//
//                    if (list != null && list.size() > 0) {
//                        vipProducts.addAll(list);
//                        vipProductAdapter.notifyDataSetChanged();
//                        recyclerVipProduct.loadMoreComplete(currentPage == jo.optInt("totalPage"));
//                    }
//                    if (vipProducts.size() > 0) {
//                        rlHead.setVisibility(View.GONE);
//                    } else {
//                        rlHead.setVisibility(View.VISIBLE);
//                    }
//                    if (!isLoadMore) {
//                        if (list != null && list.size() > 0) {
//                            recyclerVipProduct.setVisibility(View.VISIBLE);
//                        } else {
//                            recyclerVipProduct.setVisibility(View.GONE);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onBusinessError(int code, String msg) {
//                refreshLayout.setRefreshing(false);
//                ToastUtils.showShortToast(getContext(), msg);
//            }
//        }, isFirst ? loadingView : null);
//    }

//    @Override
//    public void onLoadMore() {
//        currentPage++;
//        loadPage(false, true);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_mall_banner:
                Intent mallProductListintent = new Intent(getContext(), MallProductListActivity.class);
                startActivity(mallProductListintent);
                break;
            case R.id.iv_my_cart:
                if (CommonUtils.checkLogin(getActivity())) {
                    Intent cartIntent = new Intent(getContext(), CartActivity.class);
                    startActivity(cartIntent);
                }
                break;
        }
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
}
