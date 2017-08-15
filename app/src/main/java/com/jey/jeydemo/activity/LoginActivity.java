package com.jey.jeydemo.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jey.jeydemo.utils.CommonFunctionUtils;
import com.jey.jlibs.utils.StringUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jey.jeydemo.ApiConfig;
import com.jey.jeydemo.R;

public class LoginActivity extends Activity implements View.OnClickListener {
    private TextView tvLogin;
    private TextView tvRegist;
    private EditText etLoginUserName;
    private EditText etLoginPassWord;

    private int screenHeight = 0;//屏幕高度
    private int keyHeight = 0; //软件盘弹起后所占高度
    private float scale = 0f; //logo缩放比例
    private View content;
    ScrollView scrollView;
    ImageView logo;
    private CommonFunctionUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvRegist = (TextView) findViewById(R.id.tvRegist);
        etLoginUserName = (EditText) findViewById(R.id.etLoginUserName);
        etLoginPassWord = (EditText) findViewById(R.id.etLoginPassWord);
        utils = new CommonFunctionUtils(getApplicationContext());

        logo = (ImageView) findViewById(R.id.logo);
        content = findViewById(R.id.content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        keyHeight = screenHeight * 2 / 5;//弹起高度为屏幕高度的1/3
        test();

        tvLogin.setOnClickListener(this);
        tvRegist.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvLogin://登录
                login(etLoginUserName.getText().toString(),etLoginPassWord.getText().toString());
                break;
            case R.id.tvRegist://注册
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login(String username, String password){
        if (StringUtils.isBlank(username)|| StringUtils.isBlank(password)){
            Toast.makeText(getApplicationContext(), "用户名和密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        utils.showWaitDialog("正在登录...",true,null);
//        EMClient.getInstance().login(username,password,new EMCallBack() {//回调
//            @Override
//            public void onSuccess() {
//                utils.dismissDialog();
//                EMClient.getInstance().groupManager().loadAllGroups();
//                EMClient.getInstance().chatManager().loadAllConversations();
//                Message message = handler.obtainMessage(1, "");
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                utils.dismissDialog();
//                Log.e("msgg",message);
//                Message message1 = handler.obtainMessage(2, message);
//                handler.sendMessage(message1);
//            }
//        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: /** 登录成功 */
                    ApiConfig.userName = etLoginUserName.getText().toString();
                    ApiConfig.passWord = etLoginPassWord.getText().toString();
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ChatMainActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "登录失败:"+msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void test() {
        /**
         * 禁止键盘弹起的时候可以滚动
         */
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        scrollView.addOnLayoutChangeListener(new ViewGroup.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
              /* old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值
              现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起*/
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    int dist = content.getBottom() - bottom;
                    if (dist > 0) {
                        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(content, "translationY", 0.0f, -dist);
                        mAnimatorTranslateY.setDuration(300);
                        mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                        mAnimatorTranslateY.start();
                        zoomIn(logo, dist);
                    }

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    if ((content.getBottom() - oldBottom) > 0) {
                        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(content, "translationY", content.getTranslationY(), 0);
                        mAnimatorTranslateY.setDuration(300);
                        mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                        mAnimatorTranslateY.start();
                        //键盘收回后，logo恢复原来大小，位置同样回到初始位置
                        zoomOut(logo);
                    }
                }
            }
        });
    }

    /**
     * 缩小
     *
     * @param view
     */
    public void zoomIn(final View view, float dist) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }

    /**
     * f放大
     *
     * @param view
     */
    public void zoomOut(final View view) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }
}
