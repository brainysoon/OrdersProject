package com.fat246.orders.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fat246.orders.R;
import com.fat246.orders.MyApplication;
import com.fat246.orders.bean.OrderInfo;
import com.fat246.orders.bean.OrderStandInfo;
import com.fat246.orders.parser.OrderInfoParser;

public class OrderStandInfoActivity extends AppCompatActivity {

    //订单号
    private String Order_Id;
    private Boolean Is_Passed;

    //TextView
    private TextView mProvider;
    private TextView mCompleteStatus;
    private TextView mOwner;
    private TextView mMaintain;
    private TextView mJudge;

    //layout
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_stand_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent mIntent = getIntent();

        Bundle bundle = mIntent.getExtras();

        Order_Id = bundle.getString(OrderInfo.prhsord_id);
        Is_Passed = bundle.getBoolean(OrderInfo.is_passed);

        this.setTitle(Order_Id);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (Is_Passed) {

            fab.setImageResource(R.drawable.ic_action_cancle);
        } else {

            fab.setImageResource(R.drawable.ic_action_ok);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        showBar();

        new MyAsyncTask().execute(Order_Id);
    }

    private void initView() {

        mProvider = (TextView) findViewById(R.id.provider);
        mCompleteStatus = (TextView) findViewById(R.id.complete_status);
        mOwner = (TextView) findViewById(R.id.owner);
        mMaintain = (TextView) findViewById(R.id.maintain);
        mJudge = (TextView) findViewById(R.id.judge);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
    }

    class MyAsyncTask extends AsyncTask<String, Void, OrderStandInfo> {

        @Override
        protected OrderStandInfo doInBackground(String... params) {

            return new OrderInfoParser(MyApplication.getOrderStandInfoUrl(), params[0]).getOrderInfo();
        }

        @Override
        protected void onPostExecute(OrderStandInfo orderStandInfo) {

            mProvider.append(": " + orderStandInfo.getORDER_PROVIDER());
            mCompleteStatus.append(": " + orderStandInfo.getCOMPLETE_STATUS());
            mOwner.append(": " + orderStandInfo.getORDER_OWNER());
            mMaintain.append(": " + orderStandInfo.getORDER_MAINTAIN());
            mJudge.append(": " + orderStandInfo.getORDER_JUDGE());

            hideBar();
        }
    }

    private void showBar() {

        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
    }

    private void hideBar() {

        progressBar.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
    }
}