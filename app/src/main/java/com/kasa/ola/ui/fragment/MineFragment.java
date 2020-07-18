package com.kasa.ola.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.components.ImmersionOwner;
import com.kasa.ola.R;
import com.kasa.ola.base.BaseActivity;
import com.kasa.ola.base.BaseFragment;
import com.kasa.ola.base.EventCenter;
import com.kasa.ola.bean.BaseResponseModel;
import com.kasa.ola.bean.entity.ProductBean;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.dialog.CommissionToBalanceDialog;
import com.kasa.ola.json.JSONArray;
import com.kasa.ola.json.JSONObject;
import com.kasa.ola.manager.ApiManager;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.net.BusinessBackListener;
import com.kasa.ola.ui.AccountsDetailsActivity;
import com.kasa.ola.ui.BusinessApplyActivity;
import com.kasa.ola.ui.BusinessOrderActivity;
import com.kasa.ola.ui.ImageSelectActivity;
import com.kasa.ola.ui.MainActivity;
import com.kasa.ola.ui.MyCommentNewActivity;
import com.kasa.ola.ui.MyFriendActivity;
import com.kasa.ola.ui.MyMessageActivity;
import com.kasa.ola.ui.OrderActivity;
import com.kasa.ola.ui.RealNameActivity;
import com.kasa.ola.ui.SettingActivity;
import com.kasa.ola.ui.UnderReviewActivity;
import com.kasa.ola.ui.WithdrawActivity;
import com.kasa.ola.ui.adapter.RecommendProductAdapter;
import com.kasa.ola.ui.listener.OnItemClickListener;
import com.kasa.ola.ui.popwindow.SelectImagePop;
import com.kasa.ola.utils.CommonUtils;
import com.kasa.ola.utils.DisplayUtils;
import com.kasa.ola.utils.FileUtils;
import com.kasa.ola.utils.ImageLoaderUtils;
import com.kasa.ola.utils.StringUtils;
import com.kasa.ola.utils.ToastUtils;
import com.kasa.ola.widget.GridItemSpance;
import com.kasa.ola.widget.LoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends BaseFragment implements View.OnClickListener, LoadMoreRecyclerView.LoadingListener, ImmersionOwner/*, BaseFragment.EventBusListener*/ {


    @BindView(R.id.rv_recommend)
    LoadMoreRecyclerView rvRecommend;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_mine_title)
    TextView tvMineTitle;
    @BindView(R.id.iv_setting)
    ImageView ivSetting;
    @BindView(R.id.rl_actionbar)
    RelativeLayout rlActionbar;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    Unbinder unbinder;
    private LayoutInflater inflater;
    private int currentPage = 1;
    private List<ProductBean> productBeans = new ArrayList<>();
    private RecommendProductAdapter recommendProductAdapter;
    private int bannerHeight = 0;
    private Uri outPutUri;
    private SelectImagePop selectImagePop;
    private List<TextBean> bottomList;
    private File cameraFile;
    private int businessLvl = 0;
    private TextView tvId;
    private TextView tvMobile;
    private TextView tvLevel;
    private TextView tvVacanciesValue;
    private TextView tvCommissionValue;
    private ImageView ivHeadImage;
    private TextView tvDot1;
    private TextView tvDot2;
    private TextView tvDot3;
    private TextView tvExchange;
    private TextView tvWithdraw;
    private LinearLayout llCommission;
    private LinearLayout llVacancies;
    private TextView tvWaitForPay;
    private TextView tvWaitForSend;
    private TextView tvWaitForAccept;
    private TextView tvWaitForDiscuss;
    private TextView tvAllOrders;
    private TextView tvAuthManager;
    private TextView tvAccountDetails;
    private TextView tvMyMessages;
    private TextView tvMyComments;
    private TextView tvMyFriends;
    private ImageView ivMineHeadBg;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTitle();
        initView();
        initEvent();
        loadPage(true);
        return rootView;
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
//        rvRecommend.addItemDecoration(new GridItemSpance(DisplayUtils.dip2px(getContext(),14),DisplayUtils.dip2px(getContext(),0),2));

        rvRecommend.setLayoutManager(gridLayoutManager);
        rvRecommend.setLoadingListener(this);
        rvRecommend.setLoadingMoreEnabled(true);
        recommendProductAdapter = new RecommendProductAdapter(getContext(), productBeans);
        rvRecommend.setAdapter(recommendProductAdapter);

        View headView = inflater.from(getContext()).inflate(R.layout.view_mine_head, rvRecommend, false);
        View viewShadow = headView.findViewById(R.id.view_shadow);
        tvId = headView.findViewById(R.id.tv_id);
        tvMobile = headView.findViewById(R.id.tv_mobile);
        tvLevel = headView.findViewById(R.id.tv_level);
        tvVacanciesValue = headView.findViewById(R.id.tv_vacancies_value);
        tvCommissionValue = headView.findViewById(R.id.tv_commission_value);
        ivHeadImage = headView.findViewById(R.id.iv_head_image);
        tvDot1 = headView.findViewById(R.id.tv_dot_1);
        tvDot2 = headView.findViewById(R.id.tv_dot_2);
        tvDot3 = headView.findViewById(R.id.tv_dot_3);
        tvExchange = headView.findViewById(R.id.tv_exchange);
        tvWithdraw = headView.findViewById(R.id.tv_withdraw);
        llCommission = headView.findViewById(R.id.ll_commission);
        llVacancies = headView.findViewById(R.id.ll_vacancies);
        tvWaitForPay = headView.findViewById(R.id.tv_wait_for_pay);
        tvWaitForSend = headView.findViewById(R.id.tv_wait_for_send);
        tvWaitForAccept = headView.findViewById(R.id.tv_wait_for_accept);
        tvWaitForDiscuss = headView.findViewById(R.id.tv_wait_for_discuss);
        tvAllOrders = headView.findViewById(R.id.tv_all_orders);
        tvAuthManager = headView.findViewById(R.id.tv_auth_manager);
        tvAccountDetails = headView.findViewById(R.id.tv_account_details);
        tvMyMessages = headView.findViewById(R.id.tv_my_messages);
        tvMyComments = headView.findViewById(R.id.tv_my_comments);
        tvMyFriends = headView.findViewById(R.id.tv_my_friends);
        ivMineHeadBg = headView.findViewById(R.id.iv_mine_head_bg);
        rlActionbar.setPadding(0, DisplayUtils.getStatusBarHeight2(getActivity()), 0, 0);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
        lp.height = DisplayUtils.getStatusBarHeight2(getActivity()) + DisplayUtils.dip2px(getContext(), 45);
        rvRecommend.addHeaderView(headView);
    }

    private void initTitle() {
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewShadow.getLayoutParams();
//        lp.height = DisplayUtils.getStatusBarHeight2(getActivity());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        showHeadView();
    }

    private void initData() {

    }

    private void showHeadView() {
        String mobile = LoginHandler.get().getMyInfo().optString("mobile");
        tvId.setText(getString(R.string.reference_id, LoginHandler.get().getUserId()));
        tvMobile.setText(StringUtils.getHideMiddleMobile(mobile));
        int role = LoginHandler.get().getMyInfo().optInt("memberLvl");
        businessLvl = LoginHandler.get().getMyInfo().optInt("businessLvl");
        String roleName = "";
        if (businessLvl == 1) {
            roleName = getString(R.string.business_orders);
        } else {
            roleName = getString(R.string.business_identification);
        }
        if (businessLvl == 1) {
            ivScan.setVisibility(View.VISIBLE);
        } else {
            ivScan.setVisibility(View.GONE);
        }
//        switch (role) {
//            case 0:
//                roleName = getString(R.string.common_member);
//                break;
//            case 1:
//                roleName = getString(R.string.gold_member);
//                break;
//            case 2:
//                roleName = getString(R.string.diamond_member);
//                break;
//        }
        tvLevel.setText(roleName);
        String hasEducationVIP = TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("hasEducationVIP")) ? "" : LoginHandler.get().getMyInfo().optString("hasEducationVIP");
