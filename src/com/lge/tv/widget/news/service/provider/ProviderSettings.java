
package com.lge.tv.widget.news.service.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderSettings {
    private ProviderSettings() {

    }

    public static final class WidgetDataNewsColumns implements BaseColumns {
        private WidgetDataNewsColumns() {

        }

        public static final Uri WIDGET_DATA_NEWS_CONTENT_URI =
                Uri.parse("content://" + WidgetDataProvider.AUTHORITY + "/"
                        + WidgetDataProvider.TABLE_WIDGET_DATA_NEWS);

        public static final String CONTENT_ID = "content_id";
        public static final String TITLE = "title";
        public static final String LINK_URL = "link_url";
        public static final String THUMB = "thumb";
        public static final String THUMB_URL = "thumb_url";

        public static String[] WIDGET_DATA_NEWS_PROJECTION = new String[] {
                _ID,
                CONTENT_ID,
                TITLE,
                LINK_URL,
                THUMB,
                THUMB_URL
        };
    }

}
