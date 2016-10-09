package com.fat246.orders.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fat246.orders.MyApplication;
import com.fat246.orders.R;
import com.fat246.orders.bean.UserInfo;
import com.fat246.orders.parser.LogInParser;

public class LoginPage extends AppCompatActivity {

    //View
    private AutoCompleteTextView mUser;
    private EditText mPassword;
    private View mProgressView;
    private View mLogInFormView;
    private Button mLogIn;
    private CheckBox mSavePassword;
    private CheckBox mAutoLogIn;
    private TextView mForgotPassword;
    private TextView mNewUser;
    private TextView mToSetting;

    //异步线程
    private UserLogInTask mAuthTask = null;

    //用户信息
    private UserInfo mUserInfo = null;

    //是否连接到网络
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        setView();

        setInfo();

        isConnected = isOnline();

        setListenler();
    }

    //给用户一些提示
    private void hintUser(String msg) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //判断是否连接到网络
    public boolean isOnline() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    //加载信息
    private void setInfo() {

        //从Intent中得到用户信息
        mUserInfo = ((MyApplication) getApplication()).getUserInfo();

        //设置信息
        mUser.setText(mUserInfo.getmUser());
        if (!mUser.getText().toString().trim().equals("") && !mUserInfo.getisSavePassword()) {

            //移动聚焦到密码
            mPassword.requestFocus();
        }
        if (mUserInfo.getisSavePassword()) {
            mPassword.setText(mUserInfo.getmPassword());
            mSavePassword.setChecked(true);

            //可能不需要键盘输入了
            mForgotPassword.requestFocus();
        }
        if (mUserInfo.getisAutoLogIn()) {
            mAutoLogIn.setChecked(true);
        }
    }

    //控件
    private void setView() {

        mPassword = (EditText) findViewById(R.id.password);
        mUser = (AutoCompleteTextView) findViewById(R.id.user);
        mLogIn = (Button) findViewById(R.id.email_sign_in_button);
        mLogInFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mSavePassword = (CheckBox) findViewById(R.id.save_password);
        mAutoLogIn = (CheckBox) findViewById(R.id.auto_login);
        mForgotPassword = (TextView) findViewById(R.id.action_forgot_password);
        mNewUser = (TextView) findViewById(R.id.action_new_user);
        mToSetting = (TextView) findViewById(R.id.action_to_settings);
    }

    //监听
    private void setListenler() {

        /*
        //密码输入监听
        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        */

        //登陆密码单机监听
        mLogIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //如果没有网络连接的情况下  不给予登陆 给予提示
                if (isConnected) {

                    attemptLogin();
                } else {

                    hintUser("亲，请先链接网络哦。。。");
                }
            }
        });

        //自动登陆的监听事件，自动登陆点击的情况下  一定是 保存密码选择的
        mAutoLogIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    //关联保存密码
                    if (!mSavePassword.isChecked()) mSavePassword.setChecked(true);
                }
            }
        });

        //当没有保存密码的时候也不能自动登陆
        mSavePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //当没有别选中的时候
                if (!isChecked) {

                    if (mAutoLogIn.isChecked()) mAutoLogIn.setChecked(false);
                }
            }
        });

        //忘记密码的监听事件
        mForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //新用户的监听事件
        mNewUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mToSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginPage.this, SettingActivity.class);

                startActivity(intent);
            }
        });
    }

    //尝试登陆
    private void attemptLogin() {

        //防止残留线程登陆
        if (mAuthTask != null) {
            return;
        }

        //重置错误提示信息
        mUser.setError(null);
        mPassword.setError(null);

        //保存用户名和密码
        String user = mUser.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        //标记
        boolean cancel = false;
        View focusView = null;

        //检查用密码是否合法
        if (!TextUtils.isEmpty(password) && isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        //检查用户是否合法
        if (TextUtils.isEmpty(user)) {
            mUser.setError(getString(R.string.error_field_required));
            focusView = mUser;
            cancel = true;
        } else if (isUserValid(user)) {
            mUser.setError(getString(R.string.error_invalid_user));
            focusView = mUser;
            cancel = true;
        }

        //是否需要取消登录
        if (cancel) {
            focusView.requestFocus();
        } else {

            //检查无误可以尝试登陆了
            showProgress(true);
            mAuthTask = new UserLogInTask(MyApplication.getLoginUrl());
            mAuthTask.execute(setNewUserInfo());
        }
    }

    //包装一个新的UserInfo
    public UserInfo setNewUserInfo() {

        return new UserInfo(
                mUser.getText().toString().trim(),
                mPassword.getText().toString().trim(),
                mSavePassword.isChecked(),
                mAutoLogIn.isChecked()
        );
    }

    private boolean isUserValid(String user) {

        //用户是否合法
        return user.length() < 2;
    }

    //判断 密码 是否合法
    private boolean isPasswordValid(String password) {

        //密码至少6位
        return password.length() < 6;
    }

    //显示登陆进度条
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLogInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLogInFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLogInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLogInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //异步登陆
    public class UserLogInTask extends AsyncTask<UserInfo, Void, UserInfo> {

        //URL
        private String URL_Str;

        public UserLogInTask(String URL) {

            this.URL_Str = URL;
        }

        @Override
        protected UserInfo doInBackground(UserInfo... params) {

            //新的UserInfo
            UserInfo mNewUserInfo = params[0];

            //判断
            new LogInParser(mNewUserInfo, URL_Str).checkLogIn();

            //登陆成功？
            if (mNewUserInfo.operationValue != LogInParser.ERROR_VALUE_WRONG_PASSWORD
                    && mNewUserInfo.operationValue != LogInParser.ERROR_VALUE_NETWORK_INCOORRECT) {

                //保存登陆信息到  Preferences
                ((MyApplication) getApplication()).setUserInfo(mNewUserInfo);

            }

            return mNewUserInfo;
        }

        @Override
        protected void onPostExecute(final UserInfo mNewUserInfo) {
            mAuthTask = null;
            showProgress(false);

            if (mNewUserInfo.operationValue != LogInParser.ERROR_VALUE_WRONG_PASSWORD
                    && mNewUserInfo.operationValue != LogInParser.ERROR_VALUE_NETWORK_INCOORRECT) {

                //
                MyApplication.isLoginSucceed = true;
                MyApplication.mUser = mNewUserInfo;

                if (MainPage.mInstance != null) {

                    MainPage.mInstance.updateUserInfo();
                }

                //跳转
                Intent mIntent = new Intent(LoginPage.this, MainPage.class);

                //包装数据
                UserInfo.setData(mIntent, mNewUserInfo);

                startActivity(mIntent);

                //登陆成功
                finish();
            } else {

                //登陆失败
                if (mNewUserInfo.operationValue == LogInParser.ERROR_VALUE_WRONG_PASSWORD) {
                    mPassword.setError(getString(R.string.error_incorrect_password));

                }

                if (mNewUserInfo.operationValue == LogInParser.ERROR_VALUE_NETWORK_INCOORRECT) {

                    hintUser(getString(R.string.error_incorrect_network));
                }

                mPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

