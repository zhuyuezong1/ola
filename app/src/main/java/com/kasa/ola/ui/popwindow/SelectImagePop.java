package com.kasa.ola.ui.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.ui.adapter.SelectImagePopAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;

import java.util.List;

public class SelectImagePop extends PopupWindow {

    private List<TextBean> list;
    private Context context;
    private final View rootView;
    private final RecyclerView rv_select_image_pop;

    public SelectImagePop(Context context, List<TextBean> list, final OnItemClickListener listener) {
        this.list = list;
        rootView = LayoutInflater.from(context).inflate(R.layout.view_select_image_pop, null);
        rv_select_image_pop = rootView.findViewById(R.id.rv_select_image_pop);
//        RelativeLayout rl_bg = rootView.findViewById(R.id.rl_bg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_select_image_pop.setLayoutManager(linearLayoutManager);
        SelectImagePopAdapter selectImagePopAdapter = new SelectImagePopAdapter(context, list);
        rv_select_image_pop.setAdapter(selectImagePopAdapter);
        selectImagePopAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                listener.onItemClick(position);
            }
        });
        setBackgroundDrawable(new BitmapDrawable());
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.BottomInPopWin);
        setContentView(rootView);
    }

}
