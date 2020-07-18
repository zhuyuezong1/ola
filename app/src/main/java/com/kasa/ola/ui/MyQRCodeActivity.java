package com.kasa.ola.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.listener.OnBottomGridShareListener;
import com.kasa.ola.utils.BitmapUtils;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyQRCodeActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.iv_invite)
    ImageView ivInvite;
    @BindView(R.id.ll_shot_screen_area)
    LinearLayout llShotScreenArea;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr_code);
        ButterKnife.bind(this);
        initTitle();

    }

    private void initData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        ApiManager.get().getData(Const.GET_MY_QR_CODE, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    String imageUrl = jo.optString("imageUrl");
                    ImageLoaderUtils.imageLoad(MyQRCodeActivity.this, imageUrl, ivQrCode);
                }


            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(MyQRCodeActivity.this, msg);
            }
        }, null);
    }

    private void initTitle() {
        setActionBar("", "");
        ivBack.setImageResource(R.mipmap.return_icon);
//        ivBack.setOnClickListener(this);
//        tvTitle.setTextColor(getResources().getColor(R.color.white));
//        tvTitle.setText("");
        viewActionbar.setBackgroundColor(getResources().getColor(R.color.COLOR_FFDCF1FD));
//        ImmersionBar.with(this).titleBar(R.id.view_actionbar)
//                .fitsSystemWindows(true)
//                .navigationBarColor(android.R.color.black)
//                .autoDarkModeEnable(true)
//                .autoStatusBarDarkModeEnable(true, 0.2f)
//                .init();
        tvId.setText(getString(R.string.reference_id, LoginHandler.get().getUserId()));
        ivInvite.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_invite:
//                Bitmap bitmap = capture(MyQRCodeActivity.this);
                bitmap = takeScreenShotOfView(llShotScreenArea);
                if (bitmap != null) {
                    DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    String fileName = BitmapUtils.saveBitmapReturnPath(bitmap, format.format(new Date()) + ".JPEG", MyQRCodeActivity.this);
                    showBottom(fileName);
                }
                break;
        }
    }

    public Bitmap takeScreenShotOfView(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
    }

    private void showBottom(final String fileName) {
        CommonUtils.showShareBottomSheetGrid(MyQRCodeActivity.this, new OnBottomGridShareListener() {
            @Override
            public void wechatShare() {
                CommonUtils.sharePictureToWechatFriend(MyQRCodeActivity.this, new File(fileName));
            }

            @Override
            public void wechatFriendShare() {
                CommonUtils.sharePictureToTimeLine(MyQRCodeActivity.this, new File(fileName));
            }
        });
//        CommonUtils.showSimpleBottomSheetGrid(MyQRCodeActivity.this, new OnBottomGridShareListener() {
//            @Override
//            public void wechatShare() {
//                CommonUtils.sharePictureToWechatFriend(MyQRCodeActivity.this, new File(fileName));
//            }
//
//            @Override
//            public void wechatFriendShare() {
//                CommonUtils.sharePictureToTimeLine(MyQRCodeActivity.this, new File(fileName));
//            }
//        });
    }

    public Bitmap capture(Activity activity) {
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();
        return bmp;
    }
}
