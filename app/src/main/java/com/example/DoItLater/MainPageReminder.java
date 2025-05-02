package com.example.DoItLater;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;




public class MainPageReminder extends AppCompatActivity {

    public static  AlarmManager alarmp;
    public static String messaggioo;
    private FloatingActionButton add;
    private Dialog dialog;
    private AppDatabase appDatabase;
    private RecyclerView recyclerView;
    private AdapterReminders adapter;
    private List<Reminders> temp;
    private TextView empty;
    private PendingIntent intent1;
    private AlarmManager alarmManager;
    private EditText message;
    private GeofencingClient gfc;
    private Geofence gf;
    private GeofencingRequest gfr = null;
    private  Calendar newDate = null ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        appDatabase = AppDatabase.geAppdatabase(MainPageReminder.this);

        add = findViewById(R.id.floatingButton);
        empty = findViewById(R.id.empty);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainPageReminder.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        setItemsInRecyclerView();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                Reminders EventRemove = temp.get(position);
                Intent intent = new Intent(MainPageReminder.this, NotifierAlarmReminder.class);
                intent.putExtra("Message", EventRemove.getMessage());
                intent.putExtra("RemindDate",EventRemove.getRemindDate().toString());
                intent.putExtra("Latitudine", EventRemove.getLatitudine().toString());
                intent.putExtra("Longitudine", EventRemove.getLongitudine().toString());
                intent.putExtra("Raggio", EventRemove.getRaggio().toString());
                intent.putExtra("id", EventRemove.getId());
                intent1 = PendingIntent.getBroadcast(MainPageReminder.this, EventRemove.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if((EventRemove.getCheckTime() == true) && (EventRemove.getCheckGeo() == false)) {
                    Calendar tem = Calendar.getInstance();
                    Calendar remind = Calendar.getInstance();
                    remind.setTime(EventRemove.getRemindDate());
                    if(remind.getTimeInMillis()-tem.getTimeInMillis()>0)
                        alarmManager.cancel(intent1);
                }
                else if ((EventRemove.getCheckTime() == false) && (EventRemove.getCheckGeo() == true)) {
                    //List<String> removeGeo = new ArrayList<>();
                    //removeGeo.add(String.valueOf(EventRemove.getId()));
                    gfc.removeGeofences(intent1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getBaseContext(), "GEO RIMOSSO", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else if ((EventRemove.getCheckTime() == true) && (EventRemove.getCheckGeo() == true)) {
                    Calendar tem = Calendar.getInstance();
                    Calendar remind = Calendar.getInstance();
                    remind.setTime(EventRemove.getRemindDate());
                    if(remind.getTimeInMillis()-tem.getTimeInMillis()>0)
                        alarmp.cancel(intent1);
                   // List<String> removeGeo = new ArrayList<>();
                    //removeGeo.add(String.valueOf(EventRemove.getId()));
                        gfc.removeGeofences(intent1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getBaseContext(), "GEO RIMOSSO", Toast.LENGTH_LONG).show();
                                    }
                                });
                }