//        if (hasEducationVIP.equals("0")) {
//            tvVip.setVisibility(View.GONE);
//        } else if (hasEducationVIP.equals("1")) {
//            tvVip.setVisibility(View.VISIBLE);
//        }
        String buyRate = LoginHandler.get().getMyInfo().optString("buyRate");
//        pbAmount.setProgress(new Double((Double.parseDouble(TextUtils.isEmpty(buyRate) ? "0" : buyRate) * 100)).intValue());
        tvVacanciesValue.setText(TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("balance")) ? "0.00" : CommonUtils.getTwoZero(LoginHandler.get().getMyInfo().optString("balance")));
        tvCommissionValue.setText(TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("commission")) ? "0.00" : CommonUtils.getTwoZero(LoginHandler.get().getMyInfo().optString("commission")));

//        final boolean isReal = LoginHandler.get().getMyInfo().optInt("isCertification") == 1;
//        final String name = LoginHandler.get().getMyInfo().optString("name");
//        if (isReal) {
//            tvName.setText(getString(R.string.user_id_number, name, LoginHandler.get().getMyInfo().optString("userID")));
//        } else {
//            tvName.setText(getString(R.string.user_id_number, getString(R.string.pet_name_title), LoginHandler.get().getMyInfo().optString("userID")));
//        }
//        tvName.setOnClickListener(this);
        ImageLoaderUtils.imageLoad(getContext(), R.mipmap.head_image, ivHeadImage);
        String hasNoPaid = LoginHandler.get().getMyInfo().optString("hasNoPaid");
        String hasNoSend = LoginHandler.get().getMyInfo().optString("hasNoSend");
        String hasNoReceive = LoginHandler.get().getMyInfo().optString("hasNoReceive");

        setDot(tvDot1, hasNoPaid);
        setDot(tvDot2, hasNoSend);
        setDot(tvDot3, hasNoReceive);

