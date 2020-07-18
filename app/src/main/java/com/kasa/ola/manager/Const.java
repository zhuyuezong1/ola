package com.kasa.ola.manager;

import com.webank.mbank.ocr.WbCloudOcrSDK;

public class Const {

    //Api地址
    public static boolean DEBUG = true;
    public static String API_HOST = "https://www.oulachina.com/";
    public static String API_HOST_DEBUG = "http://192.168.0.188/";//喔啦http://10.0.0.183/
//    public static String API_HOST_DEBUG = "https://www.oulachina.com/";//喔啦
//    public static String API_HOST_DEBUG = "http://www.oulachina.com/";

    public static String IMAGE_URL_PREFIX = DEBUG?"http://192.168.0.188":"https://www.oulachina.com";

    //微信商户ID
    public static final String APP_ID = "wx35eb09cdf878f4c0";
    //下载相关
    public static final String DOWNLOAD_NAME = "oula.apk";

//    public static final String SPLASH_POS_ID = "7011711501233597";
    public static final String SPLASH_POS_ID = "5091019378952265";
    public static final String AD_APP_ID = "1110480252";
    public static final String APP_KEY = "Aqc1110480252";

    public static final String appId = "TIDAw0aL";
    public static final String hostUrl = "https://ida.webank.com/";
    public static WbCloudOcrSDK.WBOCRTYPEMODE type= WbCloudOcrSDK.WBOCRTYPEMODE.WBOCRSDKTypeBankSide;
    //分享的url
    //资讯url
//    public static final String HOT_INFORMATION_URL = "https://www.oulachina.com/h5/zx/index.html";
//    public static final String SIGN_URL = " https://www.oulachina.com/register/index.html";
//    public static final String SHARE_URL = " https://www.oulachina.com/index.php/mobile/user/reg.html";
//    public static final String SHARE_URL = " https://www.oulachina.com/invite/index.html";


    public static final String PRODUCT_SHARE_URL = " https://www.oulachina.com/goodsInfo/";

    public static String[] colorRandom = {"#7F7C8D","#A396BB","#9D9A79","#5D888A","#77888A","#AB5D55"};


    public final static int TIME = 60;
    public static int PAGE_SIZE = 20;
    public static int SHARE_TYPR = 0;
    public static int LOGO_TYPE = 0;//1春节logo,0普通logo
    public static final int WECHAT_SHARE_TYPE_WECHAT = 0;
    public static final int WECHAT_SHARE_TYPE_FRIENDS = 1;

    public static final int MAX_NUM = 999;

    public static String PWD_TYPE_KEY = "PWD_TYPE_KEY";
    public static String FORGET_PASSWORD = "FORGET_PASSWORD";
    public static String SELECT_BANK = "SELECT_BANK"; //选择银行卡回传数据参数
    public static String ADDRESS_CHOOSE_KEY = "ADDRESS_CHOOSE_KEY";
    public static String BACK_ADDR_MODEL_KEY = "BACK_ADDR_MODEL_KEY";
    public final static String ADDRESS_KEY = "ADDRESS_TAG";
    public final static String ADDRESS_BUNDLE_KEY = "ADDRESS_BUNDLE_KEY";
    public static String PROVINCE_CODE = "province_code";
    public static String CITY_CODE = "city_code";
    public static String AREA_CODE = "area_code";
    public static String PROVINCE = "province";
    public static String CITY = "city";
    public static String AREA = "area";
    public static String INTENT_URL = "url";
    public static String WEB_TITLE = "web_title";
    public static String ORDER_ID_KEY = "ORDER_ID_KEY";
    public static String PRODUCT_KEY = "PRODUCT_KEY";
    public static String PRODUCT_LIST_KEY = "PRODUCT_LIST_KEY";
    public static String PAY_MSG_KEY = "PAY_MSG_KEY";

    public static String MALL_ORDER_PAY = "mallOrderPay";//订单支付
    public static String PAY_STATUS_KEY = "PAY_STATUS_KEY";
    public final static String DETAIL = "DETAIL";
    public static String MEMBER_STATUS_KEY = "MEMBER_STATUS_KEY";
    public static String LESSON_ID_TAG = "LESSON_ID_TAG";
    public static String SCHOOL_ID_TAG = "SCHOOL_ID_TAG";
    public static String IS_BUY_TAG = "IS_BUY_TAG";
    public static String LESSON_STATUS_TAG = "LESSON_STATUS_TAG";

