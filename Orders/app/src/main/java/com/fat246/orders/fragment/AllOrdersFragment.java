package com.fat246.orders.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.fat246.orders.MyApplication;
import com.fat246.orders.R;
import com.fat246.orders.activity.MoreInfo;
import com.fat246.orders.activity.OrderFinalsActivity;
import com.fat246.orders.activity.OrderStandInfoActivity;
import com.fat246.orders.bean.OrderInfo;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.parser.AllOrdersListParser;
import com.fat246.orders.parser.ApprovalOrderParser;
import com.fat246.orders.parser.OrderDateInfoParser;
import com.fat246.orders.widget.Ptr.PtrClassicFrameLayout;
import com.fat246.orders.widget.Ptr.PtrDefaultHandler;
import com.fat246.orders.widget.Ptr.PtrFrameLayout;
import com.fat246.orders.widget.Ptr.PtrHandler;

import java.util.ArrayList;
import java.util.List;

public class AllOrdersFragment extends Fragment {

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

        if (isLoadPassed) {

            slectionState.setText(R.string.popupwindow_slection_state_not);
        }

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

                Toast.makeText(getContext(), "正在查询。。。", Toast.LENGTH_SHORT).show();

                new OrderDateInfo(MyApplication.getOrderdatainfoUrl())
                        .execute(mList.get(position).getPRHSORD_ID());

                mPop.dismiss();
            }
        });

        progressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), OrderFinalsActivity.class);

                intent.putExtra(OrderFinalsActivity.ORDER_ID, mList.get(position).getPRHSORD_ID());

                getActivity().startActivity(intent);

                mPop.dismiss();
            }
        });

        slectionState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoadPassed) {

                    new ApprovalOrder(MyApplication.mUser.getmUser(),
                            MyApplication.getApprovalcancleorderUrl())
                            .execute(mList.get(position).getPRHSORD_ID());
                } else {

                    new ApprovalOrder(MyApplication.mUser.getmUser(),
                            MyApplication.getApprovalorderUrl())
                            .execute(mList.get(position).getPRHSORD_ID());
                }

                mPop.dismiss();
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

    //加载时间
    private class OrderDateInfo extends AsyncTask<String, Void, List<String>> {

        private String URL_Str;

        public OrderDateInfo(String URL_Str) {

            this.URL_Str = URL_Str;
        }

        @Override
        protected List<String> doInBackground(String... strings) {

            return OrderDateInfoParser.getOrderDataInfo(strings[0], URL_Str);
        }

        @Override
        protected void onPostExecute(List<String> strings) {

            String[] str = new String[strings.size()];

            for (int i = 0; i < strings.size(); i++) {

                str[i] = strings.get(i);
            }

            if (getContext() != null && str.length > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("订单时间信息");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setItems(str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                builder.setCancelable(false);

                builder.create().show();
            }
        }
    }

    //撤销审批
    private class ApprovalOrder extends AsyncTask<String, Void, Integer> {

        private String authName;

        private String URL_Str;

        public ApprovalOrder(String authName, String URL_Str) {

            this.authName = authName;
            this.URL_Str = URL_Str;
        }

        @Override
        protected Integer doInBackground(String... strings) {

            return ApprovalOrderParser.getApprovalOrderParser(authName, strings[0], URL_Str);
        }

        @Override
        protected void onPostExecute(Integer integer) {

            String str = "未知错误";

            if (isLoadPassed) {

                switch (integer) {

                    case 1:
                        str = "取消审批成功";
                        break;
                    case 2:
                        str = "取消审批操作";
                        break;
                    case 3:
                        str = "已安排采购，不能操作";
                }
            } else {

                switch (integer) {

                    case 1:
                        str = "审批成功";
                        break;
                    case 2:
                        str = "审批操作失败";
                        break;
                    case 3:
                        str = "审批信息变化，不能操作";
                }
            }

            if (getContext() != null) {

                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();

                mPtrFrame.autoRefresh();
            }
        }
    }
}