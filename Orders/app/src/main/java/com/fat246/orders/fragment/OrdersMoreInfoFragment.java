package com.fat246.orders.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fat246.orders.MyApplication;
import com.fat246.orders.R;
import com.fat246.orders.bean.OrderInfo;
import com.fat246.orders.bean.OrderMoreInfoListItem;
import com.fat246.orders.parser.OrdersMoreInfoListParser;

import java.util.ArrayList;
import java.util.List;

public class OrdersMoreInfoFragment extends Fragment {

    //location
    private int Location;

    //list
    private ListView mList;

    //OrderInfo
    private OrderInfo mOrderInfo;

    //datalist
    private List<OrderMoreInfoListItem> mDataList = new ArrayList<>();

    //适配器
    private OrdersMoreInfoAdapter mAdapter = new OrdersMoreInfoAdapter();


    public OrdersMoreInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_orders_more_info, container, false);

        //找到ListView
        mList = (ListView) rootView.findViewById(R.id.orders_more_info_list);
        mList.setAdapter(mAdapter);

        //设置 信息
        setData();

        //
        new OrdersMoreInfoAsyncTask(rootView, mAdapter).execute(mOrderInfo);

        return rootView;
    }

    //设置 ID 和 Location
    public void setData() {

        Bundle mBundle = getArguments();

        mOrderInfo = new OrderInfo(mBundle.getString("ID", "null"));
        this.Location = mBundle.getInt("Location", 0);
    }


    //异步加载数据
    private class OrdersMoreInfoAsyncTask extends AsyncTask<OrderInfo, Void, List<OrderMoreInfoListItem>> {

        //rootView
        private View rootView;
        private BaseAdapter mAdapter;

        public OrdersMoreInfoAsyncTask(View rootView, BaseAdapter mAdapter) {

            this.rootView = rootView;
            this.mAdapter = mAdapter;
        }

        @Override
        protected List<OrderMoreInfoListItem> doInBackground(OrderInfo... params) {

            return new OrdersMoreInfoListParser(MyApplication.getOrdersmoreinfolistUrl(), params[0]).getOrdersMoreInfoList();
        }

        @Override
        protected void onPostExecute(List<OrderMoreInfoListItem> orderMoreInfoListItemList) {


            //设置UI
            mDataList = orderMoreInfoListItemList;
            mAdapter.notifyDataSetChanged();

            //关闭进度条
            ProgressBar mProgressBar = (ProgressBar) rootView.findViewById(R.id.orders_more_info_progress_bar);
            mProgressBar.setVisibility(View.GONE);

            //LinearLayout 可见
            LinearLayout mLinearLayout = (LinearLayout) rootView.findViewById(R.id.orders_more_info_linear_layout);
            mLinearLayout.setVisibility(View.VISIBLE);

        }
    }

    //适配器
    class OrdersMoreInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //重新实例化View
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            convertView = layoutInflater.inflate(R.layout.orders_more_info_list_item, null);

            //设置  data
            setData(position, convertView);

            return convertView;
        }

        public void setData(int position, View convertView) {

            //找到相应的View
            TextView MATE_Code = (TextView) convertView.findViewById(R.id.orders_more_info_list_item_MATE_Code);
            TextView MATE_Name = (TextView) convertView.findViewById(R.id.orders_more_info_list_item_MATE_Name);
            TextView MATE_Size = (TextView) convertView.findViewById(R.id.orders_more_info_list_item_MATE_Size);
            TextView MATE_Model = (TextView) convertView.findViewById(R.id.orders_more_info_list_item_MATE_Model);
            TextView PRHSOD_AMNT = (TextView) convertView.findViewById(R.id.orders_more_info_list_item_PRHSOD_AMNT);
            TextView PSR_NAME = (TextView) convertView.findViewById(R.id.orders_more_info_list_item_PSR_NAME);

            //设置详细信息
            OrderMoreInfoListItem mItem = mDataList.get(position);
            MATE_Code.append(mItem.getMATE_Code());
            MATE_Name.append(mItem.getMATE_Name());
            MATE_Size.append(mItem.getMATE_Size());
            MATE_Model.append(mItem.getMATE_Model());
            PRHSOD_AMNT.append(mItem.getPRHSOD_AMNT());
            PSR_NAME.append(mItem.getPSR_NAME());
        }
    }
}
