package com.kasa.ola.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;
import com.kasa.ola.bean.entity.TextBean;
import com.kasa.ola.bean.entity.UserViewInfo;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.RealNameActivity;
import com.kasa.ola.ui.RegAndLoginAndModActivity;
import com.kasa.ola.ui.listener.OcrListener;
import com.kasa.ola.ui.listener.OnBottomGridShareListener;
import com.kasa.ola.ui.listener.OnConfirmDialogListener;
import com.kasa.ola.widget.bottomdialog.BottomDialog;
import com.kasa.ola.widget.bottomdialog.Item;
import com.kasa.ola.widget.bottomdialog.OnItemClickListener;
import com.kasa.ola.widget.xbanner.XBanner;
import com.kasa.ola.wxapi.WXShareEntryActivity;
import com.webank.mbank.ocr.WbCloudOcrSDK;
import com.webank.mbank.ocr.tools.ErrorCode;
import com.webank.normal.tools.WLogger;
//import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
//import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
//import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class CommonUtils {

    //Activity中加载一个Fragment,有回退栈
    public static void addFragment(FragmentManager fragmentManager, Fragment showFragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(android.R.id.content, showFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    //Fragment中另起一个Fragment，隐藏父fragment，有回退栈
    public static void addFragmentWithHide(FragmentManager fragmentManager, Fragment showFragment, int content, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentById(content);
        if (fragment != null) {
            transaction.hide(fragment);
        }
        transaction.add(content, showFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    //activity包一个Fragment，无回退栈
    public static void addFragmentWithoutBackStack(FragmentManager fragmentManager, Fragment showFragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(android.R.id.content, showFragment, tag);
        transaction.commitAllowingStateLoss();
    }

    //0未认证 1已认证
    public static boolean checkCertification(Context context) {
        if (LoginHandler.get().getMyInfo().optInt("isCertification") == 0) {
            Intent intent = new Intent(context, RealNameActivity.class);
            context.startActivity(intent);
            return false;
        }
        return true;
    }
//    public static void checkBlack(final Activity context){
//        if (LoginHandler.get().getMyInfo().optInt("isBlacklist")==1){
//            ToastUtils.showShortToast(context,context.getString(R.string.black_list_tips));
//            ApiManager.get().loginOut(new BusinessBackListener() {
//                @Override
//                public void onBusinessSuccess(BaseResponseModel responseModel) {
//                    LoginHandler.get().logout();
//                    EventBus.getDefault().post(new EventCenter.HomeSwitch(0));
//                    context.finish();
//                }
//
//                @Override
//                public void onBusinessError(int code, String msg) {
//                    ToastUtils.showShortToast(context, msg);
//                }
//            }, new LoadingDialog.Builder(context).setMessage(context.getString(R.string.login_out_tips)).create());
//        }
//    }
    //更改用户名页面
//    public static boolean checkChangeUserName(Context context,boolean isLogining) {
//        if (LoginHandler.get().getMyInfo().optInt("isModifyUserName", 1) == 0) {
//            Intent intent = new Intent(context, ModifyNickActivity.class);
//            intent.putExtra(Const.IS_LOGINING,isLogining);
//            context.startActivity(intent);
//            return false;
//        }
//        return true;
//    }

    public static boolean checkLogin(Context context) {
        if (!LoginHandler.get().checkLogined()) {
            context.startActivity(new Intent(context, RegAndLoginAndModActivity.class));
            return false;
        }
        return true;
    }

    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

//    public static void showSimpleBottomSheetGrid(final Context context, final String url, final int shareType) {//0首页分享中注册，1红包分享
//        final int TAG_SHARE_WECHAT_FRIEND = 0;
//        final int TAG_SHARE_WECHAT_MOMENT = 1;
//        QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(context);
//        builder.addItem(R.mipmap.weixing, "分享到微信", TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
//                .addItem(R.mipmap.pengyouquan, "分享到朋友圈", TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
//                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
//                    @Override
//                    public void onClick(QMUIBottomSheet dialog, View itemView) {
//                        dialog.dismiss();
//                        int tag = (int) itemView.getTag();
//                        switch (tag) {
//                            case TAG_SHARE_WECHAT_FRIEND:
//                                Intent wechatIntent = new Intent(context, WXShareEntryActivity.class);
////                                wechatIntent.putExtra(Const.EXTRA_KEY_SHARE_IMAGE_URL, "https://www.oulachina.com/public/upload/goods/201909/272c871371813a98b3e0d0ceb4a040e8.png");
//                                wechatIntent.putExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, Const.WECHAT_SHARE_TYPE_WECHAT);
//                                wechatIntent.putExtra(Const.IMAGE_LOGO_TYPE, Const.LOGO_TYPE);
//                                if (shareType==1){
//                                    wechatIntent = getRedPacketWechatIntent(wechatIntent,context,url);
//                                }else if (shareType==0){
//                                    wechatIntent = getWechatIntent(wechatIntent,context,url);
//                                }
//                                context.startActivity(wechatIntent);
//                                break;
//                            case TAG_SHARE_WECHAT_MOMENT:
//                                Intent friendsIntent = new Intent(context, WXShareEntryActivity.class);
////                                friendsIntent.putExtra(Const.EXTRA_KEY_SHARE_IMAGE_URL, "https://www.oulachina.com/public/upload/goods/201909/272c871371813a98b3e0d0ceb4a040e8.png");
//                                friendsIntent.putExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, Const.WECHAT_SHARE_TYPE_FRIENDS);
//                                friendsIntent.putExtra(Const.IMAGE_LOGO_TYPE, Const.LOGO_TYPE);
//                                if (shareType==1){
//                                    friendsIntent = getRedPacketWechatIntent(friendsIntent,context,url);
//                                }else if (shareType==0){
//                                    friendsIntent = getWechatIntent(friendsIntent,context,url);
//                                }
//                                context.startActivity(friendsIntent);
//                                break;
//                        }
//                    }
//                }).build().show();
//    }

    private static Intent getWechatIntent(Intent i,Context context,String url) {
        i.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_URL, url);
        i.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_TITLE, context.getString(R.string.share_title));
        i.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_DESCRIPTION,context.getString(R.string.share_content));
        return i;
    }
    private static Intent getRedPacketWechatIntent(Intent i,Context context,String url) {
        i.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_URL, url);
        i.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_TITLE, context.getString(R.string.share_redpacket_title));
        i.putExtra(Const.EXTRA_KEY_SHARE_WECHAT_DESCRIPTION,context.getString(R.string.share_redpacket_content));
        return i;
    }

