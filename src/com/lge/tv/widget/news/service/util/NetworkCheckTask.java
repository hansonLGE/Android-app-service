
package com.lge.tv.widget.news.service.util;

import java.net.InetAddress;

import android.os.AsyncTask;

public class NetworkCheckTask extends AsyncTask<Void, Void, Boolean> {
    private final String TestHostName = "www.google.com";

    @Override
    protected Boolean doInBackground(Void... params) {
        // TODO Auto-generated method stub
        boolean result = true;

        try {
            InetAddress.getByName(TestHostName);
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }
}
