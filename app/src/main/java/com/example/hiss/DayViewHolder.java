package com.example.hiss;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView time;
    private final ImageView line;
    public final Button event;
    private final DayAdapter.OnItemListener onItemListener;
    public DayViewHolder(@NonNull View itemView, DayAdapter.OnItemListener onItemListener) {
        super(itemView);

        time = itemView.findViewById(R.id.timeTV);
        line = itemView.findViewById(R.id.line);
        event = itemView.findViewById(R.id.event);

        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition(), (String) event.getText(),(String) time.getText(), (Button) event);
    }
}
