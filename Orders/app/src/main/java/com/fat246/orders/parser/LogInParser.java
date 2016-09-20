package com.fat246.orders.parser;

import android.util.Log;
import android.util.Xml;

import com.fat246.orders.bean.UserInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class LogInParser {

    //登陆失败代码
    public static final int ERROR_VALUE_WRONG_PASSWORD = 0;
    public static final int ERROR_VALUE_NETWORK_INCOORRECT = -1;

    //用户信息
    private UserInfo mUserInfo;

    //URL str
    private String URL_Str;

    public LogInParser(UserInfo mUserInfo, String URL_Str) {

        this.mUserInfo = mUserInfo;
        this.URL_Str = URL_Str;
    }

    //判断用户是否能够登陆
    public void checkLogIn() {

        //测试用
//        mUserInfo.operationValue=3;

        mUserInfo.operationValue = sendLogInPost("loginName=" + mUserInfo.getmUser() + "&" +
                "pwd=" + mUserInfo.getmPassword());


    }

    private int sendLogInPost(String param) {

        PrintWriter out = null;
        int result;

        try {

            URL url = new URL(URL_Str);

            Log.e("URL", "++==" + url);

            //打开和URL之间的链接
            URLConnection conn = url.openConnection();

            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 非常重要的两行
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());

            //发送请求参数
            out.print(param);

            //flush
            out.flush();

            //定义InputStream 输入流来读取URL的响应
            InputStream is = conn.getInputStream();

            result = parse(is);

            Log.e("result", result + "");

        } catch (Exception e) {
            //Log.e("misstake",""+e.getMessage());
            result = ERROR_VALUE_NETWORK_INCOORRECT;
        }


        return result;
    }

    private int parse(InputStream is) throws XmlPullParserException, IOException {

        int result = -1;

        try {

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(is, "utf-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("int")) {

                            eventType = parser.next();
                            result = Integer.parseInt(parser.getText());
                        }
                        break;
                }

                eventType = parser.next();
            }

        } finally {

            is.close();
        }
        return result;
    }
}
