package com.jikexueyuan.cloudnotes.util;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/8/6.
 */

public class UserData extends BmobObject {

    private String title;
    private String time;
    private String post;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
