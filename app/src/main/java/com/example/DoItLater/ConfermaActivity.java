package com.example.DoItLater;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ConfermaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferma);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Button si,no;
        si = findViewById(R.id.buttonSi);
        no = findViewById(R.id.buttonNo);

       // TextView prova1, prova2;
       // prova1 = findViewById(R.id.textView5);
       // prova2 = findViewById(R.id.textView6);

        Intent intent = getIntent();
       // prova1.setText(intent.getStringExtra("number"));
        //prova2.setText(intent.getStringExtra("message"));

        final String number =  intent.getStringExtra("number");
        final String message = intent.getStringExtra("message");



        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MSG mioooo: ", message + "");
                Log.e("Numerp mioooo: ", number + "");
                Bundle extras = getIntent().getExtras();
                SmsManager mySmsManager = SmsManager.getDefault();
                mySmsManager.sendTextMessage(number, null, message, null, null);
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