//        tvMobile.setText(mobile);
//        viewUserIdentity.removeAllViews();
//        TextView idView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.view_identity_label, viewUserIdentity, false);
//        int role = LoginHandler.get().getMyInfo().optInt("memberLvl");
//        String roleName = "";
//        switch (role) {
//            case 0:
//                roleName = getString(R.string.common_member);
//                break;
//            case 1:
//                roleName = getString(R.string.gold_member);
//                break;
//            case 2:
//                roleName = getString(R.string.diamond_member);
//                break;
//        }
//        idView.setText(roleName);
//        viewUserIdentity.addView(idView);
//
//        tvOrderAccumulate.setText(TextUtils.isEmpty(LoginHandler.get().getMyInfo().optString("vipTotal")) ? "0.00" : LoginHandler.get().getMyInfo().optString("vipTotal"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initEvent() {

        ivScan.setOnClickListener(this);

//        ivHeadImage.setOnClickListener(this);
        ivSetting.setOnClickListener(this);
        tvLevel.setOnClickListener(this);

        tvExchange.setOnClickListener(this);
        tvWithdraw.setOnClickListener(this);
        llCommission.setOnClickListener(this);
        llVacancies.setOnClickListener(this);

        tvWaitForPay.setOnClickListener(this);
        tvWaitForSend.setOnClickListener(this);
        tvWaitForAccept.setOnClickListener(this);
        tvWaitForDiscuss.setOnClickListener(this);

        tvAllOrders.setOnClickListener(this);
        tvAuthManager.setOnClickListener(this);
        tvAccountDetails.setOnClickListener(this);
        tvMyMessages.setOnClickListener(this);
        tvMyComments.setOnClickListener(this);
        tvMyFriends.setOnClickListener(this);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadPage(true);
                ApiManager.get().getMyInfo(new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        refreshLayout.setRefreshing(false);
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
                            LoginHandler.get().saveMyInfo(jo);
                            showHeadView();
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {
                        refreshLayout.setRefreshing(false);
                        ToastUtils.showShortToast(getContext(), msg);
                    }
                });

            }
        });
