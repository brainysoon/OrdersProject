package com.fat246.orders.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fat246.orders.MyApplication;
import com.fat246.orders.R;
import com.fat246.orders.bean.Mate;
import com.fat246.orders.parser.ApplyFinalsParser;

import java.util.ArrayList;
import java.util.List;

public class ApplyFinalsActivity extends AppCompatActivity {

    //id tag
    public static final String APPLY_ID = "APPLY_ID";

    //View
    private ListView mListView;

    private ProgressDialog progDialog;

    //Data
    private List<Mate> mDataList = new ArrayList<>();

    //Adapter
    private MateAdapter mAdapter;

    //id
    private String applyId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_finals);

        mListView = (ListView) findViewById(R.id.listfinals);

        mAdapter = new MateAdapter(this);

        mListView.setAdapter(mAdapter);

        applyId = getIntent().getStringExtra(APPLY_ID);

        //加载数据
        showProgressDialog();
        new ApplyFinalsAsyncTask(MyApplication.getApplyfinalsUrl()).execute(applyId);
    }

    //显示进度条
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setMessage("正在加载，请稍后。。。。");
        progDialog.show();
    }

    private class MateAdapter extends BaseAdapter {

        LayoutInflater layoutInflater;

        public MateAdapter(Context context) {

            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.apply_finals_items, viewGroup, false);

            initView(view, i);

            return view;
        }

        //
        public void initView(View rootView, int i) {

            TextView mateCode = (TextView) rootView.findViewById(R.id.apply_finals_mate_code);
            TextView mateName = (TextView) rootView.findViewById(R.id.apply_finals_mate_name);
            TextView applyFinals = (TextView) rootView.findViewById(R.id.apply_finals_apply_num);
            TextView orderFinals = (TextView) rootView.findViewById(R.id.apply_finals_order_num);

            Mate mate = mDataList.get(i);

            if (mate != null) {

                mateCode.setText(mate.getMateCode());
                mateName.setText(mate.getMateName());
                applyFinals.setText(mate.getApplyNum());
                orderFinals.setText(mate.getOrderNum());
            }
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public Object getItem(int i) {
            return mDataList.get(i);
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }
    }

    private class ApplyFinalsAsyncTask extends AsyncTask<String, Void, List<Mate>> {

        private String URL_Str;

        public ApplyFinalsAsyncTask(String URL_Str) {

            this.URL_Str = URL_Str;
        }

        @Override
        protected List<Mate> doInBackground(String... strings) {


            return ApplyFinalsParser.getApplyFinals(strings[0], URL_Str);
        }

        @Override
        protected void onPostExecute(List<Mate> mates) {

            if (mates != null) {

                mDataList = mates;
            }

            progDialog.dismiss();
            mAdapter.notifyDataSetChanged();
        }
    }
}
