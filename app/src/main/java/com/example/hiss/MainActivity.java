package com.example.hiss;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // UI element for sign-in button
    ImageButton signInButton;
    // Tag for logging
    String TAG;
    // Credential manager for handling credentials
    CredentialManager credentialManager;
    // Request for getting credentials
    GetCredentialRequest request;
    // Google ID token
    String googleIdToken;
    // Firebase authentication instance
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize credential manager
        initCredentialManager();

        // Initialize sign-in button and set click listener
        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            navigateToMainMenu();
        }
    }


    private static String generateNonce() {
        String rawNonce = UUID.randomUUID().toString();
        byte[] bytes = rawNonce.getBytes();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = md.digest(bytes);
        return Base64.encodeToString(digest, Base64.URL_SAFE);
    }

    // Initialize the credential manager
    private void initCredentialManager(){
        credentialManager = CredentialManager.create(getApplicationContext());

        GetSignInWithGoogleOption signInWithGoogleOption = new GetSignInWithGoogleOption.Builder("353278388473-0feqddjdm87hc4u20g56ns3b97p89g05.apps.googleusercontent.com")
                .setNonce(generateNonce())
                .build();

        request = new GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build();

        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> {
            Log.d("/////", "Preparing credentials with Google was cancelled.");
            Toast.makeText(this, "Cancelled.", Toast.LENGTH_SHORT).show();
        });
    }

    // Handle success of credential retrieval
    public void handleSuccess(GetCredentialResponse result){
        Credential credential = result.getCredential();
        if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())){
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                    googleIdToken = googleIdTokenCredential.getIdToken();
                    // Use the Google ID token to authenticate with Firebase
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(googleIdToken, null);
                    mAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    navigateToMainMenu();
                                }
                            })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                // If sign in fails, display a message to the user.
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "Unexpected type of credential");
                                }
                            });

                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error parsing Google ID token: " + e);
                }
            }
            else {
                Log.e(TAG, "Unknown credential type: " + credential.getType());
            }
        }
    }

    // Handle failure of credential retrieval
    private void handleFailure(@NonNull GetCredentialException e) {
        Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
        logger.log(Level.SEVERE, "Error getting credential: " + e);

        Log.d("/////", Objects.requireNonNull(e.getMessage()));

        createCredential();
    }

    // Create credentials with Google
    private void createCredential() {
        Log.d("/////", "Preparing credentials with Google");
    }



    @Override
    public void onClick(View v) {
        if (signInButton==v)
        {
            signIn();
        }
    }

    // Sign-in function to get credentials
    void signIn(){

        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> {

            Log.d("/////", "Preparing credentials with Google was cancelled.");
            Toast.makeText(this, "Cancelled.", Toast.LENGTH_SHORT).show();
        });
        credentialManager.getCredentialAsync(getApplicationContext(),
                request,
                cancellationSignal,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSuccess(getCredentialResponse);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        handleFailure(e);
                    }
                });
    }

    // Navigate to main menu activity
    void navigateToMainMenu(){
        Intent intent1 = new Intent(MainActivity.this, MainMenuActivity.class);
        startActivity(intent1);
    }
}