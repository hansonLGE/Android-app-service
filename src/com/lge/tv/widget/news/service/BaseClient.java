
package com.lge.tv.widget.news.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lge.tv.widget.news.service.util.ConnectionManager;

public abstract class BaseClient {
    protected String contentId;
    protected String title;
    protected String className;
    protected String api;
    protected int timer;
    protected Context context;
    protected ConnectionManager conManager;

    public abstract void refreshData(long timer);

    public abstract void gatheringData();

    public abstract void cancelGathering();

    public abstract void setNetworkListener();

    protected byte[] makeJpgIconBlob(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (bitmap != null)
        {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        return null;
    }

    protected byte[] makePngIconBlob(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (bitmap != null)
        {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }

        return null;
    }

    protected Bitmap getBitmapFromURL(String url) {
        Bitmap bitmap = null;
        InputStream is = null;

        if (url == null || "".equals(url)) {
            return null;
        }

        try {
            is = new URL(url).openConnection().getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    public void setContentId(String contendId) {
        this.contentId = contendId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setConnectionManager(ConnectionManager mgr) {
        this.conManager = mgr;
    }

    public String getApi() {
        return api;
    }

    public String getClassName() {
        return className;
    }

    public String getContentId() {
        return contentId;
    }

    public String getTitle() {
        return title;
    }
}
