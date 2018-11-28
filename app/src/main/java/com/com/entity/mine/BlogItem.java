package com.com.entity.mine;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BlogItem{
    private String id;
    private String title;
    private String postUrl;
    private String visibility;
    private String categoryId;
    private String categoryName;
    private String date;
    private String content;
    private Bitmap firstImg;
    private Bitmap lastImg;

    public BlogItem(String id, String title, String content, String visibility, String categoryId){
        this.id = id;
        this.title = title;
        this.content = content;
        this.visibility = visibility;
        this.categoryId = categoryId;
    }
    public BlogItem(String id, String title, String postUrl, String visibility, String categoryId,String categoryName, String date) {
        this(id,title,"",visibility,categoryId);
        this.postUrl = postUrl;
        this.categoryName = categoryName;
        this.date = date;
    }

    public BlogItem(String id, String title, String postUrl, String visibility, String categoryId, String categoryName, String date, String content) {
        this(id, title,postUrl,visibility,categoryId,categoryName,date);
        this.content = content;
    }

    public BlogItem(String id, String title, String postUrl, String visibility, String categoryId, String categoryName, String date, String content,Bitmap firstImg,Bitmap lastImg) {
        this(id, title,postUrl,visibility,categoryId,categoryName,date,content);
        this.firstImg = firstImg;
        this.lastImg = lastImg;
    }

    public String getTitle() {
        return title;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getDate() {
        return date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getFirstImg() {
        return firstImg;
    }

    public Bitmap getLastImg() {
        return lastImg;
    }

    public void setFirstImg(Bitmap firstImg) {
        this.firstImg = firstImg;
    }

    public void setLastImg(Bitmap lastImg) {
        this.lastImg = lastImg;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getId() {
        return id;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
