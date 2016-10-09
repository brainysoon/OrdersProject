package com.fat246.orders.parser;

import android.util.Xml;

import com.fat246.orders.bean.OrderInfo;
import com.fat246.orders.bean.OrderMoreInfoListItem;

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
 * Created by Administrator on 2016/3/28.
 */
public class OrdersMoreInfoListParser {

    //订单详细信息地址
    private String URL_Str;

    //订单信息
    private OrderInfo mOrderInfo;

    public OrdersMoreInfoListParser(String URL_Str, OrderInfo mOrderInfo) {

        this.URL_Str = URL_Str;
        this.mOrderInfo = mOrderInfo;
    }

    //得到订单详细信息List
    public List<OrderMoreInfoListItem> getOrdersMoreInfoList() {

        List<OrderMoreInfoListItem> mOrdersListInfo = sendGetOrdersMoreInfoList("OrderId=" + mOrderInfo.getPRHSORD_ID());

        return mOrdersListInfo;
    }

    //发送post 请求
    private List<OrderMoreInfoListItem> sendGetOrdersMoreInfoList(String param) {

        PrintWriter out = null;
        List<OrderMoreInfoListItem> mOrdersMoreInfoList;

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

            mOrdersMoreInfoList = parse(is);

        } catch (Exception e) {

            e.printStackTrace();
            mOrdersMoreInfoList = new ArrayList<>();
        }


        return mOrdersMoreInfoList;
    }

    //解析xml数据
    private List<OrderMoreInfoListItem> parse(InputStream is) throws XmlPullParserException, IOException {

        List<OrderMoreInfoListItem> mOrdersMoreInfoList = new ArrayList<>();

        try {

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "utf-8");

            //首先跳出 ArrayOfString

            int eventType = parser.getEventType();
            int i = 0;

            //引用
            String MATE_Code = null;
            String MATE_Name = null;
            String MATE_Size = null;
            String MATE_Model = null;
            String PRHSOD_AMNT = null;
            String PSR_NAME = null;

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
                                    PSR_NAME = str;

                                    break;

                                case 1:
                                    MATE_Code = str;

                                    break;
                                case 2:
                                    MATE_Name = str;

                                    break;

                                case 3:
                                    MATE_Model = str;

                                    break;

                                case 4:
                                    PRHSOD_AMNT = str;

                                    if (PRHSOD_AMNT == null) PRHSOD_AMNT = "";
                                    break;

                                case 5:
                                    MATE_Size = str;


                                    mOrdersMoreInfoList.add(new OrderMoreInfoListItem(MATE_Code
                                            , MATE_Name
                                            , MATE_Model
                                            , PSR_NAME
                                            , MATE_Size
                                            , PRHSOD_AMNT));
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

        return mOrdersMoreInfoList;
    }
}
