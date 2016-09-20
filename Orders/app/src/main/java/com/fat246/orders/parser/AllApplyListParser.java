package com.fat246.orders.parser;

import android.util.Log;
import android.util.Xml;

import com.fat246.orders.bean.ApplyInfo;

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
 * Created by Administrator on 2016/3/8.
 */
public class AllApplyListParser {

    //URL str
    private String URL_Str;

    private boolean isLoadPassed;

    public AllApplyListParser(boolean isLoadPassed, String URL_Str) {

        this.isLoadPassed = isLoadPassed;
        this.URL_Str = URL_Str;

    }

    //得到申请单信息
    public List<ApplyInfo> getAllApplyList() {

        //保存重网页上面下载 下来的 xml数据
        List<ApplyInfo> mApplyList = sendGetAllApplyListPost("autId=" + (isLoadPassed ? 4 : 1) + "&startRow=1&endRow=20");

        return mApplyList;
    }

    private List<ApplyInfo> sendGetAllApplyListPost(String param) {

        PrintWriter out = null;
        List<ApplyInfo> mApplyList;
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

            mApplyList = parse(is);

        } catch (Exception e) {
            //Log.e("misstake",""+e.getMessage());
            mApplyList = new ArrayList<>();
        }

        Log.e("length", mApplyList.size() + "");

        return mApplyList;
    }

    private List<ApplyInfo> parse(InputStream is) throws XmlPullParserException, IOException {

        List<ApplyInfo> mApplyList = new ArrayList<>();

        try {

            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(is, "utf-8");

            //首先跳出 ArrayOfString

            int eventType = parser.getEventType();
            int i = 0;

            //引用
            String PRHS_ID = null, DEP_NAME = null, PSD_NAME = null, PSR_NAME = null, IS_PASSED_STR = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {

                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("string")) {

                            eventType = parser.next();
                            String str = parser.getText();
                            Log.e("here", "comes---" + str);
                            switch (i % 5) {

                                case 0:
                                    PRHS_ID = str;

                                    if (PRHS_ID == null) PRHS_ID = "";
                                    break;
                                case 1:
                                    DEP_NAME = str;

                                    if (DEP_NAME == null) DEP_NAME = "";
                                    break;

                                case 2:
                                    PSD_NAME = str;
                                    if (PSD_NAME == null) PSD_NAME = "";
                                    break;

                                case 3:
                                    PSR_NAME = str;

                                    if (PSR_NAME == null) PSR_NAME = "";
                                    break;

                                case 4:

                                    IS_PASSED_STR = str;

                                    if (IS_PASSED_STR == null) IS_PASSED_STR = "";

                                    //添加到  mOrdersList
                                    Log.e("++add", "add to list");
                                    mApplyList.add(new ApplyInfo(PRHS_ID, DEP_NAME, PSD_NAME, PSR_NAME, IS_PASSED_STR.equals("4")));

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
        return mApplyList;
    }
}