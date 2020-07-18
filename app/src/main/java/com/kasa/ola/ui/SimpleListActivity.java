package com.kasa.ola.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.LoadingView;

import java.util.List;

public abstract class SimpleListActivity<T> extends BaseActivity {
    LoadMoreRecyclerView recyclerView;
    LoadingView loadingView;
    RecyclerView.Adapter adapter;
    List<T> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadingView = findViewById(R.id.loading_view);
        loadingView.setRefrechListener(new LoadingView.OnRefreshListener() {
            @Override
            public void refresh() {
                requestData();
            }
        });

        getIntentData();
        initTitleView();
        initAdatper();
        requestData();

    }

    protected abstract void initTitleView();

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void requestData() {
    }

    protected void initAdatper() {
    }

    protected void getIntentData() {
    }
}
