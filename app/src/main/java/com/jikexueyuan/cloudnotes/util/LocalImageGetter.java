package com.jikexueyuan.cloudnotes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import static android.R.attr.scaleWidth;


public class LocalImageGetter implements Html.ImageGetter {

    private Context c;
    private TextView container;
    private int width;

    public LocalImageGetter(Context c, TextView textView) {
        this.c = c;
        this.container = textView;
        this.width = c.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public Drawable getDrawable(String source) {
        LocDrawable locDrawable = new LocDrawable();
        if (source.toLowerCase().endsWith(".jpg") || source.toLowerCase().endsWith(".png") || source.toLowerCase().endsWith(".bmp")) {
            Bitmap image = BitmapFactory.decodeFile(source);
            if (image == null) {
                try {
                    image = BitmapFactory.decodeStream(c.getAssets().open("error.jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 计算缩放比例
            float scaleWidth = ((float) width) / image.getWidth();
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale((float) (scaleWidth * 0.9), (float) (scaleWidth * 0.9));
            image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
            locDrawable.bitmap = image;
            locDrawable.setBounds(0, 0, image.getWidth(), image.getHeight());
        } else {
            Bitmap image = null;
            try {
                image = BitmapFactory.decodeStream(c.getAssets().open("play.jpg"));
                float scaleWidth = width / image.getWidth();
                Matrix matrix = new Matrix();
                matrix.postScale((float) (scaleWidth * 0.5), (float) (scaleWidth * 0.5));
                image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
                locDrawable.bitmap = image;
                locDrawable.setBounds(0, 0, image.getWidth(), image.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        container.invalidate();
        container.setText(container.getText()); // 解决图文重叠

        return locDrawable;

    }

    @SuppressWarnings("deprecation")
    public class LocDrawable extends BitmapDrawable {
        private Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }

}
