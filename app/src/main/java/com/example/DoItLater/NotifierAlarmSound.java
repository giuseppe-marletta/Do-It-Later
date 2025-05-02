package com.example.DoItLater;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Date;

public class NotifierAlarmSound extends BroadcastReceiver {

    //private AppDatabase appDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {


        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent intent1 = new Intent(context,MainPageSound.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainPageSound.class);
        taskStackBuilder.addNextIntent(intent1);



        PendingIntent intent2 = taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("my_channel_01","hello", NotificationManager.IMPORTANCE_HIGH);
        }


        Boolean silenziosoo;
        if((intent.getBooleanExtra("Silenzioso",false) == true) || (( intent.getBooleanExtra("Vibrazione",false) == true) || (intent.getBooleanExtra("Suoneria",false) == true )))
        {
            silenziosoo = intent.getBooleanExtra("Silenzioso",false);
            Log.e("CHECK: ", silenziosoo + " ");
        }
        else
        {
            silenziosoo = NotifierGeoSound.checkSilenziosoNotifierGeoSound;
            Log.e("CHECK: ", silenziosoo + " ");
        }

        Boolean vibrazionee;
        if((intent.getBooleanExtra("Vibrazione",false) == true) || (( intent.getBooleanExtra("Silenzioso",false) == true) || (intent.getBooleanExtra("Suoneria",false) == true )))
        {
            vibrazionee = intent.getBooleanExtra("Vibrazione",false);
            Log.e("CHECK: ", vibrazionee + " ");
        }
        else
        {
            vibrazionee = NotifierGeoSound.checkVibrazioneNotifierGeoSound;
            Log.e("CHECK: ", vibrazionee + " ");
        }

        Boolean suoneriaa;
        if((intent.getBooleanExtra("Suoneria",false) == true) || (( intent.getBooleanExtra("Vibrazione",false) == true) || (intent.getBooleanExtra("Silenzioso",false) == true )))
        {
            suoneriaa = intent.getBooleanExtra("Suoneria",false);
            Log.e("CHECK: ", suoneriaa + " ");
        }
        else
        {
            suoneriaa = NotifierGeoSound.checkSuoneriaNotifierGeoSound;
            Log.e("CHECK: ", silenziosoo + " ");
        }

        Notification notification = null;
        AudioManager am;

        am= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

       if(silenziosoo == true){
           notification = builder.setContentTitle("SOUND SETTINGS")
                   .setContentText("Modalità non disturbare attivata! ").setAutoCancel(true)
                   .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                   //.setContentIntent(intent2)
                   .setChannelId("my_channel_01")
                   .build();
           am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
       }
       else if(vibrazionee == true) {
           notification = builder.setContentTitle("SOUND SETTINGS")
                   .setContentText("Modalità solo vibrazione attivata! ").setAutoCancel(true)
                   .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                   //.setContentIntent(intent2)
                   .setChannelId("my_channel_01")
                   .build();
           am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
       }
       else if(suoneriaa == true)
       {
           notification = builder.setContentTitle("SOUND SETTINGS")
                   .setContentText("Modalità suoneria attivata! ").setAutoCancel(true)
                   .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                   //.setContentIntent(intent2)
                   .setChannelId("my_channel_01")
                   .build();
           am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
       }






        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notification);

    }
}
