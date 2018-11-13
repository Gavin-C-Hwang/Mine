package com.controller.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
            editor.commit();
        }
    };





}
