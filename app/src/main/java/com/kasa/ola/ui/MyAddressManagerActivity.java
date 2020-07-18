package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MyAddressManagerActivity extends BaseActivity implements View.OnClickListener {


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
    @BindView(R.id.loading_view)
    LoadingView loadingView;
    @BindView(R.id.btn_add_address)
    Button btnAddAddress;
    private boolean isFirst = true;
    private boolean isChoose = false;
    private ArrayList<AddressBean> addList = new ArrayList<>();
    private AddressAdapter addrAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address_manager);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        isChoose = getIntent().getBooleanExtra(Const.ADDRESS_CHOOSE_KEY, false);
        setActionBar(getString(R.string.addr_manager), "");
//        tvRightText.setOnClickListener(this);
        btnAddAddress.setOnClickListener(this);
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                loadPage(true);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        addrRecycleview.setLayoutManager(linearLayoutManager);
        addrAdapter = new AddressAdapter(this, addList);
        addrAdapter.setAddrManagerListener(new AddressAdapter.AddrManagerListener() {
            @Override
            public void onDefaultClick(AddressBean addressBean) {
                setDefaultAddr(addressBean);
            }

            @Override
            public void onEditClick(AddressBean addressBean) {
                Intent intent = new Intent(MyAddressManagerActivity.this, AddressEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Const.ADDRESS_KEY, addressBean);
                intent.putExtra(Const.ADDRESS_BUNDLE_KEY, bundle);
//                startActivity(intent);
                startActivityForResult(intent,Const.EDIT_ADDRESS_REQUEST);
            }

            @Override
            public void onDeleteClick(final AddressBean addressBean) {
//                CommonDialog.Builder builder = new CommonDialog.Builder(MyAddressManagerActivity.this);
//                builder.setMessage(getString(R.string.delete_addr_tips))
//                        .setLeftButton(getString(R.string.cancel))
//                        .setRightButton(getString(R.string.confirm))
//                        .setDialogInterface(new CommonDialog.DialogInterface() {
//
//                            @Override
//                            public void leftButtonClick(CommonDialog dialog) {
//                                dialog.dismiss();
//                            }
//
//                            @Override
//                            public void rightButtonClick(CommonDialog dialog) {
//                                dialog.dismiss();
//                                deleteAddr(addressBean);
//                            }
//                        })
//                        .create()
//                        .show();
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
        switch (v.getId()) {
//            case R.id.tv_right_text:
//                startActivity(new Intent(MyAddressManagerActivity.this, AddressEditActivity.class));
//                break;
            case R.id.btn_add_address:
                Intent intent = new Intent(MyAddressManagerActivity.this, AddressEditActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==Const.EDIT_ADDRESS_REQUEST){
                loadPage(false);
            }
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

                            if (list != null && list.size() > 0) {
                                addList.addAll(list);
                                addrAdapter.notifyDataSetChanged();
                                addrRecycleview.setVisibility(View.VISIBLE);
                            } else {
                                addrRecycleview.setVisibility(View.GONE);
                            }
                        } else {
                            addrRecycleview.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        ToastUtils.showShortToast(MyAddressManagerActivity.this, msg);
                    }
                }, isFirst ? loadingView : null);
    }

//    private void deleteAddr(final AddressBean addressBean) {
//        JSONObject jo = new JSONObject();
//        jo.put("userID", LoginHandler.get().getUserId());
//        jo.put("addressID", addressBean.getAddressID());
//        ApiManager.get().getData(Const.DELETE_ADDRESS, jo, new BusinessBackListener() {
//            @Override
//            public void onBusinessSuccess(BaseResponseModel responseModel) {
//                ToastUtils.showShortToast(MyAddressManagerActivity.this, responseModel.resultCodeDetail);
//                loadPage(false);
//            }
//
//            @Override
//            public void onBusinessError(int code, String msg) {
//                ToastUtils.showShortToast(MyAddressManagerActivity.this, msg);
//            }
//        }, new LoadingDialog.Builder(MyAddressManagerActivity.this)
//                .setMessage(getString(R.string.deleting_tips)).create());
//    }

    private void setDefaultAddr(final AddressBean addressBean) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("addressID", addressBean.getAddressID());
        ApiManager.get().getData(Const.SET_ADDRESS_DEFAULT, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(MyAddressManagerActivity.this, responseModel.resultCodeDetail);
                loadPage(false);
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MyAddressManagerActivity.this, msg);
            }
        }, new LoadingDialog.Builder(MyAddressManagerActivity.this)
                .setMessage(getString(R.string.saving_tips)).create());
    }
}
