package com.com.entity.mine;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Gavin Hwang
 * Blog Entity
 * */
public class Blog implements Serializable {


    private String blogName;
    private String access_token;
    private HashMap<String, String> categoryMap;

    public Blog(String blogName, String access_token) {
        this.blogName = blogName;
        this.access_token = access_token;
    }

    public Blog(String blogName, String access_token, HashMap<String, String> categoryMap) {
        this(blogName,access_token);
        this.categoryMap = categoryMap;
    }

    public String getBlogName() {
        return blogName;
    }

    public String getAccess_token() {
        return access_token;
    }

    public HashMap<String, String> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(HashMap<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }
}
