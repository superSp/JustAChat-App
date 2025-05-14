package com.grapro.chatapplication.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.grapro.chatapplication.MyApplication;
import com.grapro.chatapplication.R;
import com.grapro.chatapplication.bean.BaseBean;
import com.grapro.chatapplication.bean.ChatListBean;
import com.grapro.chatapplication.bean.ChatMsgBean;
import com.grapro.chatapplication.bean.FriendBean;
import com.grapro.chatapplication.bean.MsgBean;
import com.grapro.chatapplication.content.ItemTypes;
import com.grapro.chatapplication.content.MsgStatus;
import com.grapro.chatapplication.content.MsgType;
import com.grapro.chatapplication.content.Url;
import com.grapro.chatapplication.content.SpUsersdNames;
import com.grapro.chatapplication.db.DBhelperUtil;
import com.grapro.chatapplication.util.AESCrypt;
import com.grapro.chatapplication.util.ImageUtil;
import com.grapro.chatapplication.util.JWebSocketClient;
import com.grapro.chatapplication.util.SpUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ChatListAct extends AppCompatActivity implements JWebSocketClient.onWebSocketNotice {
    private TextView tvTitle;
    private RecyclerView rv;
    private BaseQuickAdapter<ChatListBean, BaseViewHolder> adapter;
    private List<ChatListBean> datas = new ArrayList<>();
    private static final String friendUrl = Url.baseUrl + "/friend/select";
    private static final String addFriendUrl = Url.baseUrl + "/friend/insert";
    private RequestParams params;
    private static final String TAG = "ChatListAct";
    private Gson gson = new Gson();
    public JWebSocketClient client;
    private TextView btnAddFriend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);
        initViews();
        initAdapter();
        initWebSocket();
    }

    private void initWebSocket() {
        ((MyApplication) getApplication()).initWebSocket(SpUtils.getString(this, SpUsersdNames.USER_ID, ""));
        client = ((MyApplication) getApplication()).client;
        client.setOnWebSocketNotice(this);
    }

    private void initAdapter() {
        adapter = new BaseQuickAdapter<ChatListBean, BaseViewHolder>(R.layout.item_chatlist, datas) {
            @Override
            protected void convert(BaseViewHolder helper, final ChatListBean item) {
                helper.setText(R.id.name, item.getName());
                helper.setText(R.id.msg, item.getMsg());
                helper.setText(R.id.time, item.getTime());
            }
        };
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                startActivity(new Intent(ChatListAct.this, ChatDetailAct.class)
                        .putExtra("name", datas.get(position).getName())
                        .putExtra("id", datas.get(position).getFriendId())
                );
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }


    private void initViews() {
        tvTitle = findViewById(R.id.title);
        btnAddFriend = findViewById(R.id.add_friend);
        rv = findViewById(R.id.rv);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
        tvTitle.setText("Connecting to the server...");
    }

    private void showInputDialog() {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(ChatListAct.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(ChatListAct.this);
        inputDialog.setTitle("请输入好友名称").setView(editText);
        AlertDialog dialog=inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().isEmpty()) {
                            Toast.makeText(ChatListAct.this, "用户名不能为空,", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (SpUtils.getString(ChatListAct.this, SpUsersdNames.USER_NAME, "").equals(editText.getText().toString())) {
                            Toast.makeText(ChatListAct.this, "不能添加自己为好友,", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        addFriend(dialog,editText.getText().toString());
                    }
                }).show();
    }

    private void addFriend(DialogInterface inputDialog, String friendName) {
        inputDialog.dismiss();
        String url = addFriendUrl + "/" + SpUtils.getString(this, SpUsersdNames.USER_NAME, "")+"/"+friendName;
        params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, result);
                BaseBean bean = gson.fromJson(result, BaseBean.class);
                if (1 != bean.getCode()) {
                    Toast.makeText(x.app(), bean.getMsg(), Toast.LENGTH_LONG).show();
                    return;
                }
                serviceOnOpend();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(ChatListAct.this, "cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onOpen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceOnOpend();
            }
        });
    }

    private void serviceOnOpend() {
        tvTitle.setText(SpUtils.getString(this, SpUsersdNames.USER_NAME, ""));
        Toast.makeText(ChatListAct.this, "Server connection successful", Toast.LENGTH_SHORT).show();
        String url = friendUrl + "/" + SpUtils.getString(this, SpUsersdNames.USER_ID, "");
        params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, result);
                adapter.getData().clear();
                FriendBean bean = gson.fromJson(result, FriendBean.class);
                if (1 != bean.getCode()) {
                    Toast.makeText(x.app(), bean.getMsg(), Toast.LENGTH_LONG).show();
                    return;
                }
                List<FriendBean.Data> beans = bean.getData();
                if (0 == beans.size()) {
                    Toast.makeText(x.app(), "You have no friends yet", Toast.LENGTH_LONG).show();
                    return;
                }
                for (int i = 0; i < beans.size(); i++) {
                    FriendBean.Data data = beans.get(i);
                    ChatListBean listBean = new ChatListBean(data.getUser_name(), String.valueOf(data.getUser_id()), "", -1, "");
                    adapter.getData().add(listBean);
//                    adapter.notifyItemInserted(i);
                }
                adapter.notifyDataSetChanged();
                toShowTheLastMsg();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(ChatListAct.this, "cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyApplication) getApplication()).client.setOnWebSocketNotice(this);
        toShowTheLastMsg();
    }

    private void toShowTheLastMsg() {
        //每次页面重新展示，每次收到新数据，页面初始新来获取到好友列表后都要去刷新最新消息
        //todo 查看好友列表根据好友id去查找最新的一条数据
        for (int i = 0; i < adapter.getData().size(); i++) {
            ChatListBean chatListBean = DBhelperUtil.getInstance().getLastMsg(adapter.getData().get(i).getFriendId());
            if (null == chatListBean) {
                adapter.getData().get(i).setMsg("no msg");
                adapter.notifyItemChanged(i);
                return;
            }
            adapter.getData().get(i).setMsgType(chatListBean.getMsgType());
            adapter.getData().get(i).setItemType(chatListBean.getItemType());
            adapter.getData().get(i).setTime(chatListBean.getTime());
            switch (chatListBean.getMsgType()) {
                case MsgType.TEXT:
                    adapter.getData().get(i).setMsg(chatListBean.getMsg());
                    break;
                case MsgType.IMAGE:
                    adapter.getData().get(i).setMsg("[Picture]");
                    break;
                case MsgType.FIRE_IMAGE:
                    adapter.getData().get(i).setMsg("[Encrypted Picture]");
                    break;
            }
            adapter.notifyItemChanged(i);

        }


    }

    @Override
    public void onMsg(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onReceiveMsg(msg);
                toShowTheLastMsg();
                //todo 将数据从数据库里面取最新一条数据 插入新的数据到数据库
            }
        });
    }

    private void onReceiveMsg(String msg) {
//todo 存进去数据库
        String saveFilePath = "";
        MsgBean msgBean = gson.fromJson(msg, MsgBean.class);
        //解密文本
        try {
            msgBean.setTxt(AESCrypt.decrypt(AESCrypt.AESPassword, msgBean.getTxt()));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        switch (msgBean.getMsgType()) {
            case MsgType.IMAGE:
            case MsgType.FIRE_IMAGE:
                saveFilePath = getFilesDir() + "/" + msgBean.getFromUser() + System.currentTimeMillis() + ".jpg";
                ImageUtil.base64ToFile(msgBean.getBase64Image(), saveFilePath);
                break;
        }
        ChatMsgBean bean = new ChatMsgBean(msgBean.getFromUserName(), msgBean.getFromUser(), msgBean.getTxt(),
                msgBean.getMsgType(), msgBean.getTime(), ItemTypes.RECEIVE, MsgStatus.Successed, saveFilePath, false);
        //更新数据库
        DBhelperUtil.getInstance().putMsgToChatMsgTable(bean, this);
        toShowTheLastMsg();
    }
}