    public static String MALL_GOOD_ID_KEY = "MALL_GOOD_ID_KEY";
    public static String PRODUCT_TYPE_KEY = "PRODUCT_TYPE_KEY";
    public static String ORDER_TYPE = "ORDER_TYPE";
    public static String EXTRA_KEY_WX_SHARE_TYPE = "EXTRA_KEY_WX_SHARE_TYPE";
    public static String EXTRA_KEY_SHARE_IMAGE_URL = "EXTRA_KEY_SHARE_IMAGE_URL";
    public static String EXTRA_KEY_SHARE_WECHAT_URL = "EXTRA_KEY_SHARE_WECHAT_URL";
    public static String EXTRA_KEY_SHARE_WECHAT_TITLE = "EXTRA_KEY_SHARE_WECHAT_TITLE";
    public static String EXTRA_KEY_SHARE_WECHAT_DESCRIPTION = "EXTRA_KEY_SHARE_WECHAT_DESCRIPTION";
    public static String ISFIRST_USE = "ISFIRST_USE";
    public static String GUIDE_LIST_TAG = "GUIDE_LIST_TAG";
    public static String COMMIT_ORDER_ENTER_TYPE = "COMMIT_ORDER_ENTER_TYPE";
    public static String SELECT_SELF_MENTION_POINT_BACK = "SELECT_SELF_MENTION_POINT_BACK";
    public final static String SHOP_ID="SHOP_ID";

    public static String SELECTED_FILE_KEY = "SELECTED_FILE_KEY";

    public static String SP_NAME = "token_config";

    public static String TOKEN = "token";
    public static String USER_ID = "user_id";
    public static String BACK_SELF_MOTENT_POINT_KEY = "BACK_SELF_MOTENT_POINT_KEY";



    public static int WITHDRAWACTIVITY = 0x0001;
    public static int REQUEST_SELECT_IMAGES_CODE = 0x0004;
    public static int REQUEST_SELECT_IMAGES_CHANGE_CODE = 0x0005;
    public static int CAMERA_REQUEST_CODE = 0x0006;
    public static int YEAR_CARD_PAY_CODE = 0x0007;
    public static int PUBLISH_COMMENT_SUCCESS = 0x0008;
    public static int CLASS_COMMENT_BACK = 0x0009;
    public static int ENTER_CROP_PHOTO = 0x0010;
    public static int SCAN_CODE = 0x0011;

    public static int SHOP_REQUEST_SELECT_IMAGES_CODE = 0x0012;
    public static int SHOP_REQUEST_SELECT_IMAGES_CHANGE_CODE = 0x0013;
    public static int SHOP_CAMERA_REQUEST_CODE = 0x0014;


    public static int ADDRESS_CHOOSE_REQUEST_KEY = 0x1004;
    public static int ACTREQ_PROVINCE = 0x1005;
    public static int ACTREQ_CITY = 1006;
    public static int GOTO_SELECT_BANK = 0x1007; //选择银行卡
    public static int OEDER_DTEAIL_TAG = 0x1009;
    public static int SELECT_SELF_MENTION_POINT = 0x1010;//选择自提点





//    public static int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;


    public static String GUIDE_PAGE = "guidePage";
    public static String GET_HOME_PAGE = "getHomepage"; //获取首页数据
    public static String GET_MALL_CLASS_LIST = "getMallClassList"; //获取商城分类


    public static String GET_MALL_PRODUCT_DETAIL = "getMallProductDetail"; //商城商品详情

    public static String GET_MALL_PRODUCT_LIST = "getMallProductList"; //获取商城分类的商品列表
    public static String GET_MESSAGE_LIST = "getMessageList"; //获取消息列表
    public static String ADD_MALL_SHOP_CART_PRODUCT = "addMallShopCartProduct"; //添加购物车
    public static String GET_MALL_SHOP_CART_LIST = "getMallShopCartList"; //获取购物车列表
    public static String GET_PRODUCT_DELETE_LIST_TAG = "deleteMallShopCartProduct"; //删除购物车商品
    public static String UPDATE_MALL_SHOP_CART_PRODUCT = "updateMallShopCartProduct"; //修改购物车商品数量


//    public static String GET_MALL_ORDER_LIST = "getMallOrderList"; //获取订单列表
//    public static String UPDATE_MALL_ORDER_STATE = "updateMallOrderState"; //确认收货


