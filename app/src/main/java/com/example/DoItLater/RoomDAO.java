package com.example.DoItLater;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomDAO {



    @Insert
    public void Insert(Reminders... reminders);

    @Update
    public void Update(Reminders... reminders);

    @Delete
    public void Delete(Reminders reminders);

    @Query("Select * from reminder order by remindDate")
    public List<Reminders> orderThetable();

    @Query("Select * from reminder Limit 1")
    public Reminders getRecentEnteredData();

    @Query("Select * from reminder")
    public List<Reminders> getAllReminders();





    @Insert
    public void Insert(Messages... messages);

    @Update
    public void Update(Messages... messages);

    @Delete
    public void Delete(Messages  messages);

    @Query("Select * from message order by remindDate")
    public List<Messages> orderThetableMessage();

    @Query("Select * from message Limit 1")
    public Reminders getRecentEnteredDataMessage();

    @Query("Select * from message")
    public List<Messages> getAllMessages();





    @Insert
    public void Insert(Sounds... sounds);

    @Update
    public void Update(Sounds... sounds);

    @Delete
    public void Delete(Sounds sounds);

    @Query("Select * from sound order by remindDate")
    public List<Sounds> orderThetableSound();

    @Query("Select * from sound Limit 1")
    public Sounds getRecentEnteredDataSound();

    @Query("Select * from sound")
    public List<Sounds> getAllSounds();




    @Insert
    public void Insert(Bluetooths... bluetooths);

    @Update
    public void Update(Bluetooths... bluetooths);

    @Delete
    public void Delete(Bluetooths bluetooths);

    @Query("Select * from bluetooth  order by remindDate")
    public List<Bluetooths> orderThetableBluetooth();

    @Query("Select * from bluetooth Limit 1")
    public Bluetooths getRecentEnteredDataBluetooth();

    @Query("Select * from bluetooth")
    public List<Bluetooths> getAllBluetooths();




}
