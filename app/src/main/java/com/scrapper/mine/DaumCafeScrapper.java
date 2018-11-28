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

public class DaumCafeScrapper implements Scrapper{
    private final int itemCount = 90;
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
        hmDaumCafeBlog.put("tech-tech",new Blog("tech-tech","d776696567a6c6617afc47cacfc0681b_d5276a26c6d216ed5798d06c72407fec"));
        hmDaumCafeBlog.put("70th",new Blog("70th","2fd34213c527414eb9e5a002837fdf21_f4458c4a94377b83ce029fab415eb0e0"));
        hmDaumCafeBlog.put("merl",new Blog("merl","d5e2f993ccf9b94d6bb0ebf009510c3e_8e9373de78f375297d79887a43f4472e"));

    }

    public HashMap<String,Blog> getBlogs() {
        return hmDaumCafeBlog;
    }

    public Blog getCurBlog(String name){
        return hmDaumCafeBlog.get(name);
    }

    public Blog getCurBlog(int idx){
        int targetId = idx%hmDaumCafeBlog.size();
        int curCnt = 0;
        Iterator<String> it = hmDaumCafeBlog.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            if(curCnt++ == targetId){
                return hmDaumCafeBlog.get(key);
            }
        }
        return null;
    }

    public String[] getTodayDetailUrls() throws Exception{
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

            String prevDay = year +"."+ month +"."+ day;

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
    }

    public String[] getItemHtml(String cafeUrl) throws Exception{
        Document doc = Jsoup.connect(cafeUrl).header("User-Agent","Mozilla/5.0").get();
        Elements pTags = doc.select("p");
        for(Element p : pTags){
            if(p.text().contains("출처")){
                p.remove();
            }
        }
        String title = doc.select(".tit_subject").html()
                .replaceAll("\\[[가-힣]*\\]", "")
                .replaceAll("<(.*)>", "");
        Elements images = doc.select("#article img");
        for(Element image :  images){
            if(!validImage(image.attr("src"))){
                image.remove();
            }
        }

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

    /* return false if the img src is in the blacklist */
    public static boolean validImage(String src){
        String[] arBlackList = {
                "http://cfile258.uf.daum.net/image/214E124858B57D721854CE"
                ,"http://cfile247.uf.daum.net/image/2220A94858B57D74109448"
                ,"http://cfile294.uf.daum.net/image/1425B93C5133EC9F1BEA2D"
                ,"http://cfile268.uf.daum.net/image/25639847527D29AC165E60"
                ,"http://t1.daumcdn.net/cafeattach/mEr9/2e145d1682a2229fe546436aad9e3d1f4a973bc6"
                ,"http://t1.daumcdn.net/cafeattach/mEr9/0a6989bfd1cac722693b24b4c393734d4ba3bf89"
                ,"http://cfile265.uf.daum.net/image/996BF54A5A9F2D802FB722"
                ,"http://cfile290.uf.daum.net/image/998DD24D5BF6406B109B30"
                ,"http://t1.daumcdn.net/cafeattach/Uzlo/066aad1a928771f5c1cecdeba8e613eded7d799f"
                ,"http://t1.daumcdn.net/cafeattach/Uzlo/7f527d51170d81b4bbaade78699d5a239b90f990"
                ,"http://t1.daumcdn.net/cafeattach/Uzlo/1f73ee3d35f7b6fddfd6bd2366bae796acb10cfc"
                ,"http://t1.daumcdn.net/cafeattach/Uzlo/df3dcc5625577ddb7cf8864cd4587eb61fe8ae35"
                ,"http://t1.daumcdn.net/cafeattach/Uzlo/8b28fcba8b5eaea71c5cccb87d27e3adfe6cd31c"
                ,"http://t1.daumcdn.net/cafeattach/Uzlo/957a18fd6bd6be0c6c91677372150830d9b09f5f"
                ,"http://cfile242.uf.daum.net/image/99E91A505BB2E64F15D7B1"
                ,"http://cfile279.uf.daum.net/image/2176E04958F1FBE032CB10"
                ,"http://cfile262.uf.daum.net/image/99597C4A5B05BBB818DB3C"
                ,"http://cfile245.uf.daum.net/image/241DE94852B9AFAB27A61E"
        };
        for(String black : arBlackList){
            if(src.contains(black)) return false;
        }
        return true;
    }

    /* return false if the title has naughty text */
    public static String eraseNaughtyText(String text){
        String[] naughtyText = {
                "* 업로더 유의사항 : 정치, 성혐오, 광고, 분쟁성, 어그로 게시물작성 금지"
                ,"* 댓글러 유의사항 : 정치, 성혐오, 분쟁성, 어그로 댓글작성 금지"
        };
        for(String naughty : naughtyText){
            if(text.contains(naughty)) text = text.replaceAll(naughty,"");
        }
        return text;
    }

}

