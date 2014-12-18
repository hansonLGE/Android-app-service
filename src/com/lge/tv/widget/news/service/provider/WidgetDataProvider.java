
package com.lge.tv.widget.news.service.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.lge.tv.widget.news.service.provider.ProviderSettings.WidgetDataNewsColumns;

public class WidgetDataProvider extends ContentProvider {
    private static final String TAG = "WidgetDataProvider";
    private static final String DATABASE_NAME = "NewsService.db";

    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.lge.tv.widget.news.service.provider";
    public static final String TABLE_WIDGET_DATA_NEWS = "widget_data_news";

    public static final int URI_MATCH_TABLE_WIDGET_DATA_NEWS = 1;
    public static final int URI_MATCH_TABLE_WIDGET_DATA_NEWS_ID = 2;

    private DatabaseHelper mDatabaseHelper;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    {
        URI_MATCHER.addURI(AUTHORITY, TABLE_WIDGET_DATA_NEWS, URI_MATCH_TABLE_WIDGET_DATA_NEWS);
        URI_MATCHER.addURI(AUTHORITY, TABLE_WIDGET_DATA_NEWS + "/#",
                URI_MATCH_TABLE_WIDGET_DATA_NEWS_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;

        try {
            int match = URI_MATCHER.match(uri);
            if (match != UriMatcher.NO_MATCH) {
                String tableName = getTableName(match);
                if (tableName == null) {
                    return 0;
                } else {
                    db = mDatabaseHelper.getWritableDatabase();
                    db.beginTransaction();
                    int count = db.delete(tableName, selection, selectionArgs);
                    db.setTransactionSuccessful();

                    getContext().getContentResolver().notifyChange(uri, null);
                    return count;
                }
            }

        } catch (Exception e) {
            ;
        } finally {
            try {
                if (db != null) {
                    db.endTransaction();
                }
            } catch (Exception e) {
                ;
            }
        }

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        int match = URI_MATCHER.match(uri);

        switch (match)
        {
            case URI_MATCH_TABLE_WIDGET_DATA_NEWS:
                return "vnd.android.cursor.dir/" + TABLE_WIDGET_DATA_NEWS;
            case URI_MATCH_TABLE_WIDGET_DATA_NEWS_ID:
                return "vnd.android.cursor.item/" + TABLE_WIDGET_DATA_NEWS;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        // TODO Auto-generated method stub
        int result = 0;
        SQLiteDatabase db = null;

        int match = URI_MATCHER.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            String tableName = getTableName(match);

            if (tableName != null) {
                try {
                    db = mDatabaseHelper.getWritableDatabase();
                    db.beginTransaction();

                    for (int i = 0; i < values.length; i++) {
                        db.insert(tableName, null, values[i]);
                    }
                    db.setTransactionSuccessful();

                    result = values.length;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (db != null) {
                            db.endTransaction();
                        }
                        getContext().getContentResolver().notifyChange(uri, null);
                    } catch (Exception e) {
                        ;
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        Log.i(TAG, "onCreate");
        mDatabaseHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = null;

        int match = URI_MATCHER.match(uri);
        if (match != UriMatcher.NO_MATCH) {
            String tableName = getTableName(match);
            if (tableName == null) {
                return null;
            } else {
                db = mDatabaseHelper.getReadableDatabase();
                return db.query(tableName, projection, selection, selectionArgs, null, null,
                        sortOrder);
            }
        }

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private String getTableName(int match) {
        switch (match) {
            case URI_MATCH_TABLE_WIDGET_DATA_NEWS:
            case URI_MATCH_TABLE_WIDGET_DATA_NEWS_ID:
                return TABLE_WIDGET_DATA_NEWS;
            default:
                return null;
        }
    }

    /*
     * public static synchronized boolean deleteDatabase(Context context) { try
     * { if(mDatabaseHelper != null) { mDatabaseHelper.close(); mDatabaseHelper
     * = null; } } catch (Exception e) { ; } return
     * context.deleteDatabase(DATABASE_NAME); }
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_WIDGET_DATA_NEWS + " ("
                    + WidgetDataNewsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + WidgetDataNewsColumns.CONTENT_ID + " TEXT,"
                    + WidgetDataNewsColumns.TITLE + " TEXT,"
                    + WidgetDataNewsColumns.LINK_URL + " TEXT,"
                    + WidgetDataNewsColumns.THUMB + " BLOB,"
                    + WidgetDataNewsColumns.THUMB_URL + " TEXT"
                    + ")");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_WIDGET_DATA_NEWS);
            onCreate(db);
        }
    }

}
