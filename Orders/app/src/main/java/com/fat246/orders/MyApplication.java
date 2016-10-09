package com.fat246.orders;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.utils.PreferencesUtility;

public class MyApplication extends Application {

    //表示服务器地址的一些全局变量
    private static String SERVER_IP = "192.168.1.127";
    private static String SERVER_PORT = "8081";
    private static final String SERVER_NAME = "Service1.asmx";
    private static final String PRE_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "//" + SERVER_NAME;


    //请求队列
    private static RequestQueue Queue;

    //订单地址
    private static final String ALLORDERSLIST_SERVER = "getAllOrderList";
    private static final String ALLORDERSLIST_URL = PRE_URL + "//" + ALLORDERSLIST_SERVER;

    //申请单地址
    private static final String ALLAPPLYSLIST_SERVER = "getAllApplyList";
    private static final String ALLAPPLYSLIST_URL = PRE_URL + "//" + ALLAPPLYSLIST_SERVER;

    //订单详细信息地址
    private static final String ORDERSMOREINFOLIST_SERVER = "getOrderDetailList";
    private static final String ORDERSMOREINFOLIST_URL = PRE_URL + "//" + ORDERSMOREINFOLIST_SERVER;

    //申请单详细信息地址
    private static final String APPLYSMOREINFOLIST_SERVER = "getApplyDetailList";
    private static final String APPLYSMOREINFOLIST_URL = PRE_URL + "//" + APPLYSMOREINFOLIST_SERVER;

    //登陆要用到的URL 这个要配合到  URL前缀一起使用
    private static final String LOGIN_SERVER = "isLogin";
    private static final String LOGIN_URL = PRE_URL + "//" + LOGIN_SERVER;

    //订单基本信息地址
    private static final String ORDER_STAND_INFO = "getOrderInfor";
    private static final String ORDER_STAND_INFO_URL = PRE_URL + "//" + ORDER_STAND_INFO;

    //申请单基本信息地址
    private static final String APPLYINFO_SERVER = "getApplyInfor";
    private static final String APPLYINFO_URL = PRE_URL + "//" + APPLYINFO_SERVER;

    //申请单时间地址
    private static final String APPLYDATEINFO_SERVER = "getApplyDateInfor";
    private static final String APPLYDATEINFO_URL = PRE_URL + "//" + APPLYDATEINFO_SERVER;

    //订单时间地址
    private static final String ORDERDATEINFO_SERVER = "getOrderDateInfor";
    private static final String ORDERDATAINFO_URL = PRE_URL + "//" + ORDERDATEINFO_SERVER;

    //审批申请单
    private static final String APPROVALAPPLY_SERVER = "setApplyApproval";
    private static final String APPROVALAPPLY_URL = PRE_URL + "//" + APPROVALAPPLY_SERVER;

    //取消审批地址
    private static final String APPROVALCANCLEAPPLY_SERVER = "setApplyApprovalCancel";
    private static final String APPROVALCANCLEAPPLY_URL = PRE_URL + "//" + APPROVALCANCLEAPPLY_SERVER;

    //审批订单
    private static final String APPROVALORDER_SERVER = "setOrderApproval";
    private static final String APPROVALORDER_URL = PRE_URL + "//" + APPROVALORDER_SERVER;

    //取消审批
    private static final String APPROVALCANCLEORDER_SERVER = "setOrderApprovalCancel";
    private static final String APPROVALCANCLEORDER_URL = PRE_URL + "//" + APPROVALCANCLEORDER_SERVER;

    //申请单落实情况地址
    private static final String APPLYFINALS_SERVER = "getApplyFinal";
    private static final String APPLYFINALS_URL = PRE_URL + "//" + APPLYFINALS_SERVER;

    //订单落实情况地址
    private static final String ORDERFINALS_SERVER = "getOrderFinal";
    private static final String ORDERFINALS_URL = PRE_URL + "//" + ORDERFINALS_SERVER;

    //官方网站
    private static final String OFFICIAL_WEBSITE = "http://www.fat246.com";

    //保存路经
    public static String SAVE_PATH;

    //保存用户信息
    private UserInfo mUserInfo;

    //是否登录吃成功
    public static boolean isLoginSucceed = false;
    public static UserInfo mUser = null;

