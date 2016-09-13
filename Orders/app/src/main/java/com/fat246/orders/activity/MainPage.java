package com.fat246.orders.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.orders.R;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.manager.AutoUpdateManager;
import com.fat246.orders.utils.BottomBarUtils;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AutoUpdateManager.AfterUpdate {

    //用户用户信息
    private UserInfo mUserInfo;

    //显示用户名
    private TextView mUserName;
    private NavigationView nav_view;

    //记录退出时间
    private static long exitTime;

    //bottomBar
    private BottomBar mBottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //拿到用户登录信息   或者恢复数据
        mUserInfo = UserInfo.getData(this);

        initToolbar();

        initView(savedInstanceState);

    }

    private void initToolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
    }

    //setView
    private void initView(Bundle savedInstanceState) {

        mBottomBar = BottomBar.attach(this, savedInstanceState);

        mBottomBar.useFixedMode();
        mBottomBar.setItems(R.menu.menu_bottombar);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                Fragment mFragment = BottomBarUtils.getFragmentByMenuItemId(menuItemId);

                getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });

        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(1, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(2, ContextCompat.getColor(this, R.color.colorPrimary));
        mBottomBar.mapColorForTab(3, ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - exitTime > 2000) {

            Toast.makeText(MainPage.this, "再按一次退出！", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();

        } else {

            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //判断点击的是哪一个
        switch (id) {

            case R.id.action_settings:
                doSetting();
                break;
            case R.id.main_menu_search:
                doMainSearch();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //索搜
    public void doMainSearch() {
        Toast.makeText(this, "墨迹墨迹。。。", Toast.LENGTH_SHORT).show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        return true;
    }

    //点击了  drawer 设置事件
    public void doSetting() {

        Intent mIntent = new Intent(MainPage.this, SettingActivity.class);

        startActivity(mIntent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void toDoAfterUpdate() {

    }
}
