package com.grapro.chatapplication.ui;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.gson.Gson;
import com.grapro.chatapplication.MyApplication;
import com.grapro.chatapplication.R;
import com.grapro.chatapplication.adapter.ProviderMultiAdapter;
import com.grapro.chatapplication.adapter.ReceiveItemTypeProvider;
import com.grapro.chatapplication.bean.ChatMsgBean;
import com.grapro.chatapplication.bean.MsgBean;
import com.grapro.chatapplication.content.ItemTypes;
import com.grapro.chatapplication.content.MsgStatus;
import com.grapro.chatapplication.content.MsgType;
import com.grapro.chatapplication.content.SpUsersdNames;
import com.grapro.chatapplication.db.DBhelperUtil;
import com.grapro.chatapplication.util.AESCrypt;
import com.grapro.chatapplication.util.GlideEngine;
import com.grapro.chatapplication.util.ImageUtil;
import com.grapro.chatapplication.util.JWebSocketClient;
import com.grapro.chatapplication.util.SpUtils;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.io.File;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;


public class ChatDetailAct extends AppCompatActivity implements JWebSocketClient.onWebSocketNotice {
    private TextView tvTitle;
    private RecyclerView rv;
    private ProviderMultiAdapter adapter;
    private EditText sendEdt;
    private ImageView choosePicView;
    private ImageView chooseFirePicView;
    private ImageView imgLarge;
    private TextView txtLarge;
    private CountDownTimer countDownTimer;
    private static final String TAG = "ChatDetailAct";
    public JWebSocketClient client;
    private String friendId;
    private String myId;
    private String myName;
    private String friendName;
    private MsgBean msgBean = new MsgBean();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initViews();
        initAdapter();
        initTimer();
        initWebSocket();
        initHistoryMsg();
    }

    private void initHistoryMsg() {
        List<ChatMsgBean> history = DBhelperUtil.getInstance().getHistoryChatMsg(friendId);
        if (null == history) {
            return;
        }
        adapter.getData().addAll(history);
        scrollToEnd();
    }


    private void initTimer() {
        countDownTimer = new CountDownTimer(5500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, millisUntilFinished + "倒计时");
                txtLarge.setText((int) (millisUntilFinished / 1000) + "");
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "结束倒计时");
                imgLarge.setVisibility(View.GONE);
                txtLarge.setVisibility(View.GONE);
            }
        };

    }

    private void initAdapter() {
        adapter = new ProviderMultiAdapter(myName, new ReceiveItemTypeProvider.onFirerImageTouchListener() {
            @Override
            public void onTouchDown(String picPath) {
                imgLarge.setVisibility(View.VISIBLE);
                txtLarge.setVisibility(View.VISIBLE);
                Glide.with(ChatDetailAct.this).load(picPath).into(imgLarge);
                countDownTimer.start();
            }

            @Override
            public void onTouchUp(String picPath) {
                countDownTimer.cancel();
                countDownTimer.onFinish();
            }
        });
//        addFalseData();
        adapter.addChildClickViewIds(R.id.chat_item_content_image);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ChatMsgBean data = (ChatMsgBean) adapter.getData().get(position);
                switch ((data).getMsgType()) {
                    case MsgType.IMAGE:
                    case MsgType.FIRE_IMAGE:
                        if (data.isDestoryd()) {
                            return;
                        }
                        imgLarge.setVisibility(View.VISIBLE);
                        Glide.with(ChatDetailAct.this).load((data).getPicPath()).into(imgLarge);
                        break;
                }
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }


    private void initViews() {
        friendName = getIntent().getStringExtra("name");
        friendId = getIntent().getStringExtra("id");
        myId = SpUtils.getString(this, SpUsersdNames.USER_ID, "");
        myName = SpUtils.getString(this, SpUsersdNames.USER_NAME, "");
        tvTitle = findViewById(R.id.title);
        choosePicView = findViewById(R.id.pic);
        chooseFirePicView = findViewById(R.id.pic_fire);
        imgLarge = findViewById(R.id.img_large);
        txtLarge = findViewById(R.id.down_time_large);
        tvTitle.setText(friendName);
        rv = findViewById(R.id.rv);
        sendEdt = findViewById(R.id.send);
        //设置“发送"的按钮
        sendEdt.setImeOptions(EditorInfo.IME_ACTION_SEND);
        sendEdt.setInputType(TYPE_TEXT_FLAG_MULTI_LINE);
        sendEdt.setSingleLine(false);
        sendEdt.setMaxLines(4);
        sendEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        sendMsg(MsgType.TEXT, sendEdt.getText().toString(), "", "");
                }
                return false;
            }
        });
        choosePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic(false);
            }

        });
        chooseFirePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic(true);
            }
        });
        imgLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgLarge.setVisibility(View.GONE);
                txtLarge.setVisibility(View.GONE);
            }
        });
    }

    private String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private void choosePic(boolean isFire) {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectionMode(SelectModeConfig.SINGLE)
                .setCompressEngine(new CompressFileEngine() {
                    @Override
                    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
                        Luban.with(context).load(source).ignoreBy(10).setTargetDir(getFilesDir().getAbsolutePath())
                                .setCompressListener(new OnNewCompressListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onSuccess(String source, File compressFile) {
                                        if (call != null) {
                                            call.onCallback(source, compressFile.getAbsolutePath());
                                        }
                                    }

                                    @Override
                                    public void onError(String source, Throwable e) {
                                        if (call != null) {
                                            call.onCallback(source, null);
                                        }
                                    }
                                }).launch();
                    }
                }).forResult(new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(ArrayList<LocalMedia> result) {
//                String base64str = ImageUtil.imgToBase64(result.get(0).getPath());
                //获取压缩后的图片
                String base64str = ImageUtil.imgToBase64(result.get(0).getCompressPath());
                double imgSize = ImageUtil.base64ImgSize(base64str);
                if (0 == imgSize || imgSize >= 300) {
                    Toast.makeText(ChatDetailAct.this, "The picture is too large, please reselect", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMsg(isFire ? MsgType.FIRE_IMAGE : MsgType.IMAGE, "", result.get(0).getCompressPath(),
                        base64str);
            }

            @Override
            public void onCancel() {

            }
        });
    }


    private void scrollToEnd() {
        rv.scrollToPosition(adapter.getData().size() - 1);
    }

    private void sendMsg(int msgType, String txt, String localImgPath, String base64Img) {
        msgBean = new MsgBean();
        switch (msgType) {
            case MsgType.TEXT:
                //给发送文字加密
                msgBean.setTxt(replaceAllBlank(txt));
                break;
            case MsgType.IMAGE:
            case MsgType.FIRE_IMAGE:
                msgBean.setBase64Image(base64Img);
                msgBean.setLocalImgPath(localImgPath);
                break;
        }
        msgBean.setMsgType(msgType);
        msgBean.setFromUser(myId);
        msgBean.setFromUserName(myName);
        msgBean.setToUser(friendId);
        msgBean.setTime(getCurrentTime());
        ChatMsgBean bean = new ChatMsgBean(friendName, friendId, msgBean.getTxt(),
                msgBean.getMsgType(), msgBean.getTime(), ItemTypes.SEND, MsgStatus.Successed, msgBean.getLocalImgPath(), false);
        adapter.addData(bean);
        DBhelperUtil.getInstance().putMsgToChatMsgTable(bean, this);
        try {
            //对发送文本加密
            msgBean.setTxt(AESCrypt.encrypt(AESCrypt.AESPassword, replaceAllBlank(txt)));
            client.send(gson.toJson(msgBean));
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        scrollToEnd();
        sendEdt.setText("");
    }

    private void initWebSocket() {
        client = ((MyApplication) getApplication()).client;
        client.setOnWebSocketNotice(this);
    }

    @Override
    public void onOpen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void onMsg(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onReceiveMsg(msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyApplication) getApplication()).client.setOnWebSocketNotice(this);
        //todo 从数据库取最近数据
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
        if (friendId.equals(msgBean.getFromUser())) {
            //如果收到的消息是当前聊天好友发的，那么更新当前列表
            adapter.addData(bean);
        }
        //更新数据库
        DBhelperUtil.getInstance().putMsgToChatMsgTable(bean, this);
        scrollToEnd();
    }

    //去除所有空格
    public static String replaceAllBlank(String str) {
        String s = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            /*\n 回车(\u000a)
            \t 水平制表符(\u0009)
            \s 空格(\u0008)
            \r 换行(\u000d)*/
            Matcher m = p.matcher(str);
            s = m.replaceAll("");
        }
        return s;
    }
}
