
package com.lge.tv.widget.news.service.data;

public class NewsDataInfo {
    private String newsTitle = "";
    private String link = "";
    private String thumbUrl = "";
    private byte[] thumbBlob = null;

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getLink() {
        return link;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public byte[] getThumbBlob() {
        return thumbBlob;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public void setThumbBlob(byte[] thumbBlob) {
        this.thumbBlob = thumbBlob;
    }
}