    @Override
    public void onCreate() {

        super.onCreate();

        //获得服务器参数信息
        setServerRex();

        //首先从配置文件中获取用户信息
        setUserInfo();

        //初始化请求队列
        Queue = Volley.newRequestQueue(getApplicationContext());

        //获得路经
        MyApplication.SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public void setServerRex() {

        SERVER_IP = PreferencesUtility.getInstance(this).getServerIp();
        SERVER_PORT = PreferencesUtility.getInstance(this).getServerPort();
    }

    //通过配置文件获取用户的信息
    public void setUserInfo() {

        //首先得到SharedPreferences
        SharedPreferences mSP = getApplicationContext()
                .getSharedPreferences(UserInfo.login_info_key, Context.MODE_PRIVATE);

        //读取UserInfo信息
        this.mUserInfo = new UserInfo(
                mSP.getString("mUser", ""),
                mSP.getString("mPassword", ""),
                mSP.getBoolean("isSavePassword", false),
                mSP.getBoolean("isAutoLogIn", false)
        );
    }

    //用户自定义用户信息
    public void setUserInfo(UserInfo mNewUserInfo) {

        //首先将用户自定义的用户信息保存到配置文件中
        SharedPreferences mSP = getSharedPreferences(UserInfo.login_info_key
                , Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSP.edit();
        editor.putString("mUser", mNewUserInfo.getmUser());
        editor.putString("mPassword", mNewUserInfo.getmPassword());
        editor.putBoolean("isSavePassword", mNewUserInfo.getisSavePassword());
        editor.putBoolean("isAutoLogIn", mNewUserInfo.getisAutoLogIn());
        editor.putInt("operationValue", mNewUserInfo.operationValue);

        //记住一定要提交
        editor.apply();

        this.mUserInfo = mNewUserInfo;
    }

    //得到用户信息
    public UserInfo getUserInfo() {

        return this.mUserInfo;
    }

    //获得订单地址
    public static String getAllorderslistUrl() {
        return ALLORDERSLIST_URL;
    }

    //返回申请单
    public static String getAllapplyslistUrl() {
        return ALLAPPLYSLIST_URL;
    }

    //返回订单详细信息地址
    public static String getOrdersmoreinfolistUrl() {
        return ORDERSMOREINFOLIST_URL;
    }

    //返回申请单详细信息地址
    public static String getApplysmoreinfolistUrl() {
        return APPLYSMOREINFOLIST_URL;
    }

    //返回申请单时间地址
    public static String getApplydateinfoUrl() {
        return APPLYDATEINFO_URL;
    }

    //返回订单时间地址
    public static String getOrderdatainfoUrl() {
        return ORDERDATAINFO_URL;
    }

    //返回设置审批的地址
    public static String getApprovalapplyUrl() {
        return APPROVALAPPLY_URL;
    }

    //返回取消审批的地址
    public static String getApprovalcancleapplyServer() {
        return APPROVALCANCLEAPPLY_URL;
    }

    //返回设置审批的地址
    public static String getApprovalorderUrl() {
        return APPROVALORDER_URL;
    }

    //返回取消审批的地址
    public static String getApprovalcancleorderUrl() {
        return APPROVALCANCLEORDER_URL;
    }

    //返回申请单落实情况
    public static String getApplyfinalsUrl() {
        return APPLYFINALS_URL;
    }

    //返回订单落实情况
    public static String getOrderfinalsUrl() {
        return ORDERFINALS_URL;
    }

    //返回申请单基本信息地址
    public static String getApplyinfoUrl() {
        return APPLYINFO_URL;
    }

    //获得登陆的地址
    public static String getLoginUrl() {
        return LOGIN_URL;
    }

    //返回官方网址
    public static String getOfficialWebsite() {
        return OFFICIAL_WEBSITE;
    }

    //get
    public static RequestQueue getQueue() {
        return Queue;
    }

    //返回订单基本信息地址
    public static String getOrderStandInfoUrl() {
        return ORDER_STAND_INFO_URL;
    }

    //设置服务器ip
    public static void setServerIp(String ip) {

        SERVER_IP = ip;
    }

    //设置服务器端口
    public static void setServerPort(String port) {

        SERVER_PORT = port;
    }
}
