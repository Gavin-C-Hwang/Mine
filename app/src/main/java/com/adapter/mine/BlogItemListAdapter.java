package com.adapter.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.entity.mine.BlogItem;
import com.controller.mine.BlogDetailActivity;
import com.controller.mine.BlogListActivity;
import com.controller.mine.MainActivity;
import com.controller.mine.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class BlogItemListAdapter extends BaseAdapter {
    private ArrayList<BlogItem> blogItems = new ArrayList<>();
    private BlogListActivity atv;
    private boolean[] arIsChecked;  //체크박스들 체크여부
    private boolean[] arIsSucceed;    //서버 전송 성공 여부

    public BlogItemListAdapter(BlogListActivity atv, ArrayList<BlogItem> blogItems) {
        this.atv = atv;
        this.blogItems = blogItems;
        this.arIsChecked = new boolean[blogItems.size()];
        this.arIsSucceed = new boolean[blogItems.size()];
    }

    public void setAllChecked(boolean ischeked) {
        int tempSize = arIsChecked.length;
        for(int a=0 ; a<tempSize ; a++){
            arIsChecked[a] = ischeked;
        }
    }

    @Override
    public int getCount() {
        return blogItems.size();
    }

    @Override
    public Object getItem(int position) {
        return blogItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        final int pos = position;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_bloglist,parent,false);
        }

        CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.bloglist_check);
        checkBox.setChecked(arIsChecked[pos]);
        checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                arIsChecked[pos] = ((CheckBox)v).isChecked();
            }
        });

        TextView title = (TextView)convertView.findViewById(R.id.bloglist_title);
        ImageView firstImg = (ImageView)convertView.findViewById(R.id.bloglist_firstImg);
        ImageView lastImg = (ImageView)convertView.findViewById(R.id.bloglist_lastImg);
        TextView visibility = (TextView)convertView.findViewById(R.id.bloglist_visiblity);
        TextView categoryName = (TextView)convertView.findViewById(R.id.bloglist_category_name);


        final BlogItem blogItem = blogItems.get(position);
        title.setText(blogItem.getTitle());
        firstImg.setImageBitmap(blogItem.getFirstImg());
        lastImg.setImageBitmap(blogItem.getLastImg());
        visibility.setText(blogItem.getVisibility());
        categoryName.setText(blogItem.getCategoryName());

        if(arIsSucceed[pos]) convertView.setBackgroundColor(Color.rgb(70,70,70));

        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(atv, BlogDetailActivity.class);
                it.putExtra("blog",atv.getBlog());
                it.putExtra("id",blogItem.getId());
                it.putExtra("title",blogItem.getTitle());
                it.putExtra("content",blogItem.getContent());
                it.putExtra("categoryId",blogItem.getCategoryId());
                atv.startActivity(it);
            }
        });

        return convertView;
    }

    public ArrayList<BlogItem> getBlogItems() {
        return blogItems;
    }

    public boolean[] getArIsChecked() {
        return arIsChecked;
    }

    public boolean[] getArIsSucceed() {
        return arIsSucceed;
    }
}
