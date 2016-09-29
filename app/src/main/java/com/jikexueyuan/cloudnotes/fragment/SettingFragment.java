package com.jikexueyuan.cloudnotes.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jikexueyuan.cloudnotes.activity.LoginActivity;
import com.jikexueyuan.cloudnotes.activity.NotesActivity;
import com.jikexueyuan.cloudnotes.R;

import cn.bmob.v3.BmobUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private NotesActivity mActivity;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mActivity = (NotesActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_set);
        toolbar.setTitle(R.string.shezhi);
        mActivity.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_my_calendar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser currentUser = BmobUser.getCurrentUser(mActivity);
                if (currentUser != null) {
                    Toast.makeText(mActivity, R.string.yijidenglu, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

}
