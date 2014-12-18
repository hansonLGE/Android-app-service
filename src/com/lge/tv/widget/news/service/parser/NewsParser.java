
package com.lge.tv.widget.news.service.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.lge.tv.widget.news.service.data.NewsDataInfo;

public class NewsParser {
    private static final String TAG = "NewsParser";

    public NewsParser() {
        Log.i(TAG, "NewsParser");
    }

    private NewsDataInfo newsData = new NewsDataInfo();

    public List<NewsDataInfo> getNewsDataList(HttpURLConnection conn) {
        List<NewsDataInfo> dataList = new ArrayList<NewsDataInfo>();

        InputStream is = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            try {
                is = conn.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            xpp.setInput(is, "utf-8");

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {

                } else if (eventType == XmlPullParser.END_DOCUMENT) {
                    newsData = null;
                } else if (eventType == XmlPullParser.START_TAG) {
                    processStartElement(xpp);
                } else if (eventType == XmlPullParser.END_TAG) {
                    String entry = xpp.getName();
                    if ("item".equals(entry)) {
                        if (newsData != null) {
                            dataList.add(newsData);
                            newsData = null;
                            newsData = new NewsDataInfo();
                        }

                    }
                } else if (eventType == XmlPullParser.TEXT) {

                }

                eventType = xpp.next();

            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
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

        return dataList;
    }

    private void processStartElement(XmlPullParser xpp) throws XmlPullParserException {
        String name = xpp.getName();

        try {
            if ("title".equals(name)) {
                newsData.setNewsTitle(xpp.nextText());
            } else if ("link".equals(name)) {
                newsData.setLink(xpp.nextText());
            } else if ("img".equals(name)) {
                newsData.setThumbUrl(xpp.getAttributeValue(null, "src"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