    public static String COMMIT_MALL_ORDER = "commitMallOrder";//提交订单




    public static String GET_MALL_ORDER_DETAIL = "getMallOrderDetail"; //获取订单详情
    public static String GET_LOGISTICS_URL = "getLogisticsUrl"; //获取快递信息h5
    public static String GET_SEARCH_PRODUCT_LIST = "getSearchProductList"; //搜索
    public static String GET_HOME_TYPE_PRODUCT_LIST = "getHomeTypeProductList"; //搜索


    //个人中心


    public static String GET_COST_DETAIL = "getCostDetail"; //获取消费明细

    public static String GET_EDUCATION_VIP_LIST = "getEducationVIPList"; //获取教育vip列表
    public static String GET_EDUCATION_LESSON_LIST = "getEducationLessonList"; //获取课程列表




    /****************************************************************************************/
    public static String REGISTER_LOGIN_MOD_TYPR = "REGISTER_LOGIN_MOD_TYPR";

    public final static String PREFERENCE_NAME = "PREFERENCE_NAME";
    public final static String SEARCH_HISTORY="SEARCH_HISTORY";
    public final static String MAIN_ACTIVITY_TAG="MAIN_ACTIVITY_TAG";
    public static String SEARCH_TAG = "SEARCH_TAG";
    public static String PUBLISH_PRODUCT_COMMENT = "PUBLISH_PRODUCT_COMMENT";


    public static String DETAIL_TYPE = "DETAIL_TYPE";
    public static String SHOP_ORDER_TYPE = "SHOP_ORDER_TYPE";
    public static String IMAGE_LOGO_TYPE = "IMAGE_LOGO_TYPE";
    public static String EDUCATION_TYPE = "EDUCATION_TYPE";

    public final static String ORDER_TAG="ORDER_TAG";
    public final static String CLASS_ID_TAG="CLASS_ID_TAG";
    public final static String SPECIAL_ENTER_TYPE="SPECIAL_ENTER_TYPE";
    public final static String COMMENT_TYPE="COMMENT_TYPE";
    public final static String EDUCATION_ORGAN_ID_TAG="EDUCATION_ORGAN_ID_TAG";
    public final static String YEAR_CARD_TAG="YEAR_CARD_TAG";
    public final static String YEAR_CARD_PAY_STATUS_KEY="YEAR_CARD_PAY_STATUS_KEY";
    public final static String SELECT_AREA_ENTER="SELECT_AREA_ENTER";//0收货地址，1标准，不保存,2商家
    public final static String PUBLISH_ID_TAG="PUBLISH_ID_TAG";
    public final static String PRODUCT_ID_TAG="PRODUCT_ID_TAG";
    public static String COMMON_PROVINCE_CODE = "common_province_code";
    public static String COMMON_CITY_CODE = "common_city_code";
    public static String COMMON_AREA_CODE = "common_area_code";
    public static String COMMON_PROVINCE = "common_province";
    public static String COMMON_CITY = "common_city";
    public static String COMMON_AREA = "common_area";

    public static String SHOP_APPLY_PROVINCE_CODE = "shop_apply_province_code";
    public static String SHOP_APPLY_CITY_CODE = "shop_apply_city_code";
    public static String SHOP_APPLY_AREA_CODE = "shop_apply_area_code";
    public static String SHOP_APPLY_PROVINCE = "shop_apply_province";
    public static String SHOP_APPLY_CITY = "shop_apply_city";
    public static String SHOP_APPLY_AREA = "shop_apply_area";

    public static String ACTION = "ACTION";
    public static String PUBLISH_BEAN_TAG = "PUBLISH_BEAN_TAG";


    public static String PACKAGE_WECHAT = "com.tencent.mm";


