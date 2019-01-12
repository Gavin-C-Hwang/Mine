package com.controller.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asynctask.mine.UploadTistoryTask;
import com.com.entity.mine.Blog;
import com.scrapper.mine.DaumCafeScrapper;
import com.scrapper.mine.FmKoreaScrapper;
import com.scrapper.mine.Scrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private LinearLayout llBlogButtons;
    private Button btnStartService;
    private Button btnEndService;
    private Button btnResetPref;
    private Button btnDaumCafe;
    private Button btnFmKorea;
    private TextView tvSendResult;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        initViewAndListener();
        setSendResultText();
        setBlogButtons(new DaumCafeScrapper());
    }

    //init views and listeners
    private void initViewAndListener(){
        llBlogButtons = (LinearLayout)findViewById(R.id.blogs);
        btnStartService = (Button)findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(btnStartServiceListener);
        btnEndService = (Button)findViewById(R.id.btnEndService);
        btnEndService.setOnClickListener(btnEndServiceListener);
        btnResetPref = (Button)findViewById(R.id.btnResetPref);
        btnResetPref.setOnClickListener(btnResetPrefListener);
        btnDaumCafe = (Button)findViewById(R.id.btnDaumCafe);
        btnDaumCafe.setOnClickListener(btnDaumCafeListener);
        btnFmKorea = (Button)findViewById(R.id.btnFmKorea);
        btnFmKorea.setOnClickListener(btnFmKoreaListener);
        tvSendResult = (TextView)findViewById(R.id.sendResult);
        tvSendResult.setTextIsSelectable(true);
    }

    //update send result textview
    private void setSendResultText(){
        Set<String> hs2 = pref.getStringSet("sendResult",new HashSet<String>());
        Iterator<String> it2 = hs2.iterator();
        String sendResultList = "";
        while(it2.hasNext()){
            sendResultList = sendResultList +", "+ it2.next();
        }
        tvSendResult.setText(sendResultList);
    }

    private void setBlogButtons(Scrapper sc){
        HashMap<String,Blog> blogs = sc.getBlogs();
        Iterator<String> it = blogs.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            Button btn = new Button(this);
            btn.setText(key+":"+pref.getInt(key,0));
            btn.setContentDescription(  sc.getClass().getName()+"!xhdtlswk7!"+blogs.get(key).getBlogName());
            btn.setOnClickListener(btnBlogClickListener);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            llBlogButtons.addView(btn);
        }
    }
    private void fileDownload(String url, int cnt){

        Bitmap bm = null;
        InputStream in = null;
        OutputStream os = null;
        try{
            in = new URL(url).openStream();
            File folderPath = getFilesDir();
            String path = folderPath.getAbsolutePath();
            os = new FileOutputStream(new File(getFilesDir(),"downimage"+(cnt)));
            byte[] buf = new byte[1024];
            int len = 0;
            int sum = 0;
            while ((len = in.read(buf)) > 0) {
                os.write(buf, 0, len);
                sum+=buf[0];
            }
            os.flush();
            os.close();
            in.close();

            File f = new File(getFilesDir(),"downimage"+(cnt));
            Log.d("myTag",url+"downimage"+(cnt)+" f.length() "+f.getName()+"  "+f.length());
            Log.d("myTag"," sum "+sum);
        }catch(Exception er){
            Log.e("myTag",er.toString());
        }
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            fileDownload("http://cfile279.uf.daum.net/image/2176E04958F1FBE032CB10",0);
        }
    };


    View.OnClickListener btnStartServiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Intent intent = new Intent(getApplicationContext(),WatcherService.class);
            //startService(intent);
        }
    };

    View.OnClickListener btnEndServiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Intent intent = new Intent(getApplicationContext(),WatcherService.class);
            //stopService(intent);
            new Thread(run).start();
        }
    };




    View.OnClickListener btnDaumCafeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                Scrapper sc = new DaumCafeScrapper();
                new UploadTistoryTask(MainActivity.this, tvSendResult, sc).execute();
            }catch(Exception err){
                tvSendResult.setText( tvSendResult.getText()+"\n"+err.getMessage() );
            }
        }
    };

    View.OnClickListener btnFmKoreaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                Scrapper sc = new FmKoreaScrapper();
                new UploadTistoryTask(MainActivity.this, tvSendResult, sc).execute();
            }catch(Exception err){
                tvSendResult.setText( tvSendResult.getText()+"\n"+err.getMessage() );
            }
        }
    };

    View.OnClickListener btnResetPrefListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
            Scrapper scDaum = new DaumCafeScrapper();
            HashMap<String,Blog> daumBlogs = scDaum.getBlogs();
            Iterator<String> itDaumBlogs = daumBlogs.keySet().iterator();
            while(itDaumBlogs.hasNext()){
                String key = itDaumBlogs.next();
                editor.putInt(daumBlogs.get(key).getBlogName(),0);
            }

            Scrapper scFm = new FmKoreaScrapper();
            HashMap<String,Blog> fmBlogs = scFm.getBlogs();
            Iterator<String> itfmBlogs = fmBlogs.keySet().iterator();
            while(itfmBlogs.hasNext()){
                String key = itfmBlogs.next();
                editor.putInt(fmBlogs.get(key).getBlogName(),0);
            }

            editor.commit();
        }
    };

    View.OnClickListener btnBlogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent it = new Intent(MainActivity.this, BlogListActivity.class);
            it.putExtra("blogName",v.getContentDescription());
            startActivity(it);
        }
    };





}
