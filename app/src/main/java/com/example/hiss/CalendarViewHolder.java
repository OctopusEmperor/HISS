package com.example.hiss;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    // TextView to display the day of the month
    public final TextView dayOfMonth;
    // Listener for item clicks
    private final CalendarAdapter.OnItemListener onItemListener;

    // Constructor for CalendarViewHolder
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        // Initialize the dayOfMonth TextView
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        // Set the click listener for the item view
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Call the onItemClick method when an item is clicked
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
