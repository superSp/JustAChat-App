package com.grapro.chatapplication.util;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.grapro.chatapplication.R;
import com.grapro.chatapplication.bean.MsgBean;
import com.grapro.chatapplication.content.MsgType;
import com.grapro.chatapplication.ui.ChatDetailAct;
import com.grapro.chatapplication.ui.ChatListAct;
import com.luck.picture.lib.config.PictureConfig;

import java.util.Iterator;
import java.util.List;

public class NotificationUtil {
    public static NotificationUtil notificationUtil;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    private Gson gson = new Gson();

    public static NotificationUtil getInstance() {
        if (notificationUtil == null) {
            notificationUtil = new NotificationUtil();
        }
        return notificationUtil;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        Iterator var3 = appProcesses.iterator();

        ActivityManager.RunningAppProcessInfo appProcess;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            appProcess = (ActivityManager.RunningAppProcessInfo) var3.next();
        } while (!appProcess.processName.equals(context.getPackageName()));

        if (appProcess.importance == 100) {
            return false;
        } else {
            return true;
        }
    }

    public void createNotificationForNormal(Context context, String msg) {
        if (!isBackground(context)) {
            return;
        }
        MsgBean msgBean = gson.fromJson(msg, MsgBean.class);

        int mNormalNotificationId = 1;

        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 适配8.0及以上 创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("mNormalChannelId", "mNormalChannelName", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("描述");
            channel.setShowBadge(false);
            mManager.createNotificationChannel(channel);
        }
        // 点击意图 // setDeleteIntent 移除意图
        Intent intent = new Intent(context, ChatDetailAct.class)
                .putExtra("name", msgBean.getFromUserName())
                .putExtra("id", msgBean.getFromUser());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        // 构建配置
        String txt = "";
        switch (msgBean.getMsgType()) {
            case MsgType.TEXT:
                txt = "[Receive the text message, click to view]";
                break;
            case MsgType.IMAGE:
                txt = "[Receive the picture message, click to view]";
                break;
            case MsgType.FIRE_IMAGE:
                txt = "[Receive the encrypted picture message, click to view]";
                break;
        }
        mBuilder = new NotificationCompat.Builder(context, "mNormalChannelId")
                .setContentTitle(msgBean.getFromUserName() + ":") // 标题
                .setContentText(txt) // 文本
                .setSmallIcon(R.mipmap.ic_launcher_round) // 小图标
//                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_avatar)) // 大图标
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 7.0 设置优先级
                .setContentIntent(pendingIntent) // 跳转配置
                .setAutoCancel(true); // 是否自动消失（点击）or mManager.cancel(mNormalNotificationId)、cancelAll、setTimeoutAfter()
        // 发起通知
        mManager.notify(mNormalNotificationId, mBuilder.build());
    }
}
