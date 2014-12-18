
package com.lge.tv.widget.news;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class NewsDataProvider extends ContentObservable {
    public static final String TAG = "NewsDataProvider";
    public static final String NEWS_CONTENT_URI = "content://com.lge.tv.widget.news.service.provider/widget_data_news";
    private static final int MSG_RELOAD_NEWSDATA_ITEMS = 101;
    private static final long RELOAD_ITEMS_DELAY_MS = 1000;

    private final ContentResolver mContentresolver;
    private final Context mContext;
    private final ArrayList<NewsDataInfo> mNewsDataInfos;
    private final HandlerThread mhandlerThread;
    private final Handler mHandler;
    private final Object mLock;
    private static int mNewsIndex = -1;
    private ContentObserver mNewsContentObserver;

    public NewsDataProvider(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mContentresolver = context.getContentResolver();
        mNewsDataInfos = new ArrayList<NewsDataInfo>();
        mLock = new Object();

        getAllNewsData();

        mhandlerThread = new HandlerThread("news_data_provider");
        mhandlerThread.start();
        mHandler = new Handler(mhandlerThread.getLooper()) {

            public void hanleMessage(Message msg) {
                if (msg.what == MSG_RELOAD_NEWSDATA_ITEMS) {
                    Log.i(TAG, "handleMessage");
                    getAllNewsData();

                    notifyChange(false);
                }
            }

        };

        registerObserver();
    }

    public void destroy() {
        Log.i(TAG, "destroy");
        unregisterObserver();

        synchronized (mLock) {
            mNewsDataInfos.clear();
        }
    }

    private void getAllNewsData() {
        synchronized (mLock) {
            Log.i(TAG, "getAllNewsData");
            mNewsDataInfos.clear();
            mNewsDataInfos.addAll(queryNewsDataInfoList());
            Log.i(TAG, Integer.toString(mNewsDataInfos.size()));
        }
    }

    public ArrayList<NewsDataInfo> refreshNewsData() {

        ArrayList<NewsDataInfo> newsDatas = new ArrayList<NewsDataInfo>();
        synchronized (mLock) {
            if (mNewsDataInfos.size() > 0) {

                mNewsIndex++;
                Log.i(TAG, Integer.toString(mNewsIndex));
                if (mNewsIndex >= mNewsDataInfos.size())
                {
                    mNewsIndex = 0;
                }
                newsDatas.add(mNewsDataInfos.get(mNewsIndex));

                mNewsIndex++;
                Log.i(TAG, Integer.toString(mNewsIndex));
                if (mNewsIndex >= mNewsDataInfos.size())
                {
                    mNewsIndex = 0;
                }
                newsDatas.add(mNewsDataInfos.get(mNewsIndex));
            }
            else
            {
                getAllNewsData();
            }
        }

        for (int i = 0; i < newsDatas.size(); i++)
        {
            Log.i(TAG, newsDatas.get(i).mTitle);
            Log.i(TAG, newsDatas.get(i).mLink);
        }

        return newsDatas;
    }

    private ArrayList<NewsDataInfo> queryNewsDataInfoList() {
        Log.i(TAG, "queryNewsDataInfoList");
        ArrayList<NewsDataInfo> newsDatas = new ArrayList<NewsDataInfo>();
        Cursor c = null;

        try {
            c = mContentresolver.query(Uri.parse(NEWS_CONTENT_URI), null, "CONTENT_ID=?",
                    new String[] {
                        "news"
                    }, null);
            while (c != null && c.moveToNext()) {
                newsDatas.add(new NewsDataInfo(c.getString(2), c.getString(3), c.getBlob(4)));
                Log.i(TAG, c.getString(2));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return newsDatas;
    }

    private void queueReloadNewsDataInfoItems() {
        mHandler.removeMessages(MSG_RELOAD_NEWSDATA_ITEMS);
        mHandler.sendEmptyMessageAtTime(MSG_RELOAD_NEWSDATA_ITEMS, RELOAD_ITEMS_DELAY_MS);
    }

    private void registerObserver() {
        if (mNewsContentObserver == null) {
            mNewsContentObserver = new ContentObserver(mHandler) {

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    queueReloadNewsDataInfoItems();
                }
            };

            mContentresolver.registerContentObserver(Uri.parse(NEWS_CONTENT_URI), true,
                    mNewsContentObserver);
        }
    }

    private void unregisterObserver() {
        if (mNewsContentObserver != null) {
            mContentresolver.unregisterContentObserver(mNewsContentObserver);
            mNewsContentObserver = null;
        }

        mhandlerThread.quit();
    }
}
