package com.jikexueyuan.cloudnotes.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jikexueyuan.cloudnotes.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class RegActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_reg_user;
    private EditText et_reg_passOne;
    private EditText et_reg_passTwo;
    private Button btn_reg;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_page);

        //初始化界面
        init();

    }

    private void init() {
        et_reg_user = (EditText) findViewById(R.id.et_reg_user);
        et_reg_passOne = (EditText) findViewById(R.id.et_reg_passwordone);
        et_reg_passTwo = (EditText) findViewById(R.id.et_reg_passwordtwo);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        btn_back = (Button) findViewById(R.id.btn_back);

        btn_reg.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //注册按钮的点击事件
            case R.id.btn_reg:
                if (TextUtils.isEmpty(et_reg_user.getText().toString().trim())) {
                    Toast.makeText(this, R.string.zhanghaobuneng,Toast.LENGTH_SHORT).show();
                    et_reg_user.startAnimation(animei());
                    return;
                }
                if (TextUtils.isEmpty(et_reg_passOne.getText().toString().trim())||
                        TextUtils.isEmpty(et_reg_passTwo.getText().toString().trim())) {
                    Toast.makeText(this, R.string.mimabuneng,Toast.LENGTH_SHORT).show();
                    et_reg_passOne.startAnimation(animei());
                    et_reg_passTwo.startAnimation(animei());
                    return;
                }
                if (!et_reg_passOne.getText().toString().trim().equals(et_reg_passTwo.getText().toString().trim())) {
                    Toast.makeText(this, R.string.liangcimima,Toast.LENGTH_SHORT).show();
                    et_reg_passOne.startAnimation(animei());
                    et_reg_passTwo.startAnimation(animei());
                    return;
                }

                BmobUser user = new BmobUser();
                user.setUsername(et_reg_user.getText().toString().trim());
                user.setPassword(et_reg_passOne.getText().toString().trim());
                user.signUp(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(RegActivity.this, R.string.zhucechenggong,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(RegActivity.this, R.string.zhuceshibai,Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            //返回按钮的点击事件
            case R.id.btn_back:
                et_reg_user.setText("");
                et_reg_passOne.setText("");
                et_reg_passTwo.setText("");
                finish();
                break;
        }
    }

    private Animation animei() {
        Animation animation = new TranslateAnimation(0,10,0,10);
        animation.setInterpolator(new CycleInterpolator(5));
        animation.setDuration(1000);
        return animation;
    }
}
