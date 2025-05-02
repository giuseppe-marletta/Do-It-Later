package com.example.DoItLater;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "message")
public class Messages {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    String message;
    Date  remindDate;
    String number;
    String latitudine,longitudine,raggio;
    Boolean checkTime, checkGeo;

    public String getMessage() {
        return message;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public String getLatitudine() {
        return latitudine;
    }
    public String getLongitudine() {
        return longitudine;
    }
    public String getRaggio() {
        return raggio;
    }
    public Boolean getCheckTime() { return checkTime;}
    public Boolean getCheckGeo() { return checkGeo;}

    public String getNumber() {
        return number;
    }

    public int getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    public void setLatitudine(String latitudine) {
        this.latitudine = latitudine;
    }
    public void setLongitudine(String  longitudine) {
        this.longitudine = longitudine;
    }

    public void setRaggio(String raggio) { this.raggio = raggio;}

    public void setCheckTime(Boolean checkTime) { this.checkTime = checkTime;}
    public void setCheckGeo(Boolean checkGeo) { this.checkGeo = checkGeo;}


    public void setNumber(String number) {
        this.number = number ;
    }

    public void setId(int id) {
        this.id = id;
    }
}

