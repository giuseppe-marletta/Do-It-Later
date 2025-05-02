package com.example.DoItLater;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Date;

public class NotifierAlarmSms extends BroadcastReceiver {

    //private AppDatabase appDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {

        Boolean conferma = intent.getBooleanExtra("conferma",false);

        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent intent1 = new Intent(context,MainPageSms.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent3;
        intent3 = new Intent(context,ConfermaActivity.class);
        intent3.putExtra("number",intent.getStringExtra("number"));
        intent3.putExtra("message",intent.getStringExtra("Message"));
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainPageSms.class);
        taskStackBuilder.addNextIntent(intent1);

        if(conferma) {
            taskStackBuilder.addParentStack(ConfermaActivity.class);
            taskStackBuilder.addNextIntent(intent3);
        }



        PendingIntent intent2 = taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("my_channel_01","hello", NotificationManager.IMPORTANCE_HIGH);
        }

        String messagee;
        if(intent.getStringExtra("Message") != null) {
            messagee = intent.getStringExtra("Message");
            Log.e("MSG: ", messagee + "");
        }
        else
        {
            messagee = NotifierGeoSms.messaggioNotifierGeoSms;
            Log.e("MSG: ", messagee + "");
        }

        String numberr;
        if(intent.getStringExtra("number") != null) {
            numberr = intent.getStringExtra("number");
            Log.e("Numero: ", numberr + "");
        }
        else
        {
            numberr = NotifierGeoSms.messaggioNotifierGeoSms;
            Log.e("Numero: ", numberr + "");
        }

        Notification notification;
        SmsManager mySmsManager;

        if(!conferma) {
            notification = builder.setContentTitle("SMS")
                    .setContentText("sms send: " + messagee).setAutoCancel(true)
                    .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setContentIntent(intent2)
                    .setChannelId("my_channel_01")
                    .build();

            mySmsManager = SmsManager.getDefault();
            mySmsManager.sendTextMessage(numberr, null, messagee, null, null);
        } else {
            PendingIntent intent4 = taskStackBuilder.getPendingIntent(2,PendingIntent.FLAG_UPDATE_CURRENT);
            notification = builder.setContentTitle("SMS")
                    .setContentText("sms send: " + messagee + "\n clicca per mandare il messaggio").setAutoCancel(true)
                    .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(intent4)
                    .setChannelId("my_channel_01")
                    .build();

        }




        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notification);

    }
}
