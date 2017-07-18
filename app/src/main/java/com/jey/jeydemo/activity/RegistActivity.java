package com.jey.jeydemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jey.jlibs.utils.StringUtils;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jey.jeydemo.R;

public class RegistActivity extends Activity implements View.OnClickListener{
    private TextView tvRegist;
    private EditText etRegistUserName;
    private EditText etRegistPassWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        tvRegist = (TextView) findViewById(R.id.tvRegist);
        etRegistUserName = (EditText) findViewById(R.id.etRegistUserName);
        etRegistPassWord = (EditText) findViewById(R.id.etRegistPassWord);

        tvRegist.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvRegist://注册
                registUser(etRegistUserName.getText().toString(),etRegistPassWord.getText().toString());
                break;
        }
    }

    private void registUser(final String username, final String password){
        if (StringUtils.isBlank(username)|| StringUtils.isBlank(password)){
            Toast.makeText(getApplicationContext(), "用户名和密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, password);

                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode=e.getErrorCode();
                            if(errorCode== EMError.NETWORK_ERROR){
                                Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                Toast.makeText(getApplicationContext(), "用户已存在", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
                                Toast.makeText(getApplicationContext(), "没有相关权限", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                Toast.makeText(getApplicationContext(), "不合法的用户名",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
