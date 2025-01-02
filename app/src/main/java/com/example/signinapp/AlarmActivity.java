package com.example.signinapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.signinapp.databinding.ActivityAlarmBinding;

import java.util.Calendar;

import module.Utils;

/**
 * Description: this class provides a view for alarm setting. The user can set alarm and when the
 * time is up, it jumps to the AlarmReceiver broadcast.
 */

public class AlarmActivity extends AppCompatActivity {
    private ActivityAlarmBinding binding;
    private static final String TAG="CourseWork: Alarm";

    /**
     * Description: this method gets the time the user set. The returned value is deliberately designed
     * so that manager.set() can function correctly
     * @return the formatted timeInMillis
     */
    public Long getAlarmTime(){
        Calendar calendar=Calendar.getInstance();//obtain Calendar object
        //alarm setting, and get the time
        long timeBegin=calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, binding.time.getHour());//show the number of hour
        calendar.set(Calendar.MINUTE,binding.time.getMinute());
        calendar.set(Calendar.SECOND,0);//set 'second' attribute of the alarm
        long timeDif=calendar.getTimeInMillis()-timeBegin;
        /*if the alarm set is the current time, the time difference (from now to when the
        alarm is set) is usually negative, which is not allowed*/
        if(timeDif<0){
            Toast.makeText(AlarmActivity.this,"alarm set is invalid!",Toast.LENGTH_SHORT).show();
            return null;
        }
        return SystemClock.elapsedRealtime()+timeDif;
    }

    /**
     * Description: this method set the alarm. When the time is up, the pendingIntent leads the user
     * to AlarmReceiver class, where a notification is made and broadcasted
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding= ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.time.setIs24HourView(true);
        binding.alarmBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Long time= getAlarmTime();
                if(time==null){
                    return;
                }
                Intent i = new Intent(AlarmActivity.this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(AlarmActivity.this, 0, i, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pi);
                Toast.makeText(AlarmActivity.this,"alarm set successful",Toast.LENGTH_SHORT).show();
            }
        });
        Utils.backLogic(binding.back,AlarmActivity.this,MainActivity.class);//when click the 'back' arrow
    }

    /**
     * Description: at this stage, the pendingIntent no longer binds with AlarmManager
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        //close the manager when the service ends
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_IMMUTABLE);
        manager.cancel(pi);
    }
}
