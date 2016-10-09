package com.fat246.orders.parser;

import android.util.Xml;

import com.fat246.orders.bean.OrderStandInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class OrderInfoParser {

    //URL str
    private String URL_Str;

    //OrderId
    private String OrderId;

    public OrderInfoParser(String URL_Str, String OrderId) {

        this.URL_Str = URL_Str;
        this.OrderId = OrderId;
    }

    public OrderStandInfo getOrderInfo() {

        //保存中网页服务上面加载下来的  xml数据
        OrderStandInfo orderStandInfo = sendGetAllOrdersListPost("OrderId=" + OrderId);

        return orderStandInfo;
    }

    //发送  post 请求
    private OrderStandInfo sendGetAllOrdersListPost(String param) {

        PrintWriter out = null;
        OrderStandInfo orderStandInfo;

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

            orderStandInfo = parse(is);

        } catch (Exception e) {

            e.printStackTrace();
            orderStandInfo = null;
        }

        return orderStandInfo;
    }

    //解析  xml数据
    private OrderStandInfo parse(InputStream is) throws XmlPullParserException, IOException {

        OrderStandInfo orderStandInfo = null;

        try {

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(is, "utf-8");

            //首先跳出 ArrayOfString

            int eventType = parser.getEventType();
            int i = 0;

            //引用
            String ORDER_ID = null, ORDER_PROVIDER = null, COMPLETE_STATUS = null, ORDER_OWNER = null;
            String ORDER_MAINTAIN = null, ORDER_JUDGE = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("string")) {

                            eventType = parser.next();
                            String str = parser.getText();
                            if (str == null) {

                                str = "暂无信息";
                            }
                            switch (i % 6) {

                                case 0:
                                    ORDER_ID = str;

                                    break;
                                case 1:
                                    ORDER_PROVIDER = str;

                                    break;
                                case 2:
                                    COMPLETE_STATUS = str;

                                    break;

                                case 3:

                                    ORDER_OWNER = str;

                                    break;

                                case 4:

                                    ORDER_MAINTAIN = str;

                                    break;
                                case 5:

                                    ORDER_JUDGE = str;

                                    orderStandInfo = new OrderStandInfo(ORDER_ID, ORDER_PROVIDER, COMPLETE_STATUS,
                                            ORDER_OWNER, ORDER_MAINTAIN, ORDER_JUDGE);
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
        return orderStandInfo;
    }
}

