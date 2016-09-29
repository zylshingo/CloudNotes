package com.jikexueyuan.cloudnotes.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jikexueyuan.cloudnotes.R;
import com.jikexueyuan.cloudnotes.activity.EditActivity;
import com.jikexueyuan.cloudnotes.activity.LoginActivity;
import com.jikexueyuan.cloudnotes.activity.NotesActivity;
import com.jikexueyuan.cloudnotes.activity.ShowActivity;
import com.jikexueyuan.cloudnotes.provider.MyContentProvider;
import com.jikexueyuan.cloudnotes.util.Item;
import com.jikexueyuan.cloudnotes.util.UserData;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class NoteListsFragment extends Fragment implements View.OnClickListener {

    private NotesActivity mActivity;
    private Boolean isMulChoice = false; //是否多选
    private ListView listView;
    private List<Item> array = new ArrayList<>();
    private List<Item> selectId = new ArrayList<>();
    private RelativeLayout layout;
    private TextView txtCount;
    private MyListAdapter adapter;
    private FloatingActionButton fab;
    private BmobUser currentUser;

    public NoteListsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.content_notes, null);
        listView = (ListView) view.findViewById(R.id.lv_notes);
        txtCount = (TextView) view.findViewById(R.id.txtcount);
        Button btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
        Button btn_delete = (Button) view.findViewById(R.id.btn_delete);
        layout = (RelativeLayout) view.findViewById(R.id.relative);
        mActivity = (NotesActivity) getActivity();
        dataChange();
        adapter = new MyListAdapter(mActivity, txtCount);
        listView.setAdapter(adapter);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, EditActivity.class);
