
package com.lge.tv.widget.news;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class News extends AppWidgetProvider {
    private static final String TAG = "News";

    private final String ACTION_UPDATE_NEWS = "com.lge.tv.widget.news.UPDATE_NEWS";
    private final String ACTION_UPDATE_CLICK = "com.lge.tv.widget.news.UPDATE_CLICK";
    private final String ACTION_UPDATE_UP_CLICK = "com.lge.tv.widget.news.UPDATE_UP_CLICK";
    private final String ACTION_UPDATE_DOWN_CLICK = "com.lge.tv.widget.news.UPDATE_DOWN_CLICK";

    private static final int NewsDelayTime = 15000;
    private static final int RefreshDataCount = 2;
    private static NewsDataProvider mNewsDataProvider;
    private static String link_up = null;
    private static String link_down = null;

    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled");
        startService(context);
        setAlarm(context);

        mNewsDataProvider = new NewsDataProvider(context);

        super.onEnabled(context);
    }

    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled");

        if (mNewsDataProvider != null)
        {
            mNewsDataProvider.destroy();
        }

        cancelAlarm(context);
        stopService(context);

        super.onDisabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate");

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted");

        super.onDeleted(context, appWidgetIds);
    }

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        final String action = intent.getAction();

        if (ACTION_UPDATE_NEWS.equals(action)) {
            updateNewsWidget(context, AppWidgetManager.getInstance(context));
        }

        if (ACTION_UPDATE_CLICK.equals(action)) {
            final Intent intent_watch = new Intent();
            intent_watch.setAction("com.sihuatech.broadcast_WEB_URL");
            intent_watch.putExtra("WEB_URL",
                    (String) context.getResources().getText(R.string.news_url));
            intent_watch.putExtra("packageName", "cn.com.wasu.main");
            intent_watch.putExtra("startActivity", "WelcomeActivity");
            intent_watch.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent_watch);
        }

        if (ACTION_UPDATE_UP_CLICK.equals(action)) {
            Log.i(TAG, link_up);
            // DisplayToast(context, link_up);
            final Intent intent_watch = new Intent();
            intent_watch.setAction("com.sihuatech.broadcast_WEB_URL");
            intent_watch.putExtra("WEB_URL", link_up);
            intent_watch.putExtra("packageName", "cn.com.wasu.main");
            intent_watch.putExtra("startActivity", "WelcomeActivity");
            intent_watch.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent_watch);
        }

        if (ACTION_UPDATE_DOWN_CLICK.equals(action)) {
            Log.i(TAG, link_down);
            // DisplayToast(context, link_down);
            final Intent intent_watch = new Intent();
            intent_watch.setAction("com.sihuatech.broadcast_WEB_URL");
            intent_watch.putExtra("WEB_URL", link_down);
            intent_watch.putExtra("packageName", "cn.com.wasu.main");
            intent_watch.putExtra("startActivity", "WelcomeActivity");
            intent_watch.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent_watch);
        }

        super.onReceive(context, intent);
    }

    private void updateNewsWidget(Context context, AppWidgetManager appWidgetManager) {
        Log.i(TAG, "updateNewsWidget");
        ArrayList<NewsDataInfo> mRfreshDataInfos = new ArrayList<NewsDataInfo>(RefreshDataCount);
        RemoteViews mRemoteViews = null;

        if (mNewsDataProvider != null) {
            mRfreshDataInfos.clear();
            mRfreshDataInfos.addAll(mNewsDataProvider.refreshNewsData());

            if (mRfreshDataInfos.size() > 0) {

                mRemoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.news_widget_layout);

                link_up = mRfreshDataInfos.get(0).mLink;
                mRemoteViews.setTextViewText(R.id.title_up, mRfreshDataInfos.get(0).mTitle);
                mRemoteViews.setImageViewBitmap(R.id.list_pic_up,
                        mRfreshDataInfos.get(0).mThumbNail);

                link_down = mRfreshDataInfos.get(1).mLink;
                mRemoteViews.setTextViewText(R.id.title_down, mRfreshDataInfos.get(1).mTitle);
                mRemoteViews.setImageViewBitmap(R.id.list_pic_down,
                        mRfreshDataInfos.get(1).mThumbNail);

                Intent intent = new Intent();
                intent.setAction(ACTION_UPDATE_CLICK);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                mRemoteViews.setOnClickPendingIntent(R.id.grid_content, pendingIntent);

                Intent intent_up_pic = new Intent();
                intent_up_pic.setAction(ACTION_UPDATE_UP_CLICK);
                PendingIntent pendingIntentUpImg = PendingIntent.getBroadcast(context, 0,
                        intent_up_pic, 0);
                mRemoteViews.setOnClickPendingIntent(R.id.list_pic_up, pendingIntentUpImg);

                Intent intent_down_pic = new Intent();
                intent_down_pic.setAction(ACTION_UPDATE_DOWN_CLICK);
                PendingIntent pendingIntentDownImg = PendingIntent.getBroadcast(context, 0,
                        intent_down_pic, 0);
                mRemoteViews.setOnClickPendingIntent(R.id.list_pic_down, pendingIntentDownImg);

                Intent intent_up = new Intent();
                intent_up.setAction(ACTION_UPDATE_UP_CLICK);
                PendingIntent pendingIntentUp = PendingIntent
                        .getBroadcast(context, 0, intent_up, 0);
                mRemoteViews.setOnClickPendingIntent(R.id.title_up, pendingIntentUp);

                Intent intent_down = new Intent();
                intent_down.setAction(ACTION_UPDATE_DOWN_CLICK);
                PendingIntent pendingIntentDown = PendingIntent.getBroadcast(context, 0,
                        intent_down, 0);
                mRemoteViews.setOnClickPendingIntent(R.id.title_down, pendingIntentDown);

                int[] appIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                        News.class));

                appWidgetManager.updateAppWidget(appIds, mRemoteViews);
            }
            else
            {
                Log.i(TAG, "mRfreshDataInfos null");
            }
        }
        else
        {
            Log.i(TAG, "updateNewsWidget mNewsDataProvider null");
        }

    }

    private void setAlarm(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_NEWS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), NewsDelayTime, pendingIntent);
    }

    private void cancelAlarm(Context context) {
        final Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_NEWS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        am.cancel(pendingIntent);
    }

    private void DisplayToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    private void startService(Context context) {
        Log.i(TAG, "startService");
        final Intent widgetServiceIntent = new Intent();
        widgetServiceIntent.setAction("com.lge.tv.widget.news.service.WidgetDataService");
        context.startService(widgetServiceIntent);
    }

    private void stopService(Context context) {
        Log.i(TAG, "stopService");
        final Intent widgetServiceIntent = new Intent();
        widgetServiceIntent.setAction("com.lge.tv.widget.news.service.WidgetDataService");
        context.stopService(widgetServiceIntent);
    }
}
