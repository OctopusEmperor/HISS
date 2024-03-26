package com.example.hiss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.firebase.auth.GetTokenResult;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CredentialManager credMan;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GetGoogleIdOption googleIdOption;
    private static final String TAG = "MySignInActivity";
    ImageButton signInButton;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private GetCredentialRequest getCredentialRequest;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = (ImageButton) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (signInButton==v)
        {
            handleSignIn();
        }
    }

    private void handleSignIn(){
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId("353278388473-0feqddjdm87hc4u20g56ns3b97p89g05.apps.googleusercontent.com")
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credMan = CredentialManager.create(this);
        credMan.getCredentialAsync(re, request, null, Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // Handle successful credential retrieval
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        // Handle credential retrieval error
                    }
                });
    }

}