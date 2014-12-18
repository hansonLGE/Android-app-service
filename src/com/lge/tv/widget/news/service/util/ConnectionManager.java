
package com.lge.tv.widget.news.service.util;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class ConnectionManager {
    private static final String TAG = "ConnectionManager";

    private Context context;
    public static ConnectionManager manager = null;
    private Object lock = new Object();
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private final int MSG_BROADCAST_NETWORK_CHANGED = 1000;
    private final int DELAY_MS_BROADCAST_NETWORK_CHANGED = 5000;

    private enum Status {
        CONNECTED,
        DISCONNECTED,
        NO_STATUS
    };

    private Status networkStatus = Status.NO_STATUS;
    private ArrayList<NetworkListener> mNetworkListenerList = new ArrayList<NetworkListener>();

    private ConnectionManager(Context context) {
        this.context = context;
        mHandlerThread = new HandlerThread("connection_manager");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                if (msg.what == MSG_BROADCAST_NETWORK_CHANGED) {
                    broadcastConnectionChanged();
                }
            }
        };
    }

    public static ConnectionManager getConnectionManager(Context context) {
        if (manager == null) {
            manager = new ConnectionManager(context);
            manager.registerNetworkBroadcastReceiver();
        }

        return manager;
    }

    public void addListener(NetworkListener listener) {
        this.mNetworkListenerList.add(listener);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @SuppressLint("HandlerLeak")
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = !(intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));

            synchronized (lock) {
                if (networkStatus != Status.NO_STATUS) {
                    if ((!isConnected && networkStatus == Status.CONNECTED)
                            || (isConnected && networkStatus == Status.DISCONNECTED)) {
                        queueBroadcastConnectionChanged();
                    }
                }

                networkStatus = isConnected ? Status.CONNECTED : Status.DISCONNECTED;
            }
        }
    };

    private void queueBroadcastConnectionChanged() {
        mHandler.removeMessages(MSG_BROADCAST_NETWORK_CHANGED);
        mHandler.sendEmptyMessageDelayed(MSG_BROADCAST_NETWORK_CHANGED,
                DELAY_MS_BROADCAST_NETWORK_CHANGED);
    }

    public void broadcastNotifyNetworkSettingsError() {
        for (NetworkListener networkListener : mNetworkListenerList) {
            networkListener.onNotifyNetworkSettingsError();
        }

        networkStatus = Status.DISCONNECTED;
    }

    private void broadcastConnectionChanged() {
        boolean isConnected = (networkStatus == Status.CONNECTED ? true : false);

        for (NetworkListener networkListener : mNetworkListenerList) {
            networkListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public void registerNetworkBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    public void unregisterNetworkBroadcastReceiver() {
        if (networkReceiver != null) {
            context.unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }
    }

    public boolean isConnected() {
        boolean isConnected = false;

        synchronized (lock) {
            if (networkStatus == Status.NO_STATUS) {
                networkStatus = getNetworkStatus();
            }
            isConnected = (networkStatus == Status.CONNECTED ? true : false);
        }

        return isConnected;
    }

    private Status getNetworkStatus() {
        ConnectivityManager mgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethernetInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiInfo = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!ethernetInfo.isConnected() && !wifiInfo.isConnected()) {
            return Status.DISCONNECTED;
        }

        return Status.CONNECTED;
    }

    public interface NetworkListener {
        public void onNetworkConnectionChanged(boolean isConnected);

        public void onNotifyNetworkSettingsError();
    }
}
