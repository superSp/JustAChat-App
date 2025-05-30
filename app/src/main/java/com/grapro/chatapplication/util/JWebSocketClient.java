package com.grapro.chatapplication.util;


import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import javax.net.ssl.SSLParameters;

public class JWebSocketClient extends WebSocketClient {
    public onWebSocketNotice onWebSocketNotice;

    public void setOnWebSocketNotice(JWebSocketClient.onWebSocketNotice onWebSocketNotice) {
        this.onWebSocketNotice = onWebSocketNotice;
    }

    public interface onWebSocketNotice{
        void onOpen();
        void onMsg(String msg);
    }
    @Override
    protected void onSetSSLParameters(SSLParameters sslParameters) {
        super.onSetSSLParameters(sslParameters);
    }

    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.e("JWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("JWebSocketClient", "onClose()" + reason + "-****-" + remote);
    }

    @Override
    public void onError(Exception ex) {
        Log.e("JWebSocketClient", "onError()" + ex);
    }


}
