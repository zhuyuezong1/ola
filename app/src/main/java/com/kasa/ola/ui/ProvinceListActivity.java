package com.kasa.ola.ui;

import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.kasa.ola.App;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.model.ProvinceModel;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.adapter.ProvinceAdapter;
import com.kasa.ola.utils.ToastUtils;

import java.util.ArrayList;

public class ProvinceListActivity<ProvinceVO> extends SimpleListActivity implements ProvinceAdapter.ItemClickListener {
    private int enterType;

    @Override
    protected void initTitleView() {
        setActionBar("选择省份", "");
    }
    @Override
    protected void requestData() {
        super.requestData();
        ApiManager.get().getData(Const.GET_PROVINCE_LIST, new JSONObject(), new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ProvinceModel model = new Gson().fromJson(responseModel.data.toString(), ProvinceModel.class);
                data.clear();
                if (model.getList()!=null&&model.getList().size()>0){
                    data.addAll(model.getList());
                    adapter.notifyDataSetChanged();
//                recyclerView.loadMoreComplete(true);
                    recyclerView.loadComplete();
                    recyclerView.setFootViewBottom(0);
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(ProvinceListActivity.this, msg);
            }
        }, loadingView);
    }

    @Override
    protected void initAdatper() {
        super.initAdatper();
        data = new ArrayList<ProvinceVO>();
        adapter = new ProvinceAdapter(this, data, ProvinceAdapter.AddressType.PROVINCE.ordinal());
        ((ProvinceAdapter) adapter).setListener(this);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void jumpOrReturn(@Nullable ProvinceAdapter.AddressInterface vo) {
        Intent intent1 = getIntent();
        int enterType = intent1.getIntExtra(Const.SELECT_AREA_ENTER, 0);

        Intent intent = new Intent();
        intent.setClass(this, CityListActivity.class);
        intent.putExtra(Const.SELECT_AREA_ENTER,enterType);
        intent.putExtra(Const.PROVINCE_CODE, vo.getCode());
        startActivityForResult(intent, Const.ACTREQ_PROVINCE);

        if (enterType==1){
            ((App) getApplication()).putString(Const.COMMON_PROVINCE_CODE, vo.getName());
            ((App) getApplication()).putString(Const.COMMON_PROVINCE_CODE, vo.getCode());
        }else if (enterType==2){
            ((App) getApplication()).putString(Const.SHOP_APPLY_PROVINCE, vo.getName());
            ((App) getApplication()).putString(Const.SHOP_APPLY_PROVINCE_CODE, vo.getCode());
        }else {
            ((App) getApplication()).putString(Const.PROVINCE, vo.getName());
            ((App) getApplication()).putString(Const.PROVINCE_CODE, vo.getCode());
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
