package com.controller.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.mine.BlogItemListAdapter;
import com.asynctask.mine.RetrieveTistory;
import com.asynctask.mine.UpdateTistory;
import com.com.entity.mine.Blog;
import com.com.entity.mine.BlogItem;
import com.scrapper.mine.Scrapper;

import org.w3c.dom.Text;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class BlogListActivity extends AppCompatActivity implements AfterWork{
    public TextView tvProgress;
    private ListView lvBlogItemList;
    private RadioGroup rgBlogListCategory;
    private Button btnBlogListUpdate;
    private Button btnBlogListDelete;
    private CheckBox cbCheckAll;
    private Blog blog;
    private BlogItemListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);
        init();
        initData();
    }

    /*init controller*/
    private void init(){
        tvProgress = (TextView)findViewById(R.id.tvProgress);
        lvBlogItemList = (ListView)findViewById(R.id.bloglist);
        rgBlogListCategory = (RadioGroup)findViewById(R.id.rgBlogListCategory);
        btnBlogListUpdate = (Button)findViewById(R.id.btnBlogListUpdate);
        btnBlogListUpdate.setOnClickListener(btnUpdateClickListener);
        btnBlogListDelete = (Button)findViewById(R.id.btnBlogListDelete);
        btnBlogListDelete.setOnClickListener(btnDeleteClickListener);
        cbCheckAll = (CheckBox)findViewById(R.id.bloglist_check_all);
        cbCheckAll.setOnClickListener(cbCheckAllClickListener);
    }

    /*download data*/
    private void initData(){
        try{
            Intent it = getIntent();
            Class cs = Class.forName(it.getStringExtra("blogName").split("!xhdtlswk7!")[0]);
            Constructor constructor = cs.getConstructor();
            Scrapper sc = (Scrapper)constructor.newInstance();
            String blogName = it.getStringExtra("blogName").split("!xhdtlswk7!")[1];

            blog = sc.getCurBlog(blogName);

            new RetrieveTistory(this, blog).execute();
        }catch(Exception err){
            Log.d("myLog",err.getMessage());
        }
    }

    /*category info setting*/
    public void initCategory(){
        Iterator<String> it = blog.getCategoryMap().keySet().iterator();
        while(it.hasNext()){
            String id = it.next();
            String name = blog.getCategoryMap().get(id);
            RadioButton rb = new RadioButton(this);
            rb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            rgBlogListCategory.addView(rb);
            rb.setText(name);
            rb.setContentDescription(id);
        }
    }

    public void setBlogItemList(ArrayList<BlogItem> blogItems){
        adapter = new BlogItemListAdapter(this,blogItems);
        lvBlogItemList.setAdapter(adapter);
    }

    public Blog getBlog(){
        return blog;
    }

    @Override
    public void after(String message) {
        if("200".equals(message)) {
            Toast.makeText(this, "성공하였습니다.", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    View.OnClickListener btnUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = rgBlogListCategory.getCheckedRadioButtonId();
            RadioButton rb = (RadioButton)findViewById(viewId);
            boolean[] arChecked = adapter.getArIsChecked();
            boolean[] arIsSucceed = adapter.getArIsSucceed();
            ArrayList<BlogItem> arBlogItems = adapter.getBlogItems();
            for(int i = 0; i<arChecked.length; i++){
                if(arChecked[i]){
                    BlogItem blogItem = arBlogItems.get(i);
                    blogItem.setCategoryId(rb.getContentDescription().toString());
                    blogItem.setVisibility("3");
                    arIsSucceed[i] = true;
                    new UpdateTistory(BlogListActivity.this, blog, blogItem).execute();
                }
            }
        }
    };

    View.OnClickListener btnDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean[] arChecked = adapter.getArIsChecked();
            ArrayList<BlogItem> arBlogItems = adapter.getBlogItems();
            for(int i = 0; i<arChecked.length; i++){
                if(arChecked[i]){
                    BlogItem blogItem = arBlogItems.get(i);
                    blogItem.setContent("");
                    blogItem.setTitle("삭제_"+blogItem.getTitle());
                    blogItem.setVisibility("0");
                    new UpdateTistory(BlogListActivity.this, blog, blogItem).execute();
                }
            }
        }
    };

    View.OnClickListener cbCheckAllClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.setAllChecked(((CheckBox)v).isChecked());
            adapter.notifyDataSetChanged();
        }
    };

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("myTag",id+" "+position);
        }
    };
}
