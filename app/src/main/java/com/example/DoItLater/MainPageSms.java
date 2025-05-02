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




public class MainPageSms extends AppCompatActivity {

    public static  AlarmManager alarmpSMS;  //!!!!
    public static String messaggiooSMS;  //!!!!
    public static String numeroo;
    private FloatingActionButton add;
    private Dialog dialog;
    private AppDatabase appDatabase;
    private RecyclerView recyclerView;
    private AdapterSms adapter;
    private List<Messages> temp;
    private TextView empty;
    private PendingIntent intent1;
    private AlarmManager alarmManager;
    private EditText message;
    private EditText number;
    private GeofencingClient gfc;
    private Geofence gf;
    private GeofencingRequest gfr = null;
    private  Calendar newDate = null ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        appDatabase = AppDatabase.geAppdatabase(MainPageSms.this);

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainPageSms.this);
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
                Messages EventRemove = temp.get(position);
                Intent intent = new Intent(MainPageSms.this, NotifierAlarmSms.class);
                intent.putExtra("Message", EventRemove.getMessage());
                intent.putExtra("Number", EventRemove.getNumber());
                intent.putExtra("RemindDate",EventRemove.getRemindDate().toString());
                intent.putExtra("Latitudine", EventRemove.getLatitudine().toString());
                intent.putExtra("Longitudine", EventRemove.getLongitudine().toString());
                intent.putExtra("Raggio", EventRemove.getRaggio().toString());
                intent.putExtra("id", EventRemove.getId());
                intent1 = PendingIntent.getBroadcast(MainPageSms.this, EventRemove.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                        alarmpSMS.cancel(intent1);
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

        dialog = new Dialog(MainPageSms.this);
        dialog.setContentView(R.layout.floating_popup_sms);

        Button add;
        ImageView record;
        final Button maps,selectDate;
        record = dialog.findViewById(R.id.btn_record);
        add = dialog.findViewById(R.id.addButton);
        maps = dialog.findViewById(R.id.btn_maps);
        selectDate = dialog.findViewById(R.id.selectDate);
        message = dialog.findViewById(R.id.message);
        number = dialog.findViewById(R.id.number);
        final EditText latitudine = dialog.findViewById(R.id.Latitudine);
        final EditText longitudine = dialog.findViewById(R.id.Longitudine);
        final EditText raggio = dialog.findViewById(R.id.RaggioMetri);
        final RadioButton entrata = dialog.findViewById(R.id.checkBoxEntrata);
        final RadioButton uscita = dialog.findViewById(R.id.checkBoxUscita);
        final TextView  textDate = dialog.findViewById(R.id.date);
        final CheckBox checkTime = dialog.findViewById(R.id.checkBoxTime);
        final CheckBox checkGeo = dialog.findViewById(R.id.checkBoxGeo);
        final CheckBox checkConferma = dialog.findViewById(R.id.checkBoxConferma);
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
                DatePickerDialog dialog = new DatePickerDialog(MainPageSms.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {

                        newDate = Calendar.getInstance();
                        Calendar newTime = Calendar.getInstance();
                        TimePickerDialog time = new TimePickerDialog(MainPageSms.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                newDate.set(year,month,dayOfMonth,hourOfDay,minute,0);
                                Calendar tem = Calendar.getInstance();
                                Log.w("TIME",System.currentTimeMillis()+"");
                                if(newDate.getTimeInMillis()-tem.getTimeInMillis()>0)
                                    textDate.setText(newDate.getTime().toString());
                                else
                                    Toast.makeText(MainPageSms.this,"Invalid time",Toast.LENGTH_SHORT).show();

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
                Messages messages = new Messages();
                messaggiooSMS = message.getText().toString().trim();
                messages.setMessage(message.getText().toString().trim());
                numeroo = number.getText().toString().trim();
                messages.setNumber(number.getText().toString().trim());
                Date remind = null;
                if(checkTime.isChecked() == true) {
                    remind = new Date(textDate.getText().toString().trim());
                    messages.setRemindDate(remind);
                }
                else
                {
                    Calendar now = Calendar.getInstance();
                    remind = now.getTime();
                    messages.setRemindDate(remind);
                }
                messages.setLatitudine(latitudine.getText().toString().trim());
                messages.setLongitudine(longitudine.getText().toString().trim());
                messages.setRaggio(raggio.getText().toString().trim());
                messages.setCheckGeo(checkGeo.isChecked());
                messages.setCheckTime(checkTime.isChecked());
                roomDAO.Insert(messages);
                List<Messages> l = roomDAO.getAllMessages();
                messages = l.get(l.size() - 1);
                Log.e("ID chahiye", messages.getId() + "");



                int permission = ActivityCompat.checkSelfPermission(v.getContext(), ACCESS_FINE_LOCATION);
                int permission1 = ActivityCompat.checkSelfPermission(v.getContext(), ACCESS_COARSE_LOCATION);
                if (permission != PERMISSION_GRANTED || permission1 != PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainPageSms.this, new String[]{ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(MainPageSms.this, new String[]{ACCESS_COARSE_LOCATION}, 1);
                }





                gfc = LocationServices.getGeofencingClient(v.getContext());


                if (entrata.isChecked() == true) {
                    gf = new Geofence.Builder()
                            .setRequestId(String.valueOf(messages.getId()))
                            .setCircularRegion(Double.parseDouble(messages.getLatitudine()), Double.parseDouble(messages.getLongitudine()), Float.parseFloat(messages.getRaggio()))
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
                            .setRequestId(String.valueOf(messages.getId()))
                            .setCircularRegion(Double.parseDouble(messages.getLatitudine()), Double.parseDouble(messages.getLongitudine()), Float.parseFloat(messages.getRaggio()))
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
                    Intent intent = new Intent(v.getContext(), NotifierAlarmSms.class);
                    intent.putExtra("Message", messaggiooSMS);
                    intent.putExtra("number", numeroo);
                    intent.putExtra("conferma", checkConferma.isChecked());
                    intent1 = PendingIntent.getBroadcast(v.getContext(), messages.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    gfc.addGeofences(gfr, intent1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainPageSms.this ,"GEO INSERITO", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else if((checkGeo.isChecked() == false) && (checkTime.isChecked() == true))
                {
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2:00"));
                    calendar.setTime(remind);
                    calendar.set(Calendar.SECOND,0);
                    Intent intent = new Intent(MainPageSms.this,NotifierAlarmSms.class);
                    intent.putExtra("Message",messaggiooSMS);
                    intent.putExtra("number", numeroo);
                    intent.putExtra("conferma", checkConferma.isChecked());
                    intent.putExtra("RemindDate",messages.getRemindDate().toString());
                    intent.putExtra("id",messages.getId());
                    intent.putExtra("checkRipetizioneGiorno", checkOgniGiorno.isChecked());
                    intent.putExtra("checkRipetizioneSettimana", checkOgniSettimana.isChecked());
                    intent1 = PendingIntent.getBroadcast(MainPageSms.this,messages.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    if( checkOgniGiorno.isChecked() == true)
                        //alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent1);
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY ,intent1);
                    else if(checkOgniSettimana.isChecked() == true)
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),7*AlarmManager.INTERVAL_DAY,intent1);
                    else if(checkOgniGiorno.isChecked() == false && checkOgniSettimana.isChecked() == false)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent1);

                    Toast.makeText(MainPageSms.this,"Inserted Successfully",Toast.LENGTH_SHORT).show();
                }
                else if((checkGeo.isChecked() == true) && (checkTime.isChecked() == true ))
                {
                    Intent intent = new Intent(MainPageSms.this,NotifierGeoSms.class);
                    intent.putExtra("Message",messaggiooSMS);
                    intent.putExtra("number", numeroo);
                    intent.putExtra("conferma", checkConferma.isChecked());
                    intent.putExtra("RemindDate",messages.getRemindDate().toString());
                    intent.putExtra("id",messages.getId());
                    intent.putExtra("checkRipetizioneGiorno", checkOgniGiorno.isChecked());
                    intent.putExtra("checkRipetizioneSettimana", checkOgniSettimana.isChecked());
                    intent1 = PendingIntent.getBroadcast(MainPageSms.this,messages.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmpSMS = (AlarmManager)getSystemService(ALARM_SERVICE);


                    gfc.addGeofences(gfr,intent1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainPageSms.this ,"GEO INSERITO", Toast.LENGTH_SHORT).show();
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
        temp = dao.orderThetableMessage();
        if(temp.size()>0) {
            empty.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter = new AdapterSms(temp);
        recyclerView.setAdapter(adapter);

    }


}
