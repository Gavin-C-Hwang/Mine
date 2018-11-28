package com.asynctask.mine;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import com.com.entity.mine.Blog;
import com.controller.mine.MultipartUpload;
import com.scrapper.mine.Scrapper;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UploadTistoryTask extends AsyncTask<String,String,String> {
    Blog curBlog;
    Activity activity;
    TextView tvResult;
    Scrapper sc;
    String uploadResult="";



    public UploadTistoryTask(Activity activity, TextView tvResult,Scrapper sc) {
        this.activity = activity;
        this.tvResult = tvResult;
        this.sc = sc;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            //get Todays urls
            publishProgress("try to getTodayDetailUrls...",uploadResult);
            String[] urls = sc.getTodayDetailUrls();
            for(int i = 0; i<urls.length; i++){
                curBlog = sc.getCurBlog(i);
                publishProgress(curBlog.getBlogName()+":"+urls[i]+" try to get the detail html code.",uploadResult);
                String[] script = sc.getItemHtml(urls[i]);
                String content = getContentsAfterImageChanged(script[1]);
                publishProgress(curBlog.getBlogName()+":"+urls[i]+" upload to tistory",uploadResult);
                uploadResult = i+":"+getSendTistoryResult(script[0],content)+"\n"+uploadResult;
                publishProgress(uploadResult,"");
                prefWork(curBlog,i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    sendNotification(curBlog.getBlogName(), uploadResult, i);
                }
            }
        } catch (Exception e) {
            uploadResult = uploadResult+e.getMessage() +"\n";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sendNotification("끝","끝",0);
        }
        return uploadResult;
    }

    private void prefWork(Blog curBlog, int i){
        SharedPreferences pref = activity.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
        int itemCnt = pref.getInt(curBlog.getBlogName(),0);
        editor.putInt(curBlog.getBlogName(),itemCnt+1);
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String blogName, String url, int i){
        NotificationManager m = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification noti = new Notification.Builder(activity.getApplicationContext())
                .setTicker("완료")
                .setContentTitle(blogName)
                .setContentText("("+i+") "+url)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setWhen(System.currentTimeMillis())
                .build();
        m.notify(1, noti);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
       // tvResult.setText( tvResult.getText()+"\n"+idx+" : "+s );
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        tvResult.setText(values[0]+"\n"+values[1]);

    }

    /*upload html to tistory blog and return result*/
    private String getSendTistoryResult(String title, String content){
        try{content =  URLEncoder.encode(content,"UTF-8");}catch(Exception er){;}
        if("".equals(title) || "".equals(content)) return "empty";
        String reqUrl = "https://www.tistory.com/apis/post/write";
        String visibility = "0";
        String blogName = curBlog.getBlogName();
        String access_token = curBlog.getAccess_token();
        String data = String.format("access_token=%s&visibility=%s&blogName=%s&title=%s&content=%s&output=%s&"
                ,access_token,visibility,blogName,title,content,"json");
        try{
            HttpURLConnection conn = initConn(reqUrl,data,"POST");
            InputStreamReader responseBodyReader =  new InputStreamReader(conn.getInputStream(), "UTF-8");
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            String status = getTistoryStatus(jsonReader);
            jsonReader.close();
            responseBodyReader.close();
            conn.disconnect();
            return status + " : "+ blogName;
        }catch(Exception err){
            return err.getMessage() + " : "+ blogName;
        }
    }

    private HttpURLConnection initConn(String reqUrl, String data, String method) throws Exception{
        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("User-Agent","my-tistory");
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        os.close();
        return conn;
    }
    /*after send data to tistory, return parsed result*/
    private String getTistoryStatus(JsonReader jsonReader) throws Exception{
        jsonReader.beginObject();
        jsonReader.hasNext();
        jsonReader.nextName();
        jsonReader.beginObject();
        if(jsonReader.hasNext()){
            String key = jsonReader.nextName();
            if("status".equals(key)){
                if( "200".equals(jsonReader.nextString())){
                    return "Success";
                }else{
                    return jsonReader.nextString();
                }
            }else{
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        jsonReader.endObject();
        return "fail";
    }
    /*replace html's img src after upload original images*/
    private String getContentsAfterImageChanged(String html){
        Document doc = Jsoup.parse(html);
        Elements elms =  doc.select("img");
        for(Element node : elms){
            String nodeSrc = node.attr("src");
            String changedImg = replacedImg(node);
            html = html.replace(nodeSrc, changedImg);
        }
        return html;
    }
    private void fileDownload(String url){

        Bitmap bm = null;
        InputStream in = null;
        OutputStream os = null;
        try{
            in = new URL(url).openStream();
            os = new FileOutputStream(new File(activity.getFilesDir(),"downimage"));
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            os.flush();
            os.close();
            in.close();
        }catch(Exception er){
            Log.e("myTag",er.toString());
        }
    }
    private String replacedImg(Element imgTag){
        //get src addr
        String src = imgTag.attr("src");
        Log.d("myTag",src);
        //download file
        fileDownload(src);
        //upload to blog
        String replacer = uploadFileToTistory(activity.getFilesDir()+File.separator+"downimage",src);
        Log.d("myTag",replacer);
        //get replaced text
        return replacer;
    }
    private String uploadFileToTistory(String file, String src) {
        String reqUrl = "https://www.tistory.com/apis/post/attach";
        String blogName = curBlog.getBlogName();
        String access_token = curBlog.getAccess_token();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("access_token",access_token);
        param.put("blogName",blogName);
        param.put("output","json");
        HashMap<String, String> files = new HashMap<String, String>();
        files.put("uploadedfile",file);
        Log.d("myTag",file);
        JSONObject json = null;
        try {

            String url = reqUrl;
            MultipartUpload multipartUpload = new MultipartUpload(url, "UTF-8");
            //multipartUpload.setProgressListener(activity);
            json = multipartUpload.upload(param, files, src);
            return json.getJSONObject("tistory").getString("url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}