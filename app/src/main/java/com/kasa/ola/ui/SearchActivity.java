package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.NewCommonDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.utils.SPUtils;
import com.kasa.ola.utils.ToastUtils;
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

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_history_hint)
    TextView tvHistoryHint;
    @BindView(R.id.fl_search_records)
    TagFlowLayout flSearchRecords;
    @BindView(R.id.tv_everyone_hint)
    TextView tvEveryoneHint;
    @BindView(R.id.fl_everyone_search)
    TagFlowLayout flEveryoneSearch;
    @BindView(R.id.ll_history_content)
    LinearLayout llHistoryContent;
    @BindView(R.id.iv_rubbish_box)
    ImageView ivRubbishBox;
    private String keyWords;
    private final int DEFAULT_RECORD_NUMBER = 10;
    private List<String> recordList = new ArrayList<>();
    private List<String> everyoneRecordList = new ArrayList<>();
    private TagAdapter mRecordsAdapter;
    private TagAdapter everyoneRecordsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

        flSearchRecords.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public void onTagClick(View view, int position, FlowLayout parent) {
                //清空editText之前的数据
                etSearch.setText("");
                //将获取到的字符串传到搜索结果界面,点击后搜索对应条目内容
                etSearch.setText(recordList.get(position));
                etSearch.setSelection(etSearch.length());
                Intent intent = new Intent(SearchActivity.this, MallProductListActivity.class);
                intent.putExtra(Const.SEARCH_TAG,etSearch.getText().toString());
                startActivity(intent);
            }
        });
        flEveryoneSearch.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public void onTagClick(View view, int position, FlowLayout parent) {
                //清空editText之前的数据
                etSearch.setText("");
                //将获取到的字符串传到搜索结果界面,点击后搜索对应条目内容
                etSearch.setText(everyoneRecordList.get(position));
                etSearch.setSelection(etSearch.length());
                search();
            }
        });

    }

    private void initData() {
        ApiManager.get().getData(Const.GET_TOP_SEARCHES_LIST_TAG, new JSONObject(), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data!=null && responseModel.data instanceof JSONObject){
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("topList");
                    if (ja!=null && ja.length()>0){
                        for (int i=0;i<ja.length();i++){
                            everyoneRecordList.add(ja.get(i).toString());
                        }
                    }
                    everyoneRecordsAdapter.setData(everyoneRecordList);
                    everyoneRecordsAdapter.notifyDataChanged();
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(SearchActivity.this,msg);
            }
        },null);
    }

    private void initView() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        search();
                        break;
                }
                return false;
            }
        });

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
                        List<String> searchHistory = SPUtils.getSearchHistory(Const.PREFERENCE_NAME,Const.SEARCH_HISTORY);
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
                TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.tv_history,
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
                TextView tv = (TextView) LayoutInflater.from(SearchActivity.this).inflate(R.layout.tv_history,
                        flEveryoneSearch, false);
                //为标签设置对应的内容
                tv.setText(s);
                return tv;
            }
        };
        flEveryoneSearch.setAdapter(everyoneRecordsAdapter);

        ivBack.setOnClickListener(this);
        ivRubbishBox.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }

    private void search() {
        String record = etSearch.getText().toString();
        if (!TextUtils.isEmpty(record)) {
            SPUtils.saveSearchHistory(record,Const.PREFERENCE_NAME,Const.SEARCH_HISTORY);
            List<String> searchHistory = SPUtils.getSearchHistory(Const.PREFERENCE_NAME,Const.SEARCH_HISTORY);
            recordList.clear();
            recordList.addAll(searchHistory);
            if (mRecordsAdapter != null) {
                mRecordsAdapter.setData(recordList);
                mRecordsAdapter.notifyDataChanged();
            }
//            initData();
            Intent intent = new Intent(SearchActivity.this, MallProductListActivity.class);
            intent.putExtra(Const.SEARCH_TAG,record);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_rubbish_box:
                showConfirmDialog();
                break;
            case R.id.tv_search:
                search();
                break;
        }
    }
    private void showConfirmDialog() {
        NewCommonDialog.Builder builder = new NewCommonDialog.Builder(this);
        builder.setMessage(this.getString(R.string.delete_history_record_tips))
                .setLeftButton(this.getString(R.string.cancel))
                .setRightButton(this.getString(R.string.confirm))
                .setDialogInterface(new NewCommonDialog.DialogInterface() {

                    @Override
                    public void leftButtonClick(NewCommonDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void rightButtonClick(NewCommonDialog dialog) {
                        recordList.clear();
                        mRecordsAdapter.notifyDataChanged();
                        SPUtils.cleanHistory(Const.PREFERENCE_NAME);
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    private List<String> getListNumber(int number, List<String> arrs) {
        List<String> strings = arrs.subList(0, number);
        return strings;
    }

    private void goSearch(final boolean isFirst) {
        goSearch(isFirst, false);
    }

    private void goSearch(final boolean isFirst, final boolean isLoadMore) {

    }
}
