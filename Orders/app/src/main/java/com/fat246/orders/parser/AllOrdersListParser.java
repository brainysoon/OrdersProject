package com.fat246.orders.parser;

import android.util.Xml;

import com.fat246.orders.bean.OrderInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class AllOrdersListParser {

    //URL str
    private String URL_Str;

    //是否通过评审
    private boolean isLoadPassed;

    public AllOrdersListParser(boolean isLoadPassed, String URL_Str) {

        this.isLoadPassed = isLoadPassed;
        this.URL_Str = URL_Str;
    }

    public List<OrderInfo> getAllOrdersList() {

        //保存中网页服务上面加载下来的  xml数据
        List<OrderInfo> mOrdersList = sendGetAllOrdersListPost("autId=" + (isLoadPassed ? 4 : 0) + "&startRow=1&endRow=20");

        return mOrdersList;
    }

    //发送  post 请求
    private List<OrderInfo> sendGetAllOrdersListPost(String param) {

        PrintWriter out = null;
        List<OrderInfo> mOrdersList;

        try {

            URL url = new URL(URL_Str);

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

            mOrdersList = parse(is);

        } catch (Exception e) {

            e.printStackTrace();
            mOrdersList = new ArrayList<>();
        }

        return mOrdersList;
    }

    //解析  xml数据
    private List<OrderInfo> parse(InputStream is) throws XmlPullParserException, IOException {

        List<OrderInfo> mOrdersList = new ArrayList<>();

        try {

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(is, "utf-8");

            //首先跳出 ArrayOfString

            int eventType = parser.getEventType();
            int i = 0;

            //引用
            String PRHSORD_ID = null, NAMEE = null, PRAC_NAME = null,
                    SUM = null, IS_LOAD_PASSED_STR = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("string")) {

                            eventType = parser.next();
                            String str = parser.getText();
                            switch (i % 5) {

                                case 0:
                                    PRHSORD_ID = str;

                                    if (PRHSORD_ID == null) PRHSORD_ID = "";
                                    break;
                                case 1:
                                    NAMEE = str;

                                    if (NAMEE == null) NAMEE = "";
                                    break;
                                case 2:
                                    PRAC_NAME = str;

                                    if (PRAC_NAME == null) PRAC_NAME = "";
                                    break;

                                case 3:

                                    SUM = str;
                                    if (SUM == null) {

                                        SUM = "";
                                    }

                                    break;

                                case 4:

                                    IS_LOAD_PASSED_STR = str;

                                    if (IS_LOAD_PASSED_STR == null) IS_LOAD_PASSED_STR = "";

                                    //添加到  mOrdersList
                                    mOrdersList.add(new OrderInfo(PRHSORD_ID, NAMEE, PRAC_NAME,
                                            SUM, IS_LOAD_PASSED_STR.equals("4")));
                                    break;
                            }

                            //别忘了  ++
                            i++;
                        }
                        break;
                }
                eventType = parser.next();
            }

        } finally {

            is.close();
        }
        return mOrdersList;
    }
}
