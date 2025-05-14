package com.grapro.chatapplication;

// 616391073@qq.com
import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.grapro.chatapplication.content.Url;
import com.grapro.chatapplication.util.JWebSocketClient;
import com.grapro.chatapplication.util.NotificationUtil;

import org.java_websocket.handshake.ServerHandshake;
import org.xutils.x;

import java.net.URI;

public class MyApplication extends Application {
    private Handler mHandler = new Handler();
    private static final String TAG = "MyApplication";
    public JWebSocketClient client;
    private String userid;
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private static final String WebSocketUrl = Url.baseWsUrl + "/websocket/";

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        closeConnect();
    }

    /**
     * 初始化websocket
     */
    public void initWebSocket(String userId) {
        this.userid = userId;
        Log.e(TAG, "websocket的地址是：" + WebSocketUrl);
        URI uri = URI.create(WebSocketUrl + userId);
        //TODO 创建websocket
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                super.onMessage(message);
                NotificationUtil.getInstance().createNotificationForNormal(getBaseContext(),message);

                client.onWebSocketNotice.onMsg(message);
                if (!message.equals("Heartbeat")) {
                    Log.e(TAG, "websocket收到消息：" + message);
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                client.onWebSocketNotice.onOpen();
                Log.e(TAG, "websocket连接成功");

            }

            @Override
            public void onError(Exception ex) {
                super.onError(ex);
                Log.e(TAG, "websocket连接错误：" + ex);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                super.onClose(code, reason, remote);
                if (code != 1000) {
//                    reconnectWs();//意外断开马上重连
                }
                Log.e(TAG, "websocket断开连接：·code:" + code + "·reason:" + reason + "·remote:" + remote);
            }
        };
        //TODO 设置超时时间
        client.setConnectionLostTimeout(110 * 1000);
        //TODO 连接websocket
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        if (null != client) {
            Log.e("", "^_^Websocket发送的消息：-----------------------------------^_^" + msg);
            if (client.isOpen()) {
                client.send(msg);
            }

        }
    }

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "开启重连");
                    client.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            //关闭websocket
            if (null != client) {
                client.close();
            }
            //停止心跳
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    Log.e("心跳包检测websocket连接状态1", client.isOpen() + "/" + WebSocketUrl);
//                    reconnectWs();//心跳机制发现断开开启重连
                } else {
                    Log.e("心跳包检测websocket连接状态2", client.isOpen() + "/" + WebSocketUrl);
                    sendMsg("Heartbeat");
                }
            } else {
                Log.e("心跳包检测websocket连接状态重新连接", "");
                //如果client已为空，重新初始化连接
                client = null;
                initWebSocket(userid);
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };
}
