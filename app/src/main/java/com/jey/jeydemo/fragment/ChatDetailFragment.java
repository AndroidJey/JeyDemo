package com.jey.jeydemo.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jey.jlibs.fragment.PopFragment;
import com.jey.jlibs.utils.StringUtils;
import com.jey.jlibs.utils.ToastUtil;
import com.jey.jlibs.view.ClearableEditText;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.jey.jeydemo.R;
import com.jey.jeydemo.adapter.ChatAdapter;
import com.jey.jeydemo.entity.ChatModel;
import com.jey.jeydemo.entity.ItemModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatDetailFragment extends PopFragment implements View.OnClickListener {
    @BindView(R.id.etChatContent)
    public ClearableEditText etChatContent;
    @BindView(R.id.tvSend)
    public TextView tvSend;
    @BindView(R.id.rvChatDetail)
    public RecyclerView recyclerView;
    @BindView(R.id.chatSwipeRefresh)
    public SwipeRefreshLayout swipeRefreshLayout;

    private String username;

    private ChatAdapter adapter;
    private List<ItemModel> dataList;

    public ChatDetailFragment() {
        // Required empty public constructor
    }

    public static ChatDetailFragment newInstance(String username){
        ChatDetailFragment fragment = new ChatDetailFragment();
        if (!StringUtils.isBlank(username)){
            Bundle b = new Bundle();
            b.putString("username",username);
            fragment.setArguments(b);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            username = getArguments().getString("username");
        }
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        title.setText("与"+username+"聊天中");
        rightRL.setVisibility(View.GONE);
        lessRightRl.setVisibility(View.GONE);
    }

    @Override
    protected void setGoBackMethod() {
        super.setGoBackMethod();
        goBack();
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_chat_detail;
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        tvSend.setOnClickListener(this);
        initReceive();//初始化接收消息
        initListView();
        setRefreshLayoutListener();
    }

    /**
     * 初始化聊天记录
     */
    private void initListView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter = new ChatAdapter());

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        //获取此会话的所有消息
        List<EMMessage> messages = conversation.getAllMessages();
        if (messageModel==null&&messages!=null&&messages.size()>0){
            messageModel = messages.get(0);
        }
        Message message1 = handler.obtainMessage(4, messages);
        handler.sendMessage(message1);

        //SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
        //获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
//        List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvSend://发送
                sendMsg(etChatContent.getText().toString());
                break;
        }
    }

    /**
     * 发送消息
     * @param content
     */
    private void sendMsg(final String content){
        if (StringUtils.isBlank(content)){
            ToastUtil.show(getActivity(),"请输入要发送的内容");
            return;
        }
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, username);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Message message = handler.obtainMessage(1, content);
                handler.sendMessage(message);
            }

            @Override
            public void onError(int code, String error) {
                Message message = handler.obtainMessage(2, error);
                handler.sendMessage(message);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 注册消息接收的监听
     */
    private void initReceive(){
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    private EMMessage messageModel;

    private EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            if (messages!=null){
                for (EMMessage msg:messages){
                    messageModel = msg;
                    EMTextMessageBody body = (EMTextMessageBody) msg.getBody();
                    Message message1 = handler.obtainMessage(3, body.getMessage());
                    handler.sendMessage(message1);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: /** 发送消息成功  */
                    initSendData(msg.obj.toString());
                    break;
                case 2:/** 发送消息失败  */
                    ToastUtil.show(getActivity(),"发送失败:"+msg.obj.toString());
                    break;
                case 3:/** 接收单条消息 */
                    initReceiveData(msg.obj.toString());
                    break;
                case 4:
                    initHistoryChat((List<EMMessage>) msg.obj);
                    break;
            }
        }
    };

    /**
     * 将发送的消息显示到聊天界面
     * @param sendData
     */
    private void initSendData(String sendData){
        ChatModel model = new ChatModel();
        model.setIcon("http://img.my.csdn.net/uploads/201508/05/1438760758_6667.jpg");
        model.setContent(sendData);
        ArrayList<ItemModel> datalist = new ArrayList<>();
        datalist.add(new ItemModel(ItemModel.CHAT_B,model));
        adapter.addAll(datalist);

        //删除发送框里的文本
        etChatContent.setText("");
    }

    /**
     * 将接收到的消息显示到聊天界面
     * @param message
     */
    private void initReceiveData(String message) {
        ChatModel model = new ChatModel();
        model.setIcon("http://img.my.csdn.net/uploads/201508/05/1438760758_3497.jpg");
        model.setContent(message);
        ArrayList<ItemModel> datalist = new ArrayList<>();
        datalist.add(new ItemModel(ItemModel.CHAT_A,model));
        adapter.addAll(datalist);
    }

    private void initHistoryChat(List<EMMessage> messageList){
        ArrayList<ItemModel> datalist = new ArrayList<>();
        for (EMMessage msg : messageList) {
            EMTextMessageBody body = (EMTextMessageBody) msg.getBody();
            String contentText = body.getMessage();
            ChatModel model = new ChatModel();
            model.setContent(contentText);
            if (msg.direct() == EMMessage.Direct.RECEIVE){
                model.setIcon("http://img.my.csdn.net/uploads/201508/05/1438760758_3497.jpg");
                datalist.add(new ItemModel(ItemModel.CHAT_A,model));
            }else {
                model.setIcon("http://img.my.csdn.net/uploads/201508/05/1438760758_6667.jpg");
                datalist.add(new ItemModel(ItemModel.CHAT_B,model));
            }

        }
        adapter.replaceAll(datalist);
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        List<EMMessage> messages;
                        try {
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
                            messages = conversation.loadMoreMsgFromDB(messageModel.getMsgId(),
                                    20);
                        } catch (Exception e1) {
                            swipeRefreshLayout.setRefreshing(false);//停止刷新
                            return;
                        }
                        if (messages.size() > 0) {
                            initHistoryChat(messages);
                        } else {
                            ToastUtil.show(getActivity(),"没有更多了");
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void popOnPause(View view) {
        super.popOnPause(view);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);//移除消息监听
    }

}
