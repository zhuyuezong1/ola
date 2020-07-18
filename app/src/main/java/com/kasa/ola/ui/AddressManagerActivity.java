package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.AddressBean;
import com.kasa.ola.dialog.CommonDialog;
import com.kasa.ola.dialog.LoadingDialog;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.AddrManagerAdapter;
import com.kasa.ola.ui.adapter.AddressAdapter;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadingView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressManagerActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.addr_recycleview)
    RecyclerView addrRecycleview;
    @BindView(R.id.tv_add_address)
    TextView tvAddAddress;
    @BindView(R.id.view_no_result)
    LinearLayout viewNoResult;
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    private boolean isFirst = true;
    private boolean isChoose = false;
    private ArrayList<AddressBean> addList = new ArrayList<>();
    private AddressAdapter addrAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        isChoose = getIntent().getBooleanExtra(Const.ADDRESS_CHOOSE_KEY, false);
        setActionBar(getString(R.string.addr_manager), getString(R.string.add));
        tvRightText.setOnClickListener(this);
        tvAddAddress.setOnClickListener(this);
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                loadPage(true);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addrRecycleview.setLayoutManager(linearLayoutManager);
//        addrAdapter = new AddrManagerAdapter(this, addList);
        addrAdapter = new AddressAdapter(this, addList);
        addrAdapter.setAddrManagerListener(new AddressAdapter.AddrManagerListener() {
            @Override
            public void onDefaultClick(AddressBean addressBean) {
                setDefaultAddr(addressBean);
            }

            @Override
            public void onEditClick(AddressBean addressBean) {
                Intent intent = new Intent(AddressManagerActivity.this, AddressEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Const.ADDRESS_KEY, addressBean);
                intent.putExtra(Const.ADDRESS_BUNDLE_KEY, bundle);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(final AddressBean addressBean) {
                CommonDialog.Builder builder = new CommonDialog.Builder(AddressManagerActivity.this);
                builder.setMessage(getString(R.string.delete_addr_tips))
                        .setLeftButton(getString(R.string.cancel))
                        .setRightButton(getString(R.string.confirm))
                        .setDialogInterface(new CommonDialog.DialogInterface() {

                            @Override
                            public void leftButtonClick(CommonDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void rightButtonClick(CommonDialog dialog) {
                                dialog.dismiss();
                                deleteAddr(addressBean);
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onItemClick(AddressBean addressBean) {
                if (isChoose) {
                    Intent intent = new Intent();
                    intent.putExtra(Const.BACK_ADDR_MODEL_KEY, addressBean);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        addrRecycleview.setAdapter(addrAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            loadPage(true);
        } else {
            loadPage(false);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right_text:
                startActivity(new Intent(AddressManagerActivity.this, AddressEditActivity.class));
                break;
            case R.id.tv_add_address:
                startActivity(new Intent(AddressManagerActivity.this, AddressEditActivity.class));
                break;
        }
    }

    private void loadPage(boolean isFirst) {
        ApiManager.get().getData(Const.GET_MY_ADDRESS_LIST, new JSONObject().put("userID", LoginHandler.get().getUserId()),
                new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        addList.clear();
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
                            String jsonData = jo.optString("list");

                            List<AddressBean> list = new Gson().fromJson(jsonData, new TypeToken<List<AddressBean>>() {
                            }.getType());

                            if (list!=null && list.size()>0) {
                                addList.addAll(list);
                                addrAdapter.notifyDataSetChanged();
                                viewNoResult.setVisibility(View.GONE);
                                addrRecycleview.setVisibility(View.VISIBLE);
                            } else {
                                viewNoResult.setVisibility(View.VISIBLE);
                                addrRecycleview.setVisibility(View.GONE);
                            }
                        } else {
                            viewNoResult.setVisibility(View.VISIBLE);
                            addrRecycleview.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(AddressManagerActivity.this, msg);
                    }
                }, isFirst ? loadingView : null);
    }

    private void deleteAddr(final AddressBean addressBean) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("addressID", addressBean.getAddressID());
        ApiManager.get().getData(Const.DELETE_ADDRESS, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(AddressManagerActivity.this, responseModel.resultCodeDetail);
                loadPage(false);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(AddressManagerActivity.this, msg);
            }
        }, new LoadingDialog.Builder(AddressManagerActivity.this)
                .setMessage(getString(R.string.deleting_tips)).create());
    }

    private void setDefaultAddr(final AddressBean addressBean) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("addressID", addressBean.getAddressID());
        ApiManager.get().getData(Const.SET_ADDRESS_DEFAULT, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(AddressManagerActivity.this, responseModel.resultCodeDetail);
                loadPage(false);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(AddressManagerActivity.this, msg);
            }
        }, new LoadingDialog.Builder(AddressManagerActivity.this)
                .setMessage(getString(R.string.saving_tips)).create());
    }
}