    public static String GET_BASE_URL = "getBaseUrl";
    public static String USER_LOGIN = "userLogin"; //登录
    public static String USER_REGISTER = "userRegister"; //注册
    public static String GET_MY_INFO = "getMyInfo";//获取基本信息
    public static String GET_VERIFY_CODE = "getVerifyCode"; //获取短信验证码
    public static String LOGIN_OUT = "loginOut"; //退出

    public static String SET_USER_PASSWORD = "setUserPassword"; //设置登录密码
    public static String SET_PAY_PASSWORD = "setPayPassword"; //这是支付密码


    public static String GET_HOMEPAGE_INFO_TAG= "getHomepageInfo"; //获取首页数据
    public static String GET_PRODUCT_LIST_TAG= "getProductList"; //获取商品列表
    public static String GET_YEAR_CARD_LIST_TAG= "getYearCardList"; //获取年卡列表
    public static String GET_SCHOOL_LESSON_LIST_TAG= "getSchoolLessonList"; //获取校区课程列表
    public static String GET_EDUCATION_DETAIL_TAG= "getEducationDetail"; //获取机构详情
    public static String GET_EDUCATION_COMMENTS_LIST_TAG= "getEducationCommentsList"; //获取课程评论列表
    public static String GET_LESSON_DETAIL = "getLessonDetail"; //获取课程详情
    public static String GET_LESSON_CLASS_LIST= "getLessonClassList"; //获取课时列表
    public static String YEAR_CARD_PAY= "yearCardPay"; //年卡支付
    public static String UPDATE_RESERVATION_STATE_TAG = "updateReservationState"; //预约或取消
    public static String GET_EDUCATION_LIST_TAG = "getEducationList"; //获取机构列表
    public static String GET_PRODUCT_COMMENTS_LIST_TAG = "getProductCommentsList"; //获取商品评论列表




    public static String GET_RESERVATION_LIST = "getReservationList"; //获取预约列表
    public static String PUBLISH_EDUCATION_COMMENT_TAG = "publishedEducationComments"; //发布课程评论
    public static String PUBLISH_PRODUCT_COMMENT_TAG = "publishedProductComments"; //发布商品评论
//    public static String GET_SHOP_CART_LIST_TAG = "getShopCartList"; //获取购物车列表
    public static String GET_PRODUCT_DETAIL_TAG = "getProductDetail"; //获取商品详情
    public static String GET_ORDER_LIST_TAG = "getOrderList"; //获取商城订单列表
    public static String GET_COMMIT_INFO_TAG = "getCommitInfo"; //获取提交订单数据
    public static String COMMIT_ORDER_INFO_TAG = "commitOrderInfo"; //提交订单数据
    public static String DELETE_SHOP_CART_PRODUCT_TAG = "deleteShopCartProduct"; //删除购物车商品
    public static String ADD_SHOP_CART_PRODUCT_TAG = "addShopCartProduct"; //添加商品到购物车
    public static String UPDATA_SHOP_CART_PRODUCT_TAG = "updateShopCartProduct"; //更改购物车商品数量
    public static String GET_CLASS_LIST_TAG = "getClassList"; //获取商城分类
    public static String GET_ORDER_DETAIL_TAG = "getOrderDetail"; //获取订单详情
    public static String GET_MY_MEMBER_LIST = "getMyMemberList"; //获取我的下级列表
    public static String GET_EDUCATION_LESSON_DETAIL_TAG = "getEducationLessonDetail"; //课程详情
    public static String GET_EDUCATION_CLASS_LIST_TAG = "getEducationClassList"; //获取课时列表
    public static String GET_BUY_YEARCARD_LIST_TAG = "getBuyYearCardList"; //获取已购买年卡列表

    public static String PUBLISH_PRODUCT_COMMENTS_TAG = "publishedProductComments"; //发布商品评价
    public static String GET_MY_COMMENTS_TAG = "getMyCommentsList"; //获取我的评论
    public static String UPDATE_ORDER_STATE_TAG = "updateOrderState"; //确认收货或申请售后

    public static String BALANCE_CASH= "balanceCash"; //提现
    public static String COMMISSION_TO_BALANCE= "commissionToBalance"; //佣金转余额