//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                refreshLayout.setEnabled(scrollView.getScrollY() == 0);
//            }
//        });

        int lwidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int rheight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ivMineHeadBg.measure(lwidth, rheight);
        bannerHeight = ivMineHeadBg.getMeasuredHeight();

        rvRecommend.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                totalDy += dy;
                if (totalDy < 0) {
                    totalDy = 0;
                }
                if (totalDy < bannerHeight) {
                    float alpha = (float) totalDy / bannerHeight;
                    if (totalDy > 0) {
                        rlActionbar.setAlpha(alpha);
                        rlActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                        tvMineTitle.setTextColor(getResources().getColor(R.color.textColor_actionBar_title));
                        ivSetting.setImageResource(R.mipmap.blackset_icon);
                        ivScan.setImageResource(R.mipmap.scan_black);
                    } else if (totalDy == 0) {
                        rlActionbar.setAlpha(1);
                        rlActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
                        tvMineTitle.setTextColor(getResources().getColor(R.color.white));
                        ivSetting.setImageResource(R.mipmap.set_wode_icon);
                        ivScan.setImageResource(R.mipmap.scan_white);
                    }
                    ImmersionBar.with(getActivity())
                            .statusBarDarkFont(false)
                            .init();
                } else {
                    rlActionbar.setAlpha(1);
                    rlActionbar.setBackgroundColor(getResources().getColor(R.color.white));
                    ivSetting.setImageResource(R.mipmap.blackset_icon);
                    ivScan.setImageResource(R.mipmap.scan_black);
                    ImmersionBar.with(getActivity())
                            .statusBarDarkFont(true)
                            .init();
                }
            }
        });

