package com.asynctask.mine;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.com.entity.mine.Blog;
import com.com.entity.mine.BlogItem;
import com.controller.mine.BlogListActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class RetrieveTistory  extends AsyncTask<String,String,ArrayList<BlogItem>> {
    private final int itemCnt = 30;
    Blog blog;
    BlogListActivity atv;
    public RetrieveTistory(BlogListActivity atv, Blog blog) {
        this.atv = atv;
        this.blog = blog;
    }

    @Override
    protected ArrayList<BlogItem> doInBackground(String... strings) {
        ArrayList<BlogItem> result = new ArrayList<>();
        try{
            blog.setCategoryMap(getCategories());
            JSONArray items = getBlogItems();
            for (int i = 0; i < items.length(); i++) {
                JSONObject jObject = items.getJSONObject(i);
                String id = jObject.optString("id");
                String title = jObject.optString("title");
                String postUrl = jObject.optString("postUrl");
                String visibility = jObject.optString("visibility");
                String categoryId = jObject.optString("categoryId");
                String categoryName = blog.getCategoryMap().get(categoryId);
                String date = jObject.optString("date");
                String content = getBlogContent(jObject.optString("id"));
                publishProgress("("+(i+1)+"/"+items.length() +") 첫번째 이미지");
                Bitmap firstImg = getFirstImg(content);
                publishProgress("("+(i+1)+"/"+items.length() +") 마지막 이미지");
                Bitmap lastImg = getLastImg(content);
                BlogItem item = new BlogItem(id,title,postUrl,visibility,categoryId,categoryName,date,content,firstImg,lastImg);
                result.add(item);
            }

            Collections.sort(result, new Comparator<BlogItem>() {
                @Override
                public int compare(BlogItem o1, BlogItem o2) {
                    return o1.getVisibility().compareTo(o2.getVisibility());
                }
            });
            return result;
        }catch(Exception er){
            return result;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<BlogItem> s) {
        super.onPostExecute(s);
        atv.setBlogItemList(s);
        atv.initCategory();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        atv.tvProgress.setText(values[0]);
    }

    private HttpURLConnection initConnPOST(String reqUrl, String data) throws Exception{
        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("User-Agent","my-tistory");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        os.close();
        return conn;
    }

    private HttpURLConnection initConnGET(String reqUrl, String data) throws Exception{
        URL url = new URL(reqUrl+"?"+data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("User-Agent","my-tistory");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        return conn;
    }

    /*retrieve tistory blog item list*/
    private JSONArray getBlogItems(){
        String reqUrl = "https://www.tistory.com/apis/post/list";
        String data = String.format("access_token=%s&output=%s&blogName=%s&count=%d"
                ,blog.getAccess_token(),"json",blog.getBlogName(),itemCnt);
        try{
            HttpURLConnection conn = initConnGET(reqUrl,data);
            StringBuilder builder = new StringBuilder();
            String line = "";
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
            }
            conn.disconnect();
            JSONArray items = new JSONObject(builder.toString())
                    .getJSONObject("tistory")
                    .getJSONObject("item")
                    .getJSONArray("posts");
            return items;
        }catch(Exception err){
            return null;
        }
    }

    /*retrieve tistory blog item list*/
    private String getBlogContent(String postId){
        String reqUrl = "https://www.tistory.com/apis/post/read";
        String data = String.format("access_token=%s&output=%s&blogName=%s&postId=%s"
                ,blog.getAccess_token(),"json",blog.getBlogName(),postId);
        try{
            HttpURLConnection conn = initConnGET(reqUrl,data);
            StringBuilder builder = new StringBuilder();
            String line = "";
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
            }
            conn.disconnect();
            String content = new JSONObject(builder.toString())
                    .getJSONObject("tistory")
                    .getJSONObject("item")
                    .optString("content");
            return content;
        }catch(Exception err){
            return "";
        }
    }

    /*get img tag srcs from html*/
    private String[] getImgUrls(String html){
        String result="";
        Document doc = Jsoup.parse(html);
        Elements images = doc.select("img");
        for(Element node : images) {
            result = result + "," + node.attr("src");
        }
        return result.length()<1?new String[0]:result.substring(1).split(",");
    }

    /*get Bitmap object from url*/
    private Bitmap getBitmap(String url){
        Bitmap result = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // Calculate inSampleSize
            options.inSampleSize = 4;
            options.inJustDecodeBounds = false;
            result = BitmapFactory.decodeStream(in, null, options);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Bitmap getFirstImg(String content){
        String[] urls = getImgUrls(content);
        if(urls.length>0) return getBitmap(urls[0]);
        else return null;
    }
    private Bitmap getLastImg(String content){
        String[] urls = getImgUrls(content);
        if(urls.length>0) return getBitmap(urls[urls.length-1]);
        else return null;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /*return category map*/
    private HashMap<String,String> getCategories(){
        HashMap<String,String> categoryMap = new HashMap<String,String>();
        String reqUrl = "https://www.tistory.com/apis/category/list";
        String data = String.format("access_token=%s&blogName=%s&output=%s"
                ,blog.getAccess_token(),blog.getBlogName(),"json");
        String result = "";
        try{
            HttpURLConnection conn = initConnGET(reqUrl,data);
            StringBuilder builder = new StringBuilder();
            String line = "";
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
            conn.disconnect();
            JSONArray category = new JSONObject(builder.toString())
                    .getJSONObject("tistory")
                    .getJSONObject("item")
                    .getJSONArray("categories");

            for (int i = 0; i < category.length(); i++) {
                JSONObject jObject = category.getJSONObject(i);
                String name = jObject.optString("name");
                String id = jObject.optString("id");
                categoryMap.put(id,name);
            }

            return categoryMap;
        }catch(Exception err){
            return null;
        }
    }

}
