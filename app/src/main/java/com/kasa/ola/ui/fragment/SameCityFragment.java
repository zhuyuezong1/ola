package com.kasa.ola.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SameCityFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.iv_back_frag)
    ImageView ivBackFrag;
    @BindView(R.id.tv_title_frag)
    TextView tvTitleFrag;
    @BindView(R.id.tv_right_text_frag)
    TextView tvRightTextFrag;
    @BindView(R.id.webProgressBar)
    ProgressBar webProgressBar;
    @BindView(R.id.view_actionbar_frag)
    LinearLayout viewActionbarFrag;
    @BindView(R.id.tv_back)
    TextView tvBack;
    Unbinder unbinder;
    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_same_city
                , container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTitle();
        initView();
        return rootView;
    }

    private void initView() {
    }


    @Override
    public void onResume() {
        super.onResume();
//        if (isFirst) {
//            isFirst = false;
//            loadPage(true);
//        } else {
//            loadPage(false);
//        }
    }

    private void initTitle() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
        viewShadow.setBackgroundColor(getResources().getColor(R.color.black));
        setActionBar(viewActionbarFrag, getString(R.string.circle_title), "");
        tvRightTextFrag.setOnClickListener(this);
        tvRightTextFrag.setTextColor(getResources().getColor(R.color.textColor_actionBar_right));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
