package com.example.hiss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private List<String> uriList;
    private NoteAdapter.ItemClickListener mItemListener;
    Context context;

    public NoteAdapter(Context context, List<Note> notesList, List<String> uriList, ItemClickListener mItemListener) {
        this.context = context;
        this.notesList = notesList;
        this.uriList = uriList;
        this.mItemListener = mItemListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cell, parent, false);
        return new NoteAdapter.NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.titleTV.setText(note.getTitle());
        holder.descriptionTV.setText(note.getDescription());

        Log.d("TAG", "Binding view holder at position " + position);

        if (uriList != null && uriList.size() == notesList.size()) {
            String uri = uriList.get(position);
            if (uri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    Picasso.get().load(uri1).into(holder.img);
                }).addOnFailureListener(exception -> {
                    Log.e("TAG", "Failed to get download URL: " + exception.getMessage());
                });
            } else {
                Log.e("TAG", "Uri at position " + position + " is null.");
            }
        } else {
            Log.e("TAG", "uriList is null or sizes do not match.");
        }

        holder.itemView.setOnClickListener(view -> {
            if (mItemListener != null)
                mItemListener.onItemClick(position);
        });

    }

    @Override
    public int getItemCount() {return notesList.size();}

    public Bitmap base64ToBitmap(String imageString) {
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV, descriptionTV;
        ImageView img;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            img = itemView.findViewById(R.id.img);
        }
    }
}
