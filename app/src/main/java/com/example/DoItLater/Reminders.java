package com.example.DoItLater;


import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "reminder")
public class Reminders {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    String message;

    @Nullable
    Date  remindDate;

    String  latitudine, longitudine;
    String raggio;
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


    public void setId(int id) {
        this.id = id;
    }
}
