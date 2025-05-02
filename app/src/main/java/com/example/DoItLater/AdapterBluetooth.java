package com.example.DoItLater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterBluetooth extends RecyclerView.Adapter<AdapterBluetooth.MyViewHolder>{

    private List<Bluetooths> allBluetooths;
    private TextView bluetooth ,latitudine, longitudine, raggio,time;

    public AdapterBluetooth(List<Bluetooths> allBluetooths) {
        this.allBluetooths = allBluetooths;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bluetooth_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Bluetooths bluetooths = allBluetooths.get(i);
        if(bluetooths.getCheckWifi() == true && bluetooths.getCheckBluetooth() == true)
            bluetooth.setText("Wi-Fi ON - Bluetooth ON");
        if(bluetooths.getCheckWifi() == false && bluetooths.getCheckBluetooth() == true)
            bluetooth.setText("Wi-Fi OFF - Bluetooth ON");
        if(bluetooths.getCheckWifi() == true && bluetooths.getCheckBluetooth() == false)
            bluetooth.setText("Wi-Fi ON - Bluetooth OFF");
        if(bluetooths.getCheckWifi() == false && bluetooths.getCheckBluetooth() == false)
            bluetooth.setText("Wi-Fi OFF - Bluetooth OFF");


        if(bluetooths.getCheckGeo() == true) {
            if(!bluetooths.getLatitudine().equals(""))
                latitudine.setText("Latitudine:"+bluetooths.getLatitudine());
            else
                latitudine.setVisibility(View.INVISIBLE);

            if(!bluetooths.getLongitudine().equals(""))
                longitudine.setText("Longitudine:"+bluetooths.getLongitudine());
            else
                longitudine.setVisibility(View.INVISIBLE);

            if(!bluetooths.getRaggio().equals(""))
                raggio.setText("Raggio:"+bluetooths.getRaggio()+ "metri");
            else
                raggio.setVisibility(View.INVISIBLE);

        }
        if( bluetooths.getCheckTime() == true)
            time.setText(bluetooths.getRemindDate().toString());
        else
            time.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return allBluetooths.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bluetooth = itemView.findViewById(R.id.textView1);
            latitudine = itemView.findViewById(R.id.textView2);
            longitudine = itemView.findViewById(R.id.textView3);
            raggio = itemView.findViewById(R.id.textView4);
            time = itemView.findViewById(R.id.textView5);
        }
    }

}
