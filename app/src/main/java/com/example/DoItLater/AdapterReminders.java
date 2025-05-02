package com.example.DoItLater;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AdapterReminders extends RecyclerView.Adapter<AdapterReminders.MyViewHolder>{

    private List<Reminders> allReminders;
    private TextView message,latitudine, longitudine, raggio,time;
    private CardView cardview;

    public AdapterReminders(List<Reminders> allReminders) {
        this.allReminders = allReminders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reminder_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Reminders reminders = allReminders.get(i);
        if(!reminders.getMessage().equals(""))
            message.setText("Messaggio:"+ reminders.getMessage());
        else
            message.setHint("No Message");
        if(reminders.getCheckGeo() == true) {
            if(!reminders.getLatitudine().equals(""))
                latitudine.setText("Latitudine:"+reminders.getLatitudine());
            else {
                latitudine.setVisibility(View.INVISIBLE);
                latitudine.setEnabled(false); }

            if(!reminders.getLongitudine().equals(""))
                longitudine.setText("Longitudine:"+reminders.getLongitudine());
            else {
                longitudine.setVisibility(View.INVISIBLE);
                longitudine.setEnabled(false); }

            if(!reminders.getRaggio().equals(""))
                raggio.setText("Raggio:"+reminders.getRaggio()+ "metri");
            else {
                raggio.setVisibility(View.INVISIBLE);
                raggio.setEnabled(false); }




        }
       if( reminders.getCheckTime() == true)
            time.setText(reminders.getRemindDate().toString());
       else
            time.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return allReminders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.textView1);
            latitudine = itemView.findViewById(R.id.textView2);
            longitudine = itemView.findViewById(R.id.textView3);
            raggio = itemView.findViewById(R.id.textView4);
            time = itemView.findViewById(R.id.textView5);
            cardview = itemView.findViewById(R.id.cardview);
        }
    }

}
