package com.grapro.chatapplication.ui;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.grapro.chatapplication.R;
import com.grapro.chatapplication.bean.UserBean;
import com.grapro.chatapplication.content.Url;
import com.grapro.chatapplication.content.SpUsersdNames;
import com.grapro.chatapplication.util.SpUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class LoginAct extends AppCompatActivity {
    private static final String TAG = "LoginAct";
    private Button btnLogin;
    private Button btnRegist;
    private KeyguardManager mKeyguardMgr = null;
    private EditText nameEdt;
    private EditText pwdEdt;
    private RequestParams params;
    private static final String loginUrl = Url.baseUrl + "/login/select";
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mKeyguardMgr = getSystemService(KeyguardManager.class);

        nameEdt = findViewById(R.id.etUsername);
        pwdEdt = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegist = findViewById(R.id.btnRegist);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLogin(nameEdt.getText().toString(), pwdEdt.getText().toString());
            }
        });
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginAct.this, RegistAct.class));
            }
        });

        String userName = SpUtils.getString(this, SpUsersdNames.USER_NAME, "");
        if (!userName.isEmpty()) {
            nameEdt.setText(userName);
        }
        pwdEdt.setText("123");

    }

    private void toLogin(String userName, String pwd) {
        String url = loginUrl + "/" + userName + "/" + pwd;

        params = new RequestParams(url);
        // params.setSslSocketFactory(...); // 如果需要自定义SSL
//        params.addQueryStringParameter("wd", "xUtils");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, result);
                UserBean bean = gson.fromJson(result, UserBean.class);
                if (1 != bean.getCode()) {
                    Toast.makeText(x.app(), bean.getMsg(), Toast.LENGTH_LONG).show();
                    return;
                }
                SpUtils.putString(LoginAct.this, SpUsersdNames.USER_NAME, bean.getData().getUser_name());
                SpUtils.putString(LoginAct.this, SpUsersdNames.USER_ID, String.valueOf(bean.getData().getUser_id()));
                Toast.makeText(LoginAct.this, "登录成功,请先校验指纹信息", Toast.LENGTH_LONG).show();
                showScreenLockPwd();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LoginAct.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(LoginAct.this, "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data        锁屏密码校验回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1101) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "校验成功", Toast.LENGTH_LONG).show();
                toChatAct();
            } else {
                showScreenLockPwd();
            }
        }
    }

    private void toChatAct() {
        startActivity(new Intent(LoginAct.this, ChatListAct.class));
        finish();
    }

    /**
     * 跳转锁屏密码校验页面
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showScreenLockPwd() {
        Intent intent = mKeyguardMgr.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            startActivityForResult(intent, 1101);
        } else {
            Toast.makeText(LoginAct.this, "该机器不具备密码校验功能，直接进入", Toast.LENGTH_LONG).show();
            toChatAct();
        }
    }
}
