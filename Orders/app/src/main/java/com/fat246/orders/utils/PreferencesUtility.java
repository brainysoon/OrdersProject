package com.fat246.orders.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ken on 16-7-17.
 */
public class PreferencesUtility {

    //检查版本相关
    public static final String JUMP_AUTO_UPDATE_CODE = "jump_auto_update_code";

    //服务器参数
    public static final String SERVER_IP = "setting_server_ip";
    public static final String SERVER_PORT = "setting_server_port";

    private static PreferencesUtility mInstance = null;
    private static SharedPreferences mPreferences = null;

    //初始化
    private PreferencesUtility(Context context) {

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //得到实例
    public static PreferencesUtility getInstance(Context context) {

        if (mInstance == null) {

            mInstance = new PreferencesUtility(context);
        }

        return mInstance;
    }

    //获得跳过的版本号
    public int getJumpVersionCode() {

        return mPreferences.getInt(JUMP_AUTO_UPDATE_CODE, -1);
    }

    //设置跳过的版本号
    public void setJumpVersionCode(int code) {

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putInt(JUMP_AUTO_UPDATE_CODE, code);

        editor.apply();
    }

    //获得服务器的ip
    public String getServerIp() {

        return mPreferences.getString(SERVER_IP, "192.168.1.127");
    }

    //获得服务器的端口
    public String getServerPort() {

        return mPreferences.getString(SERVER_PORT, "8081");
    }
}