//                startActivityForResult(intent, 1);
                startActivity(intent);
            }
        });
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.yunbiji);
        mActivity.setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_my_calendar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = BmobUser.getCurrentUser(mActivity);
                if (currentUser != null) {
                    Toast.makeText(mActivity, R.string.yijidenglu, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        setHasOptionsMenu(true);
        toolbar.inflateMenu(R.menu.menu_notes);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        if (currentUser != null) {
                            BmobUser.logOut(mActivity);
                            currentUser = null;
                            Toast.makeText(mActivity, R.string.yijidengchu, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mActivity, R.string.weidenglu, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.menu_sync:
                        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                        if (activeNetworkInfo != null) {
                            currentUser = BmobUser.getCurrentUser(mActivity);
                            if (currentUser != null) {
                                upData();
                                dataQuery();
                            } else {
                                Toast.makeText(mActivity, R.string.qingxiandenglu, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mActivity, LoginActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(mActivity, R.string.meiyouwangluo, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return false;
            }
        });
        btn_delete.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataChange();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_notes, menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                fab.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                selectId.clear();
                adapter.isCheck.clear();
                isMulChoice = false;
                adapter.txtCount.setText("");
                break;
            case R.id.btn_delete:
                ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    if (currentUser != null) {
                        for (Item d : selectId) {
                            UserData data = new UserData();
                            data.setObjectId(d.getObjectId());
                            data.delete(mActivity, new DeleteListener() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                }
                            });
                        }
                    }
                    for (Item d : selectId) {
                        mActivity.getContentResolver().delete(MyContentProvider.uri, "title=?", new String[]{d.getTitle()});
                    }
                    fab.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                    dataChange();
                    adapter = new MyListAdapter(mActivity, txtCount);
                    adapter.txtCount.setText("");
                    listView.setAdapter(adapter);
                    isMulChoice = false;

                } else {
                    for (Item d : selectId) {
                        mActivity.getContentResolver().delete(MyContentProvider.uri, "title=?", new String[]{d.getTitle()});
                    }
                    fab.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                    dataChange();
                    adapter = new MyListAdapter(mActivity, txtCount);
                    adapter.txtCount.setText("");
                    listView.setAdapter(adapter);
                    isMulChoice = false;
                }
                selectId.clear();
                Toast.makeText(mActivity, R.string.shanchushuju, Toast.LENGTH_SHORT).show();

                break;
        }
    }

    /**
     * 自定义Adapter
     */

    private class MyListAdapter extends BaseAdapter {

        private Context mActivity;
        private SparseIntArray visibleCheck = new SparseIntArray();
        private SparseBooleanArray isCheck = new SparseBooleanArray();
        private TextView txtCount;


        MyListAdapter(Context context, TextView txtCount) {
            this.mActivity = context;
            this.txtCount = txtCount;
        }

        private class ViewHolder {
            TextView title;
            TextView time;
            CheckBox checkBox;
        }

        @Override
        public int getCount() {
            return array == null ? 0 : array.size();

        }

        @Override
        public Item getItem(int position) {
            return array.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.listview_cell, null);
                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.tv_list_title);
                holder.time = (TextView) view.findViewById(R.id.tv_list_time);
                holder.checkBox = (CheckBox) view.findViewById(R.id.cb_list_select);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Item item = getItem(position);
            holder.title.setText(item.getTitle());
            holder.time.setText(item.getTime());

            if (isMulChoice) {
                for (int i = 0; i < array.size(); ++i) {
                    visibleCheck.put(i, View.VISIBLE);
                }
            } else {
                for (int i = 0; i < array.size(); ++i) {
                    visibleCheck.put(i, View.INVISIBLE);
                }
            }
            int visible = visibleCheck.get(position);
            if (visible == View.VISIBLE) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }
            holder.checkBox.setChecked(isCheck.get(position, false));


            //添加长按事件
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                    if (activeNetworkInfo != null) {
                        Toast.makeText(mActivity, R.string.dangqianwangluozhengc, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, R.string.dangqianwuwangluo, Toast.LENGTH_SHORT).show();
                    }
                    isMulChoice = true;
                    selectId.clear();
                    isCheck.clear();
                    layout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < array.size(); ++i) {
                        adapter.visibleCheck.put(i, View.VISIBLE);
                    }
                    adapter = new MyListAdapter(mActivity, txtCount);
                    listView.setAdapter(adapter);
                    fab.setVisibility(View.INVISIBLE);
                    return true;
                }
            });
            //添加点击事件
            final ViewHolder finalHolder = holder;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMulChoice) {
                        if (finalHolder.checkBox.isChecked()) {
                            isCheck.put(position, false);
                            finalHolder.checkBox.setChecked(false);
                            selectId.remove(array.get(position));
                        } else {
                            isCheck.put(position, true);
                            finalHolder.checkBox.setChecked(true);
                            selectId.add(array.get(position));
                        }
                        txtCount.setText("共选择了" + selectId.size() + "项");
                    } else {
                        Intent showData = new Intent(mActivity, ShowActivity.class);
                        Bundle bundle = new Bundle();
                        String title = getItem(position).getTitle();
                        String post = getItem(position).getPost();
                        bundle.putString("title", title);
                        bundle.putString("post", post);
                        bundle.putInt("_id", getItem(position).get_id());
                        bundle.putString("objectId",getItem(position).getObjectId());
                        showData.putExtras(bundle);
                        startActivity(showData);
                    }
                }
            });
            return view;
        }


    }

    /**
     * 从本地上传更新到服务器
     */

    private void upData() {
        if (!array.isEmpty()) {
            for (final Item i : array) {
                if (i.getObjectId() != null) {
                    UserData data = new UserData();
                    data.setUserId(currentUser.getObjectId());
                    data.setPost(i.getPost());
                    data.setTime(i.getTime());
                    data.setTitle(i.getTitle());
                    data.update(mActivity, i.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                } else {
                    final UserData data = new UserData();
                    data.setUserId(currentUser.getObjectId());
                    data.setPost(i.getPost());
                    data.setTime(i.getTime());
                    data.setTitle(i.getTitle());
                    data.save(mActivity, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            ContentValues v = new ContentValues();
                            v.put("objectId", data.getObjectId());
                            mActivity.getContentResolver().update(MyContentProvider.uri, v, "_id=?", new String[]{i.get_id() + ""});
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }
        }
    }

    /**
     * 从服务器查询数据到本地
     */
    private void dataQuery() {
        BmobQuery<UserData> download = new BmobQuery<>();
        download.addWhereEqualTo("userId", currentUser.getObjectId());
        download.setLimit(100);
        download.order("updatedAt");
        download.findObjects(mActivity, new FindListener<UserData>() {
            @Override
            public void onSuccess(List<UserData> list) {
                if (!list.isEmpty()) {
                    for (UserData data : list) {
                        int mark = 1;
                        for (Item item : array) {
                            if (item.getTitle().equals(data.getTitle())) {
                                mark = 2;
                                break;
                            }
                        }
                        if (mark == 1) {
                            ContentValues values = new ContentValues();
                            values.put("title", data.getTitle());
                            values.put("post", data.getPost());
                            values.put("time", data.getTime());
                            values.put("objectId", data.getObjectId());
                            mActivity.getContentResolver().insert(MyContentProvider.uri, values);
                            Cursor query = mActivity.getContentResolver().query(MyContentProvider.uri, null, "title=?", new String[]{data.getTitle() + ""}, null);
                            int _id = 0;
                            if (query != null) {
                                query.moveToFirst();
                                _id = query.getInt(query.getColumnIndex("_id"));
                                query.close();
                            }
                            array.add(new Item(data.getTitle(), data.getTime(), data.getPost(), _id, data.getObjectId()));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(mActivity, R.string.yuntongbuwancheng, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void dataChange() {
        Cursor cursor = mActivity.getContentResolver().query(MyContentProvider.uri, null, null, null, null);
        if (cursor != null) {
            array.clear();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); ++i) {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String post = cursor.getString(cursor.getColumnIndex("post"));
                String objectId = cursor.getString(cursor.getColumnIndex("objectId"));
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                array.add(new Item(title, time, post, id, objectId));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}