    public static String GET_MY_ADDRESS_LIST = "getMyAddressList";//获取我的地址列表
    public static String ADD_ADDRESS = "addAddress";//添加新地址
    public static String MODIFY_ADDRESS = "modifyAddress";//修改地址
    public static String DELETE_ADDRESS = "deleteAddress";//删除地址
    public static String SET_ADDRESS_DEFAULT = "setAddressDefault";//设为默认地址


    public static String GET_DEFAULT_ADDRESS = "getDefaultAddress";//获取默认地址

    public static String GET_BANK_LIST= "getBankList"; // 银行列表

    public static String GET_MY_QR_CODE = "getMyQRcode"; //获取个人二维码

    public static String CALCULATE_HANDING_FEE= "calculateHandingFee"; //提现手续费计算


    public static String GET_HAS_LESSON_DATE= "getHasLessonDate"; //获取有课程的日期

    public static String GET_TAKE_ADDRESS_LIST = "getTakeAddressList"; //获取自提点列表
    public static String GET_SHOP_LIST = "getSuppliersList"; //获取店铺列表


    public static String GET_OCR_SIGN = "getOcrSign"; //生成ocr签名

    /****************************************************************************************/
    public static final String PRODUCT_ID_ENC_SECRET = "oulachina";


    public static int COMMISSION_POP = 0x0020;
    public static int WITHDRAW_POP = 0x0021;
    public static int ADD_BANK_CARD_FOR_BACK = 0x0022;

    public static int FOUND_REQUEST_SELECT_IMAGES_CODE = 0x0023;
    public static int FOUND_REQUEST_SELECT_IMAGES_CHANGE_CODE = 0x0024;
    public static int FOUND_CAMERA_REQUEST_CODE = 0x0025;

    public static int CONTINUE_VERIFICATION = 0x0026;
    public static int EDIT_ADDRESS_REQUEST = 0x0027;
//    public static int HEAD_VIEW_REQUEST = 0x0028;
    public static int PAY_RESULT_BACK = 0x0029;

    public static int LICENSE_REQUEST_SELECT_IMAGES_CODE = 0x0030;
    public static int LICENSE_REQUEST_SELECT_IMAGES_CHANGE_CODE = 0x0031;
    public static int LICENSE_CAMERA_REQUEST_CODE = 0x0032;

    public static int STORE_REQUEST_SELECT_IMAGES_CODE = 0x0033;
    public static int STORE_REQUEST_SELECT_IMAGES_CHANGE_CODE = 0x0034;
    public static int STORE_CAMERA_REQUEST_CODE = 0x0035;

    public static int SELECT_AMAP_LOCATION = 0x0036;
    public static int STORE_INFO_EDIT_SUBMIT_SUCCESS = 0x0037;

    public static int STORE_PRODUCT_REQUEST_SELECT_IMAGES_CODE = 0x0038;
    public static int STORE_PRODUCT_REQUEST_SELECT_IMAGES_CHANGE_CODE = 0x0039;
    public static int STORE_PRODUCT_CAMERA_REQUEST_CODE = 0x0040;

    public static int STORE_PRODUCT_DETAILS_REQUEST_SELECT_IMAGES_CODE = 0x0041;
    public static int STORE_PRODUCT_DETAILS_REQUEST_SELECT_IMAGES_CHANGE_CODE = 0x0042;
    public static int STORE_PRODUCT_DETAILS_CAMERA_REQUEST_CODE = 0x0043;

    public static String SEARCH_TYPE = "SEARCH_TYPE";//0，同时搜索，1商品搜索,2店铺抖索
    public final static String PRODUCT_PREFERENCE_NAME = "PRODUCT_PREFERENCE_NAME";
    public final static String PRODUCT_SEARCH_HISTORY="PRODUCT_SEARCH_HISTORY";
    public final static String POSITION_INFO="POSITION_INFO";

    public final static String SHOP_PREFERENCE_NAME = "SHOP_PREFERENCE_NAME";
    public final static String SHOP_SEARCH_HISTORY="SHOP_SEARCH_HISTORY";
    public final static String AD_TIME="AD_TIME";
    public final static String AD_SPLASH="AD_SPLASH";
    public final static String PAY_VALUE="PAY_VALUE";
    public final static String ID_CARD_INFO="ID_CARD_INFO";
    public final static String CHECK_ORDER_INFO="CHECK_ORDER_INFO";
    public final static String CHECK_ORDER_NO="CHECK_ORDER_NO";
    public final static String BUSINESS_LVL="BUSINESS_LVL";//0不是商家，1是商家，2审核中
    public final static String ORDER_ENTER_TYPE="ORDER_ENTER_TYPE";//0，个人中心，1，订单详情，2订单列表，3提交订单

