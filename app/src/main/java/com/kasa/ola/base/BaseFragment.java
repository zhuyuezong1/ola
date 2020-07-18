package com.kasa.ola.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kasa.ola.R;
import com.kasa.ola.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by guan on 2018/6/6 0006.
 */
public class BaseFragment extends Fragment {


    protected void setActionBar(View v, String title, String right) {
        TextView titleText = v.findViewById(R.id.tv_title_frag);
        titleText.setText(title);
        TextView rightText = v.findViewById(R.id.tv_right_text_frag);
        rightText.setText(right);
        ImageView backBtn = v.findViewById(R.id.iv_back_frag);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (this instanceof BaseActivity.EventBusListener) {
//            EventBus.getDefault().register(this);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
//        CommonUtils.checkBlack(getActivity());
    }
    public interface EventBusListener {

    }
    protected void setActionBar(View v, String title) {
        setActionBar(v, title, "");
    }

    public void finish() {
        FragmentManager fragmentManager = getFragmentManager();
        if (null != fragmentManager) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 0) {
                fragmentManager.popBackStack();
            } else {
                getActivity().finish();
            }
        }
    }

    protected void setStatusBar(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 设置状态栏底色颜色
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActivity().getWindow().setStatusBarColor(getActivity().getColor(color));
            if (isLightColor(getActivity().getColor(color))) {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }
    public boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }
//    /**
//     * 设置状态栏透明
//     */
//    private void setTranslucentStatus() {
//
//        // 5.0以上系统状态栏透明
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getActivity().getWindow();
//            //清除透明状态栏
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            //设置状态栏颜色必须添加
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);//设置透明
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //19
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//    }
}
