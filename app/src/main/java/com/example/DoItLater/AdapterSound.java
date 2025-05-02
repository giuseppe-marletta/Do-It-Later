package com.example.DoItLater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterSound extends RecyclerView.Adapter<AdapterSound.MyViewHolder>{

    private List<Sounds> allSounds;
    private TextView sound ,latitudine, longitudine, raggio,time;

    public AdapterSound(List<Sounds> allSounds) {
        this.allSounds = allSounds;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sound_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Sounds sounds = allSounds.get(i);
        if(sounds.getCheckSilenzioso() == true)
            sound.setText("Silenzioso");
        else if (sounds.getCheckVibrazione() == true)
            sound.setText("Vibrazione");
        else if (sounds.getCheckSuoneria() == true)
            sound.setText("Suoneria");

        if(sounds.getCheckGeo() == true) {
            if(!sounds.getLatitudine().equals(""))
                latitudine.setText("Latitudine:"+sounds.getLatitudine());
            else
                latitudine.setVisibility(View.INVISIBLE);

            if(!sounds.getLongitudine().equals(""))
                longitudine.setText("Longitudine:"+sounds.getLongitudine());
            else
                longitudine.setVisibility(View.INVISIBLE);

            if(!sounds.getRaggio().equals(""))
                raggio.setText("Raggio:"+sounds.getRaggio()+ "metri");
            else
                raggio.setVisibility(View.INVISIBLE);

        }
        if( sounds.getCheckTime() == true)
            time.setText(sounds.getRemindDate().toString());
        else
            time.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return allSounds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sound = itemView.findViewById(R.id.textView1);
            latitudine = itemView.findViewById(R.id.textView2);
            longitudine = itemView.findViewById(R.id.textView3);
            raggio = itemView.findViewById(R.id.textView4);
            time = itemView.findViewById(R.id.textView5);
        }
    }

}
