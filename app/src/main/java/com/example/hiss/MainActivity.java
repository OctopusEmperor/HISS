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
import androidx.credentials.PrepareGetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
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

    ImageButton signInButton;
    private Intent intent;
    String TAG;
    CredentialManager credentialManager;
    GetCredentialRequest request;
    private SignInClient oneTapClient;

    private BeginSignInRequest signInRequest;

    private static final int REQ_ONE_TAP = 100;
    private boolean credentialManagerStatus = true;
    String googleIdToken;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initCredentialManager();

        signInButton = (ImageButton) findViewById(R.id.signInButton);
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
            credentialManagerStatus = false;

            Log.d("/////", "Preparing credentials with Google was cancelled.");
            Toast.makeText(this, "Cancelled.", Toast.LENGTH_SHORT).show();
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            credentialManager.prepareGetCredentialAsync(
                    request,
                    cancellationSignal,
                    Executors.newSingleThreadExecutor(),
                    new CredentialManagerCallback<PrepareGetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(@NonNull PrepareGetCredentialResponse prepareGetCredentialResponse) {
//                            handleSignIn(prepareGetCredentialResponse);
                        }

                        @Override
                        public void onError(@NonNull GetCredentialException e) {
                            handleFailure(e);
                        }
                    }
            );
        }
    }

    public void handleSuccess(GetCredentialResponse result){
        Credential credential = result.getCredential();
        if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())){
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                    googleIdToken = googleIdTokenCredential.getIdToken();
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

    private void handleFailure(@NonNull GetCredentialException e) {
        Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
        logger.log(Level.SEVERE, "Error getting credential: " + e);

        Log.d("/////", Objects.requireNonNull(e.getMessage()));

        createCredential();
    }

    private void createCredential() {
        credentialManagerStatus = false;
        Log.d("/////", "Preparing credentials with Google");
    }



    @Override
    public void onClick(View v) {
        if (signInButton==v)
        {
            signIn();
        }
    }

    void signIn(){
        credentialManagerStatus = true;

        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> {
            credentialManagerStatus = false;

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

    void navigateToMainMenu(){
        Intent intent1 = new Intent(MainActivity.this, MainMenuActivity.class);
        startActivity(intent1);
    }
}