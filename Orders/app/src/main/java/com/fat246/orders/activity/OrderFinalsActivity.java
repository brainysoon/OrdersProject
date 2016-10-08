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
import com.fat246.orders.bean.OrderMate;
import com.fat246.orders.parser.OrderFinalsParser;

import java.util.ArrayList;
import java.util.List;

public class OrderFinalsActivity extends AppCompatActivity {

    public static final String ORDER_ID = "ORDER_ID";

    private ListView mListView;

    private ProgressDialog progDialog;

    //Data
    private List<OrderMate> mDataList = new ArrayList<>();

    //Adapter
    private OrderMateAdapter mAdapter;

    private String OrderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_finals);

        mListView = (ListView) findViewById(R.id.order_listfinals);

        mAdapter = new OrderMateAdapter(this);

        mListView.setAdapter(mAdapter);

        OrderId = getIntent().getStringExtra(ORDER_ID);

        //加载数据
        showProgressDialog();
        new OrderFinalsAsyncTask(MyApplication.getOrderfinalsUrl()).execute(OrderId);
    }

    private class OrderMateAdapter extends BaseAdapter {

        LayoutInflater layoutInflater = null;

        public OrderMateAdapter(Context context) {

            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = layoutInflater.inflate(R.layout.order_finals_items, viewGroup, false);

            initView(view, i);

            return view;
        }

        private void initView(View view, int i) {

            TextView MateCode = (TextView) view.findViewById(R.id.order_finals_mate_code);
            TextView MateName = (TextView) view.findViewById(R.id.order_finals_mate_name);
            TextView PrhsodAmnt = (TextView) view.findViewById(R.id.order_finals_amnt);
            TextView PrhsodAccein = (TextView) view.findViewById(R.id.order_finals_accein);
            TextView PrhsodBillin = (TextView) view.findViewById(R.id.order_finals_billin);
            TextView PrhsodAmntRtn = (TextView) view.findViewById(R.id.order_finals_amnt_rtn);

            OrderMate om = mDataList.get(i);

            if (om != null) {

                MateCode.setText(om.getMateCode());
                MateName.setText(om.getMateName());
                PrhsodAmnt.setText(om.getPrhsodAmnt());
                PrhsodAccein.setText(om.getPrhsodAccein());
                PrhsodBillin.setText(om.getPrhsodBillin());
                PrhsodAmntRtn.setText(om.getPrhsodAmntRtn());
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

    //显示进度条
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setMessage("正在加载，请稍后。。。。");
        progDialog.show();
    }

    private class OrderFinalsAsyncTask extends AsyncTask<String, Void, List<OrderMate>> {

        private String URL_Str;

        public OrderFinalsAsyncTask(String URL_Str) {

            this.URL_Str = URL_Str;
        }

        @Override
        protected List<OrderMate> doInBackground(String... strings) {


            return OrderFinalsParser.getOrderFinals(strings[0], URL_Str);
        }

        @Override
        protected void onPostExecute(List<OrderMate> orderMates) {

            if (orderMates != null) {

                mDataList = orderMates;
            }

            progDialog.dismiss();
            mAdapter.notifyDataSetChanged();
        }
    }
}
