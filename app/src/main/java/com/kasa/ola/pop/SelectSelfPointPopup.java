package com.kasa.ola.pop;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CardBean;
import com.kasa.ola.bean.entity.SelfMentionPointBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.BankCartEditActivity;
import com.kasa.ola.ui.SelectSelfMentionPointActivity;
import com.kasa.ola.ui.adapter.SelectBankAdapter;
import com.kasa.ola.ui.adapter.SelfMentionPointAdapter;
import com.kasa.ola.ui.adapter.SelfMentionPointAddressAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;


public class SelectSelfPointPopup extends BasePopupWindow {
    private List<SelfMentionPointBean> list = new ArrayList<>();
    private final EditText et_search;
    private int currentPage = 1;
    private Activity context;
    private final LoadMoreRecyclerView rv_self_mention_point;
//    private final SelfMentionPointAddressAdapter selfMentionPointAddressAdapter;
    private final SelfMentionPointAdapter selfMentionPointAdapter;

    public SelectSelfPointPopup(Activity context, final OnSelfItemClickListener onItemClickListener) {
        super(context);
        this.context = context;
        ImageView iv_close = findViewById(R.id.iv_close);
        rv_self_mention_point = findViewById(R.id.rv_self_mention_point);
        et_search = findViewById(R.id.et_search);
        TextView tv_search = findViewById(R.id.tv_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_self_mention_point.setLayoutManager(linearLayoutManager);
        rv_self_mention_point.setLoadingMoreEnabled(true);
        rv_self_mention_point.setLoadingListener(new LoadMoreRecyclerView.LoadingListener() {
            @Override
            public void onLoadMore() {
                currentPage++;
                loadPage(false,true);
            }
        });
        selfMentionPointAdapter = new SelfMentionPointAdapter(context, list);
        selfMentionPointAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                dismiss();
                if (onItemClickListener!=null && list.size()>0){
                    onItemClickListener.onItemClick(list.get(position));
                }
            }
        });
        rv_self_mention_point.setAdapter(selfMentionPointAdapter);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        loadPage(true);
        setPopupGravity(Gravity.BOTTOM);
    }
    private void search() {
        currentPage = 1;
        loadPage(true);
    }
    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0, 350);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0, 1f, 350);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_slide_from_bottom_select_self_point);
    }

    public interface OnSearchListener{
        void search(String text);
    }
    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(boolean isFirst, final boolean isLoadMore) {
        String key = et_search.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key",key);
        jsonObject.put("pageNum",currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        ApiManager.get().getData(Const.GET_TAKE_ADDRESS_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel.data != null && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("list");
                    if (!isLoadMore) {
                        list.clear();
                        selfMentionPointAdapter.notifyDataSetChanged();
                    }
                    List<SelfMentionPointBean> selfMentionPointBeanList = new Gson().fromJson(ja.toString(), new TypeToken<List<SelfMentionPointBean>>() {
                    }.getType());
                    if (selfMentionPointBeanList != null && selfMentionPointBeanList.size() > 0) {
                        list.addAll(selfMentionPointBeanList);
                        selfMentionPointAdapter.notifyDataSetChanged();
                        rv_self_mention_point.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
                    if (!isLoadMore) {
                        if (list != null && list.size() > 0) {
                            rv_self_mention_point.setVisibility(View.VISIBLE);
                        } else {
                            rv_self_mention_point.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showLongToast(context,msg);
            }
        },null);
    }

    public interface OnSelfItemClickListener{
        void onItemClick(SelfMentionPointBean selfMentionPointBean);
    }
}
