package com.fat246.orders.parser;

import android.util.Log;
import android.util.Xml;

import com.fat246.orders.bean.Mate;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ken on 16-9-24.
 */
public class ApplyFinalsParser {

    public static List<Mate> getApplyFinals(String ApplyId, String URL_Str) {

        return sendGetApplyFinals("ApplyId=" + ApplyId, URL_Str);
    }

    private static List<Mate> sendGetApplyFinals(String param, String URL_Str) {

        PrintWriter out = null;
        List<Mate> mApplyFinals;
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

            mApplyFinals = parse(is);

        } catch (Exception e) {
            //Log.e("misstake",""+e.getMessage());
            mApplyFinals = new ArrayList<>();
        }

        return mApplyFinals;
    }

    private static List<Mate> parse(InputStream is) throws XmlPullParserException, IOException {

        List<Mate> mApplyFinals = new ArrayList<>();

        try {

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(is, "utf-8");

            //首先跳出 ArrayOfString

            int eventType = parser.getEventType();
            int i = 0;

            //引用
            String MateCode = "", MateName = "", ApplyNum = "", OrderNum = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("string")) {

                            eventType = parser.next();
                            String str = parser.getText();
                            Log.e("here", "comes---" + str);

                            if (str == null) {

                                str = "  暂  无  信 息  ";
                            }


                            switch (i % 4) {

                                case 0:

                                    MateCode = str;

                                    break;
                                case 1:

                                    MateName = str;

                                    break;

                                case 2:

                                    ApplyNum = str;

                                    break;

                                case 3:

                                    OrderNum = str;

                                    mApplyFinals.add(new Mate(MateCode, MateName, ApplyNum, OrderNum));

                                    break;
                            }

                            i++;
                        }
                        break;
                }
                eventType = parser.next();
            }

        } finally {

            is.close();
        }
        return mApplyFinals;
    }
}
