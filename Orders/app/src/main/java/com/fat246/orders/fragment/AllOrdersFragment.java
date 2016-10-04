package com.fat246.orders.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fat246.orders.MyApplication;
import com.fat246.orders.R;
import com.fat246.orders.activity.MoreInfo;
import com.fat246.orders.activity.OrderStandInfoActivity;
import com.fat246.orders.bean.OrderInfo;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.parser.AllOrdersListParser;
import com.fat246.orders.widget.Ptr.PtrClassicFrameLayout;
import com.fat246.orders.widget.Ptr.PtrDefaultHandler;
import com.fat246.orders.widget.Ptr.PtrFrameLayout;
import com.fat246.orders.widget.Ptr.PtrHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllOrdersFragment extends Fragment {

    //地址
    private static final String SET_ORDER_APPROVAL = "http://192.168.56.1:8080//Service1.asmx//setOrderApproval";

    //订单的地址
    private String ALLORDERSLIST_URL;

    //首先是下拉刷新
    private PtrClassicFrameLayout mPtrFrame;

    //List控件
    private ListView mListView;

    private Button btmButtom;

    private OrdersAdapter mAdapter;

    private int start = 0;

    //集合List
    private List<OrderInfo> mList = new ArrayList<>();

    //用户信息
    private UserInfo mUserInfo;

    //是否通过评审
    private boolean isLoadPassed;
    private static final String IS_LOAD_PASSED = "is_load_passed";

    public AllOrdersFragment() {
    }

    public static AllOrdersFragment newInstance(boolean isLoadPassed) {

        AllOrdersFragment allOrdersFragment = new AllOrdersFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(IS_LOAD_PASSED, isLoadPassed);

        allOrdersFragment.setArguments(bundle);

        return allOrdersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            isLoadPassed = getArguments().getBoolean(IS_LOAD_PASSED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_orders, container, false);

        //得到订单的网络地址
        this.ALLORDERSLIST_URL = MyApplication.getAllorderslistUrl();

        //得到用户信息
        mUserInfo = getUserInfo();

        //设置List
        setList(rootView);

        //设置下拉刷新
        setPtr(rootView);

        return rootView;
    }

    //获取用户信息
    private UserInfo getUserInfo() {

        return UserInfo.getData(getActivity());
    }

    //设置List 以及其数据
    public void setList(View rootView) {

        mListView = (ListView) rootView.findViewById(R.id.ptr_list_all_orders);
        btmButtom = (Button) rootView.findViewById(R.id.add_more_orders);

        mAdapter = new OrdersAdapter(this.getContext());

        mListView.setAdapter(mAdapter);


        //Item 单击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), MoreInfo.class);

                //得到用户PO
                String PRHSORD_ID = mList.get(position).getPRHSORD_ID();

                intent.putExtra("PRHSORD_ID", PRHSORD_ID);
                intent.putExtra("Location", 0);

                startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                //当没在底部的时候 设置为不可见
                if ((i + i1) < i2) {

                    if (btmButtom.getVisibility() != View.GONE) {

                        btmButtom.setVisibility(View.GONE);
                    }

                }

                //当在底部的时候 和有数据的时候 设置为可见
                // i firstVisibleItem,i1 visibleItemCount, i2 totalItemCount
                else if (i2 != 0 && (i + i1) == i2 && (i1 < i2)) {

                    if (btmButtom.getVisibility() != View.VISIBLE) {

                        btmButtom.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        //设置 常按的点击事件
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showPopupWindow(view, position);

                //不响应  点击事件
                return true;
            }
        });

        //BtmClick
        btmButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //加载更多数据
                new AddMoreOrdersAsyncTask(ALLORDERSLIST_URL, start, start + 20).execute(mUserInfo);
            }
        });
    }

    private void showPopupWindow(View v, int position) {

        //首先出事话内容
        View contentView = LayoutInflater.from(getActivity())
                .inflate(R.layout.popupwindow_layout, null);

        final PopupWindow mPop = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        //设置监听事件
        setLisenler(contentView, v, mPop, position);

        mPop.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        mPop.setTouchable(true);

        //为了使其显示在上方

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPop.showAsDropDown(v, 25, -v.getHeight(), Gravity.CENTER);
        } else {
            mPop.showAsDropDown(v, 25, -v.getHeight());
        }
    }

    //设置popupwindow　的监听事件
    private void setLisenler(View contentView, final View item, final PopupWindow mPop,
                             final int position) {

        //四个按钮
        Button standInfo = (Button) contentView.findViewById(R.id.popupwindow_stand_info);
        Button timeInfo = (Button) contentView.findViewById(R.id.popupwindow_time_info);
        Button progressInfo = (Button) contentView.findViewById(R.id.popupwindow_progress_info);
        Button slectionState = (Button) contentView.findViewById(R.id.popupwindow_slection_state);

        standInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent mIntent = new Intent(AllOrdersFragment.this.getContext(), OrderStandInfoActivity.class);

                Bundle bundle = new Bundle();

                OrderInfo orderInfo = mList.get(position);

                bundle.putString(OrderInfo.prhsord_id, orderInfo.getPRHSORD_ID());

                bundle.putBoolean(OrderInfo.is_passed, orderInfo.getIS_PASSED());

                mIntent.putExtras(bundle);

                startActivity(mIntent);

                mPop.dismiss();
            }
        });

        timeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        progressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        slectionState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView mID = (TextView) item.findViewById(R.id.all_orders_prhsord_id);

                setOrderApprovalRequest(mID.getText().toString().trim());

                Log.e("here", "comes");
            }
        });
    }

    //包装的下拉刷新
    public void setPtr(View rootView) {
        mPtrFrame = (PtrClassicFrameLayout) rootView.findViewById(R.id.ptr_all_orders);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                //异步刷新加载数据
                new AllOrdersAsyncTask(frame, ALLORDERSLIST_URL).execute(mUserInfo);
            }
        });

        // 这些大部分都是默认设置
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
    }

    //异步加载所有 的 数据
    private class AllOrdersAsyncTask extends AsyncTask<UserInfo, Void, List<OrderInfo>> {

        private PtrFrameLayout frame;

        private String URL_Str;

        public AllOrdersAsyncTask(PtrFrameLayout frame, String URL_Str) {

            this.frame = frame;
            this.URL_Str = URL_Str;
        }

        @Override
        protected List<OrderInfo> doInBackground(UserInfo... params) {

            //通过AllOrdersListParser 对象  解析 xml 数据
            return new AllOrdersListParser(isLoadPassed, URL_Str).getAllOrdersList();
        }

        @Override
        protected void onPostExecute(List<OrderInfo> orderInfos) {

            mList = orderInfos;

            start = orderInfos.size() + 1;

            frame.refreshComplete();

            mAdapter.notifyDataSetChanged();
        }
    }

    //设置  订单审批
    private void setOrderApprovalRequest(final String OrderId) {

        StringRequest mRequest = new StringRequest(Request.Method.POST, SET_ORDER_APPROVAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.e("result", s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Log.e("error", volleyError.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();

                map.put("authName", mUserInfo.getmUser());

                map.put("OrderId", OrderId);
                return map;
            }
        };

        mRequest.setTag("setOrderApprovalRequest");

        MyApplication.getQueue().add(mRequest);
    }

    //加载更多
    private class AddMoreOrdersAsyncTask extends AsyncTask<UserInfo, Void, List<OrderInfo>> {

        //URL
        private String URL_Str;

        private int start;
        private int end;

        public AddMoreOrdersAsyncTask(String URL_Str, int start, int end) {

            this.URL_Str = URL_Str;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<OrderInfo> doInBackground(UserInfo... userInfos) {

            //下载并解析
            return new AllOrdersListParser(isLoadPassed, URL_Str).getAllOrdersList(start, end);
        }

        @Override
        protected void onPostExecute(List<OrderInfo> orderInfos) {

            for (int i = 0; i < orderInfos.size(); i++) {

                mList.add(orderInfos.get(i));
            }

            if (btmButtom.getVisibility() != View.GONE) {


                btmButtom.setVisibility(View.GONE);
            }

            //显示加载成功
            if (getContext() != null) {

                Toast.makeText(getContext(), R.string.laod_succeed, Toast.LENGTH_SHORT).show();
            }

            start += orderInfos.size() + 1;
            mAdapter.notifyDataSetChanged();
        }
    }

    private class OrdersAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public OrdersAdapter(Context context) {

            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            LayoutInflater mInflater = LayoutInflater.from(getActivity());
            view = mInflater.inflate(R.layout.fragment_all_orders_item, null);

            TextView mPRHSORD_ID = (TextView) view.findViewById(R.id.all_orders_prhsord_id);
            TextView mNAMEE = (TextView) view.findViewById(R.id.all_orders_namee);
            TextView mPRAC_NAME = (TextView) view.findViewById(R.id.all_orders_prac_name);
            TextView mSUM = (TextView) view.findViewById(R.id.all_orders_sum);

            OrderInfo mOI = mList.get(i);

            mPRHSORD_ID.append(mOI.getPRHSORD_ID());
            mNAMEE.append(mOI.getNAMEE());
            mPRAC_NAME.append(mOI.getPRAC_NAME());
            mSUM.append(mOI.getSUM());


            return view;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }
}