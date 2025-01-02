package com.example.signinapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Description: this class makes a notification. If the notification is clicked, the user is guided
 * to LocationSender Activity, where the location of the user is sent automatically
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context,LocationSenderActivity.class);
        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("MainActivity","onClick executed");
            Notification notif=new Notification.Builder(context,"@me")
                    .setContentTitle("message")
                    .setContentIntent(PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_IMMUTABLE))
                    .setContentText("time to sign in")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.baseline_notifications_24)
                    .build();
            //broadcast the notification to the channel
            manager.notify(1,notif);
        }
    }
}
