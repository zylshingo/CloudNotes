package com.jikexueyuan.cloudnotes.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jikexueyuan.cloudnotes.R;

import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_login_user;
    private EditText et_login_password;
    private Button btn_login;
    private Button btn_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        et_login_user = (EditText) findViewById(R.id.et_login_user);
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_reg = (Button) findViewById(R.id.btn_reg);

        btn_login.setOnClickListener(this);
        btn_reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String name = et_login_user.getText().toString().trim();
                String password = et_login_password.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    et_login_user.startAnimation(animei());
                    Toast.makeText(this, R.string.zhanghaobuneng, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    et_login_password.startAnimation(animei());
                    Toast.makeText(this, R.string.mimabuneng, Toast.LENGTH_SHORT).show();
                    return;
                }
                BmobUser user = new BmobUser();
                user.setUsername(name);
                user.setPassword(password);
                user.login(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(LoginActivity.this, R.string.dengluchenggong, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(LoginActivity.this, R.string.denglushibai, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_reg:
                Intent reg = new Intent(this, RegActivity.class);
                startActivity(reg);
                break;
        }
    }

    private Animation animei() {
        Animation animation = new TranslateAnimation(0, 10, 0, 10);
        animation.setInterpolator(new CycleInterpolator(5));
        animation.setDuration(1000);
        return animation;
    }
}