//    public static void showSimpleBottomSheetGrid(final Context context, final OnBottomGridShareListener onShareListener) {
//        final int TAG_SHARE_WECHAT_MOMENT = 0;
////        final int TAG_SHARE_QQ_MOMENT = 1;
//        final int TAG_SHARE_WECHAT_FRIEND = 2;
////        final int TAG_SHARE_QQ_SPACE = 3;
//        QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(context);
//        builder.addItem(R.mipmap.weixing, "分享到微信", TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
////                .addItem(R.mipmap.qq, "分享到QQ", TAG_SHARE_QQ_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
//                .addItem(R.mipmap.pengyouquan, "分享到微信朋友圈", TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
////                .addItem(R.mipmap.qq_qzone, "分享到QQ空间", TAG_SHARE_QQ_SPACE, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
//                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
//                    @Override
//                    public void onClick(QMUIBottomSheet dialog, View itemView) {
//                        dialog.dismiss();
//                        int tag = (int) itemView.getTag();
//                        switch (tag) {
//                            case TAG_SHARE_WECHAT_MOMENT:
//                                onShareListener.wechatShare();
//                                break;
////                            case TAG_SHARE_QQ_MOMENT:
////                                onShareListener.qqShare();
////                                break;
//                            case TAG_SHARE_WECHAT_FRIEND:
//                                onShareListener.wechatFriendShare();
//                                break;
////                            case TAG_SHARE_QQ_SPACE:
////                                onShareListener.qqSpaceShare();
////                                break;
//                        }
//                    }
//                }).build().show();
//    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
    public static String getTwo(double data){
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(data);
    }
    public static String getTwoDecimal(double data){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(data);
    }
    public static double getTwoPoint(double data){
        BigDecimal bg3 = new BigDecimal(data);
        double f3 = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f3;
    }
    public static String getTwoZero(double data){
        String str = data+"";
        if (str.contains(".")){
            String[] split = str.split("\\.");
            if (split[1].length()==1){
                str = str+"0";
            }
        }else {
            str = str+"00";
        }
        return str;
    }
    public static String getTwoZero(BigDecimal data){
        String str = data+"";
        if (str.contains(".")){
            String[] split = str.split("\\.");
            if (split[1].length()==1){
                str = str+"0";
            }
        }else {
            str = str+"00";
        }
        return str;
    }
    public static String getTwoZero(String balance){
        BigDecimal data = new BigDecimal(balance);
        String str = data+"";
        if (str.contains(".")){
            String[] split = str.split("\\.");
            if (split[1].length()==1){
                str = str+"0";
            }
        }else {
            str = str+".00";
        }
        return str;
    }
    public static void backgroundAlpha(float bgAlpha,Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }
