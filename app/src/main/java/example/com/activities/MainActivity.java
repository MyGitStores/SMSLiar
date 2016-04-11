package example.com.activities;

import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

import example.com.services.SendMessage;
import example.com.services.SendService;

public class MainActivity extends AppCompatActivity {
    private Button btn_main_send;
    private ImageButton ibtn_main_add;
    private EditText et_main_phone,et_main_content;
    private TimePicker et_main_time;
    private String phone,content;
    private int hour,minutes;
    private boolean now=true;
    private Intent sendservice;
    private SendMessage sm;
//    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initview();

        initlistener();
    }

    private void initlistener() {
        sendservice=new Intent();
        sendservice.setClass(MainActivity.this, SendService.class);
        startService(sendservice);
        final MyServiceConnection mc=new MyServiceConnection();
        bindService(sendservice, mc,1);
        btn_main_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone=et_main_phone.getText().toString().trim();
                content=et_main_content.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(getApplicationContext(),"电话不能为空 傻X",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(content)){
                    Toast.makeText(MainActivity.this, "内容都不写 怎么装B", Toast.LENGTH_SHORT).show();
                }else{
                    if (sm!=null)
                    if (now){
                        sm.sendmessages(now,null,phone,content);
                    }else{
                        btn_main_send.setText(R.string.sendnow);
                        Date d=new Date();
                        d.setHours(hour);
                        d.setMinutes(minutes);
                        sm.sendmessages(now,d,phone,content);

                        Toast.makeText(MainActivity.this, "设置完毕 坐等装B", Toast.LENGTH_SHORT).show();
                    }
                    unbindService(mc);
                    MainActivity.this.finish();
                }
            }

        });
        et_main_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd=new TimePickerDialog(MainActivity.this, new MyPickerDialog(),0,0,true);
                tpd.show();
            }
        });
    }


    private void initview() {
        setContentView(R.layout.activity_main);
        btn_main_send= (Button)findViewById(R.id.btn_main_send);
        ibtn_main_add= (ImageButton)findViewById(R.id.ibtn_main_add);
        et_main_phone= (EditText)findViewById(R.id.et_main_phone);
        et_main_content= (EditText)findViewById(R.id.et_main_content);
        et_main_time= (TimePicker)findViewById(R.id.et_main_time);
        et_main_time.setIs24HourView(true);
        et_main_time.setFocusable(true);
    }

    class MyServiceConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
                sm= (SendMessage)service;
            System.out.println("getcon");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
    class MyPickerDialog implements TimePickerDialog.OnTimeSetListener{
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            btn_main_send.setText(R.string.sendlater);
            now=false;
            hour=hourOfDay;
            minutes=minute;
            et_main_time.setCurrentHour(hourOfDay);
            et_main_time.setCurrentMinute(minute);
        }
    }
}
