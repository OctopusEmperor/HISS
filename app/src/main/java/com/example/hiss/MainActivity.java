package com.example.hiss;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Collections;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CredentialManager credMan;
    private ActivityResultLauncher<GetCredentialRequest> requestCredentialsLauncher;
    private static final String TAG = "MySignInActivity";
    Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        credMan = CredentialManager.create(this);

        requestCredentialsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                credential -> {
                    if (credential != null) {
                        handleRetrievedCredential(credential);
                    }
                });


    }


    @Override
    public void onClick(View v) {
        if (signInButton==v)
        {
            requestCredentials();
        }
    }

    private void requestCredentials() {
        // Build GetCredentialRequest to filter for Google Sign-In credentials
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .setOrigin(Collections.singletonList(GoogleSignInAccount.class.getName()))
                .setFilterByProperties(Collections.singletonList(new PropertyFilter.Builder()
                        .setId("id") // Property ID used by Google Sign-In credentials
                        .setValue(getString(R.string.default_web_client_id)) // Replace with your server client ID
                        .build()))
                .build();

        // Request credentials using ActivityResultLauncher
        requestCredentialsLauncher.launch(request);
    }
}