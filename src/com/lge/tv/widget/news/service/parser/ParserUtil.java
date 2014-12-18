
package com.lge.tv.widget.news.service.parser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ParserUtil {
    private static final String TAG = "ParserUtil";

    public static HttpURLConnection getHttpURLConnection(String apiUrl) {
        HttpURLConnection httpConn = null;

        try {
            URL url = new URL(apiUrl);
            System.setProperty("http.keepAlive", "false");
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(5000);
            httpConn.setReadTimeout(5000);

            if (HttpURLConnection.HTTP_OK == httpConn.getResponseCode()) {
                return httpConn;
            } else {
                httpConn.disconnect();
                httpConn = null;
                return null;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            httpConn.disconnect();
            e.printStackTrace();
        }

        return null;
    }
}
