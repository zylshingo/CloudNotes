package com.jikexueyuan.cloudnotes.util;

public class HtmlFile {

    private String localPath;
    private String urlPath;

    public HtmlFile() {
    }

    public HtmlFile(String localPath, String urlPath) {
        this.localPath = localPath;
        this.urlPath = urlPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public String toString() {
        return "{" +
                "localPath='" + localPath + '\'' +
                ", urlPath='" + urlPath + '\'' +
                '}';
    }
}
