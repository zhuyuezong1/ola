package com.kasa.ola.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.manager.Const;
import com.kasa.ola.ui.adapter.TaskAdapter;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskListActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_tasks)
    LoadMoreRecyclerView rvTasks;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task_publish);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra(Const.TASK_LIST_TYPE, 0);
        initTitle();
        initView();
    }

    private void initView() {
        TaskAdapter taskAdapter = new TaskAdapter(TaskListActivity.this, null);
        rvTasks.setLoadingMoreEnabled(true);
        rvTasks.setLoadingListener(this);
        rvTasks.setAdapter(taskAdapter);
    }

    private void initTitle() {
        if (type==0){
            setActionBar(getString(R.string.my_publish),"");
        }else if(type==1){
            setActionBar(getString(R.string.my_accept_orders),"");
        }
    }

    @Override
    public void onLoadMore() {

    }
}
