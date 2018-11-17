package com.controller.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartService = (Button)findViewById(R.id.btnStartService);
        Button btnEndService = (Button)findViewById(R.id.btnEndService);
        Button btnResetPref = (Button)findViewById(R.id.btnResetPref);
        btnStartService.setOnClickListener(btnStartServiceListener);
        btnEndService.setOnClickListener(btnEndServiceListener);
        btnResetPref.setOnClickListener(btnResetPrefListener);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        TextView tv2 = (TextView)findViewById(R.id.textView2);
        TextView tv3 = (TextView)findViewById(R.id.textView3);
        TextView tv4 = (TextView)findViewById(R.id.textView4);
        tv2.setText("tech-tech.tistory.com : "+pref.getInt("skdog87AtNaverDotCom",0)+"건");
        tv3.setText("70th.tistory.com : "+pref.getInt("hwangcheol1240AtGmailDotCom",0)+"건");
        tv4.setText("merl.tistory.com : "+pref.getInt("hwangcheol1241AtGmailDotCom",0)+"건");

        TextView tvBlackImage = (TextView)findViewById(R.id.blackImage);
        Set<String> hs = pref.getStringSet("blackImage",new HashSet<String>());
        Iterator<String> it = hs.iterator();
        String blackImageList = "";
        while(it.hasNext()){
            blackImageList = blackImageList +", "+ it.next();
        }
        tvBlackImage.setText(blackImageList);

        TextView tvSendResult = (TextView)findViewById(R.id.sendResult);
        Set<String> hs2 = pref.getStringSet("sendResult",new HashSet<String>());
        Iterator<String> it2 = hs2.iterator();
        String sendResultList = "";
        while(it2.hasNext()){
            sendResultList = sendResultList +", "+ it2.next();
        }
        tvSendResult.setText(sendResultList);


    }

    View.OnClickListener btnStartServiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),WatcherService.class);
            startService(intent);
        }
    };

    View.OnClickListener btnEndServiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),WatcherService.class);
            stopService(intent);
        }
    };


    View.OnClickListener btnResetPrefListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();// editor에 put 하기
            editor.putInt("skdog87AtNaverDotCom",0);
            editor.putInt("hwangcheol1240AtGmailDotCom",0);
            editor.putInt("hwangcheol1241AtGmailDotCom",0);
            editor.putStringSet("sendResult",new HashSet<String>());
            editor.commit();
        }
    };





}
