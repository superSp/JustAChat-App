package com.grapro.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.grapro.chatapplication.db.DBhelperUtil;
import com.grapro.chatapplication.ui.LoginAct;
// 616391073@qq.com
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建数据库
        DBhelperUtil.getInstance().getDb(DBhelperUtil.DB_NAME, this);
        startActivity(new Intent(this, LoginAct.class));
        finish();
//
    }
}