package com.scrapper.mine;

import android.util.Log;

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

public class DaumCafeScrapper implements Scrapper{
    private final int itemCount = 195;
    private String[][] arBoardUrlList = {
            {"http://cafe.daum.net/_c21_/bbs_list?grpid=aVeZ&fldid=6yIR&listnum=200"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=mEr9&fldid=FGFP&listnum=100"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=Uzlo&fldid=LnOm&listnum=1500"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=1IHuH&fldid=ReHf&listnum=1500"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=EK&fldid=7n&listnum=100"
            }
            ,{
            "http://m.cafe.daum.net/ok1221/6yIR"
            ,"http://m.cafe.daum.net/dotax/FGFP"
            ,"http://m.cafe.daum.net/ssaumjil/LnOm"
            ,"http://m.cafe.daum.net/subdued20club/ReHf"
            ,"http://m.cafe.daum.net/ilovenba/7n"}
    };

    private HashMap<String,Blog> hmDaumCafeBlog = new HashMap<String,Blog>();

    public DaumCafeScrapper() {
        hmDaumCafeBlog.put("tech-tech",new Blog("tech-tech","d776696567a6c6617afc47cacfc0681b_d5276a26c6d216ed5798d06c72407fec","720362",30));
        hmDaumCafeBlog.put("70th",new Blog("70th","2fd34213c527414eb9e5a002837fdf21_f4458c4a94377b83ce029fab415eb0e0","303903",30));
        hmDaumCafeBlog.put("merl",new Blog("merl","d5e2f993ccf9b94d6bb0ebf009510c3e_8e9373de78f375297d79887a43f4472e","806588",30));
        hmDaumCafeBlog.put("invit",new Blog("invit","a210ad5ddc08105f16fd874443af23c7_e01d52d9067750a09525791871d4ecf7","823504",30));
        hmDaumCafeBlog.put("kingjun",new Blog("kingjun","81ed7d2ff7ef96f1702bdca4908a5c70_93c9eb2acbe294df6995752f201d47d2","772912",15));
        hmDaumCafeBlog.put("swking",new Blog("swking","68f971bbc3c3b85303a9406d1e0e0286_953148f235f1d0a1f5a01d8e5354a055","793557",15));
        hmDaumCafeBlog.put("hajulee",new Blog("hajulee","5f12037337ab4303cc23ad4857adb518_ca41fef65c93280b0470d63ea2d64394","826466",15));
        hmDaumCafeBlog.put("doyoonman",new Blog("doyoonman","c0be1052c9df44e7796cbae6ffb26982_79a5111d5201739b561e39e1d8590208","810870",15));
        hmDaumCafeBlog.put("jyopark",new Blog("jyopark","b8db3657dc331064794b40c147a6f523_ef03c18dc91ff5afdc15ec420fc02a55","767955",15));

    }

    public HashMap<String,Blog> getBlogs() {
        return hmDaumCafeBlog;
    }

    public Blog getCurBlog(String name){
        return hmDaumCafeBlog.get(name);
    }

    public Blog getCurBlog(int idx){
        Iterator<String> it = hmDaumCafeBlog.keySet().iterator();
        Blog initBlog = hmDaumCafeBlog.get(it.next());
        while(it.hasNext()){
            String key = it.next();
            Blog curBlog = hmDaumCafeBlog.get(key);
            if(curBlog.getAvailCnt() > 0 && initBlog.getAvailCnt() < curBlog.getAvailCnt()){
                initBlog = curBlog;
            }
        }
        Log.d("myTag", initBlog.getBlogName() +" : "+  initBlog.getAvailCnt());
        initBlog.setAvailCnt(initBlog.getAvailCnt()-1);
        return initBlog;
    }

    public String[] getTodayDetailUrls() {
        try{

        ArrayList<Item> arItem = new ArrayList<>();
        for(int i = 0; i<arBoardUrlList[0].length; i++){
            Document doc = null;
            try{
                doc = Jsoup.connect(arBoardUrlList[0][i]).header("User-Agent","Mozilla/5.0").timeout(0).get();
            }catch(Exception err){
                err.printStackTrace();
            }
            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DATE, -1);
            int year = cal.get(Calendar.YEAR)-2000;
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String prevDay = year +"."+ (month<10?"0"+month:month) +"."+ (day<10?"0"+day:day);

            Elements items = doc.select("table.bbsList > tbody > tr");
            for(Element item : items){
                String num = item.select("td.num").text();
                String subject = item.select("td.subject").text();
                String date = item.select("td.date").text();
                int count = "".equals(item.select("td.count").text())?0:Integer.parseInt(item.select("td.count").text());
                String itemUrl = arBoardUrlList[1][i]+"/"+num+"?svc=daumapp";
                if(prevDay.equals(date) && validTitle(subject)){
                    arItem.add(new Item(itemUrl, count));
                }
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

        String[] result = new String[itemCount];
        for(int j = 0; j<result.length; j++){
            result[j] = arItem.get(j).getUrl();
        }
        return result;

        }catch(Exception er){
            return new String[0];
        }
    }

    public String[] getItemHtml(String cafeUrl) throws Exception{
        Document doc = Jsoup.connect(cafeUrl).header("User-Agent","Mozilla/5.0").get();
        Elements pTags = doc.select("p");
        for(Element p : pTags){
            if(p.text().contains("출처")
                    || "".equals(p.html())
                    || "<br>".equals(p.html())
                    || "<span style=\"font-size: 12pt;\"><br></span>".equals(p.html())){
                p.remove();
            }
        }

        String title = doc.select(".tit_subject").text()
                .replaceAll("\\[[가-힣]*\\]", "")
                .replaceAll("<(.*)>", "");

        String content = eraseNaughtyText( doc.select("#article").html() );
        return new String[]{title,content};
    }

    /* return false if the title has naughty text */
    public static boolean validTitle(String title){
        String[] arNaughty = {
                "ㅇㅎ","겨드랑이","ㅎㅂ","후방","어우야","ㅓㅜㅑ","ㅈㅈ","ㅂㅈ","ㅅㄱ","가슴","슴가"
        };
        for(String naughty : arNaughty){
            if(title.contains(naughty)) return false;
        }
        return true;
    }

    /* return false if the title has naughty text */
    public static String eraseNaughtyText(String text){
        String[] naughtyText = {
                "* 업로더 유의사항 : 정치, 성혐오, 광고, 분쟁성, 어그로 게시물작성 금지"
                ,"* 댓글러 유의사항 : 정치, 성혐오, 분쟁성, 어그로 댓글작성 금지"
                ,"2011.6.20이후 적용 자세한사항은 공지확인하시라예"
        };
        for(String naughty : naughtyText){
            if(text.contains(naughty)) text = text.replaceAll(naughty,"");
        }
        return text;
    }

}

