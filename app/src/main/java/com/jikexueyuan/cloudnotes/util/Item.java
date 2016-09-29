package com.jikexueyuan.cloudnotes.util;

/**
 * Created by Administrator on 2016/8/8.
 */
public class Item {
    private int _id;
    private String title;
    private String time;
    private String post;
    private String objectId;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Item(String title, String time, String post, int _id, String objectId) {
        this.title = title;
        this.time = time;
        this.post = post;
        this._id = _id;
        this.objectId = objectId;
    }
}
