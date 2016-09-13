package com.fat246.orders.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.fat246.orders.application.MyApplication;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.manager.AutoUpdateManager;
import com.fat246.orders.utils.BottomBarUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        AutoUpdateManager.AfterUpdate, AccountHeader.OnAccountHeaderListener,
        Drawer.OnDrawerItemClickListener {

    public static final int PROFILE_ITEM_NO_USER = 21;
    public static final int PROFILE_ITEM_USER = 22;
    public static final int PROFILE_ITEM_OUT = 23;

    public static final int DRAWER_ITEM_SETTING = 10;
    public static final int DRAWER_ITEM_ABOUT = 11;
    public static final int DRAWER_ITEM_FEEDBACK = 12;
    public static final int DRAWER_ITME_UPDATE = 13;


    public static final int PROFILE_ITEM_DEFAULT_POSITION = 0;

    public static MainPage mInstance = null;

    //用户用户信息
    private UserInfo mUserInfo;

    private Toolbar mToolbar;

    //显示用户名
    private TextView mUserName;
    private NavigationView nav_view;

    //记录退出时间
    private static long exitTime;

    //bottomBar
    private BottomBar mBottomBar;

    //MaterialDrawer
    private AccountHeader mAccountHeader;
    private Drawer mDrawer;

    //Handler
    private Handler mHandler = new Handler();

    //检查更新
    Runnable nagToUpdate = new Runnable() {
        @Override
        public void run() {

            AutoUpdateManager autoUpdateManager = new AutoUpdateManager(MainPage.this);

            autoUpdateManager.beginUpdate(MainPage.this);

            Toast.makeText(MainPage.this, "检查更新，请稍候...", Toast.LENGTH_SHORT).show();
        }
    };

    //反馈
    Runnable nagToFeedBack = new Runnable() {
        @Override
        public void run() {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);

            builder.setTitle(R.string.drawer_item_feedback);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("你好，你可以将反馈信息发送到 \n邮箱：kensoon918@163.com \n或则直接拨打13166956701");

            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            builder.create().show();
        }
    };

    //关于
    Runnable nagToAbout = new Runnable() {
        @Override
        public void run() {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);

            builder.setTitle(R.string.drawer_item_feedback);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("关于信息");

            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            builder.create().show();
        }
    };

    //设置
    Runnable nagToSetting = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainPage.this, SettingActivity.class);

            startActivity(intent);
        }
    };

    //登录
    Runnable nagToLogIn = new Runnable() {
        @Override
        public void run() {

            Intent intent = new Intent(MainPage.this, LoginPage.class);
            startActivity(intent);
        }
    };

    //退出登录
    Runnable nagToLogOut = new Runnable() {
        @Override
        public void run() {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);

            builder.setTitle(R.string.alert_logout);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("是否要退出登录？");

            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    MyApplication.isLoginSucceed = false;
                    MyApplication.mUser = null;

                    Intent intent = new Intent(MainPage.this, LoginPage.class);

                    startActivity(intent);

                    MainPage.this.finish();
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            });

            builder.create().show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mInstance = this;

        //拿到用户登录信息   或者恢复数据
        mUserInfo = UserInfo.getData(this);

        initToolbar();

        initView(savedInstanceState);

    }

    private void initToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
    }

    //setView
    private void initView(Bundle savedInstanceState) {


        //BottomBar
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

        //MaterialDrawer

        SecondaryDrawerItem itemSetting = new SecondaryDrawerItem();
        itemSetting.withIdentifier(DRAWER_ITEM_SETTING)
                .withName(R.string.drawer_item_setting)
                .withIcon(GoogleMaterial.Icon.gmd_settings);

        SecondaryDrawerItem itemAbout = new SecondaryDrawerItem();
        itemAbout.withIdentifier(DRAWER_ITEM_ABOUT)
                .withName(R.string.drawer_item_about)
                .withIcon(GoogleMaterial.Icon.gmd_info);

        SecondaryDrawerItem itemFeedBack = new SecondaryDrawerItem();
        itemFeedBack.withIdentifier(DRAWER_ITEM_FEEDBACK)
                .withName(R.string.drawer_item_feedback)
                .withIcon(GoogleMaterial.Icon.gmd_adb);

        SecondaryDrawerItem itemUpdate = new SecondaryDrawerItem();
        itemUpdate.withIdentifier(DRAWER_ITME_UPDATE)
                .withName(R.string.drawer_item_update)
                .withIcon(GoogleMaterial.Icon.gmd_refresh);

        ProfileSettingDrawerItem proOut = new ProfileSettingDrawerItem();
        proOut.withIdentifier(PROFILE_ITEM_OUT)
                .withName("退出登录")
                .withIcon(GoogleMaterial.Icon.gmd_alert_circle);

        ProfileDrawerItem proAccount = new ProfileDrawerItem();
        proAccount.withIdentifier(PROFILE_ITEM_NO_USER)
                .withName("未登录")
                .withEmail("点击登录或者注册")
                .withIcon(R.drawable.profile)
                .withNameShown(true);

        // Create the AccountHeader
        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        proAccount,
                        proOut
                )
                .withOnAccountHeaderListener(this)
                .withSavedInstance(savedInstanceState)
                .build();


        //
        if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

            updateUserInfo();
        }

        //Create the drawer
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(mAccountHeader) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new DividerDrawerItem(),
                        itemAbout,
                        itemSetting,
                        itemFeedBack,
                        itemUpdate
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(this)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen()) {

            mDrawer.closeDrawer();

            return;
        }

        if (System.currentTimeMillis() - exitTime > 2000) {

            Toast.makeText(MainPage.this, "再按一次退出！", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();

        } else {

            moveTaskToBack(true);
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

    @Override
    public boolean onProfileChanged(View view, IProfile profile, boolean current) {

        Runnable runnable = null;

        //处理点击事件
        if (profile.getIdentifier() == PROFILE_ITEM_NO_USER) {

            runnable = nagToLogIn;
        } else if (profile.getIdentifier() == PROFILE_ITEM_OUT) {

            runnable = nagToLogOut;
        }

        if (runnable != null) {

            mHandler.postDelayed(runnable, 300);
        }

        return false;
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

        Runnable runnable = null;

        //非用户相关

        if (drawerItem.getIdentifier() == DRAWER_ITEM_SETTING) {

            runnable = nagToSetting;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_ABOUT) {

            runnable = nagToAbout;
        } else if (drawerItem.getIdentifier() == DRAWER_ITME_UPDATE) {

            runnable = nagToUpdate;
        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_FEEDBACK) {

            runnable = nagToFeedBack;
        }

        if (runnable != null) {

            mHandler.postDelayed(runnable, 300);

        }
        return false;
    }

    //登录成功过后
    public void updateUserInfo() {

        if (MyApplication.isLoginSucceed && MyApplication.mUser != null) {

            //首先移除原来的
            mAccountHeader.removeProfile(PROFILE_ITEM_DEFAULT_POSITION);

            ProfileDrawerItem proAccount = new ProfileDrawerItem();
            proAccount.withIdentifier(PROFILE_ITEM_USER)
                    .withName(MyApplication.mUser.getmUser())
                    .withIcon(R.drawable.profile)
                    .withNameShown(true);

            mAccountHeader.addProfile(proAccount, PROFILE_ITEM_DEFAULT_POSITION);
        }
    }
}
