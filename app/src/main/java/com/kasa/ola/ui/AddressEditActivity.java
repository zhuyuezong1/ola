package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.App;
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
import com.kasa.ola.utils.LogUtil;
import com.kasa.ola.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressEditActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_tel)
    EditText etTel;
    @BindView(R.id.et_area)
    TextView etArea;
    @BindView(R.id.iv_arrow)
    ImageView ivArrow;
    @BindView(R.id.view_local)
    RelativeLayout viewLocal;
    @BindView(R.id.et_addr)
    EditText etAddr;
    @BindView(R.id.btn_set_default)
    CheckBox btnSetDefault;
    @BindView(R.id.btn_save)
    TextView btnSave;
    private AddressBean addressBean;
    private boolean addNew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setEditListener(etName);
        setEditListener(etTel);
        setEditListener(etAddr);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(Const.ADDRESS_BUNDLE_KEY);
        if (null != bundle) {
            addNew = false;
            addressBean = (AddressBean) bundle.getSerializable(Const.ADDRESS_KEY);
            initData();
        } else {
            addNew = true;
            addressBean = new AddressBean();
            addressBean.setIsDefault(0);
        }
        setActionBar(getString(addNew ? R.string.add_new_address : R.string.edit_address),addNew ? "":  getString(R.string.delete));
        tvRightText.setOnClickListener(this);
        viewLocal.setOnClickListener(this);
        btnSetDefault.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }
    private void initData() {
        etName.setText(addressBean.getName());
        etName.setSelection(etName.getText().toString().length());
        etTel.setText(addressBean.getMobile());
        etAddr.setText(addressBean.getAddressDetail());
        etArea.setText(addressBean.getProvince() + addressBean.getCity() + addressBean.getArea());
        btnSetDefault.setChecked(addressBean.getIsDefault()==1);

        App.getApp().putString(Const.PROVINCE_CODE, addressBean.getProvinceCode());
        App.getApp().putString(Const.CITY_CODE, addressBean.getCityCode());
        App.getApp().putString(Const.AREA_CODE, addressBean.getAreaCode());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCache();
    }
    private void removeCache() {
        App.getApp().removeString(Const.PROVINCE_CODE);
        App.getApp().removeString(Const.CITY_CODE);
        App.getApp().removeString(Const.AREA_CODE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_text:
                if (addNew){

                }else {
                    CommonDialog.Builder builder = new CommonDialog.Builder(AddressEditActivity.this);
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
                break;
            case R.id.view_local:
                startActivityForResult(new Intent(this, ProvinceListActivity.class), Const.ACTREQ_PROVINCE);
                break;
            case R.id.btn_set_default:
                if (addressBean.getIsDefault()==0){
                    addressBean.setIsDefault(1);
                }else if (addressBean.getIsDefault()==1){
                    addressBean.setIsDefault(0);
                }
                btnSetDefault.setChecked(addressBean.getIsDefault()==1);
                break;
            case R.id.btn_save:
                if (addNew) {
                    saveNewAddr();
                } else {
                    modifyAddr();
                }
                break;
        }
    }

    private void deleteAddr(final AddressBean addressBean) {
        JSONObject jo = new JSONObject();
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("addressID", addressBean.getAddressID());
        ApiManager.get().getData(Const.DELETE_ADDRESS, jo, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ToastUtils.showShortToast(AddressEditActivity.this, responseModel.resultCodeDetail);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(AddressEditActivity.this, msg);
            }
        }, new LoadingDialog.Builder(AddressEditActivity.this)
                .setMessage(getString(R.string.deleting_tips)).create());
    }

    private void setFocuse(EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
    }
    private void saveNewAddr() {
        if(checkInfo()){
            JSONObject jo = getJSONObject();
            ApiManager.get().getData(Const.ADD_ADDRESS, jo, new BusinessBackListener() {
                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    removeCache();
                    ToastUtils.showShortToast(AddressEditActivity.this, responseModel.resultCodeDetail);
                    finish();
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    ToastUtils.showShortToast(AddressEditActivity.this, msg);
                }
            }, new LoadingDialog.Builder(AddressEditActivity.this)
                    .setMessage(getString(R.string.saving_tips)).create());
        }
    }


    private boolean checkInfo(){
        if (checkEmpty(addressBean.getName())) {
            ToastUtils.showShortToast(AddressEditActivity.this, getString(R.string.input_host_warning));
            setFocuse(etName);
            return false;
        } else if (checkEmpty(addressBean.getMobile())) {
            ToastUtils.showShortToast(AddressEditActivity.this, getString(R.string.input_phone_warning));
            setFocuse(etTel);
            return false;
        } else if (checkEmpty(addressBean.getProvince()) &&
                checkEmpty(addressBean.getCity()) &&
                checkEmpty(addressBean.getArea())) {
            ToastUtils.showShortToast(AddressEditActivity.this, getString(R.string.input_addr_warning));
            return false;
        } else if (checkEmpty(addressBean.getAddressDetail())) {
            ToastUtils.showShortToast(AddressEditActivity.this, getString(R.string.input_addr_detail_warning));
            setFocuse(etAddr);
            return false;
        }
        return true;
    }

    private void modifyAddr() {
        if(checkInfo()){
            JSONObject jo = getJSONObject();
            ApiManager.get().getData(Const.MODIFY_ADDRESS, jo, new BusinessBackListener() {
                @Override
                public void onBusinessSuccess(BaseResponseModel responseModel) {
                    removeCache();
                    ToastUtils.showShortToast(AddressEditActivity.this, responseModel.resultCodeDetail);
                    finish();
                }

                @Override
                public void onBusinessError(int code, String msg) {
                    ToastUtils.showShortToast(AddressEditActivity.this, msg);
                }
            }, new LoadingDialog.Builder(AddressEditActivity.this)
                    .setMessage(getString(R.string.saving_tips)).create());
        }
    }

    private JSONObject getJSONObject() {
        JSONObject jo = new JSONObject();
        if (!addNew) jo.put("addressID", addressBean.getAddressID());
        jo.put("userID", LoginHandler.get().getUserId());
        jo.put("name", addressBean.getName());
        jo.put("mobile", addressBean.getMobile());

        App app = App.getApp();
        jo.put("province", app.getGlobalString(Const.PROVINCE_CODE));
        jo.put("city", app.getGlobalString(Const.CITY_CODE));
        jo.put("area", app.getGlobalString(Const.AREA_CODE));

        jo.put("addressDetail", addressBean.getAddressDetail());
        jo.put("isDefault", addressBean.getIsDefault()==1 ? "1" : "0");

        return jo;
    }

    private void setEditListener(final EditText e) {
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (e.getId()) {
                    case R.id.et_name:
                        addressBean.setName(s.toString());
                        break;
                    case R.id.et_tel:
                        addressBean.setMobile(s.toString());
                        break;
                    case R.id.et_addr:
                        addressBean.setAddressDetail(s.toString());
                        break;
                }
            }
        });
    }
    private boolean checkEmpty(String s) {
        return TextUtils.isEmpty(s);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            App app = App.getApp();
            addressBean.setProvince(app.getGlobalString(Const.PROVINCE));
            addressBean.setCity(app.getGlobalString(Const.CITY));
            addressBean.setArea(app.getGlobalString(Const.AREA));
            etArea.setText(addressBean.getProvince() + addressBean.getCity() + addressBean.getArea());

        }
    }
}
