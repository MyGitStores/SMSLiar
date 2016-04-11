package example.com.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import example.com.activities.R;

/**
 * Created by GEEKER on 2016/4/10.
 */
public class SendService extends Service {
    private String phone,content;
    private NotificationManager manager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("binded");
        return new MyIBinder();
    }

    private void sendmessage(boolean now, Date d,String phone,String content) {
        this.phone=phone;
        this.content=content;
        if(!now){
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                send();
            }
        },d);}else{send();}

    }
    private void send(){
        ContentResolver resolver=getContentResolver();
        Uri uri=Uri.parse("content://sms");
        ContentValues values=new ContentValues();
        values.put("address",phone);
        values.put("type",1);
        values.put("date",System.currentTimeMillis());
        values.put("body",content);
        resolver.insert(uri,values);
        notifaction();
    }

    private void notifaction() {
        Notification.Builder builder=new Notification.Builder(getApplicationContext());
        Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/mms");
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
        Bitmap bmp=null;
        Drawable dab=null;
        try {
            dab=getPackageManager().getApplicationIcon("com.android.mms");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(phone)
                .setContentText(content)
                .setSmallIcon(R.mipmap.sms)
                .setAutoCancel(true)
                .setContentIntent(pi);
        if (dab==null){
            bmp=BitmapFactory.decodeResource(getResources(),R.mipmap.sms);
            builder.setLargeIcon(bmp);
        }else{
            builder.setLargeIcon(((BitmapDrawable)dab).getBitmap());
        }
        Notification notifact=builder.build();

        manager= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,notifact);
    }

    private class MyIBinder extends Binder implements SendMessage{
        @Override
        public void sendmessages(boolean now, Date d, String phone, String content) {
            sendmessage(now,d,phone,content);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("unbined");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
    }
}
