package com.grapro.chatapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grapro.chatapplication.bean.ChatListBean;
import com.grapro.chatapplication.bean.ChatMsgBean;

import java.util.ArrayList;
import java.util.List;


public class DBhelperUtil {
    public static final String DB_NAME = "ChatMsg";
    public static final String TB_ChatMsg = "ChatMsg";
    private MydatabaseHelper dbHelper;//数据库
    private SQLiteDatabase db;//数据库
    private static DBhelperUtil dBhelperUtil;

    private DBhelperUtil() {
    }

    public static DBhelperUtil getInstance() {
        if (dBhelperUtil == null) {
            dBhelperUtil = new DBhelperUtil();
        }
        return dBhelperUtil;
    }


    public SQLiteDatabase getDb(String name, Context context) {
        dbHelper = new MydatabaseHelper(context, name + ".db", null, 1);
        db = dbHelper.getWritableDatabase();
        return db;
    }

    public Cursor query(SQLiteDatabase db, String table) {
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        return cursor;
    }

    public ChatListBean getLastMsg(String friendId) {
        String[] selectionArgs = {friendId};
        Cursor cursor = db.query(TB_ChatMsg, null, "id=?", selectionArgs, null, null, "autoid desc", "1");
        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToLast();
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        String msg = cursor.getString(cursor.getColumnIndexOrThrow("msg"));
        int msgType = cursor.getInt(cursor.getColumnIndexOrThrow("msgType"));
        String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
        ChatListBean chatListBean = new ChatListBean(name, id, msg, msgType, time);
        cursor.close();
        return chatListBean;
    }

    public List<ChatMsgBean> getHistoryChatMsg(String friendId) {
        List<ChatMsgBean> chatMsgBeans = new ArrayList<>();
        String[] selectionArgs = {friendId};
        Cursor cursor = db.query(TB_ChatMsg, null, "id=?", selectionArgs, null, null, "autoid asc", null);
        if (cursor.getCount() <= 0) {
            return null;
        }
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String msg = cursor.getString(cursor.getColumnIndexOrThrow("msg"));
            int msgType = cursor.getInt(cursor.getColumnIndexOrThrow("msgType"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            String picPath = cursor.getString(cursor.getColumnIndexOrThrow("picPath"));
            int itemType = cursor.getInt(cursor.getColumnIndexOrThrow("itemType"));
            int msgStatus = cursor.getInt(cursor.getColumnIndexOrThrow("msgStatus"));
            int destoryd = cursor.getInt(cursor.getColumnIndexOrThrow("destoryd"));

            ChatMsgBean msgBean = new ChatMsgBean(name, id, msg, msgType, time, itemType, msgStatus, picPath, 1 == destoryd);
            chatMsgBeans.add(msgBean);

        }
        cursor.close();
        return chatMsgBeans;
    }
    public int updateFirImgDestroy(String friendId,String time,Context context){
        ContentValues values = new ContentValues();
        values.put("destoryd","1");
        String selection = "id=? and time=?";
        String[] selectionArgs = { friendId,time };
        int count = getDb(DB_NAME, context).update(
                TB_ChatMsg,
                values,
                selection,
                selectionArgs);
        return count;
    }
    public long putMsgToChatMsgTable(ChatMsgBean bean, Context context) {
        ContentValues values = new ContentValues();
        values.put("name", bean.getName());
        values.put("id", bean.getId());
        values.put("msg", bean.getMsg());
        values.put("base64Image", "");
        values.put("msgType", bean.getMsgType());
        values.put("time", bean.getTime());
        values.put("itemType", bean.getItemType());
        values.put("msgStatus", bean.getMsgStatus());
        values.put("picPath", bean.getPicPath());
        values.put("destoryd", bean.isDestoryd());
        long result = getDb(DB_NAME, context).insert(TB_ChatMsg, null, values);
        return result;
    }
}
