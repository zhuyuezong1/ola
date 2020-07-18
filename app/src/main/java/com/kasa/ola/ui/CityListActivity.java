package com.kasa.ola.ui;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.kasa.ola.App;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.model.CityModel;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProvinceAdapter;
import com.kasa.ola.utils.ToastUtils;

import java.util.ArrayList;

public class CityListActivity<CityVO> extends SimpleListActivity implements ProvinceAdapter.ItemClickListener {
    private String provinceCode;
    private int enterType;

    @Override
    protected void initTitleView() {
        setActionBar("选择城市", "");
    }

    @Override
    protected void getIntentData() {
        super.getIntentData();
        provinceCode = getIntent().getStringExtra(Const.PROVINCE_CODE);
        enterType = getIntent().getIntExtra(Const.SELECT_AREA_ENTER,0);
    }

    @Override
    protected void requestData() {
        super.requestData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("provinceCode", provinceCode);
        ApiManager.get().getData(Const.GET_CITY_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                CityModel model = new Gson().fromJson(responseModel.data.toString(), CityModel.class);
                data.clear();
                if (model.getList()!=null&&model.getList().size()>0) {
                    data.addAll(model.getList());
                    adapter.notifyDataSetChanged();
//                recyclerView.loadMoreComplete(true);
                    recyclerView.loadComplete();
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(CityListActivity.this, msg);
            }
        }, loadingView);
    }

    @Override
    protected void initAdatper() {
        super.initAdatper();
        data = new ArrayList<CityVO>();
        adapter = new ProvinceAdapter(this, data, ProvinceAdapter.AddressType.CITY.ordinal());
        ((ProvinceAdapter) adapter).setListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void jumpOrReturn(@Nullable ProvinceAdapter.AddressInterface vo) {
        Intent intent = new Intent();
        intent.setClass(this, AreaListActivity.class);
        intent.putExtra(Const.SELECT_AREA_ENTER,enterType);
        intent.putExtra(Const.CITY_CODE, vo.getCode());
        startActivityForResult(intent, Const.ACTREQ_CITY);

        if (enterType==1){
            ((App) getApplication()).putString(Const.COMMON_CITY, vo.getName());
            ((App) getApplication()).putString(Const.COMMON_CITY_CODE, vo.getCode());
        }else if (enterType==2){
            ((App) getApplication()).putString(Const.SHOP_APPLY_CITY, vo.getName());
            ((App) getApplication()).putString(Const.SHOP_APPLY_CITY_CODE, vo.getCode());
        }else {
            ((App) getApplication()).putString(Const.CITY, vo.getName());
            ((App) getApplication()).putString(Const.CITY_CODE, vo.getCode());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
