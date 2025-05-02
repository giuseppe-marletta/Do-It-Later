package com.example.DoItLater;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.READ_SMS;
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
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
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




public class MainPageBluetooth extends AppCompatActivity {

    public static AlarmManager alarmpBluetooth;  //!!!!
    public static Boolean checkBluetoothh; //!!!!
    public static Boolean checkWifii;
    private FloatingActionButton add;
    private Dialog dialog;
    private AppDatabase appDatabase;
    private RecyclerView recyclerView;
    private AdapterBluetooth adapter;
    private List<Bluetooths> temp;
    private TextView empty;
    private PendingIntent intent1;
    private AlarmManager alarmManager;
    private Switch switchBluetooth;
    private Switch switchWifi;
    private GeofencingClient gfc;
    private Geofence gf;
    private GeofencingRequest gfr = null;
    private  Calendar newDate = null ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        appDatabase = AppDatabase.geAppdatabase(MainPageBluetooth.this);

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainPageBluetooth.this);
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
                Bluetooths EventRemove = temp.get(position);
                Intent intent = new Intent(MainPageBluetooth.this, NotifierAlarmBluetooth.class);
                intent.putExtra("Bluetooth", EventRemove.getCheckBluetooth());
                intent.putExtra("Wi-Fi", EventRemove.getCheckWifi());
                intent.putExtra("RemindDate",EventRemove.getRemindDate().toString());
                intent.putExtra("Latitudine", EventRemove.getLatitudine().toString());
                intent.putExtra("Longitudine", EventRemove.getLongitudine().toString());
                intent.putExtra("Raggio", EventRemove.getRaggio().toString());
                intent.putExtra("id", EventRemove.getId());
                intent1 = PendingIntent.getBroadcast(MainPageBluetooth.this, EventRemove.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                        alarmpBluetooth.cancel(intent1);
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


                appDatabase.getRoomDAO().Delete(EventRemove);
                temp.remove(position);
                adapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }


    public void addReminder() {

        dialog = new Dialog(MainPageBluetooth.this);
        dialog.setContentView(R.layout.floating_popup_bluetooth);

        Button add;
        final Button maps,selectDate;
        add = dialog.findViewById(R.id.addButton);
        maps = dialog.findViewById(R.id.btn_maps);
        selectDate = dialog.findViewById(R.id.selectDate);
        switchBluetooth = dialog.findViewById(R.id.switchBluetooth);
        switchWifi = dialog.findViewById(R.id.switchWifi);
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
                DatePickerDialog dialog = new DatePickerDialog(MainPageBluetooth.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                        newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();
                        TimePickerDialog time = new TimePickerDialog(MainPageBluetooth.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                newDate.set(year,month,dayOfMonth,hourOfDay,minute,0);
                                Calendar tem = Calendar.getInstance();
                                Log.w("TIME",System.currentTimeMillis()+"");
                                if(newDate.getTimeInMillis()-tem.getTimeInMillis()>0)
                                    textDate.setText(newDate.getTime().toString());
                                else
                                    Toast.makeText(MainPageBluetooth.this,"Invalid time",Toast.LENGTH_SHORT).show();

                            }
                        },newTime.get(Calendar.HOUR_OF_DAY),newTime.get(Calendar.MINUTE),true);
                        time.show();

                    }
                },newCalender.get(Calendar.YEAR),newCalender.get(Calendar.MONTH),newCalender.get(Calendar.DAY_OF_MONTH));

                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();

            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RoomDAO roomDAO = appDatabase.getRoomDAO();
                Bluetooths bluetooths = new Bluetooths();
                checkBluetoothh = switchBluetooth.isChecked();
                bluetooths.setCheckBluetooth(switchBluetooth.isChecked());
                checkWifii = switchWifi.isChecked();
                bluetooths.setCheckWifi(switchWifi.isChecked());

                Date remind = null;
                if(checkTime.isChecked() == true) {
                    remind = new Date(textDate.getText().toString().trim());
                    bluetooths.setRemindDate(remind);
                }
                else
                {
                    Calendar now = Calendar.getInstance();
                    remind = now.getTime();
                    bluetooths.setRemindDate(remind);
                }
                bluetooths.setLatitudine(latitudine.getText().toString().trim());
                bluetooths.setLongitudine(longitudine.getText().toString().trim());
                bluetooths.setRaggio(raggio.getText().toString().trim());
                bluetooths.setCheckGeo(checkGeo.isChecked());
                bluetooths.setCheckTime(checkTime.isChecked());
                roomDAO.Insert(bluetooths);
                List<Bluetooths> l = roomDAO.getAllBluetooths();
                bluetooths = l.get(l.size() - 1);
                Log.e("ID chahiye", bluetooths.getId() + "");



                int permission = ActivityCompat.checkSelfPermission(v.getContext(), ACCESS_FINE_LOCATION);
                int permission1 = ActivityCompat.checkSelfPermission(v.getContext(), ACCESS_COARSE_LOCATION);
                if (permission != PERMISSION_GRANTED || permission1 != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainPageBluetooth.this, new String[]{ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(MainPageBluetooth.this, new String[]{ACCESS_COARSE_LOCATION}, 1);
                }





                gfc = LocationServices.getGeofencingClient(v.getContext());


                if (entrata.isChecked() == true) {
                    gf = new Geofence.Builder()
                            .setRequestId(String.valueOf(bluetooths.getId()))
                            .setCircularRegion(Double.parseDouble(bluetooths.getLatitudine()), Double.parseDouble(bluetooths.getLongitudine()), Float.parseFloat(bluetooths.getRaggio()))
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
                            .setRequestId(String.valueOf(bluetooths.getId()))
                            .setCircularRegion(Double.parseDouble(bluetooths.getLatitudine()), Double.parseDouble(bluetooths.getLongitudine()), Float.parseFloat(bluetooths.getRaggio()))
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
                    Boolean checkAlarmGeo = false;
                    Intent intent = new Intent(v.getContext(), NotifierAlarmBluetooth.class);
                    intent.putExtra("checkAlarmGeo", checkAlarmGeo);
                    intent.putExtra("Bluetooth", checkBluetoothh);
                    intent.putExtra("Wi-Fi", checkWifii);
                    intent1 = PendingIntent.getBroadcast(v.getContext(), bluetooths.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    gfc.addGeofences(gfr, intent1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainPageBluetooth.this ,"GEO INSERITO", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else if((checkGeo.isChecked() == false) && (checkTime.isChecked() == true))
                {
                    Boolean checkAlarmGeo = false;
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                    calendar.setTime(remind);
                    calendar.set(Calendar.SECOND,0);
                    Intent intent = new Intent(MainPageBluetooth.this,NotifierAlarmBluetooth.class);
                    intent.putExtra("checkAlarmGeo", checkAlarmGeo);
                    intent.putExtra("Bluetooth", checkBluetoothh);
                    intent.putExtra("Wi-Fi", checkWifii);
                    intent.putExtra("RemindDate",bluetooths.getRemindDate().toString());
                    intent.putExtra("id",bluetooths.getId());
                    intent.putExtra("checkRipetizioneGiorno", checkOgniGiorno.isChecked());
                    intent.putExtra("checkRipetizioneSettimana", checkOgniSettimana.isChecked());
                    intent1 = PendingIntent.getBroadcast(MainPageBluetooth.this,bluetooths.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    if( checkOgniGiorno.isChecked() == true)
                        //alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent1);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,intent1);
                    else if(checkOgniSettimana.isChecked() == true)
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),7*AlarmManager.INTERVAL_DAY,intent1);
                    else if(checkOgniGiorno.isChecked() == false && checkOgniSettimana.isChecked() == false)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent1);

                    Toast.makeText(MainPageBluetooth.this,"Inserted Successfully",Toast.LENGTH_SHORT).show();
                }
                else if((checkGeo.isChecked() == true) && (checkTime.isChecked() == true ))
                {
                    Intent intent = new Intent(MainPageBluetooth.this,NotifierGeoBluetooth.class);
                    intent.putExtra("Bluetooth", checkBluetoothh);
                    intent.putExtra("Wi-Fi", checkWifii);
                    intent.putExtra("RemindDate",bluetooths.getRemindDate().toString());
                    intent.putExtra("id",bluetooths.getId());
                    intent.putExtra("checkRipetizioneGiorno", checkOgniGiorno.isChecked());
                    intent.putExtra("checkRipetizioneSettimana", checkOgniSettimana.isChecked());
                    intent1 = PendingIntent.getBroadcast(MainPageBluetooth.this,bluetooths.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmpBluetooth = (AlarmManager)getSystemService(ALARM_SERVICE);


                    gfc.addGeofences(gfr,intent1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainPageBluetooth.this ,"GEO INSERITO", Toast.LENGTH_SHORT).show();
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



    public void setItemsInRecyclerView(){

        RoomDAO dao = appDatabase.getRoomDAO();
        temp = dao.orderThetableBluetooth();
        if(temp.size()>0) {
            empty.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter = new AdapterBluetooth(temp);
        recyclerView.setAdapter(adapter);

    }


}
