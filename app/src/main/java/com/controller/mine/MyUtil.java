package com.controller.mine;

import android.content.SharedPreferences;

import java.util.HashMap;

public class MyUtil {
    public static String htmlCode = "";
    public static String currnentUrl = "";
    public static HashMap<String,String> skdog87AtNaverDotCom = new HashMap<String,String>();
    public static HashMap<String,String> hwangcheol1240AtGmailDotCom = new HashMap<String,String>();
    public static HashMap<String,String> hwangcheol1241AtGmailDotCom = new HashMap<String,String>();

    public static void initData(){
        skdog87AtNaverDotCom.put("access_token","d776696567a6c6617afc47cacfc0681b_d5276a26c6d216ed5798d06c72407fec");
        skdog87AtNaverDotCom.put("blogName","tech-tech");
        hwangcheol1240AtGmailDotCom.put("access_token","2fd34213c527414eb9e5a002837fdf21_f4458c4a94377b83ce029fab415eb0e0");
        hwangcheol1240AtGmailDotCom.put("blogName","70th");
        hwangcheol1241AtGmailDotCom.put("access_token","d5e2f993ccf9b94d6bb0ebf009510c3e_8e9373de78f375297d79887a43f4472e");
        hwangcheol1241AtGmailDotCom.put("blogName","merl");

    }

}
