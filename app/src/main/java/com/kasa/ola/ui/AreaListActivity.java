package com.kasa.ola.ui;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.kasa.ola.App;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.model.NewAreaModel;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProvinceAdapter;
import com.kasa.ola.utils.ToastUtils;

import java.util.ArrayList;

public class AreaListActivity<AreaVO> extends SimpleListActivity implements ProvinceAdapter.ItemClickListener {
    private String cityCode;
    private int enterType;
    @Override
    protected void getIntentData() {
        super.getIntentData();
        cityCode = getIntent().getStringExtra(Const.CITY_CODE);
        enterType = getIntent().getIntExtra(Const.SELECT_AREA_ENTER,0);
    }

    @Override
    protected void initTitleView() {
        setActionBar("选择区域", "");
    }

    @Override
    protected void requestData() {
        super.requestData();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cityCode", cityCode);
        ApiManager.get().getData(Const.GET_AREA_LIST, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                NewAreaModel model = new Gson().fromJson(responseModel.data.toString(), NewAreaModel.class);
                data.clear();
                if (model.getList()!=null&&model.getList().size()>0) {
                    data.addAll(model.getList());
                    adapter.notifyDataSetChanged();
                    recyclerView.loadComplete();
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(AreaListActivity.this, msg);
            }
        }, loadingView);
    }

    @Override
    protected void initAdatper() {
        super.initAdatper();
        data = new ArrayList<AreaVO>();
        adapter = new ProvinceAdapter(this, data, ProvinceAdapter.AddressType.AREA.ordinal());
        ((ProvinceAdapter) adapter).setListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void jumpOrReturn(@Nullable ProvinceAdapter.AddressInterface vo) {
        if (enterType==1){
            ((App) getApplication()).putString(Const.COMMON_AREA, vo.getName());
            ((App) getApplication()).putString(Const.COMMON_AREA_CODE, vo.getCode());
        }else if (enterType==2){
            ((App) getApplication()).putString(Const.SHOP_APPLY_AREA, vo.getName());
            ((App) getApplication()).putString(Const.SHOP_APPLY_AREA_CODE, vo.getCode());
        }else {
            ((App) getApplication()).putString(Const.AREA, vo.getName());
            ((App) getApplication()).putString(Const.AREA_CODE, vo.getCode());
        }
        setResult(RESULT_OK);
        finish();
    }
}
