package com.controller.mine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
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
import java.util.Iterator;
import java.util.Set;

public class WatcherService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new MyAsyncThread().execute("","","");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class MyAsyncThread extends AsyncTask<String,String,String> {
        HashMap<String,String> curBlog = null;
        @Override
        protected String doInBackground(String... strings) {
            Scrapper scr = new Scrapper();
            MyUtil.initData();
            String[] urls = null;

            try{urls = scr.getTodayDetailUrls();}catch(Exception err){}

            for(int i = 0; i<urls.length; i++){
                try {
                    MyUtil.currnentUrl = "("+i+") "+urls[i];
                    String[] contents = scr.getCafeBoardDetailHtml(urls[i]);

                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();// editor에 put 하기

                    int v1 = pref.getInt("skdog87AtNaverDotCom",0);
                    int v2 = pref.getInt("hwangcheol1240AtGmailDotCom",0);
                    int v3 = pref.getInt("hwangcheol1241AtGmailDotCom",0);
                    if(i%3==0){
                        editor.putInt("skdog87AtNaverDotCom",v1+1);
                        editor.commit();
                        curBlog = MyUtil.skdog87AtNaverDotCom;
                    }else if(i%3==1){
                        editor.putInt("hwangcheol1240AtGmailDotCom",v2+1);
                        editor.commit();
                        curBlog = MyUtil.hwangcheol1240AtGmailDotCom;
                    }else if(i%3==2){
                        editor.putInt("hwangcheol1241AtGmailDotCom",v3+1);
                        editor.commit();
                        curBlog = MyUtil.hwangcheol1241AtGmailDotCom;
                    }else ;



                    String boardItem = getBoardItem(contents[1]);
                    String status = getSendTistoryResult(contents[0],boardItem);
                    Set<String> set = pref.getStringSet("sendResult", new HashSet<String>());
                    set.add(status+"("+i+")\n");
                    editor.putStringSet("sendResult",set);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        sendNotification(curBlog.get("blogName"),urls[i],i);
                    }
                    Thread.sleep(20000l);
                } catch (Exception e) {
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
                    Set<String> set = pref.getStringSet("sendResult", new HashSet<String>());
                    set.add(e.getMessage() +"("+i+")\n");
                }

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sendNotification("끝","끝",urls.length);
            }

            return "";
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void sendNotification(String blogName, String url, int i){
            NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification noti = new Notification.Builder(getApplicationContext())
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
        }

        private String getSendTistoryResult(String title, String content){
            try{content =  URLEncoder.encode(content,"UTF-8");}catch(Exception er){;}
            if("".equals(title) || "".equals(content)) return "empty";
            String reqUrl = "https://www.tistory.com/apis/post/write";
            String visibility = "0";
            String blogName = curBlog.get("blogName");
            String access_token = curBlog.get("access_token");
            String data = String.format("access_token=%s&visibility=%s&blogName=%s&title=%s&content=%s&output=%s&"
                    ,access_token,visibility,blogName,title,content,"json");
            try{
                HttpURLConnection conn = initConn(reqUrl,data,"POST");
                // {"tistory":{"status":"200","postId":"301","url":"http:\/\/around-to-world.tistory.com\/301"}}
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
        private String getBoardItem(String html){
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            Set<String> hs = pref.getStringSet("blackImage",new HashSet<String>());


            Document doc = Jsoup.parse(html);
            Elements elms =  doc.select("img");
            label:for(Element node : elms){
                String nodeSrc = node.attr("src");
                Iterator<String> it = hs.iterator();
                while(it.hasNext()){
                    String src = it.next();
                    if(nodeSrc.equals(src)){
                        html = html.replace(node.toString(), "");
                        continue label;
                    }
                }

                String img = node.toString();
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
                //bm = BitmapFactory.decodeStream(in);
                //in.close();
                os = new FileOutputStream(new File(getFilesDir(),"downimage"));
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = in.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }

                //bm.compress(Bitmap.CompressFormat.PNG, 100, os);
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
            String replacer = uploadFileToTistory(getFilesDir()+File.separator+"downimage",src);
            Log.d("myTag",replacer);
            //get replaced text
            return replacer;
        }
        private String uploadFileToTistory(String file, String src) {
            String reqUrl = "https://www.tistory.com/apis/post/attach";
            String blogName = curBlog.get("blogName");
            String access_token = curBlog.get("access_token");
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

}
