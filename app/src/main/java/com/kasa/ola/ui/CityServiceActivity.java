package com.kasa.ola.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.dialog.SingleBtnCommonDialog;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.adapter.TaskAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityServiceActivity extends BaseActivity implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.rv_tasks)
    LoadMoreRecyclerView rvTasks;
    @BindView(R.id.tv_my_publish)
    TextView tvMyPublish;
    @BindView(R.id.tv_my_accept_orders)
    TextView tvMyAcceptOrders;
    @BindView(R.id.iv_publish)
    ImageView ivPublish;
    @BindView(R.id.sl_refresh)
    SwipeRefreshLayout slRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_service);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
        SingleBtnCommonDialog.Builder builder = new SingleBtnCommonDialog.Builder(CityServiceActivity.this);
        builder.setTitle("注意")
                .setMessage("该页面仅供测试")
                .setRightButton("确定")
                .setDialogInterface(new SingleBtnCommonDialog.DialogInterface() {
                    @Override
                    public void rightButtonClick(SingleBtnCommonDialog dialog) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void initEvent() {
        tvMyPublish.setOnClickListener(this);
        tvMyAcceptOrders.setOnClickListener(this);
        ivPublish.setOnClickListener(this);
        slRefresh.setOnRefreshListener(this);
        tvLocation.setOnClickListener(this);
    }

    private void initView() {
        TaskAdapter taskAdapter = new TaskAdapter(CityServiceActivity.this, null);
        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent taskDetailIntent = new Intent(CityServiceActivity.this, TaskDetailsActivity.class);
                startActivity(taskDetailIntent);
            }
        });
        rvTasks.setLoadingMoreEnabled(true);
        rvTasks.setLoadingListener(this);
        rvTasks.setAdapter(taskAdapter);
    }

    private void initTitle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBar(R.color.COLOR_FF1677FF);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_my_publish:
                Intent myTaskPublishIntent = new Intent(CityServiceActivity.this, TaskListActivity.class);
                myTaskPublishIntent.putExtra(Const.TASK_LIST_TYPE,0);
                startActivity(myTaskPublishIntent);
                break;
            case R.id.tv_my_accept_orders:
                Intent myAcceptIntent = new Intent(CityServiceActivity.this, TaskListActivity.class);
                myAcceptIntent.putExtra(Const.TASK_LIST_TYPE,1);
                startActivity(myAcceptIntent);
                break;
            case R.id.iv_publish:
                Intent taskPublishIntent = new Intent(CityServiceActivity.this, TaskPublishActivity.class);
                startActivity(taskPublishIntent);
                break;
            case R.id.tv_location:

                break;
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }
}