//        rvRecommend.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            private int totalDy = 0;
//
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                totalDy += scrollY - oldScrollY;
//                if (totalDy < 0) {
//                    totalDy = 0;
//                }
//                if (totalDy < bannerHeight) {
//                    float alpha = (float) totalDy / bannerHeight;
//                    if (totalDy > 0) {
//                        rlActionbar.setAlpha(alpha);
//                        rlActionbar.setBackgroundColor(getResources().getColor(R.color.white));
//                        tvMineTitle.setTextColor(getResources().getColor(R.color.textColor_actionBar_title));
//                        ivSetting.setImageResource(R.mipmap.blackset_icon);
//                        ivScan.setImageResource(R.mipmap.scan_black);
//                    } else if (totalDy == 0) {
//                        rlActionbar.setAlpha(1);
//                        rlActionbar.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        tvMineTitle.setTextColor(getResources().getColor(R.color.white));
//                        ivSetting.setImageResource(R.mipmap.set_wode_icon);
//                        ivScan.setImageResource(R.mipmap.scan_white);
//                    }
//                    ImmersionBar.with(getActivity())
//                            .statusBarDarkFont(false)
//                            .init();
//                } else {
//                    rlActionbar.setAlpha(1);
//                    rlActionbar.setBackgroundColor(getResources().getColor(R.color.white));
//                    ivSetting.setImageResource(R.mipmap.blackset_icon);
//                    ivScan.setImageResource(R.mipmap.scan_black);
//                    ImmersionBar.with(getActivity())
//                            .statusBarDarkFont(true)
//                            .init();
//                }
//            }
//        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_scan:
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.scanCode();
                }
                break;
            case R.id.iv_setting:
                Intent settingIntent = new Intent(getContext(), SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.tv_level:
                if (businessLvl == 0) {//商家申请(非商家)
                    Intent businessApplyIntent = new Intent(getContext(), BusinessApplyActivity.class);
                    startActivity(businessApplyIntent);
                } else if (businessLvl == 1) {
                    Intent busonessOrderIntent = new Intent(getContext(), BusinessOrderActivity.class);
                    startActivity(busonessOrderIntent);
                } else if (businessLvl == 2) {//商家订单(商家)
                    Intent underReviewIntent = new Intent(getContext(), UnderReviewActivity.class);
                    startActivity(underReviewIntent);
                }
                break;
//            case R.id.tv_vip:
//                Intent yearCardIntent = new Intent(getContext(), EducationYearCardActivity.class);
//                startActivity(yearCardIntent);
//                break;
            case R.id.iv_head_image:
                bottomList = getBottomList();
                selectImagePop = new SelectImagePop(getContext(), bottomList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        switch (position + 1) {
                            case 1:// 从图库选择
                                Intent frontIntent = new Intent(getContext(), ImageSelectActivity.class);
                                startActivityForResult(frontIntent, Const.REQUEST_SELECT_IMAGES_CODE);
                                selectImagePop.dismiss();
                                break;
                            case 2:// 拍照
                                captureImage();
                                selectImagePop.dismiss();
                                break;
                            case 3:// 取消
                                selectImagePop.dismiss();
                                break;

                            default:
                                break;
                        }
                    }
                });
                selectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                selectImagePop.setWindowLayoutType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                selectImagePop.showAtLocation(ivHeadImage, Gravity.BOTTOM, 0, 0);
                selectImagePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1);
                    }
                });
                backgroundAlpha(0.3f);
                break;
            case R.id.ll_commission:
            case R.id.tv_exchange:
                CommissionToBalanceDialog.Builder commissionbuilder = new CommissionToBalanceDialog.Builder(getContext());
                commissionbuilder.setLeftButton(this.getString(R.string.cancel))
                        .setRightButton(this.getString(R.string.confirm))
                        .setLimit(LoginHandler.get().getMyInfo().optString("commission"))
                        .setDialogInterface(new CommissionToBalanceDialog.DialogInterface() {

                            @Override
                            public void leftButtonClick(CommissionToBalanceDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void rightButtonClick(CommissionToBalanceDialog dialog, double num) {
                                dialog.dismiss();
                                commissionToBalance(num);
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.ll_vacancies:
            case R.id.tv_withdraw:
                if (CommonUtils.checkCertification(getContext())) {
                    Intent withdrawIntent = new Intent(getActivity(), WithdrawActivity.class);
                    startActivity(withdrawIntent);
                }
                break;
            case R.id.tv_all_orders:
                Intent orderIntent = new Intent(getContext(), OrderActivity.class);
                orderIntent.putExtra(Const.ORDER_TAG, 0);
                startActivity(orderIntent);
                break;
            case R.id.tv_wait_for_pay:
                Intent intent = new Intent(getContext(), OrderActivity.class);
                intent.putExtra(Const.ORDER_TAG, 1);
                startActivity(intent);
                break;
            case R.id.tv_wait_for_send:
                Intent intent1 = new Intent(getContext(), OrderActivity.class);
                intent1.putExtra(Const.ORDER_TAG, 2);
                startActivity(intent1);
                break;
            case R.id.tv_wait_for_accept:
                Intent intent2 = new Intent(getContext(), OrderActivity.class);
                intent2.putExtra(Const.ORDER_TAG, 3);
                startActivity(intent2);
                break;
            case R.id.tv_wait_for_discuss:
                Intent intent3 = new Intent(getContext(), OrderActivity.class);
                intent3.putExtra(Const.ORDER_TAG, 4);
                startActivity(intent3);
                break;
            case R.id.tv_auth_manager://暂时使用原来的
                Intent realNameIntent = new Intent(getContext(), RealNameActivity.class);
                startActivity(realNameIntent);
                break;
            case R.id.tv_account_details:
                Intent accountsDetailIntent = new Intent(getContext(), AccountsDetailsActivity.class);
                startActivity(accountsDetailIntent);
                break;
            case R.id.tv_my_messages:
                Intent messageIntent = new Intent(getContext(), MyMessageActivity.class);
                startActivity(messageIntent);
                break;
            case R.id.tv_my_comments:
                Intent myCommentIntent = new Intent(getContext(), MyCommentNewActivity.class);
                startActivity(myCommentIntent);
                break;
            case R.id.tv_my_friends:
                Intent myFriendIntent = new Intent(getContext(), MyFriendActivity.class);
                startActivity(myFriendIntent);
                break;
        }
    }

    private void commissionToBalance(double num) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userID", LoginHandler.get().getUserId());
        jsonObject.put("money", num);
        ApiManager.get().getData(Const.COMMISSION_TO_BALANCE, jsonObject, new BusinessBackListener() {
            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                ApiManager.get().getMyInfo(new BusinessBackListener() {
                    @Override
                    public void onBusinessSuccess(BaseResponseModel responseModel) {
                        if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                            JSONObject jo = (JSONObject) responseModel.data;
                            LoginHandler.get().saveMyInfo(jo);
                            showHeadView();
                        }
                    }

                    @Override
                    public void onBusinessError(int code, String msg) {

                    }
                });
            }

            @Override
            public void onBusinessError(int code, String msg) {
                ToastUtils.showShortToast(getContext(), msg);
            }
        }, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventCenter.EventBean eventBean) {
        showHeadView();
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        loadPage(false, true);
    }

    private void loadPage(boolean isFirst) {
        loadPage(isFirst, false);
    }

    private void loadPage(final boolean isFirst, final boolean isLoadMore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", currentPage);
        jsonObject.put("pageSize", Const.PAGE_SIZE);
        jsonObject.put("userID", TextUtils.isEmpty(LoginHandler.get().getUserId()) ? "" : LoginHandler.get().getUserId());
        jsonObject.put("otherType", "7");
        ApiManager.get().getData(Const.GET_PRODUCT_LIST_TAG, jsonObject, new BusinessBackListener() {

            @Override
            public void onBusinessSuccess(BaseResponseModel responseModel) {
                refreshLayout.setRefreshing(false);
                if (null != responseModel.data && responseModel.data instanceof JSONObject) {
                    JSONObject jo = (JSONObject) responseModel.data;
                    JSONArray ja = jo.optJSONArray("productList");
                    if (!isLoadMore) {
                        productBeans.clear();
                        recommendProductAdapter.notifyDataSetChanged();
                    }
                    List<ProductBean> list = new Gson().fromJson(ja.toString(), new TypeToken<List<ProductBean>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        productBeans.addAll(list);
                        recommendProductAdapter.notifyDataSetChanged();
                        rvRecommend.loadMoreComplete(currentPage == jo.optInt("totalPage"));
                    }
//                    if (!isLoadMore) {
//                        if (list != null && list.size() > 0) {
//                            rvRecommend.setVisibility(View.VISIBLE);
//                        } else {
//                            rvRecommend.setVisibility(View.GONE);
//                        }
//                    }
                }
            }

            @Override
            public void onBusinessError(int code, String msg) {
                refreshLayout.setRefreshing(false);
                ToastUtils.showLongToast(getContext(), msg);
            }
        }, null);
    }

    @Override
    public void onLazyBeforeView() {

    }

    @Override
    public void onLazyAfterView() {

    }

    @Override
    public void onVisible() {

    }

    @Override
    public void onInvisible() {

    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).titleBar(R.id.rl_actionbar)
                .fitsSystemWindows(true)
                .navigationBarColor(android.R.color.black)
                .autoDarkModeEnable(true)
                .autoStatusBarDarkModeEnable(true, 0.2f)
                .init();
    }

    @Override
    public boolean immersionBarEnabled() {
        return true;
    }

    private void setDot(TextView view, String have) {
        if (have.equals("0")) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        view.setTextColor(Color.WHITE);
        view.setBackgroundColor(Color.BLUE);
        view.setGravity(Gravity.CENTER);
        view.setIncludeFontPadding(false);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.psts_dot_txt_size));
        view.setSingleLine();
        Drawable dot_drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.apsts_tips, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(dot_drawable);
        } else {
            view.setBackgroundDrawable(dot_drawable);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            File file;
            if (requestCode == Const.REQUEST_SELECT_IMAGES_CODE) {
                file = (File) data.getSerializableExtra(Const.SELECTED_FILE_KEY);
                Uri imageContentUri = FileUtils.getImageContentUri(getContext(), file);
                startPhotoZoom(imageContentUri);
            } else if (requestCode == Const.CAMERA_REQUEST_CODE) {
                Uri imageContentUri = FileUtils.getImageContentUri(getContext(), cameraFile);
                startPhotoZoom(imageContentUri);
            }
        }
        if (requestCode == Const.ENTER_CROP_PHOTO) {
            if (!TextUtils.isEmpty(outPutUri.getPath())) {
                File file = FileUtils.uriToFile(outPutUri, getContext());
                uploadImages(requestCode, file);
            }
//            ToastUtils.showLongToast(getContext(),"裁剪回调成功");

//            Uri uri = data.getData();
//            File file1 = FileUtils.uriToFile(uri, getContext());
//            uploadImages(requestCode, file1);
//                uploadImages(requestCode, file);
        }
    }

    private void uploadImages(int requestCode, File file) {

    }

    /**
     * 裁剪图片方法实现
     *
     * @param inputUri
     */
    public void startPhotoZoom(Uri inputUri) {
        if (inputUri == null) {
            return;
        }
        File mCropFile = getImageSelectTempFile();
        Intent intent = new Intent("com.android.camera.action.CROP");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            outPutUri = Uri.fromFile(mCropFile);
            intent.setDataAndType(inputUri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
            intent.putExtra("noFaceDetection", false);//去除默认的人脸识别，否则和剪裁匡重叠
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        } else {
            outPutUri = Uri.fromFile(mCropFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String url = FileUtils.getPath(getContext(), inputUri);//这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(inputUri, "image/*");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        }

        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        startActivityForResult(intent, Const.ENTER_CROP_PHOTO);//这里就将裁剪后的图片的Uri返回了
    }

    private File getImageSelectTempFile() {
        File dir = new File(getActivity().getExternalFilesDir("pictures").getAbsolutePath());
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return new File(dir, System.currentTimeMillis() + ".png");
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        this.getActivity().getWindow().setAttributes(lp);
    }

    private List<TextBean> getBottomList() {
        List<TextBean> textBeans = new ArrayList<>();
        TextBean textBean1 = new TextBean();
        textBean1.setContent("从手机相册中获取");
        textBean1.setColor(R.color.black);
        TextBean textBean2 = new TextBean();
        textBean2.setContent("拍照");
        textBean2.setColor(R.color.black);
        TextBean textBean3 = new TextBean();
        textBean3.setContent("取消");
        textBean3.setColor(R.color.black);
        textBeans.add(textBean1);
        textBeans.add(textBean2);
        textBeans.add(textBean3);
        return textBeans;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void captureImage() {
        ((MainActivity) getActivity()).requestPermissions(getContext(), new String[]{Manifest.permission.CAMERA}, new BaseActivity.RequestPermissionCallBack() {
            @Override
            public void granted() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraFile = getImageSelectTempFile();
                //android 7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".fileprovider", cameraFile)); //Uri.fromFile(tempFile)
                } else {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                }
                startActivityForResult(cameraIntent, Const.CAMERA_REQUEST_CODE);
            }

            @Override
            public void denied() {
                ToastUtils.showShortToast(getContext(), "获取权限失败，正常功能受到影响");
            }
        });
    }
}
