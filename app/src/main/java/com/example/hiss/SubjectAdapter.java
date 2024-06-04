package com.example.hiss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectViewHolder> {

    private List<String> subjectList;
    private ItemClickListener mItemListener;

    public SubjectAdapter(List<String> subjectList, ItemClickListener itemClickListener) {
        this.subjectList = subjectList;
        this.mItemListener = itemClickListener;
    }
    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_cell, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int i) {
        String title = subjectList.get(i);
        holder.titleTV.setText(title);
        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(subjectList.get(i));
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface ItemClickListener{
        void onItemClick(String title);
    }
}
