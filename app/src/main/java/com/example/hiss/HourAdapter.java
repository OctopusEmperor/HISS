package com.example.hiss;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> implements View.OnLongClickListener {

    private ArrayList<ArrayList<Event>> eventList;
    private HourAdapter.ItemClickListener mItemListener;
    Context context;
    Dialog d;
    EditText titleET, descriptionET;
    SwitchCompat switchCompat;
    TextView errorMsg;
    TimePicker tp;
    Button saveBtn, e1, e2, e3;


    public HourAdapter(Context context, ArrayList<ArrayList<Event>> events, ItemClickListener itemClickListener) {
        this.eventList = events;
        this.context = context;
        this.mItemListener = itemClickListener;
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_cell, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        ArrayList<Event> events = eventList.get(position);
        String hour;
        if (position < 10) {
            hour = "0" + position;
        } else {
            hour = String.valueOf(position);
        }
        holder.timeTV.setText(hour + ":00");
        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(position);
        });

        for (int j=0; j<3; j++){
            int index = j;
            holder.events[j].setOnLongClickListener(view -> {
                eventList.get(position).remove(index);
                HourAdapter hourAdapter = new HourAdapter(context, eventList, mItemListener);
                hourAdapter.notifyItemChanged(position);
                return true;
            });
        }

        int i=0;
        for (; i < events.size() && i < 3; i++) {
            holder.events[i].setText(events.get(i).getTitle());
            holder.events[i].setVisibility(View.VISIBLE);
        }
        for (; i < 3; i++) {
            holder.events[i].setVisibility(View.GONE);
        }
        holder.events[0].setOnClickListener(view -> {
            Event event = events.get(0);
            editEventDialog(event.getTitle(), event.getDescription(), event.getTime(), position, 0, holder);
        });
        holder.events[1].setOnClickListener(view -> {
            Event event = events.get(1);
            editEventDialog(event.getTitle(), event.getDescription(), event.getTime(), position, 1, holder);
        });
        holder.events[2].setOnClickListener(view -> {
            Event event = events.get(2);
            editEventDialog(event.getTitle(), event.getDescription(), event.getTime(), position, 2, holder);
        });
    }

    @Override
    public int getItemCount() {
        return 24;
    }

    @Override
    public boolean onLongClick(View v) {
        if (e1 == v) {
            e1.setVisibility(View.GONE);
        }
        if (e2 == v) {
            e2.setVisibility(View.GONE);
        }
        if (e3 == v) {
            e3.setVisibility(View.GONE);
        }
        return true;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public void editEventDialog (String title, String description, String time,int position,
    int i, HourViewHolder holder){
        d = new Dialog(context);
        d.setContentView(R.layout.event_dialog);
        d.setTitle("Edit Event");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        descriptionET = (EditText) d.findViewById(R.id.descriptionET);
        switchCompat = (SwitchCompat) d.findViewById(R.id.switchCompat);
        errorMsg = (TextView) d.findViewById(R.id.errorMsg);
        tp = (TimePicker) d.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        tp.setVisibility(View.GONE);
        switchCompat.setVisibility(View.GONE);
        saveBtn = (Button) d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(view -> {
            Event event = new Event(titleET.getText().toString(), descriptionET.getText().toString(), tp.getHour() + ":" + tp.getMinute());
            eventList.get(position).set(i, event);
            holder.events[i].setText(event.getTitle());
            d.dismiss();
        });

        titleET.setText(title);
        descriptionET.setText(description);

        d.show();
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {
        TextView timeTV;
        Button[] events;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            events = new Button[3];
            events[0] = itemView.findViewById(R.id.event1);
            events[1] = itemView.findViewById(R.id.event2);
            events[2] = itemView.findViewById(R.id.event3);
            timeTV = itemView.findViewById(R.id.timeTV);
        }
    }
}


