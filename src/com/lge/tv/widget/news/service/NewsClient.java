
package com.lge.tv.widget.news.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.lge.tv.widget.news.service.data.NewsDataInfo;
import com.lge.tv.widget.news.service.data.WidgetDataUtil;
import com.lge.tv.widget.news.service.parser.NewsParser;
import com.lge.tv.widget.news.service.parser.ParserUtil;
import com.lge.tv.widget.news.service.util.ConnectionManager.NetworkListener;
import com.lge.tv.widget.news.service.util.NetworkCheckTask;

public class NewsClient extends BaseClient {
    private static final String TAG = "NewsClient";

    private final int defautMsg = 0;
    private NewsTask task = null;

    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    public void refreshData(long timer) {
        // TODO Auto-generated method stub
        if (WidgetDataUtil.isExistNewsData(context, this)) {
            WidgetDataUtil.removeNewsDataList(context, this);
        }

        if (handler != null) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
    }

    @Override
    public void gatheringData() {
        // TODO Auto-generated method stub
        handlerThread = new HandlerThread("NewsClient");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            private int nRetryCnt = 1;

            public void handleMessage(Message msg) {
                if (task != null) {
                    if (task.getStatus() != Status.FINISHED) {
                        task.cancel(true);
                    }
                    task = null;
                }

                Log.i(TAG, "HttpURLConnection");
                HttpURLConnection conn = ParserUtil.getHttpURLConnection(api);
                if (conn != null) {
                    task = new NewsTask(conn);
                    task.execute();

                    nRetryCnt = 1;
                } else {
                    if (nRetryCnt <= 0) {
                        // check network
                        new NetworkCheckTask() {
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    // service is not available!
                                    WidgetDataUtil.setNewsDataList(context, null, NewsClient.this);
                                } else {
                                    // network connection is failed.
                                    conManager.broadcastNotifyNetworkSettingsError();
                                }
                            }
                        }.execute();
                    } else {
                        refreshData(60000);
                        nRetryCnt--;
                    }
                }
            }
        };

		if(conManager.isConnected())
		{
		    Log.i(TAG, "network connected");
			refreshData(defautMsg);
		}
		else
		{
		    Log.i(TAG, "network disconnected");
		}  
    }

    @Override
    public void cancelGathering() {
        // TODO Auto-generated method stub
        if (handler != null) {
            handler.removeMessages(defautMsg);
            handler = null;
        }

        if (handlerThread != null) {
            handlerThread.quit();
            handlerThread = null;
        }

        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    private void refreshData() {
        if (handler != null) {
            handler.removeMessages(defautMsg);
            handler.sendMessageDelayed(handler.obtainMessage(defautMsg), timer);
        }
    }

    private void stopRefreshData() {
        if (handler != null) {
            handler.removeMessages(defautMsg);
        }
    }

    class NewsTask extends AsyncTask<Object, Void, List<NewsDataInfo>> {
        private HttpURLConnection conn;

        public NewsTask(HttpURLConnection conn) {
            this.conn = conn;
        }

        @Override
        protected List<NewsDataInfo> doInBackground(Object... params) {
            // TODO Auto-generated method stub
            NewsParser parser = new NewsParser();
            List<NewsDataInfo> dataList = new ArrayList<NewsDataInfo>();
            dataList = parser.getNewsDataList(conn);

            return dataList;
        }

        protected void onCancelled() {
            super.onCancelled();

            if (conn != null) {
                try {
                    conn.disconnect();
                    conn = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        protected void onPostExecute(List<NewsDataInfo> result) {
            storeData(result);
            conn.disconnect();
            conn = null;
            refreshData();
        }
    }

    class MakeBlobTask extends AsyncTask<List<NewsDataInfo>, Void, List<NewsDataInfo>> {

        @Override
        protected List<NewsDataInfo> doInBackground(List<NewsDataInfo>... params) {
            // TODO Auto-generated method stub
            List<NewsDataInfo> dataList = (List<NewsDataInfo>) params[0];

            for (NewsDataInfo newsDataInfo : dataList) {
                newsDataInfo.setThumbBlob(makeJpgIconBlob(getBitmapFromURL(newsDataInfo
                        .getThumbUrl())));
            }

            return dataList;
        }

        protected void onPostExecute(List<NewsDataInfo> result) {
            executeTransaction(result);
        }
    }

    private void storeData(List<NewsDataInfo> newsDataList) {
        Log.i(TAG, "storeData");
        boolean isDatachanged = false;
        List<NewsDataInfo> storedDataList = WidgetDataUtil.getNewsDataList(context, this);
        int i, j;

        for (i = 0; !isDatachanged && i < storedDataList.size(); i++) {
            String storedDataTitle = storedDataList.get(i).getNewsTitle();

            for (j = 0; j < newsDataList.size(); j++) {
                String newsDataTitle = newsDataList.get(j).getNewsTitle();

                if (storedDataTitle.equals(newsDataTitle)) {
                    break;
                }
            }

            if (j == newsDataList.size()) {
                isDatachanged = true;
            }
        }

        if (storedDataList.size() == 0 || isDatachanged) {
            Log.i(TAG, "storeData insert");
            MakeBlobTask blobTask = new MakeBlobTask();
            blobTask.execute(newsDataList);
        }

    }

    private void executeTransaction(List<NewsDataInfo> list) {
        Log.i(TAG, "executeTransaction");
        WidgetDataUtil.setNewsDataList(context, list, this);
    }

    @Override
    public void setNetworkListener() {
        // TODO Auto-generated method stub
        conManager.addListener(new NetworkListener() {

            @Override
            public void onNetworkConnectionChanged(boolean isConnected) {
                // TODO Auto-generated method stub
                if (isConnected) {
				    Log.i(TAG, "listen: network connected");
                    refreshData(defautMsg);
                } else {
				    Log.i(TAG, "listen: network disconnected");
                    stopRefreshData();
                    WidgetDataUtil.setNewsDataList(context, null, NewsClient.this);
                }
            }

            @Override
            public void onNotifyNetworkSettingsError() {
                // TODO Auto-generated method stub
                stopRefreshData();
                WidgetDataUtil.setNewsDataList(context, null, NewsClient.this);
            }

        });
    }

}