                appDatabase.getRoomDAO().Delete(EventRemove);
                temp.remove(position);
                adapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void addReminder() {

        dialog = new Dialog(MainPageReminder.this);
        dialog.setContentView(R.layout.floating_popup_reminder);

        Button add;
        ImageView record;
        final Button maps,selectDate;
        record = dialog.findViewById(R.id.btn_record);
        add = dialog.findViewById(R.id.addButton);
        maps = dialog.findViewById(R.id.btn_maps);
        selectDate = dialog.findViewById(R.id.selectDate);
        message = dialog.findViewById(R.id.message);
        final EditText latitudine = dialog.findViewById(R.id.Latitudine);
        final EditText longitudine = dialog.findViewById(R.id.Longitudine);
        final EditText raggio = dialog.findViewById(R.id.RaggioMetri);
        final RadioButton entrata = dialog.findViewById(R.id.checkBoxEntrata);
        final RadioButton uscita = dialog.findViewById(R.id.checkBoxUscita);
        final TextView  textDate = dialog.findViewById(R.id.date);
        final CheckBox checkTime = dialog.findViewById(R.id.checkBoxTime);
        final CheckBox checkGeo = dialog.findViewById(R.id.checkBoxGeo);
        final Switch checkOgniGiorno = dialog.findViewById(R.id.switchOgniGiorno);
        final Switch checkOgniSettimana = dialog.findViewById(R.id.switchOgniSettimana);

        checkTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTime.isChecked() == true)
                {
                    textDate.setEnabled(true);
                    selectDate.setEnabled(true);
                    checkOgniGiorno.setEnabled(true);
                    checkOgniSettimana.setEnabled(true);
                }
                else {
                    textDate.setEnabled(false);
                    selectDate.setEnabled(false);
                    checkOgniGiorno.setEnabled(false);
                    checkOgniSettimana.setEnabled(false);
                }
            }
        });

        checkGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkGeo.isChecked() == true)
                {
                    latitudine.setEnabled(true);
                    longitudine.setEnabled(true);
                    raggio.setEnabled(true);
                    maps.setEnabled(true);
                    entrata.setEnabled(true);
                    uscita.setEnabled(true);
                }
                else
                {
                    latitudine.setEnabled(false);
                    longitudine.setEnabled(false);
                    raggio.setEnabled(false);
                    maps.setEnabled(false);
                    entrata.setEnabled(false);
                    uscita.setEnabled(false);
                }
            }
        });

        checkOgniGiorno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkOgniGiorno.isChecked() == true)
                    checkOgniSettimana.setChecked(false);
                else if(checkOgniGiorno.isChecked() == false)
                    checkOgniSettimana.setChecked(true);
            }
        });

        checkOgniSettimana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkOgniSettimana.isChecked() == true)
                    checkOgniGiorno.setChecked(false);
                else if (checkOgniSettimana.isChecked() == false)
                    checkOgniGiorno.setChecked(true);
            }
        });

        final Calendar newCalender = Calendar.getInstance();
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(MainPageReminder.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                        newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();
                        TimePickerDialog time = new TimePickerDialog(MainPageReminder.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                newDate.set(year,month,dayOfMonth,hourOfDay,minute,0);
                                Calendar tem = Calendar.getInstance();
                                Log.w("TIME",System.currentTimeMillis()+"");
                                if(newDate.getTimeInMillis()-tem.getTimeInMillis()>0)
                                    textDate.setText(newDate.getTime().toString());
                                else
                                    Toast.makeText(MainPageReminder.this,"Invalid time",Toast.LENGTH_SHORT).show();

                            }
                        },newTime.get(Calendar.HOUR_OF_DAY),newTime.get(Calendar.MINUTE),true);
                        time.show();

                    }
                },newCalender.get(Calendar.YEAR),newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();

            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordSpeech();
            }
        });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query="+latitudine.getText().toString()+"%2C"+longitudine.getText().toString());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivityForResult(mapIntent,101);
                    }
                }, 1000);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RoomDAO roomDAO = appDatabase.getRoomDAO();
                Reminders reminders = new Reminders();
                messaggioo = message.getText().toString().trim();
                reminders.setMessage(message.getText().toString().trim());
                Date remind = null;
                if(checkTime.isChecked() == true) {
                    remind = new Date(textDate.getText().toString().trim());
                    reminders.setRemindDate(remind);
                }
                else
                {
                    Calendar now = Calendar.getInstance();
                    remind = now.getTime();
                    reminders.setRemindDate(remind);
                }
                reminders.setLatitudine(latitudine.getText().toString().trim());
                reminders.setLongitudine(longitudine.getText().toString().trim());
                reminders.setRaggio(raggio.getText().toString().trim());
                reminders.setCheckGeo(checkGeo.isChecked());
                reminders.setCheckTime(checkTime.isChecked());
                roomDAO.Insert(reminders);
                List<Reminders> l = roomDAO.getAllReminders();
                reminders = l.get(l.size() - 1);
                Log.e("ID chahiye", reminders.getId() + "");



                int permission = ActivityCompat.checkSelfPermission(v.getContext(), ACCESS_FINE_LOCATION);
                int permission1 = ActivityCompat.checkSelfPermission(v.getContext(), ACCESS_COARSE_LOCATION);
                if (permission != PERMISSION_GRANTED || permission1 != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainPageReminder.this, new String[]{ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(MainPageReminder.this, new String[]{ACCESS_COARSE_LOCATION}, 1);
                }





                gfc = LocationServices.getGeofencingClient(v.getContext());


                if (entrata.isChecked() == true) {
                gf = new Geofence.Builder()
                        .setRequestId(String.valueOf(reminders.getId()))
                        .setCircularRegion(Double.parseDouble(reminders.getLatitudine()), Double.parseDouble(reminders.getLongitudine()), Float.parseFloat(reminders.getRaggio()))
                        //.setRequestId("gf1")
                        //.setCircularRegion(37.5742819, 15.1007591, 100 )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setLoiteringDelay(1 * 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build();

                gfr = new GeofencingRequest.Builder()
                        .addGeofence(gf)
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .build();

                } else if (uscita.isChecked() ==  true) {
                    gf = new Geofence.Builder()
                            .setRequestId(String.valueOf(reminders.getId()))
                            .setCircularRegion(Double.parseDouble(reminders.getLatitudine()), Double.parseDouble(reminders.getLongitudine()), Float.parseFloat(reminders.getRaggio()))
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setLoiteringDelay(1 * 1000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build();

                    gfr = new GeofencingRequest.Builder()
                            .addGeofence(gf)
                            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
                            .build();
                }

                if((checkGeo.isChecked() == true) && (checkTime.isChecked() == false) )
                {
                    Intent intent = new Intent(v.getContext(), NotifierAlarmReminder.class);
                    intent.putExtra("Message", messaggioo);
                    intent1 = PendingIntent.getBroadcast(v.getContext(), reminders.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    gfc.addGeofences(gfr, intent1)
                     .addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {
                             Toast.makeText(MainPageReminder.this ,"GEO INSERITO", Toast.LENGTH_SHORT).show();
                         }
                     });
                }
                else if((checkGeo.isChecked() == false) && (checkTime.isChecked() == true))
                {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                    calendar.setTime(remind);
                    calendar.set(Calendar.SECOND,0);
                    Intent intent = new Intent(MainPageReminder.this,NotifierAlarmReminder.class);
                    intent.putExtra("Message",messaggioo);
                    intent.putExtra("RemindDate",reminders.getRemindDate().toString());
                    intent.putExtra("id",reminders.getId());
                    intent.putExtra("checkRipetizioneGiorno", checkOgniGiorno.isChecked());
                    intent.putExtra("checkRipetizioneSettimana", checkOgniSettimana.isChecked());
                    intent1 = PendingIntent.getBroadcast(MainPageReminder.this,reminders.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    if( checkOgniGiorno.isChecked() == true)
                        //alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent1);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,intent1);
                    else if(checkOgniSettimana.isChecked() == true)
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),7*AlarmManager.INTERVAL_DAY,intent1);
                    else if(checkOgniGiorno.isChecked() == false && checkOgniSettimana.isChecked() == false)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent1);

                    Toast.makeText(MainPageReminder.this,"Inserted Successfully",Toast.LENGTH_SHORT).show();
                }
                else if((checkGeo.isChecked() == true) && (checkTime.isChecked() == true ))
                {
                    Intent intent = new Intent(MainPageReminder.this,NotifierGeo.class);
                    intent.putExtra("Message",messaggioo);
                    intent.putExtra("RemindDate",reminders.getRemindDate().toString());
                    intent.putExtra("id",reminders.getId());
                    intent.putExtra("checkRipetizioneGiorno", checkOgniGiorno.isChecked());
                    intent.putExtra("checkRipetizioneSettimana", checkOgniSettimana.isChecked());
                    intent1 = PendingIntent.getBroadcast(MainPageReminder.this,reminders.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmp = (AlarmManager)getSystemService(ALARM_SERVICE);


                    gfc.addGeofences(gfr,intent1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainPageReminder.this ,"GEO INSERITO", Toast.LENGTH_SHORT).show();
                                }
                            });
                }


                setItemsInRecyclerView();
                AppDatabase.destroyInstance();
                dialog.dismiss();

            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }



    private void recordSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {
            startActivityForResult(intent, 1);
        }
        catch (Exception e)
        {
            Toast.makeText(this,"Your device does not support Speech recognizer",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK && data != null) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                message.setText(text.get(0));
            }
        }
    }


    public void setItemsInRecyclerView(){

        RoomDAO dao = appDatabase.getRoomDAO();
        temp = dao.orderThetable();
        if(temp.size()>0) {
            empty.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter = new AdapterReminders(temp);
        recyclerView.setAdapter(adapter);

    }


}
