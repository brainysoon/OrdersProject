package com.fat246.orders.fragment;

import android.app.AlertDialog;
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
import com.fat246.orders.bean.ApplyInfo;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.parser.AllApplyListParser;
import com.fat246.orders.parser.ApplyDataInfoParser;
import com.fat246.orders.widget.Ptr.PtrClassicFrameLayout;
import com.fat246.orders.widget.Ptr.PtrDefaultHandler;
import com.fat246.orders.widget.Ptr.PtrFrameLayout;
import com.fat246.orders.widget.Ptr.PtrHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/23.
 */
public class AllApplysFragment extends Fragment {

    //申请单地址
    private String ALLORDERSLIST_URL;

    //同样的  得有下拉刷新
    private PtrClassicFrameLayout mPtrFrame;

    //ListView
    private ListView mListView;
    private BaseAdapter mAdapter;

    //start
    private int start = 0;

    //BottomButton
    private Button btmButtom;

    //同样的  数据集合
    private List<ApplyInfo> mList = new ArrayList<>();

    //用户数据
    private UserInfo mUserInfo;

    //当前实例需要加载的是审批的
    private boolean isLoadPassed;
    private static final String IS_LOAD_PASSED = "is_load_passed";

    public AllApplysFragment() {
    }

    public static AllApplysFragment newInstance(boolean isLoadPassed) {

        AllApplysFragment allApplysFragment = new AllApplysFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(IS_LOAD_PASSED, isLoadPassed);

        allApplysFragment.setArguments(bundle);

        return allApplysFragment;
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

        View rootView = inflater.inflate(R.layout.fragment_all_applys, container, false);

        //得到用户信息
        mUserInfo = getUserInfo();

        //得到申请单的网络地址
        ALLORDERSLIST_URL = MyApplication.getAllapplyslistUrl();

        //设置List
        setList(rootView);

        //设置下来刷新
        setPtr(rootView);

        return rootView;
    }

    //获取用户信息
    private UserInfo getUserInfo() {

        return UserInfo.getData(getActivity());
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

        timeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), "正在查询。。。", Toast.LENGTH_SHORT).show();

                new ApplyDateInfo(MyApplication.getApplydateinfoUrl())
                        .execute(mList.get(position).getPRHS_ID());
            }
        });
    }

    //设置一些
    public void setList(View rootView) {

        mListView = (ListView) rootView.findViewById(R.id.ptr_list_all_applys);

        btmButtom = (Button) rootView.findViewById(R.id.add_more_applys);

        mAdapter = new ApplysAdapter();

        mListView.setAdapter(mAdapter);

        //Item 单击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), MoreInfo.class);

                //得到用户PO
                String PRHS_ID = mList.get(position).getPRHS_ID();

                intent.putExtra("PRHS_ID", PRHS_ID);
                intent.putExtra("Location", 1);

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
                else if (i2 != 0 && (i + i1) == i2) {

                    if (btmButtom.getVisibility() != View.VISIBLE) {

                        btmButtom.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        //BtmClick
        btmButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //加载更多数据
                new AddMoreApplysAsyncTask(ALLORDERSLIST_URL, start, start + 20).execute(mUserInfo);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //显示 popupWindow
                showPopupWindow(view, i);

                return true;
            }
        });
    }

    //包装的下拉刷新
    public void setPtr(View rootView) {
        mPtrFrame = (PtrClassicFrameLayout) rootView.findViewById(R.id.ptr_all_applys);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                //异步刷新加载数据
                new AllApplysAsyncTask(frame, ALLORDERSLIST_URL).execute(mUserInfo);
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

    //异步 下载并解析数据
    private class AllApplysAsyncTask extends AsyncTask<UserInfo, Void, List<ApplyInfo>> {

        //ptr
        private PtrFrameLayout frame;

        //URL
        private String URL_Str;

        public AllApplysAsyncTask(PtrFrameLayout frame, String URL_Str) {

            this.frame = frame;
            this.URL_Str = URL_Str;
        }

        @Override
        protected List<ApplyInfo> doInBackground(UserInfo... params) {

            //下载并解析
            return new AllApplyListParser(isLoadPassed, URL_Str).getAllApplyList();
        }

        @Override
        protected void onPostExecute(List<ApplyInfo> applyInfos) {

            mList = applyInfos;

            start = applyInfos.size();

            this.frame.refreshComplete();

            mAdapter.notifyDataSetChanged();
        }
    }

    //加载更多
    private class AddMoreApplysAsyncTask extends AsyncTask<UserInfo, Void, List<ApplyInfo>> {

        //URL
        private String URL_Str;

        private int start;
        private int end;

        public AddMoreApplysAsyncTask(String URL_Str, int start, int end) {

            this.URL_Str = URL_Str;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<ApplyInfo> doInBackground(UserInfo... userInfos) {

            //下载并解析
            return new AllApplyListParser(isLoadPassed, URL_Str).getAllApplyList(start, end);
        }

        @Override
        protected void onPostExecute(List<ApplyInfo> applyInfos) {

            for (int i = 0; i < applyInfos.size(); i++) {

                mList.add(applyInfos.get(i));
            }

            if (btmButtom.getVisibility() != View.GONE) {


                btmButtom.setVisibility(View.GONE);
            }

            //显示加载成功
            if (getContext() != null) {

                Toast.makeText(getContext(), R.string.laod_succeed, Toast.LENGTH_SHORT).show();
            }

            start += applyInfos.size();
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ApplysAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                LayoutInflater mInflater = LayoutInflater.from(getActivity());
                convertView = mInflater.inflate(R.layout.fragment_all_applys_item, null);

                TextView mPRHS_ID = (TextView) convertView.findViewById(R.id.all_applys_prhs_id);
                TextView mDEP_NAME = (TextView) convertView.findViewById(R.id.all_applys_dep_name);
                TextView mPSD_NAME = (TextView) convertView.findViewById(R.id.all_applys_psd_name);
                TextView mPSR_NAME = (TextView) convertView.findViewById(R.id.all_applys_psr_name);

                ApplyInfo mAF = mList.get(position);
                mPRHS_ID.append(mAF.getPRHS_ID());
                mDEP_NAME.append(mAF.getDEP_NAME());
                mPSD_NAME.append(mAF.getPSD_NAME());
                mPSR_NAME.append(mAF.getPSR_NAME());

            }

            return convertView;
        }
    }

    //加载时间
    private class ApplyDateInfo extends AsyncTask<String, Void, List<String>> {

        private String URL_Str;

        public ApplyDateInfo(String URL_Str) {

            this.URL_Str = URL_Str;
        }

        @Override
        protected List<String> doInBackground(String... strings) {

            return ApplyDataInfoParser.getApplyDataInfo(strings[0], URL_Str);
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
}
