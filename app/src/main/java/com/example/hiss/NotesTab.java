package com.example.hiss;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NotesTab extends AppCompatActivity implements View.OnClickListener {

    Button addNoteBtn, imgBtn, saveBtn;
    ImageButton exitBtn;
    RecyclerView notesRecyclerView;
    List<Note> noteList;
    List<String> uriList;
    Dialog d;
    EditText titleET, descriptionET;
    ImageView iv;
    ActivityResultLauncher<Intent> imgLauncher;
    NoteAdapter noteAdapter;
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    StorageReference sref, fileRef;
    final long ONE_MEGABYTE = 1024 * 1024;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_layout);

        FirebaseApp.initializeApp(this);
        exitBtn = findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(this);
        addNoteBtn = findViewById(R.id.addNoteBtn);
        addNoteBtn.setOnClickListener(this);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        noteList = new ArrayList<>();
        uriList = new ArrayList<>();
        firebaseUser = getIntent().getParcelableExtra("user");

                noteAdapter = new NoteAdapter(getApplicationContext(), noteList, uriList, new NoteAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                });
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        notesRecyclerView.setAdapter(noteAdapter);

        imgLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                Uri selectedImage = data.getData();
                iv.setImageURI(selectedImage);
                iv.setVisibility(View.VISIBLE);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+firebaseUser.getUid()+"/notes");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Attempting to read value: " + dataSnapshot);
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Note note = ds.getValue(Note.class);
                        noteList.add(note);
                        if (noteList.size() == uriList.size())
                            noteAdapter.notifyItemInserted(noteList.size()-1);
                    }
                    Log.d(TAG, "Successfully read value: " + noteList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value: " + databaseError);
            }
        });

        sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hiss---android-bagrut-project.appspot.com");
        fileRef = sref.child("users/" + firebaseUser.getUid() + "/images/");
        fileRef.listAll().addOnSuccessListener(listResult ->  {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uriList.add(uri.toString());
                        Log.d(TAG, "uriList found");
                        if (uriList.size() == noteList.size())
                            noteAdapter.notifyItemInserted(uriList.size()-1);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (exitBtn == v) {
            finish();
        }

        if (addNoteBtn == v) {
            createNoteDialog();
        }

        if (imgBtn == v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imgLauncher.launch(intent);
        }

        if (saveBtn == v){
            String title = titleET.getText().toString();
            String description = descriptionET.getText().toString();
            if (((BitmapDrawable) iv.getDrawable()) != null) {
                Bitmap img = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                uploadFile(img, title);
                noteList.add(new Note(title, description));
                updateNotes(new Note(title, description));
            }

            d.dismiss();
        }
    }

    public String bitmapToBase64(Bitmap img){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

//    public void uploadFile(Bitmap bitmap, String title) {
//        StorageReference imageRef = fileRef.child("/" + title + ".png");
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        UploadTask uploadTask = imageRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.d(TAG, "Failed to upload image");
//            }
//        }).addOnSuccessListener(taskSnapshot -> {
//            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                uriList.add(uri.toString());
//                Log.d(TAG, "Successfully uploaded image: " + uri);
//                if (uriList.size()==noteList.size())
//                    noteAdapter.notifyItemInserted(uriList.size()-1);
//            });
//        });
//    }

    public void uploadFile(Bitmap bitmap, String title) {
        StorageReference imageRef = fileRef.child("/" + title + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> Log.d(TAG, "Failed to upload image"))
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    uriList.add(uri.toString());
                    Log.d(TAG, "Successfully uploaded image: " + uri);
                    if (uriList.size() == noteList.size()) {
                        noteAdapter.notifyItemInserted(uriList.size() - 1); // Notify adapter of new item
                    }
                }));
    }


    public void updateNotes(Note newNote){
        DatabaseReference noteRef = FirebaseDatabase.getInstance().getReference("users/" + firebaseUser.getUid() + "/notes");
        noteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    List<Note> notes = new ArrayList<>();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Note note = ds.getValue(Note.class);
                        notes.add(note);
                    }
                    notes.add(newNote);
                    noteRef.setValue(notes);
                }
                else {
                    if (newNote != null && !noteList.contains(newNote)) {
                        noteRef.setValue(noteList);
                        if (uriList.size()==noteList.size())
                            noteAdapter.notifyItemInserted(uriList.size()-1);
                        Log.d("onDataChange", "Added new note");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled", "Failed to get reference" + noteRef);
            }
        });
    }

    public void createNoteDialog(){
        d = new Dialog(this);
        d.setContentView(R.layout.note_dialog);
        d.setTitle("Edit Event");
        d.setCancelable(true);

        titleET = (EditText) d.findViewById(R.id.titleET);
        descriptionET = (EditText) d.findViewById(R.id.descriptionET);
        imgBtn =(Button) d.findViewById(R.id.imgBtn);
        iv = d.findViewById(R.id.imgView);
        iv.setDrawingCacheEnabled(true);
        iv.buildDrawingCache();
        imgBtn.setOnClickListener(this);
        saveBtn = d.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        d.show();
    }
}
