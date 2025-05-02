package com.example.DoItLater;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.Date;

public class NotifierAlarmBluetooth extends BroadcastReceiver {

    //private AppDatabase appDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {


        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent intent1 = new Intent(context,MainPageBluetooth.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainPageBluetooth.class);
        taskStackBuilder.addNextIntent(intent1);



        PendingIntent intent2 = taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("my_channel_01","hello", NotificationManager.IMPORTANCE_HIGH);
        }


        Boolean bluetoothh;
        if(intent.getBooleanExtra("checkAlarmGeo",false) == false) {
            bluetoothh = intent.getBooleanExtra("Bluetooth",false);
            Log.e("CHECK: ", bluetoothh + " ");
        }
        else
        {
            bluetoothh = NotifierGeoBluetooth.checkBluetoothNotifierGeoSound;
            Log.e("CHECK: ", bluetoothh + " ");
        }

        Boolean wifii;
        if(intent.getBooleanExtra("checkAlarmGeo",false) == false) {
            wifii = intent.getBooleanExtra("Wi-Fi",false);
            Log.e("CHECK: ", wifii + " ");
        }
        else
        {
            wifii = NotifierGeoBluetooth.checkWifiNotifierGeoSound;
            Log.e("CHECK: ", wifii + " ");
        }


        Notification notification = null;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        if(bluetoothh == true && wifii == true){
            notification = builder.setContentTitle("SOUND SETTINGS")
                    .setContentText("Wi-Fi abilitato! Bluetooth attivato! ").setAutoCancel(true)
                    .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setContentIntent(intent2)
                    .setChannelId("my_channel_01")
                    .build();
            mBluetoothAdapter.enable();
            wifiManager.setWifiEnabled(true);
        }
        else if(bluetoothh == true && wifii == false){
            notification = builder.setContentTitle("SOUND SETTINGS")
                    .setContentText("Wi-Fi disabilitato! Bluetooth attivato! ").setAutoCancel(true)
                    .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setContentIntent(intent2)
                    .setChannelId("my_channel_01")
                    .build();
            mBluetoothAdapter.enable();
            wifiManager.setWifiEnabled(false);
        }
        if(bluetoothh == false && wifii == true){
            notification = builder.setContentTitle("SOUND SETTINGS")
                    .setContentText("Wi-Fi abilitato! Bluetooth disattivato! ").setAutoCancel(true)
                    .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setContentIntent(intent2)
                    .setChannelId("my_channel_01")
                    .build();
            mBluetoothAdapter.disable();
            wifiManager.setWifiEnabled(true);
        }
        if(bluetoothh == false && wifii == false){
            notification = builder.setContentTitle("SOUND SETTINGS")
                    .setContentText("Wi-Fi disabilitato! Bluetooth disattivato! ").setAutoCancel(true)
                    .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher_round)
                    //.setContentIntent(intent2)
                    .setChannelId("my_channel_01")
                    .build();
            mBluetoothAdapter.disable();
            wifiManager.setWifiEnabled(false);
        }



        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notification);

    }
}
