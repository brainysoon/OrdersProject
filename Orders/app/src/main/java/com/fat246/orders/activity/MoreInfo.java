package com.fat246.orders.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fat246.orders.R;
import com.fat246.orders.fragment.ApplysMoreInfoFragment;
import com.fat246.orders.fragment.OrdersMoreInfoFragment;

public class MoreInfo extends AppCompatActivity {

    //Intent
    Intent mIntent;

    //详细详细
    private String ID;
    private int Location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();

        setTitleID();

        //拿到Container
        FrameLayout mFrameLayout = (FrameLayout) findViewById(R.id.fragment_framelayout);
        if (mFrameLayout != null) {
            if (savedInstanceState != null) {
                return;
            }
        }

        //Fragment
        setFragment();

    }

    //设置 Fragment
    public void setFragment() {

        Fragment mFragment = null;

        if (Location % 2 == 0) {
            mFragment = new OrdersMoreInfoFragment();
        } else {
            mFragment = new ApplysMoreInfoFragment();
        }

        //设置 ID 和 Location 信息
        Bundle mBundle = new Bundle();
        mBundle.putString("ID", ID);
        mBundle.putInt("Location", Location);

        mFragment.setArguments(mBundle);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_framelayout, mFragment).commit();

    }

    private void getData() {
        //拿到信息
        mIntent = getIntent();

        //判断是订单还是  申请单
        Location = mIntent.getIntExtra("Location", 0);
        if (Location % 2 == 0) {

            ID = mIntent.getStringExtra("PRHSORD_ID");
        } else {

            ID = mIntent.getStringExtra("PRHS_ID");
        }
    }

    //设置标题ID
    private void setTitleID() {

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        toolbarLayout.setTitle(ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:

                Toast.makeText(this, "here", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}