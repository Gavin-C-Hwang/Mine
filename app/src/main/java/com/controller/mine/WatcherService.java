package com.controller.mine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WatcherService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new MyAsyncThread().execute("","","");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class MyAsyncThread extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            int a = 0;
            while(a<100){
                if(isNew("https://www.tistory.com/invitation")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        sendNotification();
                    }
                }
                try {
                    Thread.sleep(300000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a++;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void sendNotification(){
            NotificationManager m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification noti = new Notification.Builder(getApplicationContext())
                    .setTicker("티스토리 초대장 알림")
                    .setContentTitle("티스토리 초대장 알림")
                    .setContentText("https://www.tistory.com/invitation")
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setWhen(System.currentTimeMillis())
                    .build();
            m.notify(1, noti);
        }

        public String prevTitle = "";
        private boolean isNew(String reqUrl){
            try{
                Document doc = Jsoup.connect(reqUrl).header("User-Agent","Mozilla/5.0").get();
                String title = doc.select("#invitationPostWrap > li").html();

                if(prevTitle.equals(title)){
                    return false;
                }else{
                    prevTitle = title;
                    return true;
                }
            }catch(Exception err){
                err.printStackTrace();
                return false;
            }
        }
    }
}
