package com.jey.jeydemo.activity;

import android.os.Bundle;

import com.jey.jlibs.activity.BaseActivity;
import com.jey.jeydemo.R;
import com.jey.jeydemo.fragment.ChatMainFragment;

public class ChatMainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        NavFragment(ChatMainFragment.newInstance());
    }

}