    public final static String STORE_ORDER_TAG="ORDER_TAG";
    public final static String SHARE_TEXT="SHARE_TEXT";
    public final static String STORE_INFO_EDIT="STORE_INFO_EDIT";
    public final static String START_TIME="START_TIME";
    public final static String END_TIME="END_TIME";
    public final static String STORE_PRODUCT_DETAILS="STORE_PRODUCT_DETAILS";
    public final static String STORE_CLASSIFY_ID="STORE_CLASSIFY_ID";
    public final static String TASK_LIST_TYPE="TASK_LIST_TYPE";//0:我的发布，1：我的接单
    public static String DEFAULT_CITY = "台州";


    public static String GET_SHOP_CART_LIST_TAG = "getShopCartList"; //获取购物车列表
    public static String UPLOAD_IMAGE = "uploadImage"; //上传单张图片
    public static String MODIFY_HEAD_IMAGE_TAG = "modifyHeadImage"; //修改头像（包括第一次上传）

    //省市区
    public static String GET_PROVINCE_LIST = "getProvinceList";//获取省份列表
    public static String GET_CITY_LIST = "getCityList"; //请求城市列表
    public static String GET_AREA_LIST = "getAreaList"; //请求区列表


    public static String DELETE_BANK_CARD = "deleteBankCard"; //删除银行卡
    public static String SET_BANK_CARD_DEFAULT = "setBankCardDefault"; //设为默认银行卡
    public static String ADD_BANK_CARD = "addBankCard"; //添加银行卡

    public static String GET_COMMISSION_DETAIL = "getCommissionDetail"; //获取活动金明细
    public static String GET_BALANCE_DETAIL = "getBalanceDetail"; //获取余额明细

    public static String BALANCE_CASH_DETAILS= "balanceCashDetails"; //获取提现页面数据
    public static String NAME_CERTIFICATION = "nameCertification"; //实名认证
    public static String GET_MY_BANK_CARD = "getMyBankCard"; //获取我的银行卡

    public static String GET_SUPPLIERS_CLASSIFY_LIST = "getSuppliersClassifyList"; //获取商家分类列表
    public static String GET_SHOP_DETAIL = "getSuppliersDetail"; //获取店铺详情
    public static String GET_SHOP_PRODUCT_LIST = "getSuppliersProductList"; //获取商家商品列表
    public static String GET_SUPPLIERS_ORDER_LIST = "getSuppliersOrderList"; //获取商家订单
    public static String GET_SUPPLIERS_MONEY_DETAIL = "getSuppliersMoneyDetail"; //获取商家对账信息
    public static String GET_SUPPLIERS_INFO = "getSuppliersInfo"; //获取商家信息
    public static String SUPPLIERS_CERTIFICATION = "suppliersCertification"; //提交商家认证
    public static String SUPPLIERS_PRODUCT_ON_OR_OFF = "suppliersProductOnOrOff"; //商品上架下架删除
    public static String ADD_PRODUCT = "addProduct"; //添加商品
    public static String GET_SUPPLIERS_PRODUCT_INFO = "getSuppliersProductInfo"; //获取商家商品信息
    public static String GET_QUALITY_SHOP_LIST = "getQualityShopList"; //获取精选商品列表

    public static String GET_CHECK_ORDER_INFO= "getCheckOrderDetail"; //获取核销订单信息
    public static String VERIFICATION_ORDER = "verificationOrder"; //核销订单

    public static String GET_TOP_SEARCHES_LIST_TAG = "getTopSearchesList"; //获取热门搜索



    public static String GET_FOUND_LIST = "getFoundList"; //获取发现列表
    public static String PUBLISH = "publish"; //发布发现

    public static String ORDER_PAY = "orderPay"; //订单支付
    public static String VERIFY_PAY_PWD = "verifyPayPwd";//验证支付密码

}
