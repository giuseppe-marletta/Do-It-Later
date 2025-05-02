package com.example.DoItLater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterSms extends RecyclerView.Adapter<AdapterSms.MyViewHolder>{

     List<Messages> allMessages;
     TextView message,time,number, latitudine, longitudine, raggio ;

    public AdapterSms(List<Messages> allMessages) {
        this.allMessages = allMessages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sms_item,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Messages messages  = allMessages.get(i);
        if(!messages.getMessage().equals(""))
            message.setText("Messaggio:" +  messages.getMessage());
        else
            message.setHint("No Message");

        if(!messages.getNumber().equals(""))
            number.setText("Numero:" + messages.getNumber());
        else
            number.setHint("No Number");

        if(messages.getCheckGeo() == true) {
            if(!messages.getLatitudine().equals(""))
                latitudine.setText("Latitudine:"+messages.getLatitudine());
            else
                latitudine.setVisibility(View.INVISIBLE);

            if(!messages.getLongitudine().equals(""))
                longitudine.setText("Longitudine:"+messages.getLongitudine());
            else
                longitudine.setVisibility(View.INVISIBLE);

            if(!messages.getRaggio().equals(""))
                raggio.setText("Raggio:"+messages.getRaggio()+ "metri");
            else
                raggio.setVisibility(View.INVISIBLE);

        }
        if( messages.getCheckTime() == true)
            time.setText(messages.getRemindDate().toString());
        else
            time.setVisibility(View.INVISIBLE);





    }

    @Override
    public int getItemCount() {
        return allMessages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.textView1);
            time = itemView.findViewById(R.id.textView2);
            number = itemView.findViewById(R.id.textView3);
            latitudine = itemView.findViewById(R.id.textView4);
            longitudine = itemView.findViewById(R.id.textView5);
            raggio = itemView.findViewById(R.id.textView6);
        }
    }

}
