package com.example.DoItLater;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NotifierGeo extends BroadcastReceiver {

    public static String messaggioNotifierGeo;

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
        String ddate = intent.getStringExtra("RemindDate").trim();
        Date date = new Date(ddate);
        calendar.setTime(date);
        //calendar.setTime((Date)intent.getSerializableExtra("RemindDate"));
        calendar.set(Calendar.SECOND,0);
        Intent intent3 = new Intent(context,NotifierAlarmReminder.class);
        messaggioNotifierGeo = intent.getStringExtra("Message");
        Log.e("OOOO", messaggioNotifierGeo + "");
        intent.putExtra("Messagee",messaggioNotifierGeo);
        //intent.putExtra("Message",intent.getStringExtra("Message"));
        PendingIntent intent4 = PendingIntent.getBroadcast(context,intent.getIntExtra("id",0),intent3,PendingIntent.FLAG_UPDATE_CURRENT);
        MainPageReminder.alarmp = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar tem = Calendar.getInstance();
        if( intent.getBooleanExtra("checkRipetizioneGiorno", false) == true)
        {
            if(calendar.getTimeInMillis()-tem.getTimeInMillis()>=0)
            {
                MainPageReminder.alarmp.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,intent4);
            }
        }
        else if(intent.getBooleanExtra("checkRipetizioneSettimana",false) == true)
        {
            if(calendar.getTimeInMillis()-tem.getTimeInMillis()>=0)
            {
                MainPageReminder.alarmp.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),7*AlarmManager.INTERVAL_DAY,intent4);
            }
        }
        else if((intent.getBooleanExtra("checkRipetizioneSettimana",false) == false) && ( intent.getBooleanExtra("checkRipetizioneGiorno", false) == false))
        {
            if(calendar.getTimeInMillis()-tem.getTimeInMillis()>=0)
                MainPageReminder.alarmp.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent4);
        }
    }
}