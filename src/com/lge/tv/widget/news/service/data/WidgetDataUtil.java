
package com.lge.tv.widget.news.service.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.lge.tv.widget.news.service.BaseClient;
import com.lge.tv.widget.news.service.provider.ProviderSettings.WidgetDataNewsColumns;

public class WidgetDataUtil {
    private static final String TAG = "WidgetDataUtil";

    public static void setNewsDataList(Context context, List<NewsDataInfo> list, BaseClient client) {
        removeNewsDataList(context, client);

        if (list != null && list.size() > 0) {
            addNewsDataList(context, list, client);
        }
    }

    public static List<NewsDataInfo> getNewsDataList(Context context, BaseClient client) {
        Log.i(TAG, "getNewsDataList");
        List<NewsDataInfo> retList = new ArrayList<NewsDataInfo>();
        Cursor c = null;

        try {
            String selection = WidgetDataNewsColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
                    WidgetDataNewsColumns.WIDGET_DATA_NEWS_CONTENT_URI,
                    WidgetDataNewsColumns.WIDGET_DATA_NEWS_PROJECTION, selection, selectionArgs,
                    null);
            while (c != null && c.moveToNext()) {
                NewsDataInfo data = new NewsDataInfo();
                data.setNewsTitle(c.getString(c.getColumnIndex(WidgetDataNewsColumns.TITLE)));
                retList.add(data);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return retList;
    }

    public static boolean isExistNewsData(Context context, BaseClient client) {
        boolean result = false;
        Cursor c = null;

        try {
            String selection = WidgetDataNewsColumns.CONTENT_ID + "=?";
            String[] selectionArgs = {
                client.getContentId()
            };

            c = context.getContentResolver().query(
                    WidgetDataNewsColumns.WIDGET_DATA_NEWS_CONTENT_URI, new String[] {
                        "count(content_id) AS contentid_num"
                    }, selection, selectionArgs, null);
            while (c != null && c.moveToNext()) {
                result = c.getInt(0) > 0 ? true : false;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    public static void addNewsDataList(Context context, List<NewsDataInfo> addList,
            BaseClient client) {
        Log.i(TAG, "addNewsDataList");
        int arraySize = addList.size();
        ContentValues[] valueArray = new ContentValues[arraySize];

        for (int i = 0; i < arraySize; i++) {
            ContentValues values = new ContentValues();
            values.put(WidgetDataNewsColumns.CONTENT_ID, client.getContentId());
            values.put(WidgetDataNewsColumns.TITLE, addList.get(i).getNewsTitle());
            values.put(WidgetDataNewsColumns.LINK_URL, addList.get(i).getLink());
            values.put(WidgetDataNewsColumns.THUMB, addList.get(i).getThumbBlob());
            values.put(WidgetDataNewsColumns.THUMB_URL, addList.get(i).getThumbUrl());

            valueArray[i] = values;
            Log.i(TAG, client.getContentId());
            Log.i(TAG, addList.get(i).getNewsTitle());
            Log.i(TAG, addList.get(i).getLink());
            Log.i(TAG, addList.get(i).getThumbUrl());
        }

        context.getContentResolver().bulkInsert(WidgetDataNewsColumns.WIDGET_DATA_NEWS_CONTENT_URI,
                valueArray);
    }

    public static void removeNewsDataList(Context context, BaseClient client) {
        String selection = WidgetDataNewsColumns.CONTENT_ID + "=?";
        String[] selectionArgs = {
            client.getContentId()
        };
        context.getContentResolver().delete(WidgetDataNewsColumns.WIDGET_DATA_NEWS_CONTENT_URI,
                selection, selectionArgs);
    }
    /*
     * public static void deleteDatabase(Context context) {
     * WidgetDataProvider.deleteDatabase(context); }
     */
}
