package com.fat246.orders.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.orders.R;
import com.fat246.orders.application.MyApplication;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.fragment.AllApplysFragment;
import com.fat246.orders.fragment.AllOrdersFragment;
import com.fat246.orders.manager.AutoUpdateManager;
import com.fat246.orders.widget.ChangeTabWithColorView;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AutoUpdateManager.AfterUpdate {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    //View
    private ChangeTabWithColorView[] mAction = new ChangeTabWithColorView[4];

    //用户用户信息
    private UserInfo mUserInfo;

    //toolBar
    private Toolbar mToolBar;

    //显示用户名
    private TextView mUserName;
    private NavigationView nav_view;

    //抽屉
    private DrawerLayout mDrawerLayou;
    private ActionBarDrawerToggle mABD;

    //记录退出时间
    private static long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //拿到用户登录信息   或者恢复数据
        if (savedInstanceState != null) {

            mUserInfo = UserInfo.getData(savedInstanceState);
            int whichTab = savedInstanceState.getInt("whichTab", 0);

            mViewPager.setCurrentItem(whichTab);

        } else {

            mUserInfo = UserInfo.getData(this);
        }

        setView();

        setListenler();

        setSomeThing();

    }

    //恢复 activity 状态
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //用户数据信息
        mUserInfo.getBundle(outState);

        //Activity 数据
        outState.putInt("whichTab", mViewPager.getCurrentItem());

    }

    //setView
    private void setView() {

        mAction[0] = (ChangeTabWithColorView) findViewById(R.id.action_all_orders);
        mAction[1] = (ChangeTabWithColorView) findViewById(R.id.action_all_applys);
        mAction[2] = (ChangeTabWithColorView) findViewById(R.id.action_mine_orders);
        mAction[3] = (ChangeTabWithColorView) findViewById(R.id.action_mine_applys);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mDrawerLayou = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        nav_view = (NavigationView) findViewById(R.id.nav_view);
    }

    //设置点击监听事件
    private void setListenler() {

        for (ChangeTabWithColorView i : mAction
                ) {
            i.setOnClickListener(this);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (positionOffset > 0) {
                    mAction[position].setIconAlpha(1 - positionOffset);
                    mAction[position + 1].setIconAlpha(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setSomeThing() {

        //手动设置ActionBar
        setSupportActionBar(mToolBar);

        //实例化FragmentAdapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //给ViewPager设置FragmentAdapter
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //选中第一个
        mAction[0].setIconAlpha(1.0f);

        //设置抽屉
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayou, mToolBar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayou.setDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);

        setHeader();

        /*      这里有待调试
        //设置用户名
        View headerView=nav_view.getHeaderView(0);
        mUserName=(TextView)headerView.findViewById(R.id.drawer_user_name);
        mUserName.append(mUserInfo.getmUser());
        */
    }

    private void setHeader() {

        View mHeader = nav_view.getHeaderView(0);

        //用户名
        TextView mText = (TextView) mHeader.findViewById(R.id.drawer_user_name);

        mText.setText(mUserInfo.getmUser());

        //注销
        TextView mCancle = (TextView) mHeader.findViewById(R.id.nav_cancle);

        mCancle.setVisibility(View.VISIBLE);

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(MainPage.this, LoginPage.class);

                mUserInfo.operationValue = 99;
                //保存登陆信息到  Preferences
                ((MyApplication) getApplication()).setUserInfo(mUserInfo);

                startActivity(mIntent);

                MainPage.this.finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

        //判断抽屉是否打开
        if (mDrawerLayou.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayou.closeDrawer(GravityCompat.START);

            return;
        }

        if (System.currentTimeMillis() - exitTime > 2000) {

            Toast.makeText(MainPage.this, "再按一次退出！", Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();

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

        //得到点击的那个  Item的 id
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_setting:

                //处理点击了设置的事件
                doSetting();
                break;
            case R.id.nav_share:

                //处理点击了分享的事件
                doShare();
                break;
            case R.id.nav_send:

                //处理点击了  发送事件
                doSend();
                break;

            case R.id.nav_update:

                //点击了检查更新事件
                doUpdate();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //点击了  drawer 设置事件
    public void doSetting() {

        Intent mIntent = new Intent(MainPage.this, SettingActivity.class);

        startActivity(mIntent);
    }

    //点击了 drawer 分享事件
    public void doShare() {
        Toast.makeText(this, "墨迹墨迹。。。", Toast.LENGTH_SHORT).show();

    }

    //点击了  drawer 发送事件
    public void doSend() {
        Toast.makeText(this, "墨迹墨迹。。。", Toast.LENGTH_SHORT).show();

    }

    //doUpdate
    public void doUpdate() {

        Toast.makeText(this, "开始检查更新。。。", Toast.LENGTH_SHORT).show();

        AutoUpdateManager autoUpdateManager = new AutoUpdateManager(this);

        autoUpdateManager.beginUpdate(this);
    }

    //重置其他Tab
    private void setIndex() {

        for (ChangeTabWithColorView i : mAction
                ) {
            i.setIconAlpha(0);
        }
    }

    @Override
    public void onClick(View v) {

        //首先重置其他Tab
        setIndex();

        switch (v.getId()) {

            case R.id.action_all_orders:
                mAction[0].setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.action_all_applys:
                mAction[1].setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.action_mine_orders:
                mAction[2].setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.action_mine_applys:
                mAction[3].setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }

    }

    //重写FragmentPagerAdapter类
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public Fragment[] fragments = new Fragment[4];

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (fragments[position] == null) {

                switch (position) {

                    case 0:

                        fragments[position] = AllOrdersFragment.newInstance(true);
                        break;

                    case 1:

                        fragments[position] = AllApplysFragment.newInstance(true);
                        break;

                    case 2:

                        fragments[position] = AllOrdersFragment.newInstance(false);
                        break;
                    case 3:

                        fragments[position] = AllApplysFragment.newInstance(false);
                        break;
                }
            }

            return fragments[position];
        }

        @Override
        public int getCount() {

            return 4;
        }
    }

    @Override
    public void toDoAfterUpdate() {

    }
}
