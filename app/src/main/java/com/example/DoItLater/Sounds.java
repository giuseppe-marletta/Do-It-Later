package com.example.DoItLater;


import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "sound")
public class Sounds {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;


    @Nullable
    Date  remindDate;

    String  latitudine, longitudine;
    String raggio;
    Boolean checkTime, checkGeo;
    Boolean checkSilenzioso, checkVibrazione, checkSuoneria;



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
    public Boolean getCheckSilenzioso() { return checkSilenzioso;}
    public Boolean getCheckVibrazione() { return checkVibrazione;}
    public Boolean getCheckSuoneria() { return checkSuoneria;}

    public int getId() {
        return id;
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
    public void setCheckSilenzioso(Boolean checkSilenzioso) { this.checkSilenzioso = checkSilenzioso;}
    public void setCheckVibrazione(Boolean checkVibrazione) { this.checkVibrazione = checkVibrazione;}
    public void setCheckSuoneria(Boolean checkSuoneria) { this.checkSuoneria = checkSuoneria;}


    public void setId(int id) {
        this.id = id;
    }
}
