package com.grapro.chatapplication.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.grapro.chatapplication.R;
import com.grapro.chatapplication.bean.UserBean;
import com.grapro.chatapplication.content.SpUsersdNames;
import com.grapro.chatapplication.content.Url;
import com.grapro.chatapplication.util.SpUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class RegistAct extends AppCompatActivity {
    private Button btnRegist;
    private EditText nameEdt;
    private EditText pwdEdt;
    private RequestParams params;
    private static final String registUrl = Url.baseUrl + "/regist/instert";
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        nameEdt = findViewById(R.id.etUsername);
        pwdEdt = findViewById(R.id.etPassword);
        btnRegist = findViewById(R.id.btnRegist);
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegist(nameEdt.getText().toString(), pwdEdt.getText().toString());
            }
        });
    }

    private void toRegist(String name, String pwd) {
        String url = registUrl + "/" + name + "/" + pwd;

        params = new RequestParams(url);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                UserBean bean = gson.fromJson(result, UserBean.class);
                if (1 != bean.getCode()) {
                    Toast.makeText(x.app(), bean.getMsg(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(RegistAct.this, "Successful registration！", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(RegistAct.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(RegistAct.this, "cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }
}
