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

public class NotifierGeoBluetooth extends BroadcastReceiver {


    public static Boolean checkBluetoothNotifierGeoSound;
    public static Boolean checkWifiNotifierGeoSound;

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean checkAlarmGeo = true;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
        String ddate = intent.getStringExtra("RemindDate").trim();
        Date date = new Date(ddate);
        calendar.setTime(date);
        //calendar.setTime((Date)intent.getSerializableExtra("RemindDate"));
        calendar.set(Calendar.SECOND,0);
        Intent intent3 = new Intent(context,NotifierAlarmBluetooth.class);
        checkBluetoothNotifierGeoSound = intent.getBooleanExtra("Bluetooth", false);
        Log.e("OOOO",  checkBluetoothNotifierGeoSound + "");
        checkWifiNotifierGeoSound = intent.getBooleanExtra("Wi-Fi", false);
        Log.e("OOOO",  checkWifiNotifierGeoSound + "");


        intent.putExtra("checkAlarmGeo", checkAlarmGeo);
        intent.putExtra("Bluetoothh", checkBluetoothNotifierGeoSound);
        intent.putExtra("Wi-Fii", checkWifiNotifierGeoSound);
        //intent.putExtra("Message",intent.getStringExtra("Message"));
        PendingIntent intent4 = PendingIntent.getBroadcast(context,intent.getIntExtra("id",0),intent3,PendingIntent.FLAG_UPDATE_CURRENT);
        MainPageBluetooth.alarmpBluetooth = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar tem = Calendar.getInstance();
        if( intent.getBooleanExtra("checkRipetizioneGiorno", false) == true)
        {
            if(calendar.getTimeInMillis()-tem.getTimeInMillis()>=0)
            {
                MainPageBluetooth.alarmpBluetooth.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,intent4);
            }
        }
        else if(intent.getBooleanExtra("checkRipetizioneSettimana",false) == true)
        {
            if(calendar.getTimeInMillis()-tem.getTimeInMillis()>=0)
            {
                MainPageBluetooth.alarmpBluetooth.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),7*AlarmManager.INTERVAL_DAY,intent4);
            }
        }
        else if((intent.getBooleanExtra("checkRipetizioneSettimana",false) == false) && ( intent.getBooleanExtra("checkRipetizioneGiorno", false) == false))
        {
            if(calendar.getTimeInMillis()-tem.getTimeInMillis()>=0)
                MainPageBluetooth.alarmpBluetooth.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent4);
        }
    }
}
