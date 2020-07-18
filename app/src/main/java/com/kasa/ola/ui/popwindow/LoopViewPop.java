package com.kasa.ola.ui.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;


import com.kasa.ola.R;
import com.kasa.ola.bean.entity.BankCardInfoBean;
import com.kasa.ola.widget.datepicker.LoopScrollListener;
import com.kasa.ola.widget.datepicker.LoopView;

import java.util.ArrayList;
import java.util.List;

public class LoopViewPop extends PopupWindow implements View.OnClickListener {

    private View rootView;
    private Button btnCancel, btnConfirm;
    private LoopView loopView;
    private ArrayList<BankCardInfoBean> mList;
    private int pos = 0;
    private TypeListener typeListener;

    public interface TypeListener {
        void onConfirm(String id, String desc);
    }

    public void setTypeListener(TypeListener l) {
        typeListener = l;
    }

    public LoopViewPop(Context context, ArrayList<BankCardInfoBean> mList) {
        this.mList = mList;
        rootView = LayoutInflater.from(context).inflate(R.layout.view_loop, null);

        btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
        loopView = rootView.findViewById(R.id.loop_view);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        loopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                pos = item;
            }
        });
        List<String> typeList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            typeList.add(mList.get(i).getDesc());
        }
        loopView.setDataList(typeList);
        loopView.setInitPosition(0);

        setBackgroundDrawable(new BitmapDrawable());
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(rootView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (null != typeListener) {
                    typeListener.onConfirm(mList.get(pos).getId(), mList.get(pos).getDesc());
                }
                dismiss();
                break;
        }
    }
}
