
package com.lge.tv.widget.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NewsDataInfo {
    public String mTitle;
    public String mLink;
    public Bitmap mThumbNail;

    public NewsDataInfo(String mTitle, String mLink, byte[] mThumbNail) {
        // TODO Auto-generated constructor stub
        this.mTitle = mTitle;
        this.mLink = mLink;
        this.mThumbNail = getBitmap(mThumbNail);
    }

    public static Bitmap getBitmap(byte[] data) {
        Bitmap bitmap = null;

        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        return bitmap;
    }
}
