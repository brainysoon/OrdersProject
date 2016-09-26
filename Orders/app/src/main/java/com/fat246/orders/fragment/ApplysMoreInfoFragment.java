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
import com.fat246.orders.bean.ApplyInfo;
import com.fat246.orders.bean.ApplyMoreInfoListItem;
import com.fat246.orders.parser.ApplysMoreInfoListParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sun on 2016/2/19.
 */
public class ApplysMoreInfoFragment extends Fragment {

    //ID 和 Location
    private int Location;

    //ListView
    private ListView mList;

    //DataList
    private List<ApplyMoreInfoListItem> mDataList = new ArrayList<>();

    //申请单信息
    private ApplyInfo mApplyInfo;

    //适配器
    private BaseAdapter mAdapter = new ApplysMoreInfoAdapter();


    public ApplysMoreInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_applys_more_info, container, false);

        mList = (ListView) rootView.findViewById(R.id.apply_more_info_list);
        mList.setAdapter(mAdapter);

        //设置 ID Location
        setData();

        //异步
        new ApplysMoreInfoAsyncTask(rootView, mAdapter).execute(mApplyInfo);

        return rootView;
    }

    public void setData() {

        Bundle mBundle = getArguments();

        mApplyInfo = new ApplyInfo(mBundle.getString("ID", "null"));
        this.Location = mBundle.getInt("Location", 0);
    }

    //异步加载数据
    private class ApplysMoreInfoAsyncTask extends AsyncTask<ApplyInfo, Void, List<ApplyMoreInfoListItem>> {

        //View
        private View rootView;

        private BaseAdapter mAdapter;

        public ApplysMoreInfoAsyncTask(View rootView, BaseAdapter mAdapter) {

            this.rootView = rootView;
            this.mAdapter = mAdapter;
        }

        @Override
        protected List<ApplyMoreInfoListItem> doInBackground(ApplyInfo... params) {

            return new ApplysMoreInfoListParser(MyApplication.getApplysmoreinfolistUrl(), mApplyInfo).getApplysMoreInfoList();
        }

        @Override
        protected void onPostExecute(List<ApplyMoreInfoListItem> applyMoreInfoList) {

            //设置UI
            mDataList = applyMoreInfoList;
//            mAdapter.notify();

            //关闭进度条的显示
            ProgressBar mProgressBar = (ProgressBar) rootView.findViewById(R.id.applys_more_info_progress_bar);
            mProgressBar.setVisibility(View.GONE);

            //LinearLayout 可见
            LinearLayout mLinearLayout = (LinearLayout) rootView.findViewById(R.id.applys_more_info_linear_layout);
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    class ApplysMoreInfoAdapter extends BaseAdapter {

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

            //LayoutInflater
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            convertView = layoutInflater.inflate(R.layout.applys_more_info_list_item, null);

            //setData
            setData(position, convertView);

            return convertView;
        }

        private void setData(int position, View convertView) {

            //找到相应的View
            TextView MATE_Code = (TextView) convertView.findViewById(R.id.apply_more_info_list_item_MATE_Code);
            TextView MATE_Name = (TextView) convertView.findViewById(R.id.apply_more_info_list_item_MATE_Name);
            TextView MATE_Size = (TextView) convertView.findViewById(R.id.apply_more_info_list_item_MATE_Size);
            TextView PRHSD_AMNT = (TextView) convertView.findViewById(R.id.apply_more_info_list_item_PRHSD_AMNT);
            TextView MATE_PRICEP = (TextView) convertView.findViewById(R.id.apply_more_info_list_item_MATE_PRICEP);
            TextView totle = (TextView) convertView.findViewById(R.id.apply_more_info_list_item_totle);

            //设置数据
            ApplyMoreInfoListItem mItem = mDataList.get(position);
            MATE_Code.append(mItem.getMATE_Code());
            MATE_Name.append(mItem.getMATE_Name());
            MATE_Size.append(mItem.getMATE_Size());
            PRHSD_AMNT.append(mItem.getPRHSD_AMNT());
            MATE_PRICEP.append(mItem.getMATE_PRICEP());

            try {

                double num = Double.parseDouble(mItem.getPRHSD_AMNT());
                double price = Double.parseDouble(mItem.getMATE_PRICEP());

                totle.append(((Double) (num * price)).shortValue() + "");
            } catch (Exception e) {

                totle.append("无法计算");
            }
        }
    }
}
