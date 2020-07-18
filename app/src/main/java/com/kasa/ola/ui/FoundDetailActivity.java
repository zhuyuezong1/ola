package com.kasa.ola.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.CommentBean;
import com.kasa.ola.bean.entity.FoundItemBean;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.pop.CommentInputPopup;
import com.kasa.ola.ui.adapter.FoundCommentAdapter;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.LoadMoreRecyclerView;
import com.kasa.ola.widget.RoundImageView;
import com.kasa.ola.widget.xbanner.XBanner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FoundDetailActivity extends BaseActivity implements LoadMoreRecyclerView.LoadingListener, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    @BindView(R.id.view_actionbar)
    LinearLayout viewActionbar;
    @BindView(R.id.rv_found_comment)
    LoadMoreRecyclerView rvFoundComment;
    private FoundCommentAdapter foundCommentAdapter;
    private List<CommentBean> foundCommentBean = new ArrayList<>();
    private TextView tv_comment;
    private Calendar calendar;
    private int currentPage = 1 ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        translucentStatus = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_detail);
        ButterKnife.bind(this);
        initTitle();
        initView();
        initEvent();
    }

    private void initEvent() {
        tv_comment.setOnClickListener(this);
    }

    private void initTitle() {
        setActionBar(getString(R.string.find_good_shop),"");
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FoundDetailActivity.this);
        rvFoundComment.setLayoutManager(linearLayoutManager);
        rvFoundComment.setLoadingMoreEnabled(true);
        rvFoundComment.setLoadingListener(this);

        FoundCommentAdapter foundCommentAdapter = new FoundCommentAdapter(FoundDetailActivity.this, null);
        rvFoundComment.setAdapter(foundCommentAdapter);
        View headView = getLayoutInflater().from(FoundDetailActivity.this).inflate(R.layout.head_found_comment, rvFoundComment, false);
        XBanner found_comment_banner = headView.findViewById(R.id.found_comment_banner);
        TextView tv_comment_content = headView.findViewById(R.id.tv_comment_content);
        RelativeLayout cv_item = headView.findViewById(R.id.cv_item);
        ImageView iv_shop_logo = headView.findViewById(R.id.iv_shop_logo);
        TextView tv_shop_name = headView.findViewById(R.id.tv_shop_name);
        TextView tv_shop_address = headView.findViewById(R.id.tv_shop_address);
        TextView tv_distance = headView.findViewById(R.id.tv_distance);
        TextView tv_comment_num = headView.findViewById(R.id.tv_comment_num);
        RoundImageView image_head = headView.findViewById(R.id.image_head);
        tv_comment = headView.findViewById(R.id.tv_comment);


        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) found_comment_banner.getLayoutParams();
        int width = DisplayUtils.getScreenWidth(this)-DisplayUtils.dip2px(FoundDetailActivity.this,12)*2;
        lp.height = width*300/350;

        //以下展示数据部分
        found_comment_banner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                if (model instanceof String) {
                    ImageLoaderUtils.imageLoad(FoundDetailActivity.this, (String) model, (ImageView) view);
                }
            }
        });

        rvFoundComment.addHeaderView(headView);
    }

    @Override
    public void onLoadMore() {
        currentPage++;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_comment:
                showCommentPop();
                break;
        }
    }

    private void showCommentPop() {
        CommentInputPopup slideFromBottomInputPopup = new CommentInputPopup(FoundDetailActivity.this, new CommentInputPopup.OnSendClickListener() {
            @Override
            public void sendClick(String text) {
                if (TextUtils.isEmpty(text)){
                    ToastUtils.showShortToast(FoundDetailActivity.this,getString(R.string.comment_content_tips));
                }else {
                    publish(text);
//                    ToastUtils.showLongToast(FoundDetailActivity.this,"调发布评论接口");
                }
            }
        });
        slideFromBottomInputPopup.showPopupWindow();
    }

    public void publish(String text){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID",LoginHandler.get().getUserId());
        jsonObject.put("comment",text);
        jsonObject.put("time",calendar.getTime());

        //目前发布评论接口还未出来，下面的接口只是测试用的商品评价接口
        ApiManager.get().getData(Const.PUBLISH_PRODUCT_COMMENTS_TAG, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                if (responseModel != null && responseModel.data instanceof JSONObject){
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray jsonArray = jo.optJSONArray("commentslist");
                    List<CommentBean> list =new Gson().fromJson(jsonArray.toString(),new TypeToken<List<CommentBean>>(){
                    }.getType());

                    if (list != null && list.size()>0){
                        foundCommentBean.addAll(list);
                        foundCommentAdapter = new FoundCommentAdapter(FoundDetailActivity.this,foundCommentBean);
                        rvFoundComment.setAdapter(foundCommentAdapter);
                        foundCommentBean.clear();
                    }
                }

                tv_comment.setText("");
                ToastUtils.showShortToast(FoundDetailActivity.this,"评论成功");
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(FoundDetailActivity.this,msg);
            }
        },null);
    }


}
