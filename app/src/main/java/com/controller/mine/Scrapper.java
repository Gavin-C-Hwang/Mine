package com.controller.mine;

import android.content.SharedPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

public class Scrapper {

    private String[][] arBoardUrlList = {
            {"http://cafe.daum.net/_c21_/bbs_list?grpid=aVeZ&fldid=6yIR&listnum=200"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=mEr9&fldid=FGFP&listnum=100"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=Uzlo&fldid=LnOm&listnum=1500"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=1IHuH&fldid=ReHf&listnum=1500"
                    ,"http://cafe.daum.net/_c21_/bbs_list?grpid=EK&fldid=7n&listnum=100"}
            ,{
            "http://m.cafe.daum.net/ok1221/6yIR"
            ,"http://m.cafe.daum.net/dotax/FGFP"
            ,"http://m.cafe.daum.net/ssaumjil/LnOm"
            ,"http://m.cafe.daum.net/subdued20club/ReHf"
            ,"http://m.cafe.daum.net/ilovenba/7n"
    }
    };

    public String[][] getCafeUrls(){
        return arBoardUrlList;
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
                if(prevDay.equals(date)){
                    arItem.add(new Item(itemUrl, count));
                }
            }

        }

        Collections.sort(arItem, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if(o1.count < o2.count) return 1;
                else if(o1.count == o2.count) return 0;
                else return -1;
            }
        });

        String[] result = new String[90];
        for(int j = 0; j<result.length; j++){
            result[j] = arItem.get(j).url;
        }
        return result;
    }


    public String[] getCafeBoardDetailHtml(String cafeUrl) throws Exception{
        Document doc = Jsoup.connect(cafeUrl).header("User-Agent","Mozilla/5.0").get();
        Elements pTags = doc.select("p");
        for(Element p : pTags){
            if(p.text().contains("출처")){
                p.remove();
            }
        }
        String title = doc.select(".tit_subject").html().replaceAll("\\[[가-힣]*\\]", "");
        String content = doc.select("#article").html();
        return new String[]{title,content};
    }

}

class Item{
    String url="";
    int count = 0;
    public Item(String url, int count) {
        this.url = url;
        this.count = count;
    }
}

