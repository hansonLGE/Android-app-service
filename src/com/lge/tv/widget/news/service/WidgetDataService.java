
package com.lge.tv.widget.news.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.lge.tv.widget.news.service.util.ConnectionManager;

public class WidgetDataService extends Service {
    private static final String TAG = "WidgetDataService";

    private ArrayList<BaseClient> mClientList = new ArrayList<BaseClient>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();

        requestRefreshAllClient();
    }

    public void onDestroy() {
        ConnectionManager.getConnectionManager(getApplicationContext())
                .unregisterNetworkBroadcastReceiver();
        destroyAllClient();

        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        startService();
        return START_STICKY;
    }

    private void requestRefreshAllClient() {
        for (BaseClient client : mClientList) {
            client.refreshData(0);
        }
    }

    private void destroyAllClient() {
        for (int i = mClientList.size() - 1; i >= 0; i--) {
            BaseClient client = mClientList.get(i);
            mClientList.remove(i);

            client.cancelGathering();
            client = null;
        }
    }

    private void startService() {
        BaseClient client = null;

        client = createNewsModel(NewsClient.class);
        if (client != null) {
            client.gatheringData();
            client.setNetworkListener();
            mClientList.add(client);
        }
    }

    private BaseClient createNewsModel(Class<NewsClient> clientClass) {
        BaseClient client = null;

        String contentId = "news";
        String title = "News";
        String className = "NewsClient";
        String api = "http://www.******.html"; /*remote server api*/
        int timer = 300000;

        try {
            client = (BaseClient) clientClass.newInstance();
            client.setApi(api);
            client.setContentId(contentId);
            client.setTitle(title);
            client.setClassName(className);
            client.setTimer(timer);
            client.setContext(getApplicationContext());
            client.setConnectionManager(ConnectionManager
                    .getConnectionManager(getApplicationContext()));
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return client;
    }
}
