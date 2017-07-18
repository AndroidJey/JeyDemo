package com.jey.jeydemo.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.fragment.PageFragment;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.jey.jeydemo.R;
import com.jey.jeydemo.adapter.ChatHistoryAdapter;
import com.jey.jeydemo.entity.UnReadMsgModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatMainFragment extends PageFragment {
    @BindView(R.id.lvChatHistory)
    public ListView listView;

    private List<UnReadMsgModel> dataModel;

    public ChatMainFragment() {
        // Required empty public constructor
    }

    public static ChatMainFragment newInstance(){
        ChatMainFragment fragment = new ChatMainFragment();
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_chat_main;
    }

    @Override
    protected void initViews() {
        getChatHistory();
    }

    private void getChatHistory() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        if (dataModel==null){
            dataModel = new ArrayList<>();
        }else {
            dataModel.clear();
        }
        for (Map.Entry<String, EMConversation> kv : conversations.entrySet()){
            UnReadMsgModel model = new UnReadMsgModel();
            model.setUserName(kv.getKey());
            EMConversation value = kv.getValue();
            EMMessage message = value.getLastMessage();
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            String msg = body.getMessage();
            model.setMessage(msg);
            dataModel.add(model);
        }
        int size = dataModel.size();
        if (size!=0){
            initListView();
        }
    }

    private void initListView(){
        listView.setAdapter(new ChatHistoryAdapter(getActivity(),dataModel));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BroadcastCenter.publish(BroadcastCenter.TITLE.NAVIGATETOPAGE,
                        ChatDetailFragment.newInstance(dataModel.get(i).getUserName()));
            }
        });
    }

    @OnClick({R.id.tvLogOut})
    public void onclick(View view){
        switch (view.getId()){
            case R.id.tvLogOut:
                logOut();
                break;
        }
    }

    /**
     * 退出登录
     */
    private void logOut(){
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Message message = handler.obtainMessage(1, "");
                handler.sendMessage(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Message message1 = handler.obtainMessage(2, message);
                handler.sendMessage(message1);
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: /** 成功  */
                    Toast.makeText(getActivity(), "成功退出", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "退出失败:"+msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getChatHistory();
    }
}
