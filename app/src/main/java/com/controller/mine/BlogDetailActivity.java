package com.controller.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.asynctask.mine.DownloadImage;
import com.asynctask.mine.UpdateTistory;
import com.com.entity.mine.Blog;
import com.com.entity.mine.BlogItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;


public class BlogDetailActivity extends AppCompatActivity implements AfterWork {
    private EditText etTitle;
    private EditText etHtml;
    private Button btnUpdate;
    private Button btnDelete;
    private RadioGroup rgBlogDetailCategory;
    private LinearLayout llBlogDetailImages;
    private WebView wvContents;
    private Blog blog;
    private String id;
    private String title;
    private String content;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);
        init();
        initCategory();
        initImages();
        initWebView();
    }

    /*controller setting*/
    private void init(){
        Intent it = getIntent();
        id = it.getStringExtra("id");
        title = it.getStringExtra("title");
        content = it.getStringExtra("content");
        categoryId = it.getStringExtra("categoryId");

        etTitle = (EditText)findViewById(R.id.etBlogDetailTitle);
        etHtml = (EditText)findViewById(R.id.etBlogDetailHtml);
        btnUpdate = (Button)findViewById(R.id.btnBlogDetailUpdate);
        btnUpdate.setOnClickListener(btnUpdateClickListener);
        btnDelete = (Button)findViewById(R.id.btnBlogDetailDelete);
        btnDelete.setOnClickListener(btnDeleteClickListener);
        rgBlogDetailCategory = (RadioGroup)findViewById(R.id.rgBlogDetailCategory);
        llBlogDetailImages = (LinearLayout)findViewById(R.id.llBlogDetailImages);
        wvContents = (WebView) findViewById(R.id.wvContents);
        blog = (Blog)it.getSerializableExtra("blog");
        etTitle.setText(title);
        etHtml.setText(content);
    }

    /*webView load*/
    private void initWebView(){
        try{
            WebSettings wsetting = wvContents.getSettings();
            wsetting.setBuiltInZoomControls(true);
            wsetting.setSupportZoom(true);
            wsetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            wvContents.loadData(content,"text/html","UTF-8");
        }catch(Exception err){
            Log.d("myLog",err.getMessage());
        }
    }
    /*category info setting*/
    private void initCategory(){
        Iterator<String> it = blog.getCategoryMap().keySet().iterator();
        while(it.hasNext()){
            String id = it.next();
            String name = blog.getCategoryMap().get(id);
            RadioButton rb = new RadioButton(this);
            rb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rgBlogDetailCategory.addView(rb);
            rb.setText(name);
            rb.setContentDescription(id);
            if(id.equals(categoryId)){
                rb.setChecked(true);
            }
        }
    }

    /*download Images*/
    private void initImages(){
        Document doc = Jsoup.parse(content);
        Elements images = doc.select("img");
        for(Element node : images){
            String img = node.attr("src");
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            llBlogDetailImages.addView(iv);
            iv.setContentDescription(node.toString());
            new DownloadImage(iv).execute(img);
            iv.setOnClickListener(ivListener);
        }
    }

    @Override
    public void after(String message) {
        if("200".equals(message)) {
            Toast.makeText(this, "성공하였습니다.", Toast.LENGTH_SHORT).show();
            this.finish();
        }else{
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    View.OnClickListener btnUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = rgBlogDetailCategory.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton)findViewById(viewId);
            String selectedText = rb.getText().toString();
            BlogItem blogItem = new BlogItem(id,title,content,"3",rb.getContentDescription().toString());
            new UpdateTistory(BlogDetailActivity.this, blog, blogItem).execute();
        }
    };

    View.OnClickListener btnDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = rgBlogDetailCategory.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton)findViewById(viewId);
            String selectedText = rb.getText().toString();
            BlogItem blogItem = new BlogItem(id,title,content,"0","0");
            new UpdateTistory(BlogDetailActivity.this, blog, blogItem).execute();
        }
    };

    View.OnClickListener ivListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //delete clicked img src
            content = content.replace(v.getContentDescription().toString(),"");
            llBlogDetailImages.removeView(v);
            etHtml.setText(content);
            initWebView();
        }
    };
}
