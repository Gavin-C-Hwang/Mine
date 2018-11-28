package com.asynctask.mine;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;
import com.com.entity.mine.Blog;
import com.com.entity.mine.BlogItem;
import com.controller.mine.AfterWork;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateTistory extends AsyncTask<String,String,String> {

    Blog blog;
    BlogItem blogItem;
    AfterWork atv;

    public UpdateTistory(AfterWork atv, Blog blog,BlogItem blogItem) {
        this.atv = atv;
        this.blog = blog;
        this.blogItem = blogItem;
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = "https://www.tistory.com/apis/post/modify";
        String data = String.format("access_token=%s&output=%s&blogName=%s&postId=%s&title=%s&content=%s&visibility=%s&category=%s"
        ,blog.getAccess_token(),"json",blog.getBlogName(),blogItem.getId(),blogItem.getTitle(),blogItem.getContent(),blogItem.getVisibility(),blogItem.getCategoryId());



        return sendResult(url,data);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        atv.after(s);
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

    private String sendResult(String reqUrl, String data){
        try{
            HttpURLConnection conn = initConnPOST(reqUrl,data);
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
            String result = new JSONObject(builder.toString())
                    .getJSONObject("tistory")
                    .optString("status");
            return result;
        }catch(Exception err){
            return err.getMessage();
        }

    }
}
