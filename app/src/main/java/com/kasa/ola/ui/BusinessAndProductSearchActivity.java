package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.bean.entity.ShopInfoBean;
import com.kasa.ola.dialog.NewCommonDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.BusinessesAdapter;
import com.kasa.ola.ui.adapter.ProductVerticalAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.SPUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.flowlayout.FlowLayout;
import com.kasa.ola.widget.flowlayout.TagAdapter;
import com.kasa.ola.widget.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BusinessAndProductSearchActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_everyone_hint)
    TextView tvEveryoneHint;
    @BindView(R.id.fl_everyone_search)
    TagFlowLayout flEveryoneSearch;
    @BindView(R.id.tv_history_hint)
    TextView tvHistoryHint;
    @BindView(R.id.iv_rubbish_box)
    ImageView ivRubbishBox;
    @BindView(R.id.fl_search_records)
    TagFlowLayout flSearchRecords;
    @BindView(R.id.ll_history_content)
    LinearLayout llHistoryContent;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_search_result)
    LinearLayout llSearchResult;
    @BindView(R.id.rv_businesses)
    LoadMoreRecyclerView rvBusinesses;
    @BindView(R.id.sl_layout)
    SwipeRefreshLayout slLayout;
    @BindView(R.id.iv_result_back)
    ImageView ivResultBack;
    @BindView(R.id.rb_product)
    RadioButton rbProduct;
    @BindView(R.id.rb_shop)
    RadioButton rbShop;
    @BindView(R.id.rg_search)
    RadioGroup rgSearch;
    @BindView(R.id.tv_multiple_sort)
    TextView tvMultipleSort;
    @BindView(R.id.tv_sale_sort)
    TextView tvSaleSort;
    @BindView(R.id.tv_price_sort)
    TextView tvPriceSort;
    @BindView(R.id.rl_price)
    RelativeLayout rlPrice;
    @BindView(R.id.ll_product_sort)
    LinearLayout llProductSort;
    @BindView(R.id.rv_product)
    LoadMoreRecyclerView rvProduct;
    @BindView(R.id.tv_search_result)
    TextView tvSearchResult;
    private List<String> recordProductList = new ArrayList<>();
    private List<String> everyoneRecordProductList = new ArrayList<>();
    private List<String> recordShopList = new ArrayList<>();
    private List<String> everyoneRecordShopList = new ArrayList<>();
    private final int DEFAULT_RECORD_NUMBER = 10;
    private TagAdapter mRecordsAdapter;
    private TagAdapter everyoneRecordsAdapter;
    private String key;
    private int currentPage = 1;
    private int productCurrentPage = 1;
    private List<ShopInfoBean> shopInfoBeans = new ArrayList<>();
    private BusinessesAdapter businessesAdapter;
    private int allSearchType = 1;//1商品，2商家,
    private ProductVerticalAdapter productVerticalAdapter;
    private List<ProductBean> productBeans = new ArrayList<>();
    private int selectTab = 0;
    private int priceSort = 0;
    private String classID;
    private String saleSort = "";
    private int searchType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_search);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        searchType = intent.getIntExtra(Const.SEARCH_TYPE, 0);
        classID = intent.getStringExtra(Const.CLASS_ID_TAG);
        switch (searchType) {
            case 0:
                rgSearch.setVisibility(View.VISIBLE);
                etSearch.setHint(R.string.input_search_product_tip);
                allSearchType = 1;
                break;
            case 1:
                rgSearch.setVisibility(View.GONE);
                etSearch.setHint(R.string.input_search_product_tip);
                allSearchType = 1;
                break;
            case 2:
                rgSearch.setVisibility(View.GONE);
                etSearch.setHint(R.string.input_search_shop_tip);
                allSearchType = 2;
                break;
            case 3:
                llSearch.setVisibility(View.GONE);
                slLayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setStatusBar(R.color.COLOR_FF1677FF);
                }
                rgSearch.setVisibility(View.GONE);
                etSearch.setHint(R.string.input_search_product_tip);
                allSearchType = 1;
                break;
        }
        //默认
        setProductRankTab(2,"0",0);
        initView();
        initData();
        initEvent();
    }

    /**
     * @param price 对应价格排序的三种状态0：无序，对应传参""，1：降序，对应传参0，2，升序，对应传参1
     * @param sale 直接对应传参，"",无序，"0"，降序,"1"，升序
     * @param tabIndex tab选中index
     */
    private void setProductRankTab(int price,String sale,int tabIndex){
        priceSort = price;
        saleSort = sale;
        selectTab = tabIndex;
        switchTabSelect(tabIndex);
        swithPriceImg(price);
    }
    private void getHotSearch() {
        JSONObject jsonObject = new JSONObject();
        if (allSearchType == 1) {
            jsonObject.put("type", "1");
        } else if (allSearchType == 2) {
            jsonObject.put("type", "2");
        }
        ApiManager.get().getData(Const.GET_TOP_SEARCHES_LIST_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("topList");
                    if (ja != null && ja.length() > 0) {
                        everyoneRecordProductList.clear();
                        everyoneRecordShopList.clear();
                        for (int i = 0; i < ja.length(); i++) {
                            if (allSearchType == 1) {
                                everyoneRecordProductList.add(ja.get(i).toString());
                                everyoneRecordsAdapter.setData(everyoneRecordProductList);
                            } else if (allSearchType == 2) {
                                everyoneRecordShopList.add(ja.get(i).toString());
                                everyoneRecordsAdapter.setData(everyoneRecordShopList);
                            }
                        }
                        everyoneRecordsAdapter.notifyDataChanged();
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(BusinessAndProductSearchActivity.this, msg);
            }
        }, null);
    }

    private void initData() {
        if (searchType == 3) {
            loadProductPage(true);
        } else {
            getHotSearch();
        }
    }

    private void initView() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        search(etSearch.getText().toString());
                        break;
                }
                return false;
            }
        });
        if (allSearchType == 1) {
            initHistory(recordProductList, everyoneRecordProductList, allSearchType);
        } else if (allSearchType == 2) {
            initHistory(recordShopList, everyoneRecordShopList, allSearchType);
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BusinessAndProductSearchActivity.this);
        rvBusinesses.setLayoutManager(linearLayoutManager);
        rvBusinesses.setLoadingListener(this);
        businessesAdapter = new BusinessesAdapter(BusinessAndProductSearchActivity.this, shopInfoBeans);
        businessesAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(BusinessAndProductSearchActivity.this, ShopDetailActivity.class);
                intent.putExtra(Const.SHOP_ID, shopInfoBeans.get(position).getSuppliersID());
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(BusinessAndProductSearchActivity.this);
        rvProduct.setLayoutManager(linearLayoutManager1);
        rvProduct.setLoadingListener(this);
        productVerticalAdapter = new ProductVerticalAdapter(BusinessAndProductSearchActivity.this, productBeans);
        rvProduct.setAdapter(productVerticalAdapter);
        rvBusinesses.setAdapter(businessesAdapter);
        if (allSearchType == 1) {
            rvProduct.setVisibility(View.VISIBLE);
            rvBusinesses.setVisibility(View.GONE);
        } else if (allSearchType == 2) {
            rvProduct.setVisibility(View.GONE);
            rvBusinesses.setVisibility(View.VISIBLE);
        }
    }

    private void initHistory(List<String> recordList, List<String> everyoneRecordList, int type) {
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
                emitter.onNext(DEFAULT_RECORD_NUMBER >= recordList.size() ? recordList : getListNumber(DEFAULT_RECORD_NUMBER, recordList));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> s) throws Exception {
                        List<String> searchHistory = new ArrayList<>();
                        if (type == 1) {
                            searchHistory = SPUtils.getSearchHistory(Const.PRODUCT_PREFERENCE_NAME, Const.PRODUCT_SEARCH_HISTORY);
                        } else if (type == 2) {
                            searchHistory = SPUtils.getSearchHistory(Const.SHOP_PREFERENCE_NAME, Const.SHOP_SEARCH_HISTORY);
                        }
                        recordList.clear();
                        recordList.addAll(searchHistory);
                        if (mRecordsAdapter != null) {
                            mRecordsAdapter.setData(recordList);
                            mRecordsAdapter.notifyDataChanged();
                        }

                    }
                });

        mRecordsAdapter = new TagAdapter<String>(recordList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(BusinessAndProductSearchActivity.this).inflate(R.layout.tv_history,
                        flSearchRecords, false);
                //为标签设置对应的内容
                tv.setText(s);
                return tv;
            }
        };
        flSearchRecords.setAdapter(mRecordsAdapter);
        everyoneRecordsAdapter = new TagAdapter<String>(everyoneRecordList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(BusinessAndProductSearchActivity.this).inflate(R.layout.tv_history,
                        flEveryoneSearch, false);
                //为标签设置对应的内容
                tv.setText(s);
                return tv;
            }
        };
        flEveryoneSearch.setAdapter(everyoneRecordsAdapter);
    }

    private void initEvent() {
        rgSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_product:
                        etSearch.setHint(R.string.input_search_product_tip);
                        allSearchType = 1;
                        initHistory(recordProductList, everyoneRecordProductList, allSearchType);
                        getHotSearch();
                        break;
                    case R.id.rb_shop:
                        etSearch.setHint(R.string.input_search_shop_tip);
                        allSearchType = 2;
                        initHistory(recordShopList, everyoneRecordShopList, allSearchType);
                        getHotSearch();
                        break;
                }
            }
        });
        ivBack.setOnClickListener(this);
        ivResultBack.setOnClickListener(this);
        llSearchResult.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        ivRubbishBox.setOnClickListener(this);
        flSearchRecords.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public void onTagClick(View view, int position, FlowLayout parent) {
                //清空editText之前的数据
                etSearch.setText("");
                //将获取到的字符串传到搜索结果界面,点击后搜索对应条目内容
                if (allSearchType == 1) {
                    etSearch.setText(recordProductList.get(position));
                } else if (allSearchType == 2) {
                    etSearch.setText(recordShopList.get(position));
                }
                etSearch.setSelection(etSearch.length());
                search(etSearch.getText().toString());
            }
        });
        flEveryoneSearch.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public void onTagClick(View view, int position, FlowLayout parent) {
                //清空editText之前的数据
                etSearch.setText("");
                //将获取到的字符串传到搜索结果界面,点击后搜索对应条目内容
                if (allSearchType == 1) {
                    etSearch.setText(everyoneRecordProductList.get(position));
                } else if (allSearchType == 2) {
                    etSearch.setText(everyoneRecordShopList.get(position));
                }
                etSearch.setSelection(etSearch.length());
                search(etSearch.getText().toString());
            }
        });
        slLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search(etSearch.getText().toString());
            }
        });

        tvMultipleSort.setOnClickListener(this);
        tvSaleSort.setOnClickListener(this);
        rlPrice.setOnClickListener(this);
    }

    private void search(String searchContent) {
        if (!TextUtils.isEmpty(searchContent)) {
            if (allSearchType == 1) {
                llProductSort.setVisibility(View.VISIBLE);
                SPUtils.saveSearchHistory(searchContent, Const.PRODUCT_PREFERENCE_NAME, Const.PRODUCT_SEARCH_HISTORY);
                List<String> searchHistory = SPUtils.getSearchHistory(Const.PRODUCT_PREFERENCE_NAME, Const.PRODUCT_SEARCH_HISTORY);
                recordProductList.clear();
                recordProductList.addAll(searchHistory);
                if (mRecordsAdapter != null) {
                    mRecordsAdapter.setData(recordProductList);
                    mRecordsAdapter.notifyDataChanged();
                }
                loadProductPage(true);
            } else if (allSearchType == 2) {
                llProductSort.setVisibility(View.GONE);
                SPUtils.saveSearchHistory(searchContent, Const.SHOP_PREFERENCE_NAME, Const.SHOP_SEARCH_HISTORY);
                List<String> searchHistory = SPUtils.getSearchHistory(Const.SHOP_PREFERENCE_NAME, Const.SHOP_SEARCH_HISTORY);
                recordShopList.clear();
                recordShopList.addAll(searchHistory);
                if (mRecordsAdapter != null) {
                    mRecordsAdapter.setData(recordShopList);
                    mRecordsAdapter.notifyDataChanged();
                }
                loadPage(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.iv_result_back:
                finish();
                break;
            case R.id.ll_search_result:
                slLayout.setVisibility(View.GONE);
                llSearch.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setStatusBar(defaultColor);
                }
                break;
            case R.id.tv_search:
                currentPage = 1;
                loadPage(true);
                search(etSearch.getText().toString());
                break;
            case R.id.iv_rubbish_box:
                if (allSearchType == 1) {
                    showConfirmDialog(recordProductList, allSearchType);
                } else if (allSearchType == 2) {
                    showConfirmDialog(recordShopList, allSearchType);
                }
                break;
            case R.id.tv_multiple_sort:
                setProductRankTab(2,"0",0);
                loadProductPage(true);
                break;
            case R.id.tv_sale_sort:
                setProductRankTab(0,"0",1);
                loadProductPage(true);
                break;
            case R.id.rl_price:
                if (selectTab != 2) {
                    priceSort = 1;
                } else {
                    if (priceSort == 1) {
                        priceSort = 2;
                    } else if (priceSort == 2) {
                        priceSort = 1;
                    }
                }
                setProductRankTab(priceSort,"",2);
                loadProductPage(true);
                break;
        }
    }

    private void swithPriceImg(int priceSort) {
        if (priceSort == 0) {
            DisplayUtils.setViewDrawableRight(tvPriceSort, R.mipmap.upanddownbutton);
        } else if (priceSort == 1) {
            DisplayUtils.setViewDrawableRight(tvPriceSort, R.mipmap.upanddownbutton1);
        } else if (priceSort == 2) {
            DisplayUtils.setViewDrawableRight(tvPriceSort, R.mipmap.upanddownbutton2);
        }
    }

    private void switchTabSelect(int selectTab) {
        switch (selectTab) {
            case 0:
                tvMultipleSort.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                tvSaleSort.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
                tvPriceSort.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
                break;
            case 1:
                tvMultipleSort.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
                tvSaleSort.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                tvPriceSort.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
                break;
            case 2:
                tvMultipleSort.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
                tvSaleSort.setTextColor(getResources().getColor(R.color.COLOR_FF333333));
                tvPriceSort.setTextColor(getResources().getColor(R.color.COLOR_FF1677FF));
                break;
        }
    }

    private void showConfirmDialog(List<String> recordList, int type) {
        NewCommonDialog.Builder builder = new NewCommonDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_history_record_tips))
                .setLeftButton(getString(R.string.cancel))
                .setRightButton(getString(R.string.confirm))
                .setDialogInterface(new NewCommonDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(NewCommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(NewCommonDialog dialog) {
                        recordList.clear();
                        mRecordsAdapter.notifyDataChanged();
                        if (type == 1) {
                            SPUtils.cleanHistory(Const.PRODUCT_PREFERENCE_NAME);
                        } else if (type == 2) {
                            SPUtils.cleanHistory(Const.SHOP_PREFERENCE_NAME);
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private List<String> getListNumber(int number, List<String> arrs) {
        List<String> strings = arrs.subList(0, number);
        return strings;
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
                slLayout.setRefreshing(false);
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    llSearch.setVisibility(View.GONE);
                    slLayout.setVisibility(View.VISIBLE);
                    rvProduct.setVisibility(View.GONE);
                    rvBusinesses.setVisibility(View.VISIBLE);
                    tvSearchResult.setText(key);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setStatusBar(R.color.COLOR_FF1677FF);
                    }
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("suppliersList");
                    if (!isLoadMore) {
                        shopInfoBeans.clear();
                        businessesAdapter.notifyDataSetChanged();
                    }
                    List<ShopInfoBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ShopInfoBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        shopInfoBeans.addAll(list);
                        businessesAdapter.notifyDataSetChanged();
                        rvBusinesses.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slLayout.setRefreshing(false);
                ToastUtils.showLongToast(BusinessAndProductSearchActivity.this, msg);
            }
        }, null);
    }

    @Override
    public void onLoadMore() {
        if (allSearchType==1){
            productCurrentPage++;
            loadProductPage(false,true);
        }else if (allSearchType==2){
            currentPage++;
            loadPage(false, true);
        }

    }

    private void loadProductPage(boolean isFirst) {
        loadProductPage(isFirst, false);
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

    private void loadProductPage(final boolean isFirst, final boolean isLoadMore) {
        key = etSearch.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", productCurrentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE / 2);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        jsonObject.put("key", key);
        jsonObject.put("classID", TextUtils.isEmpty(classID) ? "" : classID);
        jsonObject.put("priceSort", getRankValue(priceSort));
        jsonObject.put("saleSort", saleSort);
        ApiManager.get().getData(Const.GET_PRODUCT_LIST_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                slLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    llSearch.setVisibility(View.GONE);
                    slLayout.setVisibility(View.VISIBLE);
                    rvProduct.setVisibility(View.VISIBLE);
                    rvBusinesses.setVisibility(View.GONE);
                    tvSearchResult.setText(key);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setStatusBar(R.color.COLOR_FF1677FF);
                    }
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        productVerticalAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        productVerticalAdapter.notifyDataSetChanged();
                        rvBusinesses.loadMoreComplete(productCurrentPage == jo.optInt("totalPage"));
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                slLayout.setRefreshing(false);
                ToastUtils.showLongToast(BusinessAndProductSearchActivity.this, msg);
            }
        }, null);
    }
}
