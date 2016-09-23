package com.fat246.orders.parser;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class ApprovalApplyParser {

    public static Integer getApprovalApplyParser(String authName, String applyId, String URL_Str) {

        return sendApprovalApplyDatePost("authName=" + authName + "&ApplyId=" + applyId, URL_Str);
    }

    private static Integer sendApprovalApplyDatePost(String param, String URL_Str) {

        PrintWriter out = null;
        Integer code = 0;
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

            code = parse(is);

        } catch (Exception e) {
            //Log.e("misstake",""+e.getMessage());
            code = 0;
        }

        return code;
    }

    private static Integer parse(InputStream is) throws XmlPullParserException, IOException {

        Integer code = 0;

        try {

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(is, "utf-8");

            //首先跳出 ArrayOfString

            int eventType = parser.getEventType();
            int i = 0;

            //引用
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("int")) {

                            eventType = parser.next();
                            String str = parser.getText();

                            code = Integer.parseInt(str);
                        }
                        break;
                }
                eventType = parser.next();
            }

        } finally {

            is.close();
        }
        return code;
    }
}
