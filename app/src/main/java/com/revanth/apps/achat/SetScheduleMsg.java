package com.revanth.apps.achat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

public class SetScheduleMsg extends AppCompatActivity {

    private TimePicker mTimePicker;
    private Button mSetMessageBtn;
    private TextInputEditText mMessageEditText;
    private String mReceiverId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_schedule_msg);
        mTimePicker=(TimePicker) findViewById(R.id.timePicker);
        mSetMessageBtn=(Button)findViewById(R.id.set_time_message_btn);
        mMessageEditText=(TextInputEditText)findViewById(R.id.schedule_msg_inputLayout);
        mReceiverId=getIntent().getStringExtra("receiverId");

        if(mSetMessageBtn==null)
        {
            Log.d("revaa","Button is null");
        }

        mSetMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=mMessageEditText.getText().toString();
                Calendar calendar= Calendar.getInstance();
                if(Build.VERSION.SDK_INT>=23)
                    calendar.set(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            mTimePicker.getHour(),
                            mTimePicker.getMinute(),
                            0
                    );
                else
                    calendar.set(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            mTimePicker.getCurrentHour(),
                            mTimePicker.getCurrentMinute(),
                            0
                    );
                setAlarm(calendar.getTimeInMillis(),message);
              /*  final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("Message Scheduled");
                builder.show();*/
                mMessageEditText.setText(" ");
            }
        });
    }
    private void setAlarm(long timeInMillis,String message)
    {
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(this,AlarmActionClass.class);
        intent.putExtra("receiverId",mReceiverId);
        intent.putExtra("message",message);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.set(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent);
    }
}
