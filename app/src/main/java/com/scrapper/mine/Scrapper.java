package com.scrapper.mine;

import com.com.entity.mine.Blog;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Gavin Hwang
 * Scrape Interface
 * */
public interface Scrapper {
    /* scrape todays url array */
    public String[] getTodayDetailUrls();
    /* get detail item html code from url */
    public String[] getItemHtml(String url) throws Exception;
    /* get blogs Info */
    public HashMap<String,Blog> getBlogs();
    /* get blog Info */
    public Blog getCurBlog(String name);
    /* get blog Info */
    public Blog getCurBlog(int idx);


}
