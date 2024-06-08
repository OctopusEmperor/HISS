package com.example.hiss;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectViewHolder> {

    private List<String> subjectList;
    private ItemClickListener mItemListener;
    private FirebaseUser firebaseUser;

    public SubjectAdapter(List<String> subjectList, FirebaseUser firebaseUser, ItemClickListener itemClickListener) {
        this.subjectList = subjectList;
        this.mItemListener = itemClickListener;
        this.firebaseUser = firebaseUser;
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
            mItemListener.onItemClick(i);
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                SubjectAdapter subjectAdapter = new SubjectAdapter(subjectList, firebaseUser, mItemListener);

                DatabaseReference subjectRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/subjects");
                subjectRef.child("/" + position).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        subjectList.remove(position);
                        subjectAdapter.notifyItemRemoved(position);
                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }
}
