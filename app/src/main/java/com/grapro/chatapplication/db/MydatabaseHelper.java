package com.grapro.chatapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//sqlite相关
public class MydatabaseHelper extends SQLiteOpenHelper {
    private static final String create_chatmsg_sql = "create table " + DBhelperUtil.TB_ChatMsg + "(" + "autoid INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name text," +
            "id text," +
            "msg text," +
            "base64Image text," +
            "msgType INTEGER," +
            "time text," +
            "itemType INTEGER," +
            "msgStatus INTEGER," +
            "picPath text," +
            "destoryd INTEGER)";


    public MydatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_chatmsg_sql);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
