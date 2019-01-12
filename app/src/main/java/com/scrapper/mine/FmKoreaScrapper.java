package com.scrapper.mine;

import com.com.entity.mine.Blog;
import com.com.entity.mine.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

public class FmKoreaScrapper implements Scrapper {
    private final int itemCount = 75;
    private String urlFormat = "https://www.fmkorea.com/index.php?mid=humor&sort_index=pop&order_type=desc&listStyle=list&page=%d";

    private HashMap<String,Blog> hmFmBlog = new HashMap<String,Blog>();

    public FmKoreaScrapper() {
        //hmFmBlog.put("5789",new Blog("5789","2af41212b92adcc20b2444740df1ad60_48cfed51aa0231c00b8691c8d4f4e878"));
        //hmFmBlog.put("everyissues",new Blog("everyissues","2af41212b92adcc20b2444740df1ad60_48cfed51aa0231c00b8691c8d4f4e878"));
        //hmFmBlog.put("ace4",new Blog("ace4","2af41212b92adcc20b2444740df1ad60_48cfed51aa0231c00b8691c8d4f4e878"));
        //hmFmBlog.put("funnybest",new Blog("funnybest","2af41212b92adcc20b2444740df1ad60_48cfed51aa0231c00b8691c8d4f4e878"));
        //hmFmBlog.put("7world7",new Blog("7world7","2af41212b92adcc20b2444740df1ad60_48cfed51aa0231c00b8691c8d4f4e878"));

    }

    public HashMap<String,Blog> getBlogs() {
        return hmFmBlog;
    }

    public Blog getCurBlog(String name){
        return hmFmBlog.get(name);
    }

    public Blog getCurBlog(int idx){
        int targetId = idx%hmFmBlog.size();
        int curCnt = 0;
        Iterator<String> it = hmFmBlog.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            if(curCnt++ == targetId){
                return hmFmBlog.get(key);
            }
        }
        return null;
    }

    /* scrape todays url array */
    public String[] getTodayDetailUrls(){

        try{
        ArrayList<Item> arItem = new ArrayList<>();

        for(int i = 28; i<=41; i++){
            String curUrl = String.format(urlFormat,i);
            System.out.println(curUrl);
            Document doc = Jsoup.connect(curUrl).header("User-Agent","Mozilla/5.0").timeout(0).get();

            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DATE, -2);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String prevDay = year +"."+ month +"."+ day;

            Elements items = doc.select("tbody tr");
            for(Element item : items){
                String subject = item.select("td.title a").eq(0).text();
                String date = item.select("td.time").text();
                int count = Integer.parseInt(item.select("td.m_no").eq(0).text().replaceAll(" ",""));
                String itemUrl = item.select("td.title a").eq(0).attr("href");
                if(prevDay.equals(date) && validTitle(subject)){
                    arItem.add(new Item(itemUrl, count));
                }
            }

            Collections.sort(arItem, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if(o1.getCount() < o2.getCount()) return 1;
                    else if(o1.getCount() == o2.getCount()) return 0;
                    else return -1;
                }
            });

        }
        String[] result = new String[itemCount];
        for(int j = 0; j<result.length; j++){
            result[j] = arItem.get(j).getUrl();
        }
        return result;
        }catch(Exception er){
            return new String[0];
        }
    }

    /* get detail item html code from url */
    public String[] getItemHtml(String url) throws Exception{
        url = "https://www.fmkorea.com"+url;
        Document doc = Jsoup.connect(url).header("User-Agent","Mozilla/5.0").get();
        Elements imgs = doc.select("img");
        for(Element img : imgs){
            if( "".equals( img.attr("data-original") )){
                img.attr("src",img.attr("src").replaceAll("//image","http://ext"));
            }else{
                img.attr("src",img.attr("data-original").replaceAll("//image","http://ext"));
                img.attr("data-original","");
            }
        }
        String title = doc.select("span.np_18px_span").html();
        String content = doc.select("article").html();
        return new String[]{title,content};
    }

    /* return false if the title has naughty text */
    public boolean validTitle(String title){
        String[] arNaughty = {
                "ㅇㅎ","겨드랑이","ㅎㅂ","후방","어우야","ㅓㅜㅑ","ㅈㅈ","ㅂㅈ","ㅅㄱ","가슴","슴가"
        };
        for(String naughty : arNaughty){
            if(title.contains(naughty)) return false;
        }
        return true;
    }


}
