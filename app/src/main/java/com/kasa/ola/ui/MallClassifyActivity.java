package com.kasa.ola.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ClassTabBean;
import com.kasa.ola.bean.entity.MallProductBean;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.MallAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;
import com.kasa.ola.widget.TabLayoutView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MallClassifyActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener{

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.mall_tab)
    TabLayoutView mallTab;
    @BindView(R.id.mall_recycle)
    LoadMoreRecyclerView mallRecycle;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.view_no_result)
    LinearLayout viewNoResult;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private String[] titles /*= {"矿泉水", "酒水","矿泉水", "酒水","矿泉水", "酒水","矿泉水", "酒水","矿泉水", "酒水","矿泉水", "酒水","矿泉水", "酒水", "酒水","矿泉水", "酒水"}*/;
    private int[] imgs /*= {R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg,R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg,R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg,R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg,R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg,R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg,R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg,R.drawable.selector_mall_tab_bg, R.drawable.selector_mall_tab_bg}*/; //新版的导航栏图标
    private int selected = 0;
    private int currentPage = 1;
    private List<ClassTabBean> classBeans;
    private ArrayList<MallProductBean> mallProductList = new ArrayList<>();
    private MallAdapter mallAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_classify);
        ButterKnife.bind(this);
        setActionBar(getString(R.string.mall), "");
        initView();
        initTab();
    }

    private void initTab() {
        ApiManager.get().getData(Const.GET_MALL_CLASS_LIST, null, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    String jsonData = ((JSONObject) responseModel.data).optString("firstList");
                    classBeans = new Gson().fromJson(jsonData, new TypeToken<List<ClassTabBean>>() {
                    }.getType());
                    if (classBeans != null && classBeans.size() > 0) {
                        titles = new String[classBeans.size()];
                        imgs = new int[classBeans.size()];
                        for (int i = 0; i < classBeans.size(); i++) {
                            titles[i] = classBeans.get(i).getClassName();
                            imgs[i] = R.drawable.selector_mall_tab_bg;
                        }
                        mallTab.setDataSource(titles, imgs, 0);
                        mallTab.setTextStyle(13, R.color.COLOR_FF9A9A9A, R.color.black);
                        mallTab.initDatasVertical();
                        loadPage(true);
                        mallTab.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
                            @Override
                            public void onItemClick(int index) {

                            }

                            @Override
                            public void onVerticalItemClick(int index, View view) {
                                mallTab.setSelectStyleVertical(index);
                                if (selected!=index){
                                    selected = index;
                                    currentPage = 1;
                                    mallProductList.clear();
                                    mallAdapter.notifyDataSetChanged();
                                    loadPage(true);
                                }else {

                                }
                            }
                        });
                    }

                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MallClassifyActivity.this, msg);
            }
        }, null);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst,false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE * 20);
        jsonObject.put("classID", classBeans.get(selected).getClassID());
        ApiManager.get().getData(Const.GET_MALL_PRODUCT_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    refreshLayout.setRefreshing(false);
                    if (!isLoadMore) {
                        mallProductList.clear();
                    }
                    JSONObject jo = (JSONObject) responseModel.data;
                    String jsonData = jo.optJSONArray("mallProductList").toString();
                    List<MallProductBean> list = new Gson().fromJson(jsonData, new TypeToken<List<MallProductBean>>() {
                    }.getType());

                    if (list!=null && list.size()>0) {
                        mallProductList.addAll(list);
                        mallAdapter.notifyDataSetChanged();
                        mallRecycle.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }

                    if (!isLoadMore) {
                        if (list!=null&& list.size() > 0) {
                            viewNoResult.setVisibility(View.GONE);
                            mallRecycle.setVisibility(View.VISIBLE);
                        }else {
                            viewNoResult.setVisibility(View.VISIBLE);
                            mallRecycle.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {

            }
        },isFirst ? loadingView : null);

    }

    private void initView() {
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

        mallRecycle.setLayoutManager(new LinearLayoutManager(MallClassifyActivity.this));
        mallRecycle.setLoadingListener(this);
        mallAdapter = new MallAdapter(MallClassifyActivity.this, mallProductList);
        mallRecycle.setAdapter(mallAdapter);

//        mallTab.setDataSource(titles, imgs, 0);
//        mallTab.setTextStyle(13, R.color.COLOR_FF9A9A9A, R.color.black);
//        mallTab.initDatasVertical();
//        mallTab.setOnItemOnclickListener(new TabLayoutView.OnItemOnclickListener() {
//            @Override
//            public void onItemClick(int index) {
//
//
//            }
//
//            @Override
//            public void onVerticalItemClick(int index, View view) {
//                mallTab.setSelectStyleVertical(index);
////                initListPopupIfNeed();
////                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_LEFT);
////                mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_NONE);
////                mListPopup.setPositionOffsetX(250);
////                mListPopup.show(view);
//                if (selected!=index){
//                    selected = index;
////                    currentPage = 1;
////                    mallProductList.clear();
////                    mallAdapter.notifyDataSetChanged();
////                    loadPage(true);
//                }else {
//
//                }
//            }
//        });
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }
//    private void initListPopupIfNeed() {
//        if (mListPopup == null) {
//
//            String[] listItems = new String[]{
//                    "Item 1",
//                    "Item 2",
//                    "Item 3",
//                    "Item 4",
//                    "Item 5",
//            };
//            List<String> data = new ArrayList<>();
//
//            Collections.addAll(data, listItems);
//
//            ArrayAdapter adapter = new ArrayAdapter<>(MallClassifyActivity.this, R.layout.simple_list_item, data);
//
//            mListPopup = new QMUIListPopup(MallClassifyActivity.this, QMUIPopup.DIRECTION_NONE, adapter);
//            mListPopup.create(QMUIDisplayHelper.dp2px(MallClassifyActivity.this, 250), QMUIDisplayHelper.dp2px(MallClassifyActivity.this, 200), new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    Toast.makeText(MallClassifyActivity.this, "Item " + (i + 1), Toast.LENGTH_SHORT).show();
//                    mListPopup.dismiss();
//                }
//            });
//            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
////                    mActionButton2.setText(getContext().getResources().getString(R.string.popup_list_action_button_text_show));
//                }
//            });
//        }
//    }
}
