package com.jikexueyuan.cloudnotes.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jikexueyuan.cloudnotes.R;
import com.jikexueyuan.cloudnotes.provider.MyContentProvider;
import com.jikexueyuan.cloudnotes.util.FileUtil;
import com.jikexueyuan.cloudnotes.util.HtmlTagHandler;
import com.jikexueyuan.cloudnotes.util.LocalImageGetter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.a.a.b.a.i.iv;
import static com.jikexueyuan.cloudnotes.R.id.btn_login;
import static com.jikexueyuan.cloudnotes.R.id.et;
import static okhttp3.internal.http.HttpDate.format;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int IMAGE_CODE = 0x123;
    public static final int VIDEO_CODE = 0x124;
    private EditText et_edit_title;
    private EditText et_edit_content;
    private Bitmap bitmap;
    private boolean isEdit = false;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_content);

        init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_CODE:
                    //获取图片资源
                    if (data != null) {
                        Uri image = data.getData();
                        try {
                            bitmap = BitmapFactory.decodeStream(resolver.openInputStream(image));
                            bitmap = bitmapProcessing(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                            insertIntoEditText(getBitmapMime(bitmap, new FileUtil().getPath(this, image)));
                        }
                    }
                    break;
                case VIDEO_CODE:
                    //获取录像资源
                    Uri video = data.getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getAssets().open("play.jpg"));
                        bitmap = bitmapProcessing(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        insertIntoEditText(getBitmapMime(bitmap, new FileUtil().getPath(this, video)));
                    }
            }
        }
    }

    private SpannableString getBitmapMime(Bitmap pic, String filePath) {
        String path = "<img src='" + filePath + "'/>";
        SpannableString ss = new SpannableString(path);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void insertIntoEditText(SpannableString ss) {
        Editable et = et_edit_content.getText();// 先获取Edittext中的内容
        int start = et_edit_content.getSelectionStart();
        et.insert(start, ss); // 设置ss要添加的位置
        et.insert(start + ss.length(), "\n");
        et.insert(start, "\n");
        et_edit_content.setText(et); // 把et添加到Edittext中
        et_edit_content.setSelection(et.length());// 设置Edittext中光标在最后面显示
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit_edit:
                if (!isEdit) {
                    String title = et_edit_title.getText().toString().trim();
                    String content = et_edit_content.getText().toString().trim();
                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                        Toast.makeText(this, R.string.biaotiheneirong, Toast.LENGTH_SHORT).show();
                    } else {
                        Cursor query = getContentResolver().query(MyContentProvider.uri, new String[]{"title"}, null, null, null);
                        if (query != null) {
                            query.moveToFirst();
                            for (int i = 0; i < query.getCount(); ++i) {
                                String existTitle = query.getString(query.getColumnIndex("title"));
                                if (title.equals(existTitle)) {
                                    Toast.makeText(this, R.string.biaotiyjchunzai, Toast.LENGTH_SHORT).show();
                                    query.close();
                                    return;
                                }
                            }
                            query.close();
                        }
                        ContentValues values = new ContentValues();
                        values.put("title", title);
                        values.put("post", content);
                        String s = SimpleDateFormat.getInstance().format(new Date(System.currentTimeMillis()));
                        values.put("time", s);
                        getContentResolver().insert(MyContentProvider.uri, values);
                        Toast.makeText(this, R.string.shujucunrubendi, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    String title = et_edit_title.getText().toString().trim();
                    String content = et_edit_content.getText().toString().trim();
                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                        Toast.makeText(this, R.string.biaotiheneirong, Toast.LENGTH_SHORT).show();
                    } else {
                        ContentValues values = new ContentValues();
                        values.put("title", title);
                        values.put("post", content);
                        String s = SimpleDateFormat.getInstance().format(new Date(System.currentTimeMillis()));
                        values.put("time", s);
                        getContentResolver().update(MyContentProvider.uri, values, "_id=?", new String[]{getIntent().getExtras().getInt("_id") + ""});
                        Toast.makeText(this, R.string.shujucunrubendi, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                }
        }
    }

    /**
     * 将获得的图片进行处理
     * @param b
     * @return
     */
    private Bitmap bitmapProcessing(Bitmap b) {
        float scaleWidth = ((float) width) / b.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale((float) (scaleWidth * 0.4), (float) (scaleWidth * 0.4));
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
    }

    /**
     * 界面初始化
     */
    private void init() {
        width = getResources().getDisplayMetrics().widthPixels;
        et_edit_title = (EditText) findViewById(R.id.et_edit_title);
        et_edit_content = (EditText) findViewById(R.id.et_edit_content);
        ImageView iv_edit_image = (ImageView) findViewById(R.id.iv_edit_image);
        ImageView iv_edit_camera = (ImageView) findViewById(R.id.iv_edit_camera);
        Button btn_edit_edit = (Button) findViewById(R.id.btn_edit_edit);

        if (getIntent().getExtras() != null) {
            isEdit = true;
            List<String> image = new ArrayList<>();
            SparseIntArray start = new SparseIntArray();
            SparseArray<SpannableString> sss = new SparseArray<>();
            Bundle bundle = getIntent().getExtras();
            et_edit_title.setText(bundle.getString("title"));
            String replace = bundle.getString("post");

            Pattern pattern = Pattern.compile("<img src='.*'/>");
            Matcher matcher = null;
            if (replace != null) {
                matcher = pattern.matcher(replace);
            }
            int count = 0;
            if (matcher != null) {
                while (matcher.find()) {
                    image.add(matcher.group());
                    start.put(count, matcher.start());
                    count++;
                }
            }
            for (int i = 0; i < image.size(); ++i) {
                if (replace != null) {
                    replace = replace.replace(image.get(i), "");
                }
                Bitmap editImage = null;
                String[] split = image.get(i).split("'");
                String path = "<img src='" + split[1] + "'/>";
                SpannableString ss = new SpannableString(path);
                if (split[1].toLowerCase().endsWith(".jpg") || split[1].toLowerCase().endsWith(".png") || split[1].toLowerCase().endsWith(".bmp")) {
                    editImage = BitmapFactory.decodeFile(split[1]);
                    editImage = bitmapProcessing(editImage);
                } else {
                    try {
                        editImage = BitmapFactory.decodeStream(this.getAssets().open("play.jpg"));
                        editImage = bitmapProcessing(editImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ImageSpan span = new ImageSpan(this, editImage);
                ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sss.put(i, ss);
            }
            Editable et = Editable.Factory.getInstance().newEditable(replace);
            for (int i = 0; i < start.size(); ++i) {
                et.insert(start.get(i), sss.get(i));
            }
            et_edit_content.setText(et);
            et_edit_content.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (btn_edit_edit != null) {
            btn_edit_edit.setOnClickListener(this);
        }
        if (iv_edit_image != null) {
            iv_edit_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    getImage.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(getImage, IMAGE_CODE);
                }
            });
        }
        if (iv_edit_camera != null) {
            iv_edit_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent getVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(getVideo, VIDEO_CODE);
                }
            });
        }
    }
}