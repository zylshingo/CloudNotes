package com.jikexueyuan.cloudnotes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jikexueyuan.cloudnotes.R;
import com.jikexueyuan.cloudnotes.provider.MyContentProvider;
import com.jikexueyuan.cloudnotes.util.LinkMovementMethodExt;
import com.jikexueyuan.cloudnotes.util.LocalImageGetter;
import com.jikexueyuan.cloudnotes.util.MessageSpan;
import com.jikexueyuan.cloudnotes.util.UserData;

import org.xml.sax.XMLReader;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;

public class ShowActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_show_title;
    private TextView tv_show_content;
    private Button btn_show_edit;
    private Button btn_show_delete;
    private String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_content);

        init();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
                break;
            case R.id.btn_show_delete:
                BmobUser user = BmobUser.getCurrentUser(ShowActivity.this);
                if (user != null) {
                    String str = getIntent().getExtras().getString("objectId");
                    if (str != null) {
                        UserData del = new UserData();
                        del.setObjectId(str);
                        del.delete(this, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ShowActivity.this, "云端删除完成", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(ShowActivity.this, "云端删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                getContentResolver().delete(MyContentProvider.uri, "title=?", new String[]{tv_show_title.getText().toString()});
                finish();
                break;
        }
    }

    private void init() {
        tv_show_title = (TextView) findViewById(R.id.tv_show_title);
        tv_show_content = (TextView) findViewById(R.id.tv_show_content);
        btn_show_edit = (Button) findViewById(R.id.btn_show_edit);
        btn_show_delete = (Button) findViewById(R.id.btn_show_delete);
        Bundle bundle = getIntent().getExtras();
        tv_show_title.setText(bundle.getString("title"));
        html = bundle.getString("post");
        btn_show_delete.setOnClickListener(this);
        btn_show_edit.setOnClickListener(this);

        //处理未知标签,通常是系统默认不能处理的标签
        final Html.TagHandler tagHandler = new Html.TagHandler() {
            int contentIndex = 0;

            /**
             * opening : 是否为开始标签
             * tag: 标签名称
             * output:输出信息，用来保存处理后的信息
             * xmlReader: 读取当前标签的信息，如属性等
             */
            public void handleTag(boolean opening, String tag, Editable output,
                                  XMLReader xmlReader) {
                if ("mytag".equals(tag)) {
                    if (opening) {//获取当前标签的内容开始位置
                        contentIndex = output.length();
                        try {
                            final String color = (String) xmlReader.getProperty("color");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        final int length = output.length();
                        String content = output.subSequence(contentIndex, length).toString();
                        SpannableString spanStr = new SpannableString(content);
                        spanStr.setSpan(new ForegroundColorSpan(Color.GREEN), 0, content.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        output.replace(contentIndex, length, spanStr);
                    }
                }
                System.out.println("opening:" + opening + ",tag:" + tag + ",output:" + output);
            }
        };

        Spanned spanned = Html.fromHtml(html, new LocalImageGetter(this, tv_show_content), tagHandler);
        tv_show_content.setText(spanned);
        Log.d("img", "onCreate: " + spanned);
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (what == 200) {
                    MessageSpan ms = (MessageSpan) msg.obj;
                    Object[] spans = (Object[]) ms.getObj();

                    for (Object span : spans) {
                        if (span instanceof ImageSpan) {
                            String file = ((ImageSpan) span).getSource();
                            if (file.toLowerCase().endsWith(".jpg") || file.toLowerCase().endsWith(".png") || file.toLowerCase().endsWith(".bmp")) {
                                Uri uri = Uri.parse(file);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "image/*");
                                startActivity(intent);
                            } else {
                                Uri uri = Uri.parse(file);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, "video/*");
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        };
        tv_show_content.setMovementMethod(LinkMovementMethodExt.getInstance(handler, ImageSpan.class));
    }
}




