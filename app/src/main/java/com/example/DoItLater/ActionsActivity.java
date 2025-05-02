package com.example.DoItLater;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;

public class ActionsActivity extends AppCompatActivity {

    public static Boolean checkPermissionSound = false  ;

    CardView buttonReminder , buttonSms, buttonSound, buttonBluetooth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        buttonReminder = findViewById(R.id.btn_reminder);
        buttonSms = findViewById(R.id.btn_sms);
        buttonSound = findViewById(R.id.btn_sound);
        buttonBluetooth = findViewById(R.id.btn_bluetooth);
        int permission = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        if (permission != PERMISSION_GRANTED || permission1 != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActionsActivity.this , new String[]{ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(ActionsActivity.this , new String[]{ACCESS_COARSE_LOCATION}, 1);
        }

        buttonReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPageReminder.class);
                startActivity(intent);
            }
        });

        buttonSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPageSms.class);
                startActivity(intent);
                int permission2 = ActivityCompat.checkSelfPermission(v.getContext(), SEND_SMS);
                int permission3 = ActivityCompat.checkSelfPermission(v.getContext(), READ_SMS);
                if ( permission2 != PERMISSION_GRANTED || permission3 != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActionsActivity.this , new String[]{SEND_SMS}, 1);
                    ActivityCompat.requestPermissions(ActionsActivity.this , new String[]{READ_SMS}, 1);
                }

            }
        });

        buttonSound.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), MainPageSound.class);
               startActivity(intent);
            }
        });

        buttonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPageBluetooth.class);
                startActivity(intent);
            }
        });





    }
}
