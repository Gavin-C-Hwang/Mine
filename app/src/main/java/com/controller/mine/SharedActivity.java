package com.controller.mine;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class SharedActivity extends Activity {
    private SharedActivity activity;
    public EditText etTag;
    public EditText etTitle;
    public EditText etHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
        MyUtil.initData();
        activity = this;

        etTag = (EditText)findViewById(R.id.etTag);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etHtml = (EditText)findViewById(R.id.etHtml);

        Button btnTech = (Button)findViewById(R.id.btnTech);
        Button btn70th = (Button)findViewById(R.id.btn70th);
        Button btnMerl = (Button)findViewById(R.id.btnMerl);
        btnTech.setOnClickListener(btnTechListener);
        btn70th.setOnClickListener(btn70thListener);
        btnMerl.setOnClickListener(btnMerlListener);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        btnTech.setText(btnTech.getText() + " " + pref.getInt("skdog87AtNaverDotCom",0));
        btn70th.setText(btn70th.getText() + " " + pref.getInt("hwangcheol1240AtGmailDotCom",0));
        btnMerl.setText(btnMerl.getText() + " " + pref.getInt("hwangcheol1241AtGmailDotCom",0));
        new CategoryTask((RadioGroup)findViewById(R.id.radioGroup1)).execute(MyUtil.skdog87AtNaverDotCom.get("blogName"), MyUtil.skdog87AtNaverDotCom.get("access_token"));
        new CategoryTask((RadioGroup)findViewById(R.id.radioGroup2)).execute(MyUtil.hwangcheol1240AtGmailDotCom.get("blogName"), MyUtil.hwangcheol1240AtGmailDotCom.get("access_token"));
        new CategoryTask((RadioGroup)findViewById(R.id.radioGroup3)).execute(MyUtil.hwangcheol1241AtGmailDotCom.get("blogName"), MyUtil.hwangcheol1241AtGmailDotCom.get("access_token"));
        new BoardItemThread().execute(getIntent().getStringExtra(Intent.EXTRA_TEXT),"","");
    }

    View.OnClickListener btnTechListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup1);
            int id = rg.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton) findViewById(id);
            String selectedText = rb.getText().toString();
            MyUtil.htmlCode = etHtml.getText().toString();
            new MyAsyncThread().execute(etTitle.getText().toString()+"@xhdtlswk7@"+etTag.getText().toString(),"btnTech",selectedText);
        }
    };

    View.OnClickListener btn70thListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup2);
            int id = rg.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton) findViewById(id);
            String selectedText = rb.getText().toString();
            MyUtil.htmlCode = etHtml.getText().toString();
            new MyAsyncThread().execute(etTitle.getText().toString()+"@xhdtlswk7@"+etTag.getText().toString(),"btn70th",selectedText);
        }
    };

    View.OnClickListener btnMerlListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            final RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup3);
            int id = rg.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton) findViewById(id);
            String selectedText = rb.getText().toString();
            MyUtil.htmlCode = etHtml.getText().toString();
            new MyAsyncThread().execute(etTitle.getText().toString()+"@xhdtlswk7@"+etTag.getText().toString(),"btnMerl",selectedText);
        }
    };

    View.OnClickListener ivListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
            Set<String> hs = pref.getStringSet("blackImage",new HashSet<String>());
            Document doc = Jsoup.parse(v.getContentDescription().toString());
            String src = doc.select("img").attr("src");
            hs.add(src);
            editor.putStringSet("blackImage",hs);
            editor.commit();

            Log.d("myTag",MyUtil.htmlCode);
            MyUtil.htmlCode = MyUtil.htmlCode.replace(v.getContentDescription().toString(),"");
            Log.d("myTag",MyUtil.htmlCode);
            LinearLayout ll = (LinearLayout)findViewById(R.id.images);
            ll.removeView(v);
            etHtml.setText(MyUtil.htmlCode);

        }
    };

    private class BoardItemThread extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            String[] boardItem = getBoardItem(strings[0]);
            String result = "";
            for(String item : boardItem){
                result = result + item + "@xhdtlswk7@";
            }
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            if(activity != null){
                String[] item = s.split("@xhdtlswk7@");
                activity.etTitle.setText(item[0]);
                activity.etTag.setText(item[2]);
                getImages(item[1]);

            }
            super.onPostExecute(s);
        }

        private void getImages(String html){
            MyUtil.htmlCode = html;
            Document doc = Jsoup.parse(html);
            Elements images = doc.select("img");
            for(Element node : images){
                String img = node.attr("src");
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                LinearLayout ll = (LinearLayout)findViewById(R.id.images);
                ll.addView(iv);
                iv.setContentDescription(node.toString());
                new DownloadImageTask(iv).execute(img);
                iv.setOnClickListener(ivListener);

                etHtml.setText(MyUtil.htmlCode);
            }
        }
        private String[] getBoardItem(String reqUrl){
            try{
                Log.d("myTag", reqUrl);
                if(reqUrl.startsWith("http://bhu.co.kr")){
                    return getMbongItem(reqUrl);
                }else if(reqUrl.startsWith("http://m.cafe.daum.net")){
                    return getMcafeItem(reqUrl);
                }
            }catch(Exception err){

            }
            return new String[]{"","",""};
        }
        private String[] getMbongItem(String reqUrl) throws Exception{
            Document doc = Jsoup.connect(reqUrl).header("User-Agent","Mozilla/5.0").get();
            String title = doc.select("#bo_v > header > div > table > tbody > tr > td > div").html();
            String content = doc.select("#bo_v_con").html();
            Elements elms =  doc.select("#bo_v_con").select("img");
            String tag = title.replaceAll(" ",",");
            return new String[]{title,content,tag};
        }
        private String[] getMcafeItem(String reqUrl) throws Exception{
            Document doc = Jsoup.connect(reqUrl).header("User-Agent","Mozilla/5.0").get();
            String title = doc.select(".tit_subject").html();
            String content = doc.select("#article").html();
            Elements elms =  doc.select("#article").select("img");
            String tag = title.replaceAll(" ",",");
            return new String[]{title,content,tag};
        }
    }

    private class MyAsyncThread extends AsyncTask<String,String,String>{

        private int maxItemCnt = 30;
        HashMap<String,String> curBlog = null;
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);


        private HashMap<String,String> getBlog(String btn){
            SharedPreferences.Editor editor = pref.edit();// editor에 put 하기

            int v1 = pref.getInt("skdog87AtNaverDotCom",0);
            int v2 = pref.getInt("hwangcheol1240AtGmailDotCom",0);
            int v3 = pref.getInt("hwangcheol1241AtGmailDotCom",0);

            if(v1>=maxItemCnt && v2>=maxItemCnt && v3>=maxItemCnt ){
                editor.putInt("skdog87AtNaverDotCom",0);
                editor.putInt("hwangcheol1240AtGmailDotCom",0);
                editor.putInt("hwangcheol1241AtGmailDotCom",0);
                editor.commit();
                return null;
            }

            if(v1<maxItemCnt && "btnTech".equals(btn) ){
                editor.putInt("skdog87AtNaverDotCom",v1+1);
                editor.commit();
                return MyUtil.skdog87AtNaverDotCom;
            }
            if(v2<maxItemCnt && "btn70th".equals(btn)  ){
                editor.putInt("hwangcheol1240AtGmailDotCom",v2+1);
                editor.commit();
                return MyUtil.hwangcheol1240AtGmailDotCom;
            }
            if(v3<maxItemCnt && "btnMerl".equals(btn) ){
                editor.putInt("hwangcheol1241AtGmailDotCom",v3+1);
                editor.commit();
                return MyUtil.hwangcheol1241AtGmailDotCom;
            }
            return null;
        }
        @Override
        protected String doInBackground(String... strings) {
            curBlog = getBlog(strings[1]);
            String boardItem = getBoardItem(MyUtil.htmlCode);
            return getSendTistoryResult(strings[0],boardItem,strings[2]);
        }
        @Override
        protected void onPostExecute(String s) {
            if(activity != null){
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            }
            activity.finish();
            super.onPostExecute(s);
        }

        private String getSendTistoryResult(String titleTag, String content, String category){
            String[] arTitleTag = titleTag.split("@xhdtlswk7@");
            String title = arTitleTag[0];
            String tag = arTitleTag[1];
            Log.d("myTag",content);
            try{content =  URLEncoder.encode(content,"UTF-8");}catch(Exception er){;}
            if("".equals(title) || "".equals(content)) return "empty";
            String reqUrl = "https://www.tistory.com/apis/post/write";
            String visibility = "2";
            String blogName = curBlog.get("blogName");
            String access_token = curBlog.get("access_token");
            String data = String.format("access_token=%s&visibility=%s&blogName=%s&title=%s&content=%s&tag=%s&output=%s&category=%s"
                    ,access_token,visibility,blogName,title,content,tag,"json",getCategoryID(category));
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
        private String getCategoryID(String categoryName){
            String reqUrl = "https://www.tistory.com/apis/category/list";
            String blogName = curBlog.get("blogName");
            String access_token = curBlog.get("access_token");
            String data = String.format("access_token=%s&blogName=%s&output=%s"
                    ,access_token,blogName,"json");
            reqUrl = reqUrl+"?"+data;
            String result = "";
            try{
                HttpURLConnection conn = (HttpURLConnection) (new URL(reqUrl)).openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                StringBuilder builder = new StringBuilder();
                String line = "";
                String receiveMsg = "";
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    receiveMsg = builder.toString();
                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

                JSONArray category = new JSONObject(receiveMsg)
                        .getJSONObject("tistory")
                        .getJSONObject("item")
                        .getJSONArray("categories");

                for (int i = 0; i < category.length(); i++) {
                    JSONObject jObject = category.getJSONObject(i);
                    String name = jObject.optString("name");
                    String id = jObject.optString("id");
                    if(categoryName.equals(name)){
                        result = id;
                        break;
                    }
                }
                conn.disconnect();
                return result;
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
            Document doc = Jsoup.parse(html);
            Elements elms =  doc.select("img");
            for(Element node : elms){
                String img = node.toString();
                String changedImg = replacedImg(node);
                html = html.replace(node.attr("src"), changedImg);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class CategoryTask extends AsyncTask<String, Void, ArrayList<String>> {
        RadioGroup radioGroup;

        public CategoryTask(RadioGroup radioGroup) {
            this.radioGroup = radioGroup;
        }

        protected ArrayList<String> doInBackground(String... urls) {

            String reqUrl = "https://www.tistory.com/apis/category/list";
            String blogName = urls[0];
            String access_token = urls[1];

            String data = String.format("access_token=%s&blogName=%s&output=%s"
                    ,access_token,blogName,"json");
            reqUrl = reqUrl+"?"+data;
            ArrayList<String> result = new ArrayList<String>();
            try{
                HttpURLConnection conn = (HttpURLConnection) (new URL(reqUrl)).openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                StringBuilder builder = new StringBuilder();
                String line = "";
                String receiveMsg = "";
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    receiveMsg = builder.toString();
                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

                JSONArray category = new JSONObject(receiveMsg)
                        .getJSONObject("tistory")
                        .getJSONObject("item")
                        .getJSONArray("categories");

                for (int i = 0; i < category.length(); i++) {
                    JSONObject jObject = category.getJSONObject(i);
                    String name = jObject.optString("name");
                    result.add(name);

                }
                conn.disconnect();
                return result;
            }catch(Exception err){
                return null;
            }
        }

        protected void onPostExecute(ArrayList<String> result) {
            for(String s : result){
                RadioButton rb = new RadioButton(activity);
                rb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                radioGroup.addView(rb);
                rb.setText(s);
            }
        }
    }

}
