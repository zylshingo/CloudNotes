package com.jikexueyuan.cloudnotes.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.jikexueyuan.cloudnotes.R;
import com.jikexueyuan.cloudnotes.adapter.FragmentsAdapter;
import com.jikexueyuan.cloudnotes.fragment.NoteListsFragment;
import com.jikexueyuan.cloudnotes.fragment.SettingFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;


public class NotesActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioButton rbtn_notes;
    private RadioButton rbtn_setting;
    public ViewPager mViewPager;
    private FragmentsAdapter adapter;
    private long pressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        init();
        addListener();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rbtn_notes:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rbtn_setting:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    private void init() {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(NotesActivity.this));
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId("b1a5d7b1f0ae32ec31b4796e6cbf08a6")
                .setConnectTimeout(30)
                .setUploadBlockSize(1024 * 1024)
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);

        rbtn_notes = (RadioButton) findViewById(R.id.rbtn_notes);
        rbtn_setting = (RadioButton) findViewById(R.id.rbtn_setting);
        rbtn_notes.setTextColor(0xffff4081);
        mViewPager = (ViewPager) findViewById(R.id.vp_notes);
        List<Fragment> lists = new ArrayList<>();
        lists.add(new NoteListsFragment());
        lists.add(new SettingFragment());
        adapter = new FragmentsAdapter(getSupportFragmentManager(), lists);
        mViewPager.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        if (pressTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.tuichuchengxu,Toast.LENGTH_SHORT).show();
        }
        pressTime = System.currentTimeMillis();
    }

    private void addListener() {
        rbtn_notes.setOnClickListener(this);
        rbtn_setting.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rbtn_notes.setTextColor(0xffff4081);
                        rbtn_setting.setTextColor(0xff000000);
                        break;
                    case 1:
                        rbtn_notes.setTextColor(0xff000000);
                        rbtn_setting.setTextColor(0xffff4081);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