//    public static void showEditTextDialog(final Context context, String title, String hintText, String leftText, String rightText, final OnConfirmDialogListener onConfirmDialogListener) {
//        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
//        builder.setTitle(title)
//                .setPlaceholder(hintText)
//                .setInputType(InputType.TYPE_CLASS_NUMBER)
//                .addAction(leftText, new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        dialog.dismiss();
//                        if (onConfirmDialogListener!=null){
//                            onConfirmDialogListener.cancel();
//                        }
//                    }
//                })
//                .addAction(rightText, new QMUIDialogAction.ActionListener() {
//                    @Override
//                    public void onClick(QMUIDialog dialog, int index) {
//                        CharSequence text = builder.getEditText().getText();
//                        if (text != null && text.length() > 0) {
////                            Toast.makeText(getActivity(), "您的昵称: " + text, Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            if (onConfirmDialogListener!=null){
//                                onConfirmDialogListener.confirm(text.toString());
//                            }
//                        } else {
//                            ToastUtils.showLongToast(context,context.getString(R.string.no_empty));
//                        }
//                    }
//                })
//                .create(Const.mCurrentDialogStyle).show();
//    }
    /**
     * 分享单张图片到微信好友
     *
     * @param context context
     * @param picFile 要分享的图片文件
     */
    public static void sharePictureToWechatFriend(Context context, File picFile) {
        if (isApkInstalled(context,Const.PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName cop = new ComponentName(Const.PACKAGE_WECHAT, "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(cop);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            if (picFile != null) {
                if (picFile.isFile() && picFile.exists()) {
//                    Uri uri = Uri.fromFile(picFile);
                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", picFile);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, "sharePictureToWechatFriend"));
        } else {
            ToastUtils.showLongToast(context,"请先安装微信客户端");
        }
    }
    /**
     * 分享单张图片到朋友圈
     *
     * @param context context
     * @param picFile 图片文件
     */
    public static void sharePictureToTimeLine(Context context, File picFile) {
        if (isApkInstalled(context,Const.PACKAGE_WECHAT)) {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(Const.PACKAGE_WECHAT, "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
//            Uri uri = Uri.fromFile(picFile);
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", picFile);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra("Kdescription", "sharePictureToTimeLine");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            ToastUtils.showLongToast(context,"请先安装微信客户端");
        }
    }
    public static void showShareBottomSheetGrid(final Context context, OnBottomGridShareListener onShareListener) {
        BottomDialog.Builder builder = new BottomDialog.Builder(context);
        builder.layout(BottomDialog.GRID)              //设置内容layout,默认为线性(LinearLayout)
                .orientation(BottomDialog.VERTICAL)     //设置滑动方向,默认为横向
                .background(R.color.white)
                .inflateMenu(R.menu.menu_share,new OnItemClickListener() {
                    @Override
                    public void click(Item item, BottomDialog dialog) {
                        dialog.dismiss();
                        switch (item.getId()){
                            case R.id.weixing:
                                if (onShareListener!=null){
                                    onShareListener.wechatShare();
                                }
                                break;
                            case R.id.pengyouquan:
                                if (onShareListener!=null) {
                                    onShareListener.wechatFriendShare();
                                }
                                break;
                        }
                    }
                }) .create().show();


    }
    public static void showShareBottomSheetGrid(final Context context, final String url, final int shareType){
        BottomDialog.Builder builder = new BottomDialog.Builder(context);
        builder.layout(BottomDialog.GRID)              //设置内容layout,默认为线性(LinearLayout)
                .orientation(BottomDialog.VERTICAL)     //设置滑动方向,默认为横向
                .background(R.color.white)
                .inflateMenu(R.menu.menu_share,new OnItemClickListener() {
                    @Override
                    public void click(Item item, BottomDialog dialog) {
                        dialog.dismiss();
                        switch (item.getId()){
                            case R.id.weixing:
                                Intent wechatIntent = new Intent(context, WXShareEntryActivity.class);
                                wechatIntent.putExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, Const.WECHAT_SHARE_TYPE_WECHAT);
                                wechatIntent.putExtra(Const.IMAGE_LOGO_TYPE, Const.LOGO_TYPE);
                                if (shareType==1){
                                    wechatIntent = getRedPacketWechatIntent(wechatIntent,context,url);
                                }else if (shareType==0){
                                    wechatIntent = getWechatIntent(wechatIntent,context,url);
                                }
                                context.startActivity(wechatIntent);
                                break;
                            case R.id.pengyouquan:
                                Intent friendsIntent = new Intent(context, WXShareEntryActivity.class);
                                friendsIntent.putExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, Const.WECHAT_SHARE_TYPE_FRIENDS);
                                friendsIntent.putExtra(Const.IMAGE_LOGO_TYPE, Const.LOGO_TYPE);
                                if (shareType==1){
                                    friendsIntent = getRedPacketWechatIntent(friendsIntent,context,url);
                                }else if (shareType==0){
                                    friendsIntent = getWechatIntent(friendsIntent,context,url);
                                }
                                context.startActivity(friendsIntent);
                                break;
                        }
                    }
                }) .create().show();
    }
    public static void showShareBottomSheetGrid(final Context context,String text){
        BottomDialog.Builder builder = new BottomDialog.Builder(context);
        builder.layout(BottomDialog.GRID)              //设置内容layout,默认为线性(LinearLayout)
                .orientation(BottomDialog.VERTICAL)     //设置滑动方向,默认为横向
                .background(R.color.white)
                .inflateMenu(R.menu.menu_share_wechat,new OnItemClickListener() {
                    @Override
                    public void click(Item item, BottomDialog dialog) {
                        dialog.dismiss();
                        switch (item.getId()){
                            case R.id.weixing:
                                Intent wechatIntent = new Intent(context, WXShareEntryActivity.class);
                                wechatIntent.putExtra(Const.EXTRA_KEY_WX_SHARE_TYPE, Const.WECHAT_SHARE_TYPE_WECHAT);
                                wechatIntent.putExtra(Const.SHARE_TEXT,text);
                                context.startActivity(wechatIntent);
                                break;
                        }
                    }
                }) .create().show();
    }
    public static void startOcrDemo(final Activity activity, String sign, String title, String orderNo, String appId, String openApiAppVersion, String nonce, String userId, final ProgressDialog progressDlg, final OcrListener ocrListener,WbCloudOcrSDK.WBOCRTYPEMODE type) {
        //启动SDK，进入SDK界面
        Bundle data = new Bundle();
        WbCloudOcrSDK.InputData inputData = new WbCloudOcrSDK.InputData(
                orderNo,
                appId,
                openApiAppVersion,
                nonce,
                userId,
                sign/*,
                "ip=xxx.xxx.xxx.xxx",
                "lgt=xxx,xxx;lat=xxx.xxx"*/);
        data.putSerializable(WbCloudOcrSDK.INPUT_DATA, inputData);
        data.putString(WbCloudOcrSDK.TITLE_BAR_COLOR, "#ffffff");
        data.putString(WbCloudOcrSDK.TITLE_BAR_CONTENT, title);
        data.putString(WbCloudOcrSDK.WATER_MASK_TEXT, "仅供本次业务使用");
        data.putLong(WbCloudOcrSDK.SCAN_TIME, 20000);
        //这项配置可选，只适用标准模式：传“2”则在标准模式下会对正反面识别进行强校验，即正反面都识别了“完成”按钮才能点击；不传或传其他则不校验
        data.putString(WbCloudOcrSDK.OCR_FLAG, "1");
        //启动SDK，进入SDK界面
        WbCloudOcrSDK.getInstance().init(activity, data, new WbCloudOcrSDK.OcrLoginListener() {
            @Override
            public void onLoginSuccess() {
                //登录成功,拉起SDk页面
                WLogger.d(activity.getLocalClassName(), "onLoginSuccess()");
                if (progressDlg != null) {
                    progressDlg.dismiss();
                }
                //证件结果回调接口
                WbCloudOcrSDK.getInstance().startActivityForOcr(activity, new WbCloudOcrSDK.IDCardScanResultListener() {
                    @Override
                    public void onFinish(String resultCode, String resultMsg) {
                        WLogger.d(activity.getLocalClassName(), "onFinish()" + resultCode + " msg:" + resultMsg);
                        // resultCode为0，则刷脸成功；否则刷脸失败
                        if ("0".equals(resultCode)) {
                            // 登录成功  第三方应用对扫描的结果进行操作
                            Intent i;
                            WbCloudOcrSDK.WBOCRTYPEMODE modeType = WbCloudOcrSDK.getInstance().getModeType();
                            if (modeType.equals(WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeBankSide)) {
                                //银行卡识别
                                if (ocrListener!=null){
                                    ocrListener.bankOcrResult(WbCloudOcrSDK.getInstance().getBankCardResult());
                                }
                            } else if (modeType.equals(WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeNormal)||modeType.equals(WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeFrontSide)||
                                    modeType.equals(WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeBackSide)) {
                                WLogger.d(activity.getLocalClassName(), "识别成功" + WbCloudOcrSDK.getInstance().getResultReturn().frontFullImageSrc);
                                WLogger.d(activity.getLocalClassName(), "识别成功" + WbCloudOcrSDK.getInstance().getResultReturn().backFullImageSrc);
                                //身份证识别
                                if (ocrListener!=null){
                                    ocrListener.idResult(WbCloudOcrSDK.getInstance().getResultReturn());
                                }
                            } else if (modeType.equals(WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeDriverLicenseSide)) {
                                if (ocrListener!=null){
                                    ocrListener.driverLicenseOcrResult(WbCloudOcrSDK.getInstance().getDriverLicenseResult());
                                }
                            }else {
                                if (ocrListener!=null){
                                    ocrListener.vehicleLicenseOcrResult(WbCloudOcrSDK.getInstance().getVehicleLicenseResultOriginal());
                                }
                            }
                        } else {
                            WLogger.d(activity.getLocalClassName(), "识别失败");
                        }

                    }
                }, type);
            }

            @Override
            public void onLoginFailed(String errorCode, String errorMsg) {
                WLogger.d(activity.getLocalClassName(), "onLoginFailed()");
                if (progressDlg != null) {
                    progressDlg.dismiss();
                }
                if (errorCode.equals(ErrorCode.IDOCR_LOGIN_PARAMETER_ERROR)) {
                    Toast.makeText(activity, "传入参数有误！" + errorMsg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "登录OCR sdk失败！" + "errorCode= " + errorCode + " ;errorMsg=" + errorMsg, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public static void setScroll(RecyclerView recyclerView){
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusableInTouchMode(false);
        recyclerView.requestFocus();
    }
    public static void cancelRecyclerViewTouchEvent(RecyclerView recyclerView,View parent){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return parent.onTouchEvent(event);
            }
        });
    }
    public static int getDataSize(List<File> files) {
        return files == null ? 0 : files.size();
    }
    public static int getImageDataSize(List<String> files) {
        return files == null ? 0 : files.size();
    }
    public static List<TextBean> getBottomList() {
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


    /**
     * 查找信息 图片预览
     * @param list 图片集合
     * @param mNglContent
     */
    public static void computeBoundsBackward(List<UserViewInfo> list, RecyclerView mNglContent) {
        if (mNglContent.getChildCount()>0){
            for (int i = 0;i < mNglContent.getChildCount(); i++) {
                View itemView = mNglContent.getChildAt(i);
                Rect bounds = new Rect();
                if (itemView != null) {
                    ImageView thumbView = (ImageView) itemView;
                    thumbView.getGlobalVisibleRect(bounds);
                }
                list.get(i).setBounds(bounds);
                list.get(i).setUrl(list.get(i).getUrl());
            }
        }
    }
    /**
     * 查找信息 图片预览
     * @param list 图片集合
     * @param mNglContent
     */
    public static void computeBoundsBackward(List<UserViewInfo> list, XBanner mNglContent) {
        if (mNglContent.getChildCount()>0){
            for (int i = 0;i < mNglContent.getChildCount(); i++) {
                View itemView = mNglContent.getChildAt(i);
                Rect bounds = new Rect();
                if (itemView != null) {
                    ImageView thumbView = (ImageView) itemView;
                    thumbView.getGlobalVisibleRect(bounds);
                }
                list.get(i).setBounds(bounds);
                list.get(i).setUrl(list.get(i).getUrl());
            }
        }
    }
}
