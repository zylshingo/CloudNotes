package com.jikexueyuan.cloudnotes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class UrlImageGetter implements Html.ImageGetter {

    private Context c;
    private TextView container;
    private int width;

    /**
     * @param t
     * @param c
     */
    public UrlImageGetter(TextView t, Context c) {
        this.c = c;
        this.container = t;
        width = c.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public Drawable getDrawable(String source) {
        final UrlDrawable urlDrawable = new UrlDrawable();
        ImageLoader.getInstance().loadImage(source, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // 计算缩放比例
                float scaleWidth = ((float) width) / loadedImage.getWidth();
                // 取得想要缩放的matrix参数
                Log.d("img", "UrlImageGetter: " + width);

                Matrix matrix = new Matrix();
                matrix.postScale((float) (scaleWidth * 0.9), (float) (scaleWidth * 0.9));
                loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, false);
                urlDrawable.bitmap = loadedImage;
                Log.d("img", "UrlImageGetter: " + scaleWidth);
                urlDrawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());
                container.invalidate();
                container.setText(container.getText()); // 解决图文重叠
            }
        });
        return urlDrawable;
    }

    @SuppressWarnings("deprecation")
    private class UrlDrawable extends BitmapDrawable {
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
